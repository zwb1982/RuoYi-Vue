<template>
  <div class="data-chat-input-page">
    <a-page-header
      :title="`AI对话数据录入 (表: ${tableId})`"
      sub-title="通过自然语言描述信息，AI将辅助提取并填充数据"
      @back="goBack"
    />

    <div class="chat-layout">
      <div class="chat-area">
        <div class="messages-container" ref="messagesContainerRef">
          <div v-for="message in messages" :key="message.id" :class="['message', message.type]">
            <div class="avatar">
              <UserOutlined v-if="message.type === 'user'" />
              <RobotOutlined v-if="message.type === 'ai'" />
            </div>
            <div class="content-wrapper">
              <div class="content">{{ message.content }}</div>
              <div v-if="message.fileInfo" class="file-info">
                <PaperClipOutlined /> {{ message.fileInfo.name }} ({{ formatFileSize(message.fileInfo.size) }})
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <a-textarea
            v-model:value="userInput"
            placeholder="请描述您要录入的信息，AI将帮您提取关键数据... (Ctrl+Enter发送)"
            :rows="4"
            @keydown.enter.ctrl.prevent="sendMessage"
            @keydown.enter.exact.prevent="sendMessage"
          />
          <div class="actions-bar">
            <a-upload
              :before-upload="handleFileUpload"
              :show-upload-list="false"
              name="file"
              action="/api/workspace/file/upload"  _comment="This is a dummy action, beforeUpload handles it"
              @change="handleUploadChange"
            >
              <a-button type="text" title="上传文件辅助录入">
                <template #icon><PaperClipOutlined /></template>
              </a-button>
            </a-upload>
            <a-button type="primary" @click="sendMessage" :loading="processingMessage">
              发送 (Ctrl+Enter)
            </a-button>
          </div>
        </div>
      </div>

      <div class="extracted-data-panel" v-if="fieldsForTable.length > 0">
        <a-card title="AI提取的数据预览" :bordered="false">
          <div v-if="!isDataExtracted && !extracting" class="empty-data-placeholder">
            <InfoCircleOutlined style="font-size: 24px; margin-bottom: 16px;" />
            <p>发送信息后，AI提取的数据将在此处显示。</p>
          </div>
          <div v-if="extracting" style="text-align: center; padding: 20px;">
             <a-spin tip="AI努力提取中..." />
          </div>
          <div v-if="isDataExtracted && !extracting">
            <a-descriptions bordered size="small" :column="1">
              <a-descriptions-item
                v-for="field in fieldsForTable"
                :key="field.fieldId"
                :label="field.fieldLabel"
              >
                {{ extractedData[field.fieldName] || '-' }}
              </a-descriptions-item>
            </a-descriptions>
            <div class="extracted-data-actions" style="margin-top: 16px; text-align: right;">
              <a-button @click="reExtract" style="margin-right: 8px;" :disabled="!lastUserMessageContent && !lastUserFile">
                重新提取
              </a-button>
              <a-button type="primary" @click="confirmAndSaveData">
                确认并保存数据
              </a-button>
            </div>
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { PageHeader, Input, Button, Upload, Descriptions, Card, Spin, message, UploadChangeParam, UploadFile } from 'ant-design-vue';
import { UserOutlined, RobotOutlined, PaperClipOutlined, InfoCircleOutlined } from '@ant-design/icons-vue';
import type { TableField } from '../types';
import { fetchTableFieldsAPI } from '../services/tableService';
import { extractDataAI, createRecordAPI } from '../services/recordService';


interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  fileInfo?: { name: string; size: number; type: string; raw: File };
}
interface ExtractedData {
  [key: string]: any;
}

export default defineComponent({
  name: 'DataChatInputView',
  components: {
    APageHeader: PageHeader, ATextarea: Input.TextArea, AButton: Button, AUpload: Upload,
    ADescriptions: Descriptions, ADescriptionsItem: Descriptions.Item, ACard: Card, ASpin: Spin,
    UserOutlined, RobotOutlined, PaperClipOutlined, InfoCircleOutlined,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const workspaceId = ref(route.params.workspaceId as string);
    const tableId = ref(route.params.tableId as string);

    const messagesContainerRef = ref<HTMLElement | null>(null);
    const messages = ref<Message[]>([]);
    const userInput = ref('');
    const processingMessage = ref(false); // General loading for send message and save
    const extracting = ref(false); // Specific for AI extraction phase
    const isDataExtracted = ref(false);
    const extractedData = reactive<ExtractedData>({});
    const fieldsForTable = ref<TableField[]>([]);
    const lastUserMessageContent = ref<string | null>(null);
    const lastUserFile = ref<File | null>(null);


    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainerRef.value) {
          messagesContainerRef.value.scrollTop = messagesContainerRef.value.scrollHeight;
        }
      });
    };

    const fetchTableFields = async () => {
      processingMessage.value = true;
      try {
        const fields = await fetchTableFieldsAPI(tableId.value);
        fieldsForTable.value = fields.map(f => ({...f, fieldId: String(f.fieldId) }));
      } catch (error: any) {
        message.error('加载表字段失败: ' + error.message);
        fieldsForTable.value = [];
      } finally {
        processingMessage.value = false;
      }
    };

    onMounted(() => {
      fetchTableFields();
      messages.value.push({id: String(Date.now()), type: 'ai', content: '您好！请描述您要录入的数据，或上传相关文件，我会尽力帮您提取。'});
    });

    const addMessage = (type: 'user' | 'ai', content: string, fileInfo?: Message['fileInfo']) => {
      messages.value.push({ id: String(Date.now()), type, content, fileInfo });
      scrollToBottom();
    };

    const formatFileSize = (size: number) => {
        if (size < 1024) return size + ' Bytes';
        else if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB';
        else return (size / (1024 * 1024)).toFixed(2) + ' MB';
    };

    const handleFileUpload = (file: File) => {
      // Display file in chat as user message part
      // Actual file processing will happen when sendMessage is called if text is also present,
      // or could trigger processWithAI directly if desired.
      // For now, we just store it and let sendMessage handle it.
      lastUserFile.value = file;
      addMessage('user', `已选择文件: ${file.name}`, { name: file.name, size: file.size, type: file.type, raw: file });
      userInput.value = `请处理文件 ${file.name}。`; // Optional: prefill text area
      return false; // Prevent actual antd upload component from uploading
    };

    const handleUploadChange = (info: UploadChangeParam) => {
        // This is mostly for antd's own state, but since we block actual upload,
        // it might not be very relevant unless we want to show its internal progress (which we are not).
        if (info.file.status === 'done') {
            message.success(`${info.file.name} file selection recorded.`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} file selection failed.`);
        }
    };

    const processWithAI = async (text: string, file?: File | null) => {
      processingMessage.value = true;
      extracting.value = true;
      isDataExtracted.value = false;

      let aiQueryText = text;
      let fileNameInfo = file ? file.name : undefined;

      addMessage('ai', `正在分析您的输入: "${aiQueryText.substring(0,50)}..." ${fileNameInfo ? `(文件: ${fileNameInfo})` : ''} 请稍候。`);

      try {
        const extracted = await extractDataAI(aiQueryText, tableId.value, fileNameInfo);
        Object.keys(extractedData).forEach(key => delete extractedData[key]);
        Object.assign(extractedData, extracted);
        isDataExtracted.value = true;
        addMessage('ai', '数据提取完成！请在右侧面板查看并确认。');
      } catch (error: any) {
        message.error('AI提取数据失败: ' + error.message);
        addMessage('ai', '抱歉，数据提取时遇到问题。');
      } finally {
        processingMessage.value = false;
        extracting.value = false;
      }
    };

    const sendMessage = () => {
      const currentText = userInput.value.trim();
      if (!currentText && !lastUserFile.value) {
        message.info('请输入信息或上传文件。');
        return;
      }

      if(currentText) { // If there's text, add it as a message. File might have been added visually already.
          if(!lastUserFile.value || messages.value[messages.value.length-1]?.fileInfo?.name !== lastUserFile.value.name) {
            // If no file OR if the last message wasn't about this specific file, add new user text message.
            addMessage('user', currentText);
          } else {
            // If last message was about this file, maybe append text to it or just use currentText for AI.
            // For simplicity, we'll just use currentText. The file is already in lastUserFile.
          }
      } else if (lastUserFile.value && !currentText) {
        // If only file is present (text was auto-filled and maybe cleared, or user just uploaded)
        // The file message is already added by handleFileUpload.
      }

      lastUserMessageContent.value = currentText;

      processWithAI(currentText, lastUserFile.value); // Pass both current text and potentially selected file
      userInput.value = '';
      // Do not clear lastUserFile.value here, allow re-extraction with it.
    };

    const reExtract = () => {
      if (lastUserMessageContent.value !== null || lastUserFile.value !== null) {
        addMessage('user', `请求重新提取: "${lastUserMessageContent.value || ''}" ${lastUserFile.value ? `(文件: ${lastUserFile.value.name})` : ''}`);
        processWithAI(lastUserMessageContent.value || '', lastUserFile.value);
      } else {
        message.warn("没有可供重新提取的先前输入。");
      }
    };

    const confirmAndSaveData = async () => {
      if (Object.keys(extractedData).length === 0) {
        message.warn('没有可保存的已提取数据。');
        return;
      }
      processingMessage.value = true;
      try {
        await createRecordAPI(tableId.value, extractedData);
        message.success('数据已成功保存!');

        Object.keys(extractedData).forEach(key => delete extractedData[key]);
        isDataExtracted.value = false;
        lastUserMessageContent.value = null;
        lastUserFile.value = null;
        messages.value = [];
        addMessage('ai', '数据已保存。请输入下一条信息。');

      } catch (error: any) {
        message.error('保存数据失败: ' + error.message);
      } finally {
        processingMessage.value = false;
      }
    };

    const goBack = () => router.back();

    return {
      workspaceId, tableId, messagesContainerRef, messages, userInput, processingMessage,
      extractedData, fieldsForTable, isDataExtracted, extracting, lastUserMessageContent, lastUserFile,
      sendMessage, handleFileUpload, handleUploadChange, reExtract, confirmAndSaveData, goBack, formatFileSize
    };
  },
});
</script>

<style scoped>
.data-chat-input-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px); /* Assuming 64px global header */
  background-color: #f0f2f5;
}
.chat-layout {
  flex-grow: 1;
  display: flex;
  overflow: hidden; /* Important for child scrolling */
  padding: 12px; /* Half of page padding for inner elements */
  gap: 12px;
}
.chat-area {
  flex: 2; /* Takes 2/3 of space */
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.09);
  overflow: hidden;
}
.messages-container {
  flex-grow: 1;
  padding: 16px;
  overflow-y: auto;
}
.message {
  display: flex;
  margin-bottom: 16px;
}
.message.user {
  flex-direction: row-reverse;
}
.message .avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background-color: #ddd; display: flex;
  align-items: center; justify-content: center;
  font-size: 18px;
}
.message.user .avatar { margin-left: 12px; background-color: #1890ff; color: white;}
.message.ai .avatar { margin-right: 12px; background-color: #7cb305; color: white;}

.content-wrapper {
    max-width: calc(100% - 60px);
}
.message .content {
  padding: 8px 12px;
  border-radius: 8px;
  background-color: #f0f0f0;
  word-break: break-word;
}
.message.user .content { background-color: #e6f7ff; }
.message .file-info {
    font-size: 0.85em;
    color: #888;
    margin-top: 4px;
}
.message.user .file-info { text-align: right; }


.input-area {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
}
.actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.extracted-data-panel {
  flex: 1; /* Takes 1/3 of space */
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.09);
  overflow-y: auto; /* Scroll if content is too long */
}
.extracted-data-panel .ant-card {
    height: 100%;
    display: flex;
    flex-direction: column;
}
.extracted-data-panel .ant-card-body {
    flex-grow: 1;
    overflow-y: auto;
}
.empty-data-placeholder {
    text-align: center;
    color: #888;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content:center;
    height:100%;
}
</style>

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
              <a-button @click="reExtract" style="margin-right: 8px;" :disabled="!lastUserMessageContent">
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
// import axios from 'axios';

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
    const processingMessage = ref(false);
    const extracting = ref(false);
    const isDataExtracted = ref(false);
    const extractedData = reactive<ExtractedData>({});
    const fieldsForTable = ref<TableField[]>([]); // To be fetched based on tableId
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
      // Mock: In a real app, fetch fields for tableId
      // const response = await axios.get(`/api/workspace/field/list?tableId=${tableId.value}`);
      // fieldsForTable.value = response.data.rows;
      setTimeout(() => {
        fieldsForTable.value = [
          { fieldId: 'f1', tableId: tableId.value, fieldName: 'productName', fieldLabel: '产品名称', fieldType: 'text', sortOrder: 1, isRequired: true },
          { fieldId: 'f2', tableId: tableId.value, fieldName: 'quantity', fieldLabel: '数量', fieldType: 'number', sortOrder: 2, isRequired: true },
          { fieldId: 'f3', tableId: tableId.value, fieldName: 'customerNotes', fieldLabel: '客户备注', fieldType: 'rich_text', sortOrder: 3, isRequired: false },
        ];
      }, 500);
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
      addMessage('user', `上传了文件: ${file.name}`, { name: file.name, size: file.size, type: file.type, raw: file });
      lastUserFile.value = file;
      // Process file immediately or wait for text message? For now, just adds to chat.
      // Can trigger AI extraction if only file is provided.
      // For this mock, we assume text is still primary trigger for AI.
      // Prevent actual upload by returning false or a Promise that resolves to false
      return false;
    };

    const handleUploadChange = (info: UploadChangeParam) => {
        if (info.file.status === 'done') { // This won't be hit due to beforeUpload returning false
            message.success(`${info.file.name} file uploaded successfully`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    const processWithAI = async (text: string, file?: File | null) => {
      processingMessage.value = true;
      extracting.value = true;
      isDataExtracted.value = false;

      let aiQuery = text;
      if (file) {
        aiQuery += `
[附加文件: ${file.name}]`; // Simplistic representation
      }
      addMessage('ai', `正在分析您的输入: "${aiQuery.substring(0,50)}..." 请稍候。`);

      // Mock AI processing & data extraction
      // In real app: const response = await axios.post('/api/workspace/record/ai-extract', { text: aiQuery, tableId: tableId.value, file: fileData (if sending file content) });
      // Object.assign(extractedData, response.data.data);
      setTimeout(() => {
        const mockExtracted: ExtractedData = {};
        if (text.toLowerCase().includes('苹果') || (file && file.name.toLowerCase().includes('apple'))) {
          mockExtracted.productName = '苹果';
          mockExtracted.quantity = Math.floor(Math.random() * 10) + 1;
        } else {
          mockExtracted.productName = '示例产品';
          mockExtracted.quantity = 1;
        }
        if (text.length > 20) mockExtracted.customerNotes = text.substring(0, 20) + "... (备注)";

        // Clear previous data and assign new
        Object.keys(extractedData).forEach(key => delete extractedData[key]);
        Object.assign(extractedData, mockExtracted);

        isDataExtracted.value = true;
        processingMessage.value = false;
        extracting.value = false;
        addMessage('ai', '数据提取完成！请在右侧面板查看并确认。');
      }, 2000);
    };

    const sendMessage = () => {
      if (!userInput.value.trim() && !lastUserFile.value) { // Allow sending if only file was "uploaded"
        message.info('请输入信息或上传文件。');
        return;
      }
      const currentText = userInput.value.trim();
      if(currentText) addMessage('user', currentText);

      lastUserMessageContent.value = currentText; // Save for re-extract
      // If a file was just added via handleFileUpload, it's in lastUserFile.
      // If text is added after file, use both.

      processWithAI(currentText, lastUserFile.value);
      userInput.value = ''; // Clear input after sending
      // lastUserFile.value = null; // Consume the file for this interaction, or keep for re-extract? Let's keep for re-extract.
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
      console.log('Confirming and saving data:', extractedData);
      // Mock API Call
      // await axios.post(`/api/workspace/record/table/${tableId.value}`, { tableId: tableId.value, recordData: extractedData });
      message.success('数据已保存 (模拟)！');
      // Clear form or navigate away
      Object.keys(extractedData).forEach(key => delete extractedData[key]);
      isDataExtracted.value = false;
      lastUserMessageContent.value = null;
      lastUserFile.value = null;
      messages.value = [ {id: String(Date.now()), type: 'ai', content: '数据已保存。请继续输入新数据。'} ];

    };

    const goBack = () => router.back();

    return {
      workspaceId, tableId, messagesContainerRef, messages, userInput, processingMessage,
      extractedData, fieldsForTable, isDataExtracted, extracting, lastUserMessageContent,
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

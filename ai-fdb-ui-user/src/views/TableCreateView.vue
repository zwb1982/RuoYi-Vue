<template>
  <div class="table-create-page">
    <a-page-header title="创建新数据表" :sub-title="`在工作空间 '${workspaceId || '未知'}' 中`" @back="goBack" />

    <a-form :model="tableForm" layout="vertical" class="table-create-form" @finish="handleCreateTable">
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="数据表名称" name="tableName" :rules="[{ required: true, message: '请输入数据表名称' }]">
            <a-input v-model:value="tableForm.tableName" placeholder="例如：客户订单表" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
             <!-- Placeholder for other table settings if any -->
        </a-col>
      </a-row>

      <a-form-item label="数据表描述" name="tableDescription">
        <a-textarea
          v-model:value="tableForm.tableDescription"
          placeholder="描述这个数据表的用途、主要包含的数据等信息..."
          :rows="3"
        />
        <a-button
          type="link"
          @click="generateDescription"
          :loading="generatingDescription"
          style="margin-top: 8px; padding-left: 0;"
        >
          <template #icon><BulbOutlined /></template>
          AI智能生成描述
        </a-button>
      </a-form-item>

      <a-divider>字段设计</a-divider>

      <div class="field-design-area">
        <a-button
          type="dashed"
          @click="suggestFields"
          :loading="suggestingFields"
          block
          style="margin-bottom: 24px;"
        >
          <template #icon><RobotOutlined /></template>
          AI推荐字段 (模拟)
        </a-button>

        <div v-if="aiSuggestedFields.length > 0" class="suggested-fields-section">
          <h3>AI推荐的字段：</h3>
          <a-list item-layout="horizontal" :data-source="aiSuggestedFields" bordered size="small">
            <template #renderItem="{ item, index }">
              <a-list-item>
                <a-list-item-meta
                  :title="`${item.fieldLabel} (${item.fieldName})`"
                  :description="`类型: ${item.fieldType} - ${item.fieldDescription || '暂无描述'}`"
                >
                </a-list-item-meta>
                <template #actions>
                  <a-button type="link" @click="addFieldToTable(item, index)">添加到表</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </div>

        <h3>当前表字段：</h3>
        <div v-if="tableForm.fields.length === 0" style="text-align: center; color: #888; margin-bottom:16px;">
            暂无字段，请从AI推荐添加或手动创建。
        </div>
        <a-list item-layout="horizontal" :data-source="tableForm.fields" bordered size="small" v-else>
            <template #renderItem="{ item, index }">
              <a-list-item>
                <a-list-item-meta
                  :title="`${item.fieldLabel} (${item.fieldName})`"
                  :description="`类型: ${item.fieldType} - ${item.fieldDescription || '暂无描述'}`"
                >
                </a-list-item-meta>
                 <template #actions>
                  <a-popconfirm title="确定移除此字段吗?" @confirm="removeFieldFromTable(index)">
                    <a-button type="link" danger>移除</a-button>
                  </a-popconfirm>
                </template>
              </a-list-item>
            </template>
        </a-list>
        <!-- Basic manual field add - simplified -->
        <a-button type="dashed" @click="showManualAddFieldModal = true" block style="margin-top: 16px;">
            <template #icon><PlusOutlined /></template>手动添加字段 (简化版)
        </a-button>
      </div>

      <a-form-item style="margin-top: 32px;">
        <a-button type="primary" html-type="submit" :loading="creatingTable" size="large">
          创建数据表
        </a-button>
        <a-button style="margin-left: 10px;" @click="goBack" size="large">
          取消
        </a-button>
      </a-form-item>
    </a-form>

    <!-- Manual Add Field Modal -->
    <a-modal v-model:open="showManualAddFieldModal" title="手动添加字段" @ok="handleManualAddField">
        <a-form :model="manualFieldForm" layout="vertical">
            <a-form-item label="字段名称 (英文)" name="fieldName" :rules="[{required: true}]"><a-input v-model:value="manualFieldForm.fieldName" /></a-form-item>
            <a-form-item label="字段标签 (中文)" name="fieldLabel" :rules="[{required: true}]"><a-input v-model:value="manualFieldForm.fieldLabel" /></a-form-item>
            <a-form-item label="字段类型" name="fieldType" :rules="[{required: true}]">
                 <a-select v-model:value="manualFieldForm.fieldType">
                    <a-select-option value="text">文本 (text)</a-select-option>
                    <a-select-option value="number">数字 (number)</a-select-option>
                    <a-select-option value="date">日期 (date)</a-select-option>
                    <a-select-option value="datetime">日期时间 (datetime)</a-select-option>
                    <a-select-option value="boolean">布尔 (boolean)</a-select-option>
                    <a-select-option value="richtext">富文本 (rich_text)</a-select-option>
                 </a-select>
            </a-form-item>
            <a-form-item label="字段描述" name="fieldDescription"><a-textarea v-model:value="manualFieldForm.fieldDescription" /></a-form-item>
        </a-form>
    </a-modal>

  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { PageHeader, Form, Input, Button, Textarea, Divider, List, Modal, Select, Popconfirm, message } from 'ant-design-vue';
import { BulbOutlined, RobotOutlined, PlusOutlined } from '@ant-design/icons-vue';
import type { TableField } from '../types'; // Assuming TableField is defined in types
// import axios from 'axios'; // For actual API calls

export default defineComponent({
  name: 'TableCreateView',
  components: {
    APageHeader: PageHeader, AForm: Form, AFormItem: Form.Item, AInput: Input, AButton: Button,
    ATextarea: Textarea, ADivider: Divider, AList: List, AListItem: List.Item, AListItemMeta: List.Item.Meta,
    AModal: Modal, ASelect: Select, ASelectOption: Select.Option, APopconfirm: Popconfirm,
    BulbOutlined, RobotOutlined, PlusOutlined,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const workspaceId = ref(route.params.workspaceId as string || '');

    const tableForm = reactive<{
      tableName: string;
      tableDescription: string;
      fields: TableField[];
    }>({
      tableName: '',
      tableDescription: '',
      fields: [],
    });

    const generatingDescription = ref(false);
    const suggestingFields = ref(false);
    const creatingTable = ref(false);
    const aiSuggestedFields = ref<TableField[]>([]);

    const showManualAddFieldModal = ref(false);
    const manualFieldForm = reactive<Partial<TableField>>({
        fieldName: '', fieldLabel: '', fieldType: 'text', fieldDescription: ''
    });


    const goBack = () => {
      router.back();
    };

    const generateDescription = async () => {
      if (!tableForm.tableName) {
        message.error('请输入数据表名称以生成描述');
        return;
      }
      generatingDescription.value = true;
      // Mock AI call
      // const response = await axios.post(`/api/workspace/table/0/ai-generate-description`, { tableName: tableForm.tableName });
      // tableForm.tableDescription = response.data.data;
      setTimeout(() => {
        tableForm.tableDescription = `这是为"${tableForm.tableName}"AI智能生成的描述：一个用于管理和跟踪相关信息的综合数据表。`;
        generatingDescription.value = false;
        message.success('AI描述已生成！');
      }, 1000);
    };

    const suggestFields = async () => {
      if (!tableForm.tableName) {
        message.error('请输入数据表名称以推荐字段');
        return;
      }
      suggestingFields.value = true;
      // Mock AI call
      // const response = await axios.post(`/api/workspace/table/0/field/ai-suggest`, { tableName: tableForm.tableName, tableDescription: tableForm.tableDescription });
      // aiSuggestedFields.value = response.data.data;
      setTimeout(() => {
        aiSuggestedFields.value = [
          { fieldId: 'temp1', tableId: '', fieldName: 'itemName', fieldLabel: '项目名称', fieldType: 'text', sortOrder: 1, isRequired: true, fieldDescription: '物品或任务的名称' },
          { fieldId: 'temp2', tableId: '', fieldName: 'quantity', fieldLabel: '数量', fieldType: 'number', sortOrder: 2, isRequired: true, fieldDescription: '项目的数量' },
          { fieldId: 'temp3', tableId: '', fieldName: 'status', fieldLabel: '状态', fieldType: 'select', sortOrder: 3, isRequired: false, fieldDescription: '当前状态' },
          { fieldId: 'temp4', tableId: '', fieldName: 'dueDate', fieldLabel: '截止日期', fieldType: 'date', sortOrder: 4, isRequired: false, fieldDescription: '任务或项目的截止日期' },
        ];
        suggestingFields.value = false;
        message.success('AI字段推荐已加载！');
      }, 1000);
    };

    const addFieldToTable = (field: TableField, index: number) => {
      // Avoid adding duplicates by fieldName
      if (!tableForm.fields.find(f => f.fieldName === field.fieldName)) {
        tableForm.fields.push({ ...field, fieldId: String(Date.now()) }); // Use new temp ID
        aiSuggestedFields.value.splice(index, 1); // Remove from suggestions
      } else {
        message.warning(`字段 '${field.fieldName}' 已存在于表中。`);
      }
    };

    const removeFieldFromTable = (index: number) => {
        tableForm.fields.splice(index, 1);
    };

    const handleManualAddField = () => {
        if(!manualFieldForm.fieldName || !manualFieldForm.fieldLabel || !manualFieldForm.fieldType) {
            message.error("字段名称、标签和类型为必填项。");
            return;
        }
        const newField: TableField = {
            fieldId: String(Date.now()), // Temporary ID
            tableId: '', // Will be set upon table creation
            fieldName: manualFieldForm.fieldName!,
            fieldLabel: manualFieldForm.fieldLabel!,
            fieldType: manualFieldForm.fieldType!,
            fieldDescription: manualFieldForm.fieldDescription,
            isRequired: false, // Default
            sortOrder: tableForm.fields.length + 1,
        };
        if (!tableForm.fields.find(f => f.fieldName === newField.fieldName)) {
            tableForm.fields.push(newField);
        } else {
             message.warning(`字段 '${newField.fieldName}' 已存在于表中。`);
        }
        showManualAddFieldModal.value = false;
        manualFieldForm.fieldName = '';
        manualFieldForm.fieldLabel = '';
        manualFieldForm.fieldType = 'text';
        manualFieldForm.fieldDescription = '';
    };


    const handleCreateTable = async () => {
      if (tableForm.fields.length === 0) {
        message.error('数据表至少需要一个字段。');
        return;
      }
      creatingTable.value = true;
      const payload = {
        workspaceId: workspaceId.value,
        tableName: tableForm.tableName,
        tableDescription: tableForm.tableDescription,
        fields: tableForm.fields.map(f => ({...f, fieldId: undefined})) // Remove temp frontend fieldIds
      };
      console.log('Creating table with payload:', payload);
      // Mock API Call
      // await axios.post(`/api/workspace/table/workspace/${workspaceId.value}`, payload);
      setTimeout(() => {
        creatingTable.value = false;
        message.success(`数据表 "${tableForm.tableName}" 创建成功 (模拟)`);
        router.push(`/workspace/${workspaceId.value}/tables`); // Navigate to table list or details for this workspace
      }, 1500);
    };

    onMounted(() => {
        if (!workspaceId.value) {
            message.error("未指定工作空间ID！将返回上一页。");
            // router.push('/workspace'); // Or handle error appropriately
        }
    });

    return {
      workspaceId,
      tableForm,
      generatingDescription,
      suggestingFields,
      creatingTable,
      aiSuggestedFields,
      goBack,
      generateDescription,
      suggestFields,
      addFieldToTable,
      removeFieldFromTable,
      handleCreateTable,
      showManualAddFieldModal,
      manualFieldForm,
      handleManualAddField,
    };
  },
});
</script>

<style scoped>
.table-create-page {
  padding: 0 24px 24px 24px; /* No top padding, relying on PageHeader */
}
.table-create-form {
  background: #fff;
  padding: 24px;
  border-radius: 6px;
}
.field-design-area {
  /* Add some styling if needed */
}
.suggested-fields-section {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 4px;
}
.suggested-fields-section h3 {
    font-size: 16px;
    margin-bottom: 12px;
}
</style>

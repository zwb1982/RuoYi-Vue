<template>
  <div class="workspace-list-page">
    <div class="header-bar">
      <h1>我的工作空间</h1>
      <a-button type="primary" @click="handleCreateWorkspace">
        <template #icon><PlusOutlined /></template>
        创建工作空间
      </a-button>
    </div>

    <div v-if="loading" class="loading-spinner">
      <a-spin size="large" />
    </div>

    <div v-else-if="workspaceList.length === 0" class="empty-state">
      <a-empty description="暂无工作空间，快去创建一个吧！" />
    </div>

    <div v-else class="workspace-grid">
      <a-card
        v-for="workspace in workspaceList"
        :key="workspace.workspaceId"
        class="workspace-card"
        hoverable
        @click="enterWorkspace(workspace.workspaceId)"
      >
        <template #title>
          <div class="card-title-content">
            <DatabaseOutlined style="margin-right: 8px; color: #1890ff;" />
            {{ workspace.workspaceName }}
          </div>
        </template>
        <p class="workspace-description">{{ workspace.description || '暂无描述' }}</p>
        <div class="workspace-stats">
          <a-tag color="blue">
            <template #icon><TableOutlined /></template>
            数据表: {{ workspace.tableCount || 0 }}
          </a-tag>
          <a-tag color="green">
            <template #icon><OrderedListOutlined /></template>
            记录数: {{ workspace.recordCount || 0 }}
          </a-tag>
        </div>
      </a-card>
    </div>

    <!-- Placeholder for Create Workspace Modal -->
    <a-modal
      v-model:open="showCreateModal"
      title="创建新工作空间"
      @ok="submitCreateWorkspace"
      @cancel="showCreateModal = false"
      :confirm-loading="submittingCreate"
    >
      <a-form :model="newWorkspaceForm" layout="vertical" ref="createFormRef">
        <a-form-item label="工作空间名称" name="name" :rules="[{required: true, message: '请输入工作空间名称'}]">
          <a-input v-model:value="newWorkspaceForm.name" placeholder="例如：项目A数据中心" />
        </a-form-item>
        <a-form-item label="描述 (可选)" name="description">
          <a-textarea v-model:value="newWorkspaceForm.description" placeholder="简要描述这个工作空间的用途" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { Button, Card, Spin, Empty, Modal, Form, Input, Tag, message } from 'ant-design-vue'; // Added message
import { PlusOutlined, DatabaseOutlined, TableOutlined, OrderedListOutlined } from '@ant-design/icons-vue';
import type { Workspace } from '../types';
import { fetchWorkspaces as apiFetchWorkspaces, createWorkspace as apiCreateWorkspace } from '../services/workspaceService';


export default defineComponent({
  name: 'WorkspaceListView',
  components: {
    PlusOutlined,
    DatabaseOutlined,
    TableOutlined,
    OrderedListOutlined,
    // Explicitly list Ant Design components if not globally registered or for clarity
    AButton: Button, ACard: Card, ASpin: Spin, AEmpty: Empty, AModal: Modal,
    AForm: Form, AFormItem: Form.Item, AInput: Input, ATextarea: Input.TextArea, ATag: Tag
  },
  setup() {
    const router = useRouter();
    const loading = ref(true);
    const workspaceList = ref<Workspace[]>([]);
    const showCreateModal = ref(false);
    const submittingCreate = ref(false);
    const createFormRef = ref(); // For form validation if needed

    const newWorkspaceForm = reactive({
      name: '',
      description: '',
    });

    const fetchWorkspaces = async () => {
      loading.value = true;
      try {
        // Params for pagination/filtering can be added here if queryParams state is maintained
        // Example: apiFetchWorkspaces({ pageNum: queryParams.pageNum, pageSize: queryParams.pageSize })
        const response = await apiFetchWorkspaces({ /* pageNum: 1, pageSize: 10 */ });
        if (response.code === 200) {
          workspaceList.value = response.rows.map(ws => ({
            ...ws,
            workspaceId: String(ws.workspaceId) // Ensure ID is string for frontend consistency
            // tableCount & recordCount are not in the backend AiWorkspace.java domain by default
            // These would need to be added to backend response or calculated separately if needed
          }));
          // total.value = response.total; // If using pagination state like RuoYi admin
        } else {
          message.error('获取工作空间列表失败: ' + response.msg);
          workspaceList.value = [];
        }
      } catch (error: any) {
        message.error('获取工作空间列表时发生错误: ' + (error.message || '未知错误'));
        workspaceList.value = [];
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchWorkspaces();
    });

    const enterWorkspace = (workspaceId: string) => {
      console.log('Entering workspace:', workspaceId);
      // router.push(`/workspace/${workspaceId}/tables`); // Example future navigation
      alert(`导航到工作空间 ${workspaceId} (待实现)`);
    };

    const handleCreateWorkspace = () => {
      newWorkspaceForm.name = '';
      newWorkspaceForm.description = '';
      if (createFormRef.value) {
        createFormRef.value.resetFields();
      }
      showCreateModal.value = true;
    };

    const submitCreateWorkspace = async () => {
      if (!newWorkspaceForm.name.trim()) {
        message.error('请输入工作空间名称');
        return;
      }
      // Add form validation if createFormRef is used
      // await createFormRef.value.validate();

      submittingCreate.value = true;
      try {
        await apiCreateWorkspace({
            workspaceName: newWorkspaceForm.name,
            description: newWorkspaceForm.description
        });
        message.success(`工作空间 "${newWorkspaceForm.name}" 创建成功!`);
        showCreateModal.value = false;
        fetchWorkspaces(); // Refetch the list to show the new workspace
      } catch (error: any) {
        message.error('创建工作空间失败: ' + (error.message || '未知错误'));
      } finally {
        submittingCreate.value = false;
      }
    };

    return {
      loading,
      workspaceList,
      showCreateModal,
      submittingCreate,
      createFormRef,
      newWorkspaceForm,
      enterWorkspace,
      handleCreateWorkspace,
      submitCreateWorkspace,
    };
  },
});
</script>

<style scoped>
.workspace-list-page {
  padding: 24px;
  background-color: #fff;
  min-height: calc(100vh - 64px);
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.header-bar h1 {
  font-size: 24px;
  font-weight: 600;
}
.loading-spinner, .empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
.workspace-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}
.workspace-card {
  /* transition: all 0.3s; */
}
.workspace-card:hover {
    /* box-shadow: 0 4px 12px rgba(0,0,0,0.1); */
    /* border-color: #1890ff; */
}
.card-title-content {
    display: flex;
    align-items: center;
}
.workspace-description {
  color: #555;
  margin-bottom: 16px;
  min-height: 44px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
.workspace-stats {
  display: flex;
  gap: 8px;
}
</style>

<template>
  <div id="app-layout">
    <a-layout style="min-height: 100vh;">
      <a-layout-header v-if="userStore.isAuthenticated" class="app-header">
        <div class="logo">AI-FDB</div>
        <div class="user-actions">
          <span v-if="userStore.username" style="margin-right: 16px; color: white;">
            你好, {{ userStore.username }}
          </span>
          <a-button type="primary" @click="handleLogout">退出登录</a-button>
        </div>
      </a-layout-header>
      <a-layout-content :style="{ padding: userStore.isAuthenticated ? '0 24px 24px' : '0', background: '#fff' }">
         <!-- Add breadcrumbs or navigation here if needed later -->
        <router-view />
      </a-layout-content>
      <a-layout-footer style="text-align: center" v-if="userStore.isAuthenticated">
        AI数据空间管理 ©2025 Created by Jules for AI-FDB Project
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useRouter }
  from 'vue-router';
import { useUserStore } from './store/userStore';
import { Layout, Button, message } from 'ant-design-vue';


export default defineComponent({
  name: 'App',
  components: {
    ALayout: Layout,
    ALayoutHeader: Layout.Header,
    ALayoutContent: Layout.Content,
    ALayoutFooter: Layout.Footer,
    AButton: Button,
  },
  setup() {
    const userStore = useUserStore();
    const router = useRouter();

    const handleLogout = () => {
      userStore.logout();
      message.success('您已成功退出登录！');
      router.push('/login');
    };

    return {
      userStore,
      handleLogout,
    };
  }
});
</script>

<style>
/* Global styles from previous setup */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
/* App.vue specific styles */
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #001529; /* Dark background for header */
  padding: 0 24px;
}
.logo {
  font-size: 20px;
  color: white;
  font-weight: bold;
}
/* Ensure content has white background if header is present */
.ant-layout-content {
    /* background: #fff; */ /* Moved to inline style for conditional padding */
}
</style>

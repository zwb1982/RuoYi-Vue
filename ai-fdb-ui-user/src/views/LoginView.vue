<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h2>AI数据空间管理</h2>
      </div>
      <a-form :model="loginForm" @finish="handleLogin" layout="vertical">
        <a-form-item
          name="username"
          label="用户名"
          :rules="[{ required: true, message: '请输入用户名!' }]"
        >
          <a-input
            v-model:value="loginForm.username"
            placeholder="用户名"
            size="large"
          >
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item
          name="password"
          label="密码"
          :rules="[{ required: true, message: '请输入密码!' }]"
        >
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="密码"
            size="large"
          >
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>

        <a-form-item v-if="loginError">
            <a-alert type="error" :message="loginError" banner />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue';
import { Form, Input, Button, message, Alert } from 'ant-design-vue'; // Added Alert
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/userStore'; // Import Pinia store

export default defineComponent({
  name: 'LoginView',
  components: {
    UserOutlined,
    LockOutlined,
    AForm: Form, AFormItem: Form.Item, AInput: Input, AInputPassword: Input.Password, AButton: Button, AAlert: Alert,
  },
  setup() {
    const loginForm = reactive({
      username: '',
      password: '',
    });
    const loading = ref(false);
    const loginError = ref<string | null>(null); // For displaying login errors
    const router = useRouter();
    const userStore = useUserStore();

    const handleLogin = async () => {
      loading.value = true;
      loginError.value = null;
      try {
        const success = await userStore.login({ username: loginForm.username, password: loginForm.password });
        if (success) {
          message.success('登录成功！');
          router.push('/workspace'); // Redirect to workspace list or desired page
        } else {
          // This case might not be hit if store.login throws error on failure
          loginError.value = '登录失败，请检查您的凭据。';
        }
      } catch (error: any) {
        console.error('Login failed:', error);
        loginError.value = error.message || '登录时发生未知错误。';
      } finally {
        loading.value = false;
      }
    };

    return {
      loginForm,
      loading,
      loginError,
      handleLogin,
    };
  },
});
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.login-form-wrapper {
  width: 400px;
  padding: 32px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.login-header {
  text-align: center;
  margin-bottom: 24px;
}
.login-header h2 {
  font-size: 24px;
  color: #333;
}
</style>

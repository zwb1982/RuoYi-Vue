// src/router/index.ts
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../store/userStore'; // Import Pinia store
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import WorkspaceListView from '../views/WorkspaceListView.vue';
import TableCreateView from '../views/TableCreateView.vue';
import DataChatInputView from '../views/DataChatInputView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    // redirect: '/workspace', // Common to redirect to a useful page
    meta: { requiresAuth: true } // Make home require auth too, or redirect
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { guestOnly: true } // Prevent authenticated users from seeing login
  },
  {
    path: '/workspace',
    name: 'workspace-list',
    component: WorkspaceListView,
    meta: { requiresAuth: true }
  },
  {
    path: '/workspace/:workspaceId/table/create',
    name: 'table-create',
    component: TableCreateView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/workspace/:workspaceId/table/:tableId/chat-input',
    name: 'data-chat-input',
    component: DataChatInputView,
    props: true,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore(); // Get store instance
  const isAuthenticated = userStore.isAuthenticated;

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'login', query: { redirect: to.fullPath } }); // Redirect to login, save original destination
    } else {
      next(); // Proceed if authenticated
    }
  } else if (to.matched.some(record => record.meta.guestOnly)) {
    if (isAuthenticated) {
      next({ name: 'home' }); // Or 'workspace-list', redirect away from login if already auth
    } else {
      next();
    }
  } else {
    next(); // Always call next()
  }
});

export default router;

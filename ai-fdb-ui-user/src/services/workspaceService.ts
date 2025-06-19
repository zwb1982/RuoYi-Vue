// src/services/workspaceService.ts
import apiClient from './api';
import type { Workspace } from '../types'; // Assuming Workspace type is defined

// RuoYi's TableDataInfo typically wraps list in { rows: [], total: number, code: number, msg: string }
interface RuoYiTableData<T> {
  rows: T[];
  total: number;
  code: number;
  msg: string;
}

export const fetchWorkspaces = async (params?: any): Promise<RuoYiTableData<Workspace>> => {
  // The backend controller for AiWorkspaceController is at /workspace/workspace
  const response = await apiClient.get('/workspace/workspace/list', { params });
  return response.data;
};

export const createWorkspace = async (workspaceData: { workspaceName: string; description?: string }): Promise<any> => { // Return type can be more specific if backend guarantees
  // Backend is POST /workspace/workspace
  const response = await apiClient.post('/workspace/workspace', workspaceData);
  // RuoYi's toAjax typically returns { code, msg } or { code, msg, data } for single item creation
  // The controller method `add` returns `toAjax(aiWorkspaceService.insertAiWorkspace(aiWorkspace))`
  // `insertAiWorkspace` in service returns int. So backend likely returns { code: 200, msg: "操作成功"} or similar for success.
  if (response.data && response.data.code === 200) {
    // If the actual created workspace object is not returned, the caller will typically refetch the list.
    return response.data; // Return the whole response data {code, msg, ...}
  } else {
    throw new Error(response.data.msg || "Failed to create workspace");
  }
};

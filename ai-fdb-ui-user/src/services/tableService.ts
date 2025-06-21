// src/services/tableService.ts
import apiClient from './api';
import type { AxiosResponse } from 'axios';
import type { DataTable, TableField } from '../types';

interface AiDescriptionResponse { // Assuming AjaxResult wraps the string
    msg: string;
    code: number;
    data: string;
}

interface AiFieldSuggestionResponse { // Assuming AjaxResult wraps the list
    msg: string;
    code: number;
    data: TableField[];
}

// For creating table, backend AiDataTableController.add returns toAjax(service.insertAiDataTable(table))
// service.insertAiDataTable returns int (affected rows).
// The created table object with its ID is not directly returned by that specific endpoint.
// This is a common RuoYi pattern. Client often refetches or relies on input if ID is not essential immediately.
// HOWEVER, for creating fields, we NEED the tableId.
// The backend AiDataTable domain object has 'tableId' which is auto-generated.
// The Mybatis insert sets keyProperty="tableId". So, the AiDataTable object *passed to the service* gets its ID populated.
// The controller's `toAjax(tableService.insertAiDataTable(table))` where `table` is the input object,
// means `table.tableId` should be populated after the call IF the service method modified the input `table` object reference to include the ID.
// Or, the service method should return the AiDataTable object.
// Current backend: `public int insertAiDataTable(AiDataTable aiDataTable)` - it returns int.
// This needs adjustment in backend OR a different approach for frontend (e.g. create, then query by name to get ID - not ideal).

// **Assumption for now**: We will modify the backend AiDataTableController's add method
// or its service to return the created AiDataTable object with its ID.
// Let's define the service call assuming backend returns the created table in AjaxResult.data.
interface CreateTableResponse { // Assuming AjaxResult wraps the created DataTable
    msg: string;
    code: number;
    data: DataTable; // Expecting the created table with its new ID
}


export const generateTableDescriptionAI = async (tableIdPlaceholder: string | number, tableName: string): Promise<string> => {
  // Backend: POST /workspace/table/{tableId}/ai-generate-description with {tableName: "name"}
  const response: AxiosResponse<AiDescriptionResponse> = await apiClient.post(`/workspace/table/${tableIdPlaceholder}/ai-generate-description`, { tableName });
  if (response.data.code === 200) {
    return response.data.data;
  } else {
    throw new Error(response.data.msg || 'Failed to generate table description');
  }
};

export const suggestTableFieldsAI = async (tableIdPlaceholder: string | number, tableName: string, tableDescription?: string): Promise<TableField[]> => {
  // Backend: POST /workspace/table/{tableId}/field/ai-suggest with {tableName, tableDescription}
  const response: AxiosResponse<AiFieldSuggestionResponse> = await apiClient.post(`/workspace/table/${tableIdPlaceholder}/field/ai-suggest`, { tableName, tableDescription });
  if (response.data.code === 200) {
    return response.data.data;
  } else {
    throw new Error(response.data.msg || 'Failed to suggest table fields');
  }
};

export const createTableAPI = async (tableData: { workspaceId: string; tableName: string; tableDescription?: string }): Promise<DataTable> => {
  // Backend: POST /workspace/table/workspace/{workspaceId}
  // This endpoint needs to return the created AiDataTable object, particularly its new tableId.
  // For now, we assume the backend controller has been adjusted to return AjaxResult.success(createdTableObject).
  const response: AxiosResponse<CreateTableResponse> = await apiClient.post(`/workspace/table/workspace/${tableData.workspaceId}`, tableData);
  if (response.data.code === 200 && response.data.data && response.data.data.tableId) {
    return response.data.data;
  } else {
    // This is a critical point. If tableId is not returned, cannot add fields.
    throw new Error(response.data.msg || 'Failed to create table or tableId missing in response.');
  }
};

export const addFieldsToTableAPI = async (tableId: string, fields: Partial<TableField>[]): Promise<any> => {
    // Backend: POST /workspace/field/batch/{tableId}
    // AiTableFieldController.addBatch expects List<AiTableField> in request body
    // It returns toAjax(aiTableFieldService.insertAiTableFieldBatch(aiTableFields));
    // which is an int (number of fields added).
    const response: AxiosResponse<{code: number, msg: string, data?: any}> = await apiClient.post(`/workspace/field/batch/${tableId}`, fields);
    if (response.data.code === 200) {
        return response.data; // Or just true for success
    } else {
        throw new Error(response.data.msg || 'Failed to add fields to table');
    }
};

// RuoYi's TableDataInfo typically wraps list in { rows: [], total: number, code: number, msg: string }
interface RuoYiTableData<T> {
    rows: T[];
    total: number;
    code: number;
    msg: string;
}

export const fetchTableFieldsAPI = async (tableId: string): Promise<TableField[]> => {
  // Backend: GET /workspace/field/list?tableId={tableId}
  // AiTableFieldController.list takes AiTableField as parameter.
  // We send { tableId: tableId } as query params.
  const response: AxiosResponse<RuoYiTableData<TableField>> = await apiClient.get('/workspace/field/list', {
    params: { tableId }
  });
  if (response.data.code === 200) {
    return response.data.rows;
  } else {
    throw new Error(response.data.msg || 'Failed to fetch table fields');
  }
};

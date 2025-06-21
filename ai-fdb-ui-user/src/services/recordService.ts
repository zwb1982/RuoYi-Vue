// src/services/recordService.ts
import apiClient from './api';
import type { AxiosResponse } from 'axios';
import type { DataRecord } from '../types'; // Assuming DataRecord type is defined

interface AiExtractDataResponse { // Assuming AjaxResult wraps the extracted data map
    msg: string;
    code: number;
    data: { [key: string]: any };
}

// Backend AiDataRecordController.add returns toAjax(recordService.insertAiDataRecord(record))
// which is an int. Let's assume success if code is 200.
interface CreateRecordResponse {
    msg: string;
    code: number;
    // data might contain the created record ID if backend is modified, or affected rows count
    data?: any;
}

export const extractDataAI = async (text: string, tableId: string, fileName?: string): Promise<{ [key: string]: any }> => {
  // Backend: POST /workspace/record/ai-extract with {text, tableId}
  // File handling for extraction is not deeply implemented in backend yet, so fileName is informational for now.
  const payload: any = { text, tableId: Number(tableId) };
  if (fileName) payload.fileName = fileName; // Can pass if backend ever uses it

  const response: AxiosResponse<AiExtractDataResponse> = await apiClient.post('/workspace/record/ai-extract', payload);
  if (response.data.code === 200) {
    return response.data.data;
  } else {
    throw new Error(response.data.msg || 'Failed to extract data using AI');
  }
};

export const createRecordAPI = async (tableId: string, recordData: { [key: string]: any }): Promise<CreateRecordResponse> => {
  // Backend: POST /workspace/record/table/{tableId} with recordData (as AiDataRecord domain object)
  // The AiDataRecord domain needs `recordData` as a JSON string.
  const payload = {
      tableId: Number(tableId),
      recordData: JSON.stringify(recordData) // Ensure recordData is stringified JSON
  };
  const response: AxiosResponse<CreateRecordResponse> = await apiClient.post(`/workspace/record/table/${tableId}`, payload);
  if (response.data.code === 200) {
    return response.data;
  } else {
    throw new Error(response.data.msg || 'Failed to create data record');
  }
};

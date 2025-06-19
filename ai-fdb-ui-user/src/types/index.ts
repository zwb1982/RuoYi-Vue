// src/types/index.ts
export interface Workspace {
  workspaceId: string; // In design doc, example shows number, but TS type says string. Using string.
  workspaceName: string;
  description?: string;
  // Fields from design doc example not in TS type: createBy, updateBy, status, delFlag, createTime, updateTime
  // Fields from TS type not in example: tableCount, recordCount
  // For UI display, tableCount and recordCount are more relevant.
  // The backend domain AiWorkspace.java uses Long for workspaceId. For TS, string or number can be used.
  // Let's stick to string for IDs in frontend for now, can be parsed if needed.
  tableCount?: number;
  recordCount?: number;
  // Add other fields if they become necessary for display or interaction
}

export interface DataTable {
  tableId: string;
  workspaceId: string;
  tableName: string;
  tableDescription?: string;
  fieldCount?: number;
  recordCount?: number;
  // createBy: string; ... other audit fields
}

export interface TableField {
  fieldId: string;
  tableId: string;
  fieldName: string;
  fieldLabel: string;
  fieldType: string; // e.g., 'text', 'number', 'date'
  fieldDescription?: string;
  isRequired: boolean;
  defaultValue?: string;
  fieldOptions?: any; // JSON options
  extractionPrompt?: string;
  sortOrder: number;
  // createBy: string; ... other audit fields
}

export interface DataRecord {
  recordId: string;
  tableId: string;
  recordData: any; // JSON data
  // createBy: string; ... other audit fields
}

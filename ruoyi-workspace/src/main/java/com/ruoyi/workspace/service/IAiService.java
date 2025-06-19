package com.ruoyi.workspace.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.workspace.domain.AiTableField;

/**
 * AI Service interface for Qwen API integration
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface IAiService {

    /**
     * Generates a description for a given table name.
     *
     * @param tableName The name of the table.
     * @return A string containing the AI-generated description.
     */
    String generateTableDescription(String tableName);

    /**
     * Generates field suggestions for a given table name and description.
     *
     * @param tableName The name of the table.
     * @param tableDescription The description of the table.
     * @return A list of AiTableField objects suggested by the AI.
     */
    List<AiTableField> generateFieldSuggestions(String tableName, String tableDescription);

    /**
     * Extracts structured data from a given text based on the fields of a specified table.
     *
     * @param text The input text to extract data from.
     * @param tableId The ID of the table whose fields define the extraction schema.
     * @return A map where keys are field names and values are the extracted data.
     */
    Map<String, Object> extractDataFromText(String text, Long tableId);
}

package com.ruoyi.workspace.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workspace.domain.AiTableField;
import com.ruoyi.workspace.mapper.AiTableFieldMapper;
import com.ruoyi.workspace.service.IAiRecordFileRelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiTableFieldServiceImplTest {

    @Mock
    private AiTableFieldMapper aiTableFieldMapper;

    @Mock
    private IAiRecordFileRelationService aiRecordFileRelationService;

    @InjectMocks
    private AiTableFieldServiceImpl aiTableFieldService;

    private AiTableField tableField;
    private Date fixedDate;

    @BeforeEach
    void setUp() {
        tableField = new AiTableField();
        tableField.setFieldId(1L);
        tableField.setTableId(100L);
        tableField.setFieldName("testField");
        tableField.setFieldLabel("Test Field");
        tableField.setFieldType("text");

        fixedDate = new Date();
    }

    @Test
    void testInsertAiTableField_SetsDefaultsAndAuditFields() {
        AiTableField newField = new AiTableField();
        newField.setTableId(100L);
        newField.setFieldName("newField");
        newField.setFieldLabel("New Field");
        newField.setFieldType("text");
        // isRequired and sortOrder are null initially

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiTableFieldMapper.insertAiTableField(any(AiTableField.class))).thenReturn(1);

            aiTableFieldService.insertAiTableField(newField);

            assertEquals("testUser", newField.getCreateBy());
            assertEquals(fixedDate, newField.getCreateTime());
            assertEquals("0", newField.getDelFlag());
            assertEquals("0", newField.getStatus());
            assertEquals(0, newField.getSortOrder()); // Default value check
            assertEquals(0, newField.getIsRequired()); // Default value check
            verify(aiTableFieldMapper, times(1)).insertAiTableField(newField);
        }
    }

    @Test
    void testInsertAiTableFieldBatch_SetsDefaultsAndAuditFieldsForEach() {
        AiTableField field1 = new AiTableField();
        field1.setFieldName("field1");
        field1.setFieldLabel("Field 1");
        field1.setFieldType("text");
        // tableId will be set by service logic if not present, or should be pre-set
        // For this test, assume it's set on the objects if needed by mapper, or handled by service.
        // The current batch insert logic in service sets audit fields.

        AiTableField field2 = new AiTableField();
        field2.setFieldName("field2");
        field2.setFieldLabel("Field 2");
        field2.setFieldType("number");
        field2.setSortOrder(1); // Explicitly set
        field2.setIsRequired(1);

        List<AiTableField> fieldsToInsert = Arrays.asList(field1, field2);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("batchUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate); // Consistent date for all

            // Mock mapper to return 1 for each successful insert
            when(aiTableFieldMapper.insertAiTableField(any(AiTableField.class))).thenReturn(1);

            int result = aiTableFieldService.insertAiTableFieldBatch(fieldsToInsert);

            assertEquals(fieldsToInsert.size(), result);

            // Verify field1 (default values)
            assertEquals("batchUser", field1.getCreateBy());
            assertEquals(fixedDate, field1.getCreateTime());
            assertEquals("0", field1.getDelFlag());
            assertEquals("0", field1.getStatus());
            assertEquals(0, field1.getSortOrder());
            assertEquals(0, field1.getIsRequired());

            // Verify field2 (explicit values kept)
            assertEquals("batchUser", field2.getCreateBy());
            assertEquals(fixedDate, field2.getCreateTime());
            assertEquals("0", field2.getDelFlag());
            assertEquals("0", field2.getStatus());
            assertEquals(1, field2.getSortOrder());
            assertEquals(1, field2.getIsRequired());

            verify(aiTableFieldMapper, times(2)).insertAiTableField(any(AiTableField.class));
        }
    }


    @Test
    void testUpdateAiTableField_SetsAuditFieldsAndCallsMapper() {
         try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUserUpdater");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiTableFieldMapper.updateAiTableField(any(AiTableField.class))).thenReturn(1);

            int result = aiTableFieldService.updateAiTableField(tableField);

            assertEquals(1, result);
            assertEquals("testUserUpdater", tableField.getUpdateBy());
            assertEquals(fixedDate, tableField.getUpdateTime());
            verify(aiTableFieldMapper, times(1)).updateAiTableField(tableField);
        }
    }

    @Test
    void testDeleteAiTableFieldByFieldIds_CallsCascadingDeleteAndMapper() {
        Long[] fieldIds = {1L, 2L};
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds(fieldIds)).thenReturn(3); // Assume 3 file relations deleted
        when(aiTableFieldMapper.deleteAiTableFieldByFieldIds(fieldIds)).thenReturn(fieldIds.length);

        int result = aiTableFieldService.deleteAiTableFieldByFieldIds(fieldIds);

        assertEquals(fieldIds.length, result);
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByFieldIds(fieldIds);
        verify(aiTableFieldMapper, times(1)).deleteAiTableFieldByFieldIds(fieldIds);
    }

    @Test
    void testDeleteAiTableFieldByTableIds_FetchesFieldsAndCallsCascadingDelete() {
        Long[] tableIds = {100L};
        AiTableField fieldA = new AiTableField(); fieldA.setFieldId(1L); fieldA.setTableId(100L);
        AiTableField fieldB = new AiTableField(); fieldB.setFieldId(2L); fieldB.setTableId(100L);
        List<AiTableField> fieldsInTable = Arrays.asList(fieldA, fieldB);
        Long[] expectedFieldIdsToDelete = {1L, 2L};

        // Mock fetching fields for the table
        when(aiTableFieldMapper.selectAiTableFieldListByTableId(100L)).thenReturn(fieldsInTable);

        // Mock cascading delete of file relations for these specific field IDs
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds(expectedFieldIdsToDelete)).thenReturn(5);

        // Mock final deletion of fields by table IDs from mapper
        when(aiTableFieldMapper.deleteAiTableFieldByTableIds(tableIds)).thenReturn(fieldsInTable.size());

        int result = aiTableFieldService.deleteAiTableFieldByTableIds(tableIds);

        assertEquals(fieldsInTable.size(), result);
        verify(aiTableFieldMapper, times(1)).selectAiTableFieldListByTableId(100L);
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByFieldIds(expectedFieldIdsToDelete);
        verify(aiTableFieldMapper, times(1)).deleteAiTableFieldByTableIds(tableIds);
    }

    @Test
    void testDeleteAiTableFieldByTableIds_NoFieldsFound_DoesNothing() {
        Long[] tableIds = {999L}; // Assume this table has no fields
        when(aiTableFieldMapper.selectAiTableFieldListByTableId(999L)).thenReturn(new ArrayList<>());

        // Refined expectation for no fields found:
        when(aiTableFieldMapper.selectAiTableFieldListByTableId(999L)).thenReturn(new ArrayList<>());
        // deleteAiRecordFileRelationByFieldIds will be called with an empty array
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds(new Long[]{})).thenReturn(0);
        // deleteAiTableFieldByTableIds will be called with the original tableIds
        when(aiTableFieldMapper.deleteAiTableFieldByTableIds(tableIds)).thenReturn(0);

        int result = aiTableFieldService.deleteAiTableFieldByTableIds(tableIds);

        assertEquals(0, result);
        verify(aiTableFieldMapper, times(1)).selectAiTableFieldListByTableId(999L); // Called once for each tableId
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByFieldIds(new Long[]{}); // Called with empty array
        verify(aiTableFieldMapper, times(1)).deleteAiTableFieldByTableIds(tableIds); // Called with original tableIds
    }


    @Test
    void testSelectAiTableFieldByFieldId_CallsMapper() {
        Long fieldId = 1L;
        when(aiTableFieldMapper.selectAiTableFieldByFieldId(fieldId)).thenReturn(tableField);

        AiTableField result = aiTableFieldService.selectAiTableFieldByFieldId(fieldId);

        assertNotNull(result);
        assertEquals(tableField.getFieldName(), result.getFieldName());
        verify(aiTableFieldMapper, times(1)).selectAiTableFieldByFieldId(fieldId);
    }
}

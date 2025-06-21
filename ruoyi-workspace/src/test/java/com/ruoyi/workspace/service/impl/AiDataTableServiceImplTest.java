package com.ruoyi.workspace.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workspace.domain.AiDataTable;
import com.ruoyi.workspace.mapper.AiDataTableMapper;
import com.ruoyi.workspace.service.IAiDataRecordService;
import com.ruoyi.workspace.service.IAiTableFieldService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiDataTableServiceImplTest {

    @Mock
    private AiDataTableMapper aiDataTableMapper;

    @Mock
    private IAiTableFieldService aiTableFieldService;

    @Mock
    private IAiDataRecordService aiDataRecordService;

    @InjectMocks
    private AiDataTableServiceImpl aiDataTableService;

    private AiDataTable dataTable;
    private Date fixedDate;

    @BeforeEach
    void setUp() {
        dataTable = new AiDataTable();
        dataTable.setTableId(1L);
        dataTable.setWorkspaceId(100L);
        dataTable.setTableName("Test Table");

        fixedDate = new Date();
    }

    @Test
    void testInsertAiDataTable_SetsAuditFieldsAndCallsMapper() {
        AiDataTable newTable = new AiDataTable();
        newTable.setTableName("New Table");
        newTable.setWorkspaceId(100L);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiDataTableMapper.insertAiDataTable(any(AiDataTable.class))).thenReturn(1);

            int result = aiDataTableService.insertAiDataTable(newTable);

            assertEquals(1, result);
            assertEquals("testUser", newTable.getCreateBy());
            assertEquals(fixedDate, newTable.getCreateTime());
            assertEquals("0", newTable.getDelFlag());
            assertEquals("0", newTable.getStatus());
            verify(aiDataTableMapper, times(1)).insertAiDataTable(newTable);
        }
    }

    @Test
    void testUpdateAiDataTable_SetsAuditFieldsAndCallsMapper() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUserUpdater");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiDataTableMapper.updateAiDataTable(any(AiDataTable.class))).thenReturn(1);

            int result = aiDataTableService.updateAiDataTable(dataTable);

            assertEquals(1, result);
            assertEquals("testUserUpdater", dataTable.getUpdateBy());
            assertEquals(fixedDate, dataTable.getUpdateTime());
            verify(aiDataTableMapper, times(1)).updateAiDataTable(dataTable);
        }
    }

    @Test
    void testDeleteAiDataTableByTableIds_CallsCascadingDeletesAndMapper() {
        Long[] tableIds = {1L, 2L};
        when(aiTableFieldService.deleteAiTableFieldByTableIds(tableIds)).thenReturn(5); // Assume 5 fields deleted
        when(aiDataRecordService.deleteAiDataRecordByTableIds(tableIds)).thenReturn(10); // Assume 10 records deleted
        when(aiDataTableMapper.deleteAiDataTableByTableIds(tableIds)).thenReturn(tableIds.length);

        int result = aiDataTableService.deleteAiDataTableByTableIds(tableIds);

        assertEquals(tableIds.length, result);
        verify(aiTableFieldService, times(1)).deleteAiTableFieldByTableIds(tableIds);
        verify(aiDataRecordService, times(1)).deleteAiDataRecordByTableIds(tableIds);
        verify(aiDataTableMapper, times(1)).deleteAiDataTableByTableIds(tableIds);
    }

    @Test
    void testDeleteAiDataTableByTableId_CallsDeleteByTableIds() {
        Long tableId = 1L;
        // Use spy to verify internal call to deleteAiDataTableByTableIds if needed,
        // or just verify the mocks are called as deleteAiDataTableByTableId internally calls deleteAiDataTableByTableIds

        // Mock the dependent service calls that would be triggered by deleteAiDataTableByTableIds
        when(aiTableFieldService.deleteAiTableFieldByTableIds(any(Long[].class))).thenReturn(1);
        when(aiDataRecordService.deleteAiDataRecordByTableIds(any(Long[].class))).thenReturn(1);
        when(aiDataTableMapper.deleteAiDataTableByTableIds(any(Long[].class))).thenReturn(1);

        int result = aiDataTableService.deleteAiDataTableByTableId(tableId);

        assertEquals(1, result); // Assuming it successfully "deletes" one table
        // Verify that the services for cascading delete were called with an array containing the single ID
        verify(aiTableFieldService, times(1)).deleteAiTableFieldByTableIds(new Long[]{tableId});
        verify(aiDataRecordService, times(1)).deleteAiDataRecordByTableIds(new Long[]{tableId});
        verify(aiDataTableMapper, times(1)).deleteAiDataTableByTableIds(new Long[]{tableId});
    }


    @Test
    void testSelectAiDataTableByTableId_CallsMapper() {
        Long tableId = 1L;
        when(aiDataTableMapper.selectAiDataTableByTableId(tableId)).thenReturn(dataTable);

        AiDataTable result = aiDataTableService.selectAiDataTableByTableId(tableId);

        assertNotNull(result);
        assertEquals(dataTable.getTableName(), result.getTableName());
        verify(aiDataTableMapper, times(1)).selectAiDataTableByTableId(tableId);
    }

    @Test
    void testSelectAiDataTableList_CallsMapper() {
        AiDataTable filter = new AiDataTable();
        List<AiDataTable> expectedList = new ArrayList<>();
        expectedList.add(dataTable);
        when(aiDataTableMapper.selectAiDataTableList(filter)).thenReturn(expectedList);

        List<AiDataTable> result = aiDataTableService.selectAiDataTableList(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(aiDataTableMapper, times(1)).selectAiDataTableList(filter);
    }
}

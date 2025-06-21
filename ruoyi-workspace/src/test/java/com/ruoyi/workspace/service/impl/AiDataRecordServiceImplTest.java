package com.ruoyi.workspace.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workspace.domain.AiDataRecord;
import com.ruoyi.workspace.mapper.AiDataRecordMapper;
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
class AiDataRecordServiceImplTest {

    @Mock
    private AiDataRecordMapper aiDataRecordMapper;

    @Mock
    private IAiRecordFileRelationService aiRecordFileRelationService;

    @InjectMocks
    private AiDataRecordServiceImpl aiDataRecordService;

    private AiDataRecord dataRecord;
    private Date fixedDate;

    @BeforeEach
    void setUp() {
        dataRecord = new AiDataRecord();
        dataRecord.setRecordId(1L);
        dataRecord.setTableId(100L);
        dataRecord.setRecordData("{\"key\":\"value\"}");

        fixedDate = new Date();
    }

    @Test
    void testInsertAiDataRecord_SetsAuditFields() {
        AiDataRecord newRecord = new AiDataRecord();
        newRecord.setTableId(100L);
        newRecord.setRecordData("{}");

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiDataRecordMapper.insertAiDataRecord(any(AiDataRecord.class))).thenReturn(1);

            aiDataRecordService.insertAiDataRecord(newRecord);

            assertEquals("testUser", newRecord.getCreateBy());
            assertEquals(fixedDate, newRecord.getCreateTime());
            assertEquals("0", newRecord.getDelFlag());
            assertEquals("0", newRecord.getStatus());
            verify(aiDataRecordMapper, times(1)).insertAiDataRecord(newRecord);
        }
    }

    @Test
    void testUpdateAiDataRecord_SetsAuditFields() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUserUpdater");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiDataRecordMapper.updateAiDataRecord(any(AiDataRecord.class))).thenReturn(1);

            aiDataRecordService.updateAiDataRecord(dataRecord);

            assertEquals("testUserUpdater", dataRecord.getUpdateBy());
            assertEquals(fixedDate, dataRecord.getUpdateTime());
            verify(aiDataRecordMapper, times(1)).updateAiDataRecord(dataRecord);
        }
    }

    @Test
    void testDeleteAiDataRecordByRecordIds_CallsCascadingDeleteAndMapper() {
        Long[] recordIds = {1L, 2L};
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(recordIds)).thenReturn(5); // Assume 5 file relations deleted
        when(aiDataRecordMapper.deleteAiDataRecordByRecordIds(recordIds)).thenReturn(recordIds.length);

        int result = aiDataRecordService.deleteAiDataRecordByRecordIds(recordIds);

        assertEquals(recordIds.length, result);
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByRecordIds(recordIds);
        verify(aiDataRecordMapper, times(1)).deleteAiDataRecordByRecordIds(recordIds);
    }

    @Test
    void testDeleteAiDataRecordByTableIds_FetchesRecordsAndCallsCascadingDelete() {
        Long[] tableIds = {100L};
        AiDataRecord recordA = new AiDataRecord(); recordA.setRecordId(1L); recordA.setTableId(100L);
        AiDataRecord recordB = new AiDataRecord(); recordB.setRecordId(2L); recordB.setTableId(100L);
        List<AiDataRecord> recordsInTable = Arrays.asList(recordA, recordB);
        Long[] expectedRecordIdsToDelete = {1L, 2L};

        // Mock fetching records for the table
        // The service's deleteAiDataRecordByTableIds creates a filter object. We need to match this.
        AiDataRecord filter = new AiDataRecord();
        filter.setTableId(100L);
        when(aiDataRecordMapper.selectAiDataRecordList(any(AiDataRecord.class))).thenReturn(recordsInTable); // Make it flexible for filter object

        // Mock cascading delete of file relations for these specific record IDs
        // This is called by the deleteAiDataRecordByRecordIds method, which is called internally.
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(expectedRecordIdsToDelete)).thenReturn(5);

        // Mock the deletion of records by their own IDs (also part of the internal call to deleteAiDataRecordByRecordIds)
        when(aiDataRecordMapper.deleteAiDataRecordByRecordIds(expectedRecordIdsToDelete)).thenReturn(expectedRecordIdsToDelete.length);

        // Mock final deletion of records by table IDs from mapper
        when(aiDataRecordMapper.deleteAiDataRecordByTableIds(tableIds)).thenReturn(recordsInTable.size());

        int result = aiDataRecordService.deleteAiDataRecordByTableIds(tableIds);

        assertEquals(recordsInTable.size(), result); // This is the return from the final mapper call.
        verify(aiDataRecordMapper, times(1)).selectAiDataRecordList(argThat(arg -> arg.getTableId().equals(100L))); // Check filter
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByRecordIds(expectedRecordIdsToDelete);
        verify(aiDataRecordMapper, times(1)).deleteAiDataRecordByRecordIds(expectedRecordIdsToDelete);
        verify(aiDataRecordMapper, times(1)).deleteAiDataRecordByTableIds(tableIds);
    }

    @Test
    void testDeleteAiDataRecordByTableIds_NoRecordsFound() {
        Long[] tableIds = {999L};
        AiDataRecord filter = new AiDataRecord();
        filter.setTableId(999L);
        when(aiDataRecordMapper.selectAiDataRecordList(any(AiDataRecord.class))).thenReturn(new ArrayList<>());

        // If no records, deleteAiDataRecordByRecordIds won't be called with non-empty array.
        // The service calls it with an empty array.
        when(aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(new Long[]{})).thenReturn(0);
        when(aiDataRecordMapper.deleteAiDataRecordByRecordIds(new Long[]{})).thenReturn(0);

        // The final mapper call to delete by tableIds will still happen.
        when(aiDataRecordMapper.deleteAiDataRecordByTableIds(tableIds)).thenReturn(0);


        int result = aiDataRecordService.deleteAiDataRecordByTableIds(tableIds);

        assertEquals(0, result);
        verify(aiDataRecordMapper, times(1)).selectAiDataRecordList(argThat(arg -> arg.getTableId().equals(999L)));
        verify(aiRecordFileRelationService, times(1)).deleteAiRecordFileRelationByRecordIds(new Long[]{});
        verify(aiDataRecordMapper, times(1)).deleteAiDataRecordByRecordIds(new Long[]{});
        verify(aiDataRecordMapper, times(1)).deleteAiDataRecordByTableIds(tableIds);
    }

    @Test
    void testSelectAiDataRecordByRecordId_CallsMapper() {
        Long recordId = 1L;
        when(aiDataRecordMapper.selectAiDataRecordByRecordId(recordId)).thenReturn(dataRecord);

        AiDataRecord result = aiDataRecordService.selectAiDataRecordByRecordId(recordId);

        assertNotNull(result);
        assertEquals(dataRecord.getRecordData(), result.getRecordData());
        verify(aiDataRecordMapper, times(1)).selectAiDataRecordByRecordId(recordId);
    }
}

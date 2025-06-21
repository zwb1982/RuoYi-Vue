package com.ruoyi.workspace.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workspace.domain.AiRecordFileRelation;
import com.ruoyi.workspace.mapper.AiRecordFileRelationMapper;
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
class AiRecordFileRelationServiceImplTest {

    @Mock
    private AiRecordFileRelationMapper aiRecordFileRelationMapper;

    @InjectMocks
    private AiRecordFileRelationServiceImpl aiRecordFileRelationService;

    private AiRecordFileRelation fileRelation;
    private Date fixedDate;

    @BeforeEach
    void setUp() {
        fileRelation = new AiRecordFileRelation();
        fileRelation.setRelationId(1L);
        fileRelation.setRecordId(10L);
        fileRelation.setFieldId(20L);
        fileRelation.setFileName("test.jpg");
        fileRelation.setFilePath("/path/to/test.jpg");

        fixedDate = new Date();
    }

    @Test
    void testInsertAiRecordFileRelation_SetsAuditAndDefaultFields() {
        AiRecordFileRelation newRelation = new AiRecordFileRelation();
        // uploadTime is null initially

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("fileUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate); // For createTime and default uploadTime
            when(aiRecordFileRelationMapper.insertAiRecordFileRelation(any(AiRecordFileRelation.class))).thenReturn(1);

            aiRecordFileRelationService.insertAiRecordFileRelation(newRelation);

            assertEquals("fileUser", newRelation.getCreateBy());
            assertEquals(fixedDate, newRelation.getCreateTime());
            assertEquals(fixedDate, newRelation.getUploadTime()); // Default uploadTime
            assertEquals("0", newRelation.getDelFlag());
            verify(aiRecordFileRelationMapper, times(1)).insertAiRecordFileRelation(newRelation);
        }
    }

    @Test
    void testInsertAiRecordFileRelation_WithProvidedUploadTime_KeepsIt() {
        AiRecordFileRelation newRelation = new AiRecordFileRelation();
        Date specificUploadTime = new Date(fixedDate.getTime() - 100000); // An earlier time
        newRelation.setUploadTime(specificUploadTime);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("fileUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate); // For createTime
            when(aiRecordFileRelationMapper.insertAiRecordFileRelation(any(AiRecordFileRelation.class))).thenReturn(1);

            aiRecordFileRelationService.insertAiRecordFileRelation(newRelation);

            assertEquals(specificUploadTime, newRelation.getUploadTime()); // Provided uploadTime should be kept
            verify(aiRecordFileRelationMapper, times(1)).insertAiRecordFileRelation(newRelation);
        }
    }


    @Test
    void testUpdateAiRecordFileRelation_SetsAuditFields() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("fileUpdater");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);
            when(aiRecordFileRelationMapper.updateAiRecordFileRelation(any(AiRecordFileRelation.class))).thenReturn(1);

            aiRecordFileRelationService.updateAiRecordFileRelation(fileRelation);

            assertEquals("fileUpdater", fileRelation.getUpdateBy());
            assertEquals(fixedDate, fileRelation.getUpdateTime());
            verify(aiRecordFileRelationMapper, times(1)).updateAiRecordFileRelation(fileRelation);
        }
    }

    @Test
    void testDeleteAiRecordFileRelationByRelationIds_CallsMapper() {
        Long[] ids = {1L, 2L};
        when(aiRecordFileRelationMapper.deleteAiRecordFileRelationByRelationIds(ids)).thenReturn(ids.length);
        int result = aiRecordFileRelationService.deleteAiRecordFileRelationByRelationIds(ids);
        assertEquals(ids.length, result);
        verify(aiRecordFileRelationMapper, times(1)).deleteAiRecordFileRelationByRelationIds(ids);
    }

    @Test
    void testDeleteAiRecordFileRelationByRelationIds_EmptyOrNull_ReturnsZero() {
        int resultNull = aiRecordFileRelationService.deleteAiRecordFileRelationByRelationIds(null);
        assertEquals(0, resultNull);
        int resultEmpty = aiRecordFileRelationService.deleteAiRecordFileRelationByRelationIds(new Long[]{});
        assertEquals(0, resultEmpty);
        verify(aiRecordFileRelationMapper, never()).deleteAiRecordFileRelationByRelationIds(any());
    }


    @Test
    void testDeleteAiRecordFileRelationByRecordIds_CallsMapper() {
        Long[] recordIds = {10L, 11L};
        when(aiRecordFileRelationMapper.deleteAiRecordFileRelationByRecordIds(recordIds)).thenReturn(5); // e.g., 5 relations deleted
        int result = aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(recordIds);
        assertEquals(5, result);
        verify(aiRecordFileRelationMapper, times(1)).deleteAiRecordFileRelationByRecordIds(recordIds);
    }

    @Test
    void testDeleteAiRecordFileRelationByFieldIds_CallsMapper() {
        Long[] fieldIds = {20L, 21L};
        when(aiRecordFileRelationMapper.deleteAiRecordFileRelationByFieldIds(fieldIds)).thenReturn(3); // e.g., 3 relations deleted
        int result = aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds(fieldIds);
        assertEquals(3, result);
        verify(aiRecordFileRelationMapper, times(1)).deleteAiRecordFileRelationByFieldIds(fieldIds);
    }

    @Test
    void testSelectAiRecordFileRelationByRelationId_CallsMapper() {
        Long id = 1L;
        when(aiRecordFileRelationMapper.selectAiRecordFileRelationByRelationId(id)).thenReturn(fileRelation);
        AiRecordFileRelation result = aiRecordFileRelationService.selectAiRecordFileRelationByRelationId(id);
        assertNotNull(result);
        assertEquals(fileRelation.getFileName(), result.getFileName());
        verify(aiRecordFileRelationMapper, times(1)).selectAiRecordFileRelationByRelationId(id);
    }

    @Test
    void testSelectAiRecordFileRelationList_CallsMapper() {
        AiRecordFileRelation filter = new AiRecordFileRelation();
        List<AiRecordFileRelation> expectedList = new ArrayList<>();
        expectedList.add(fileRelation);
        when(aiRecordFileRelationMapper.selectAiRecordFileRelationList(filter)).thenReturn(expectedList);

        List<AiRecordFileRelation> result = aiRecordFileRelationService.selectAiRecordFileRelationList(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(aiRecordFileRelationMapper, times(1)).selectAiRecordFileRelationList(filter);
    }
}

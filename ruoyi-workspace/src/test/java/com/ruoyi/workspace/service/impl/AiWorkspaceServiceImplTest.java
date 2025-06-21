package com.ruoyi.workspace.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.workspace.domain.AiWorkspace;
import com.ruoyi.workspace.mapper.AiWorkspaceMapper;
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
class AiWorkspaceServiceImplTest {

    @Mock
    private AiWorkspaceMapper aiWorkspaceMapper;

    @InjectMocks
    private AiWorkspaceServiceImpl aiWorkspaceService;

    private AiWorkspace workspace;
    private Date fixedDate;

    @BeforeEach
    void setUp() {
        workspace = new AiWorkspace();
        workspace.setWorkspaceId(1L);
        workspace.setWorkspaceName("Test Workspace");

        fixedDate = new Date(); // Fixed date for consistent time checks if needed, though DateUtils.getNowDate() is tricky
    }

    @Test
    void testInsertAiWorkspace_SetsAuditFieldsAndCallsMapper() {
        // Arrange
        AiWorkspace newWorkspace = new AiWorkspace();
        newWorkspace.setWorkspaceName("New Workspace");
        // We need to mock static SecurityUtils.getUsername() and DateUtils.getNowDate()
        // For DateUtils.getNowDate(), it's harder. Let's assume it works or verify its call.
        // For SecurityUtils.getUsername(), we use MockedStatic

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUser");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate); // Consistent date

            when(aiWorkspaceMapper.insertAiWorkspace(any(AiWorkspace.class))).thenReturn(1);

            // Act
            int result = aiWorkspaceService.insertAiWorkspace(newWorkspace);

            // Assert
            assertEquals(1, result);
            assertEquals("testUser", newWorkspace.getCreateBy());
            assertEquals(fixedDate, newWorkspace.getCreateTime());
            assertEquals("0", newWorkspace.getDelFlag());
            assertEquals("0", newWorkspace.getStatus());
            verify(aiWorkspaceMapper, times(1)).insertAiWorkspace(newWorkspace);
        }
    }

    @Test
    void testSelectAiWorkspaceList_NonAdmin_FiltersByCreateBy() {
        // Arrange
        AiWorkspace filter = new AiWorkspace();
        List<AiWorkspace> expectedList = new ArrayList<>();
        expectedList.add(workspace);

        // Mock SecurityUtils static methods
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getUserId).thenReturn(101L); // Non-admin user ID
            mockedSecurityUtils.when(() -> SecurityUtils.isAdmin(101L)).thenReturn(false);
            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUser");

            when(aiWorkspaceMapper.selectAiWorkspaceList(any(AiWorkspace.class))).thenReturn(expectedList);

            // Act
            List<AiWorkspace> actualList = aiWorkspaceService.selectAiWorkspaceList(filter);

            // Assert
            assertNotNull(actualList);
            assertEquals(1, actualList.size());
            assertEquals("testUser", filter.getCreateBy()); // Verify createBy was set on the filter object
            verify(aiWorkspaceMapper, times(1)).selectAiWorkspaceList(filter);
        }
    }

    @Test
    void testSelectAiWorkspaceList_Admin_DoesNotFilterByCreateBy() {
        // Arrange
        AiWorkspace filter = new AiWorkspace();
        List<AiWorkspace> expectedList = new ArrayList<>();
        expectedList.add(workspace);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getUserId).thenReturn(1L); // Admin user ID
            mockedSecurityUtils.when(() -> SecurityUtils.isAdmin(1L)).thenReturn(true);
            // getUsername would not be called to set filter.createBy for admin

            when(aiWorkspaceMapper.selectAiWorkspaceList(any(AiWorkspace.class))).thenReturn(expectedList);

            // Act
            List<AiWorkspace> actualList = aiWorkspaceService.selectAiWorkspaceList(filter);

            // Assert
            assertNotNull(actualList);
            assertEquals(1, actualList.size());
            assertNull(filter.getCreateBy()); // Verify createBy was NOT set on the filter object for admin
            verify(aiWorkspaceMapper, times(1)).selectAiWorkspaceList(filter);
        }
    }

    @Test
    void testUpdateAiWorkspace_SetsAuditFieldsAndCallsMapper() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
             MockedStatic<DateUtils> mockedDateUtils = Mockito.mockStatic(DateUtils.class)) {

            mockedSecurityUtils.when(SecurityUtils::getUsername).thenReturn("testUserUpdater");
            mockedDateUtils.when(DateUtils::getNowDate).thenReturn(fixedDate);

            when(aiWorkspaceMapper.updateAiWorkspace(any(AiWorkspace.class))).thenReturn(1);

            // Act
            int result = aiWorkspaceService.updateAiWorkspace(workspace);

            // Assert
            assertEquals(1, result);
            assertEquals("testUserUpdater", workspace.getUpdateBy());
            assertEquals(fixedDate, workspace.getUpdateTime());
            verify(aiWorkspaceMapper, times(1)).updateAiWorkspace(workspace);
        }
    }

    @Test
    void testDeleteAiWorkspaceByWorkspaceIds_CallsMapper() {
        // Arrange
        Long[] idsToDelete = {1L, 2L};
        when(aiWorkspaceMapper.deleteAiWorkspaceByWorkspaceIds(idsToDelete)).thenReturn(idsToDelete.length);

        // Act
        int result = aiWorkspaceService.deleteAiWorkspaceByWorkspaceIds(idsToDelete);

        // Assert
        assertEquals(idsToDelete.length, result);
        verify(aiWorkspaceMapper, times(1)).deleteAiWorkspaceByWorkspaceIds(idsToDelete);
    }

    @Test
    void testSelectAiWorkspaceByWorkspaceId_CallsMapper() {
        // Arrange
        Long idToSelect = 1L;
        when(aiWorkspaceMapper.selectAiWorkspaceByWorkspaceId(idToSelect)).thenReturn(workspace);

        // Act
        AiWorkspace result = aiWorkspaceService.selectAiWorkspaceByWorkspaceId(idToSelect);

        // Assert
        assertNotNull(result);
        assertEquals(workspace.getWorkspaceName(), result.getWorkspaceName());
        verify(aiWorkspaceMapper, times(1)).selectAiWorkspaceByWorkspaceId(idToSelect);
    }
}

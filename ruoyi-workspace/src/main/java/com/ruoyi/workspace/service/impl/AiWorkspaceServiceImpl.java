package com.ruoyi.workspace.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils; // Required for getUsername
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.workspace.mapper.AiWorkspaceMapper;
import com.ruoyi.workspace.domain.AiWorkspace;
import com.ruoyi.workspace.service.IAiWorkspaceService;

/**
 * AI工作空间Service业务层处理
 *
 * @author Jules
 * @date 2025-06-19
 */
@Service
public class AiWorkspaceServiceImpl implements IAiWorkspaceService
{
    @Autowired
    private AiWorkspaceMapper aiWorkspaceMapper;

    /**
     * 查询AI工作空间
     *
     * @param workspaceId AI工作空间主键
     * @return AI工作空间
     */
    @Override
    public AiWorkspace selectAiWorkspaceByWorkspaceId(Long workspaceId)
    {
        return aiWorkspaceMapper.selectAiWorkspaceByWorkspaceId(workspaceId);
    }

    /**
     * 查询AI工作空间列表
     *
     * @param aiWorkspace AI工作空间
     * @return AI工作空间
     */
    @Override
    public List<AiWorkspace> selectAiWorkspaceList(AiWorkspace aiWorkspace)
    {
        // 数据权限过滤：用户只能查看自己创建的工作空间
        // This assumes non-admin users. Admins might have different logic
        // or this could be enhanced with data scope annotations if applicable.
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
             aiWorkspace.setCreateBy(SecurityUtils.getUsername());
        }
        return aiWorkspaceMapper.selectAiWorkspaceList(aiWorkspace);
    }

    /**
     * 新增AI工作空间
     *
     * @param aiWorkspace AI工作空间
     * @return 结果
     */
    @Override
    public int insertAiWorkspace(AiWorkspace aiWorkspace)
    {
        aiWorkspace.setCreateBy(SecurityUtils.getUsername());
        aiWorkspace.setCreateTime(DateUtils.getNowDate());
        aiWorkspace.setDelFlag("0"); // Ensure del_flag is '0' on creation
        aiWorkspace.setStatus("0"); // Default status to '0' (normal)
        return aiWorkspaceMapper.insertAiWorkspace(aiWorkspace);
    }

    /**
     * 修改AI工作空间
     *
     * @param aiWorkspace AI工作空间
     * @return 结果
     */
    @Override
    public int updateAiWorkspace(AiWorkspace aiWorkspace)
    {
        aiWorkspace.setUpdateBy(SecurityUtils.getUsername());
        aiWorkspace.setUpdateTime(DateUtils.getNowDate());
        return aiWorkspaceMapper.updateAiWorkspace(aiWorkspace);
    }

    /**
     * 批量删除AI工作空间
     *
     * @param workspaceIds 需要删除的AI工作空间主键
     * @return 结果
     */
    @Override
    public int deleteAiWorkspaceByWorkspaceIds(Long[] workspaceIds)
    {
        // Here, we could also implement cascading deletes for related entities if necessary,
        // e.g., delete associated AiDataTables, AiTableFields etc.
        // For now, only deleting workspaces as per typical RuoYi structure.
        // The database schema has ON DELETE CASCADE for ai_data_table,
        // so related tables will be deleted by the DB if this is a hard delete.
        // Since it's a logical delete, related entities need to be handled if required.
        return aiWorkspaceMapper.deleteAiWorkspaceByWorkspaceIds(workspaceIds);
    }

    /**
     * 删除AI工作空间信息
     *
     * @param workspaceId AI工作空间主键
     * @return 结果
     */
    @Override
    public int deleteAiWorkspaceByWorkspaceId(Long workspaceId)
    {
        return aiWorkspaceMapper.deleteAiWorkspaceByWorkspaceId(workspaceId);
    }
}

package com.ruoyi.workspace.mapper;

import java.util.List;
import com.ruoyi.workspace.domain.AiWorkspace;

/**
 * AI工作空间Mapper接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface AiWorkspaceMapper
{
    /**
     * 查询AI工作空间
     *
     * @param workspaceId AI工作空间主键
     * @return AI工作空间
     */
    public AiWorkspace selectAiWorkspaceByWorkspaceId(Long workspaceId);

    /**
     * 查询AI工作空间列表
     *
     * @param aiWorkspace AI工作空间
     * @return AI工作空间集合
     */
    public List<AiWorkspace> selectAiWorkspaceList(AiWorkspace aiWorkspace);

    /**
     * 新增AI工作空间
     *
     * @param aiWorkspace AI工作空间
     * @return 结果
     */
    public int insertAiWorkspace(AiWorkspace aiWorkspace);

    /**
     * 修改AI工作空间
     *
     * @param aiWorkspace AI工作空间
     * @return 结果
     */
    public int updateAiWorkspace(AiWorkspace aiWorkspace);

    /**
     * 删除AI工作空间
     *
     * @param workspaceId AI工作空间主键
     * @return 结果
     */
    public int deleteAiWorkspaceByWorkspaceId(Long workspaceId);

    /**
     * 批量删除AI工作空间
     *
     * @param workspaceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiWorkspaceByWorkspaceIds(Long[] workspaceIds);
}

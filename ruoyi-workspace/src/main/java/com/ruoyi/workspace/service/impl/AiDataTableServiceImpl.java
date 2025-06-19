package com.ruoyi.workspace.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for cascading deletes
import com.ruoyi.workspace.mapper.AiDataTableMapper;
import com.ruoyi.workspace.domain.AiDataTable;
import com.ruoyi.workspace.service.IAiDataTableService;
import com.ruoyi.workspace.service.IAiTableFieldService; // To delete related fields
import com.ruoyi.workspace.service.IAiDataRecordService; // To delete related records

/**
 * AI数据表定义Service业务层处理
 *
 * @author Jules
 * @date 2025-06-19
 */
@Service
public class AiDataTableServiceImpl implements IAiDataTableService
{
    @Autowired
    private AiDataTableMapper aiDataTableMapper;

    @Autowired
    private IAiTableFieldService aiTableFieldService; // Injected for cascading delete

    @Autowired
    private IAiDataRecordService aiDataRecordService; // Injected for cascading delete


    /**
     * 查询AI数据表定义
     *
     * @param tableId AI数据表定义主键
     * @return AI数据表定义
     */
    @Override
    public AiDataTable selectAiDataTableByTableId(Long tableId)
    {
        return aiDataTableMapper.selectAiDataTableByTableId(tableId);
    }

    /**
     * 查询AI数据表定义列表
     *
     * @param aiDataTable AI数据表定义
     * @return AI数据表定义
     */
    @Override
    public List<AiDataTable> selectAiDataTableList(AiDataTable aiDataTable)
    {
        // Add any specific data permission logic if tables are user-specific beyond workspace
        // For now, assumes filtering by workspaceId in query is sufficient if provided
        return aiDataTableMapper.selectAiDataTableList(aiDataTable);
    }

    /**
     * 新增AI数据表定义
     *
     * @param aiDataTable AI数据表定义
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiDataTable(AiDataTable aiDataTable)
    {
        aiDataTable.setCreateBy(SecurityUtils.getUsername());
        aiDataTable.setCreateTime(DateUtils.getNowDate());
        aiDataTable.setDelFlag("0");
        aiDataTable.setStatus("0"); // Default status
        return aiDataTableMapper.insertAiDataTable(aiDataTable);
    }

    /**
     * 修改AI数据表定义
     *
     * @param aiDataTable AI数据表定义
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAiDataTable(AiDataTable aiDataTable)
    {
        aiDataTable.setUpdateBy(SecurityUtils.getUsername());
        aiDataTable.setUpdateTime(DateUtils.getNowDate());
        return aiDataTableMapper.updateAiDataTable(aiDataTable);
    }

    /**
     * 批量删除AI数据表定义
     *
     * @param tableIds 需要删除的AI数据表定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiDataTableByTableIds(Long[] tableIds)
    {
        // Handle cascading logical deletes
        // 1. Delete related fields
        aiTableFieldService.deleteAiTableFieldByTableIds(tableIds);
        // 2. Delete related records (which in turn should handle related files)
        aiDataRecordService.deleteAiDataRecordByTableIds(tableIds);
        // 3. Delete the tables themselves
        return aiDataTableMapper.deleteAiDataTableByTableIds(tableIds);
    }

    /**
     * 删除AI数据表定义信息
     *
     * @param tableId AI数据表定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiDataTableByTableId(Long tableId)
    {
         return deleteAiDataTableByTableIds(new Long[]{tableId});
    }
}

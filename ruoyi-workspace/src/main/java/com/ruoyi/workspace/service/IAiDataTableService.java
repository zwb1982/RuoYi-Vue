package com.ruoyi.workspace.service;

import java.util.List;
import com.ruoyi.workspace.domain.AiDataTable;

/**
 * AI数据表定义Service接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface IAiDataTableService
{
    /**
     * 查询AI数据表定义
     *
     * @param tableId AI数据表定义主键
     * @return AI数据表定义
     */
    public AiDataTable selectAiDataTableByTableId(Long tableId);

    /**
     * 查询AI数据表定义列表
     *
     * @param aiDataTable AI数据表定义
     * @return AI数据表定义集合
     */
    public List<AiDataTable> selectAiDataTableList(AiDataTable aiDataTable);

    /**
     * 新增AI数据表定义
     *
     * @param aiDataTable AI数据表定义
     * @return 结果
     */
    public int insertAiDataTable(AiDataTable aiDataTable);

    /**
     * 修改AI数据表定义
     *
     * @param aiDataTable AI数据表定义
     * @return 结果
     */
    public int updateAiDataTable(AiDataTable aiDataTable);

    /**
     * 批量删除AI数据表定义
     *
     * @param tableIds 需要删除的AI数据表定义主键集合
     * @return 结果
     */
    public int deleteAiDataTableByTableIds(Long[] tableIds);

    /**
     * 删除AI数据表定义信息
     *
     * @param tableId AI数据表定义主键
     * @return 结果
     */
    public int deleteAiDataTableByTableId(Long tableId);
}

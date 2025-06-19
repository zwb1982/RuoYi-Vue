package com.ruoyi.workspace.service;

import java.util.List;
import com.ruoyi.workspace.domain.AiTableField;

/**
 * AI数据表字段定义Service接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface IAiTableFieldService
{
    /**
     * 查询AI数据表字段定义
     *
     * @param fieldId AI数据表字段定义主键
     * @return AI数据表字段定义
     */
    public AiTableField selectAiTableFieldByFieldId(Long fieldId);

    /**
     * 查询AI数据表字段定义列表
     *
     * @param aiTableField AI数据表字段定义
     * @return AI数据表字段定义集合
     */
    public List<AiTableField> selectAiTableFieldList(AiTableField aiTableField);

    /**
     * 根据表ID查询AI数据表字段定义列表
     *
     * @param tableId 表ID
     * @return AI数据表字段定义集合
     */
    public List<AiTableField> selectAiTableFieldListByTableId(Long tableId);

    /**
     * 新增AI数据表字段定义
     *
     * @param aiTableField AI数据表字段定义
     * @return 结果
     */
    public int insertAiTableField(AiTableField aiTableField);

    /**
     * 修改AI数据表字段定义
     *
     * @param aiTableField AI数据表字段定义
     * @return 结果
     */
    public int updateAiTableField(AiTableField aiTableField);

    /**
     * 批量新增AI数据表字段定义
     *
     * @param aiTableFields AI数据表字段定义列表
     * @return 结果
     */
    public int insertAiTableFieldBatch(List<AiTableField> aiTableFields);


    /**
     * 批量删除AI数据表字段定义
     *
     * @param fieldIds 需要删除的AI数据表字段定义主键集合
     * @return 结果
     */
    public int deleteAiTableFieldByFieldIds(Long[] fieldIds);

    /**
     * 删除AI数据表字段定义信息
     *
     * @param fieldId AI数据表字段定义主键
     * @return 结果
     */
    public int deleteAiTableFieldByFieldId(Long fieldId);

    /**
     * 根据TableID批量删除AI数据表字段定义
     * (Used by AiDataTableService for cascading delete)
     *
     * @param tableIds 表ID集合
     * @return 结果
     */
    public int deleteAiTableFieldByTableIds(Long[] tableIds);
}

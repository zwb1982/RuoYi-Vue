package com.ruoyi.workspace.mapper;

import java.util.List;
import com.ruoyi.workspace.domain.AiTableField;

/**
 * AI数据表字段定义Mapper接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface AiTableFieldMapper
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
     * 删除AI数据表字段定义
     *
     * @param fieldId AI数据表字段定义主键
     * @return 结果
     */
    public int deleteAiTableFieldByFieldId(Long fieldId);

    /**
     * 批量删除AI数据表字段定义
     *
     * @param fieldIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiTableFieldByFieldIds(Long[] fieldIds);

    /**
     * 根据TableId批量删除AI数据表字段定义
     *
     * @param tableIds 需要删除的TableID集合
     * @return 结果
     */
    public int deleteAiTableFieldByTableIds(Long[] tableIds);
}

package com.ruoyi.workspace.service;

import java.util.List;
import com.ruoyi.workspace.domain.AiRecordFileRelation;

/**
 * AI记录文件关联Service接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface IAiRecordFileRelationService
{
    /**
     * 查询AI记录文件关联
     *
     * @param relationId AI记录文件关联主键
     * @return AI记录文件关联
     */
    public AiRecordFileRelation selectAiRecordFileRelationByRelationId(Long relationId);

    /**
     * 查询AI记录文件关联列表
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return AI记录文件关联集合
     */
    public List<AiRecordFileRelation> selectAiRecordFileRelationList(AiRecordFileRelation aiRecordFileRelation);

    /**
     * 新增AI记录文件关联
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return 结果
     */
    public int insertAiRecordFileRelation(AiRecordFileRelation aiRecordFileRelation);

    /**
     * 修改AI记录文件关联
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return 结果
     */
    public int updateAiRecordFileRelation(AiRecordFileRelation aiRecordFileRelation);

    /**
     * 批量删除AI记录文件关联
     *
     * @param relationIds 需要删除的AI记录文件关联主键集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRelationIds(Long[] relationIds);

    /**
     * 删除AI记录文件关联信息
     *
     * @param relationId AI记录文件关联主键
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRelationId(Long relationId);

    /**
     * 根据RecordID批量删除AI记录文件关联
     * (Used by AiDataRecordService for cascading delete)
     *
     * @param recordIds 记录ID集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRecordIds(Long[] recordIds);

    /**
     * 根据FieldID批量删除AI记录文件关联
     * (Used by AiTableFieldService for cascading delete)
     *
     * @param fieldIds 字段ID集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByFieldIds(Long[] fieldIds);
}

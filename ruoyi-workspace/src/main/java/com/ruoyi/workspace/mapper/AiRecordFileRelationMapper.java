package com.ruoyi.workspace.mapper;

import java.util.List;
import com.ruoyi.workspace.domain.AiRecordFileRelation;

/**
 * AI记录文件关联Mapper接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface AiRecordFileRelationMapper
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
     * 删除AI记录文件关联
     *
     * @param relationId AI记录文件关联主键
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRelationId(Long relationId);

    /**
     * 批量删除AI记录文件关联
     *
     * @param relationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRelationIds(Long[] relationIds);

    /**
     * 根据RecordIds批量删除文件关联记录
     * @param recordIds 需要删除的RecordID集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByRecordIds(Long[] recordIds);

    /**
     * 根据FieldIds批量删除文件关联记录
     * @param fieldIds 需要删除的FieldID集合
     * @return 结果
     */
    public int deleteAiRecordFileRelationByFieldIds(Long[] fieldIds);

    /**
     * 根据TableIds批量删除文件关联记录 (indirectly, by finding records of those tables)
     * This requires a more complex query, possibly joining with ai_data_record.
     * For now, we'll assume direct table_id is not in ai_record_file_relation.
     * Deletion by tableIds would typically be handled in service layer by first fetching relevant recordIds or fieldIds.
     */
    // public int deleteAiRecordFileRelationByTableIds(Long[] tableIds); // Placeholder if direct relation needed

}

package com.ruoyi.workspace.service;

import java.util.List;
import com.ruoyi.workspace.domain.AiDataRecord;

/**
 * AI数据记录Service接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface IAiDataRecordService
{
    /**
     * 查询AI数据记录
     *
     * @param recordId AI数据记录主键
     * @return AI数据记录
     */
    public AiDataRecord selectAiDataRecordByRecordId(Long recordId);

    /**
     * 查询AI数据记录列表
     *
     * @param aiDataRecord AI数据记录
     * @return AI数据记录集合
     */
    public List<AiDataRecord> selectAiDataRecordList(AiDataRecord aiDataRecord);

    /**
     * 新增AI数据记录
     *
     * @param aiDataRecord AI数据记录
     * @return 结果
     */
    public int insertAiDataRecord(AiDataRecord aiDataRecord);

    /**
     * 修改AI数据记录
     *
     * @param aiDataRecord AI数据记录
     * @return 结果
     */
    public int updateAiDataRecord(AiDataRecord aiDataRecord);

    /**
     * 批量删除AI数据记录
     *
     * @param recordIds 需要删除的AI数据记录主键集合
     * @return 结果
     */
    public int deleteAiDataRecordByRecordIds(Long[] recordIds);

    /**
     * 删除AI数据记录信息
     *
     * @param recordId AI数据记录主键
     * @return 结果
     */
    public int deleteAiDataRecordByRecordId(Long recordId);

    /**
     * 根据TableID批量删除AI数据记录
     * (Used by AiDataTableService for cascading delete)
     *
     * @param tableIds 表ID集合
     * @return 结果
     */
    public int deleteAiDataRecordByTableIds(Long[] tableIds);
}

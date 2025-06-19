package com.ruoyi.workspace.mapper;

import java.util.List;
import com.ruoyi.workspace.domain.AiDataRecord;

/**
 * AI数据记录Mapper接口
 *
 * @author Jules
 * @date 2025-06-19
 */
public interface AiDataRecordMapper
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
     * 删除AI数据记录
     *
     * @param recordId AI数据记录主键
     * @return 结果
     */
    public int deleteAiDataRecordByRecordId(Long recordId);

    /**
     * 批量删除AI数据记录
     *
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiDataRecordByRecordIds(Long[] recordIds);

    /**
     * 根据TableId批量删除AI数据记录
     *
     * @param tableIds 需要删除的TableID集合
     * @return 结果
     */
    public int deleteAiDataRecordByTableIds(Long[] tableIds);
}

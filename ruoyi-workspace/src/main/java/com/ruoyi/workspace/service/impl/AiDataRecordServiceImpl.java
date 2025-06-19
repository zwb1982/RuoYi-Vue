package com.ruoyi.workspace.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.workspace.mapper.AiDataRecordMapper;
import com.ruoyi.workspace.domain.AiDataRecord;
import com.ruoyi.workspace.service.IAiDataRecordService;
import com.ruoyi.workspace.service.IAiRecordFileRelationService; // To delete related file relations

/**
 * AI数据记录Service业务层处理
 *
 * @author Jules
 * @date 2025-06-19
 */
@Service
public class AiDataRecordServiceImpl implements IAiDataRecordService
{
    @Autowired
    private AiDataRecordMapper aiDataRecordMapper;

    @Autowired
    private IAiRecordFileRelationService aiRecordFileRelationService; // Injected for cascading delete

    /**
     * 查询AI数据记录
     *
     * @param recordId AI数据记录主键
     * @return AI数据记录
     */
    @Override
    public AiDataRecord selectAiDataRecordByRecordId(Long recordId)
    {
        return aiDataRecordMapper.selectAiDataRecordByRecordId(recordId);
    }

    /**
     * 查询AI数据记录列表
     *
     * @param aiDataRecord AI数据记录
     * @return AI数据记录
     */
    @Override
    public List<AiDataRecord> selectAiDataRecordList(AiDataRecord aiDataRecord)
    {
        return aiDataRecordMapper.selectAiDataRecordList(aiDataRecord);
    }

    /**
     * 新增AI数据记录
     *
     * @param aiDataRecord AI数据记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiDataRecord(AiDataRecord aiDataRecord)
    {
        aiDataRecord.setCreateBy(SecurityUtils.getUsername());
        aiDataRecord.setCreateTime(DateUtils.getNowDate());
        aiDataRecord.setDelFlag("0");
        aiDataRecord.setStatus("0"); // Default status
        return aiDataRecordMapper.insertAiDataRecord(aiDataRecord);
    }

    /**
     * 修改AI数据记录
     *
     * @param aiDataRecord AI数据记录
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAiDataRecord(AiDataRecord aiDataRecord)
    {
        aiDataRecord.setUpdateBy(SecurityUtils.getUsername());
        aiDataRecord.setUpdateTime(DateUtils.getNowDate());
        return aiDataRecordMapper.updateAiDataRecord(aiDataRecord);
    }

    /**
     * 批量删除AI数据记录
     *
     * @param recordIds 需要删除的AI数据记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiDataRecordByRecordIds(Long[] recordIds)
    {
        if (recordIds == null || recordIds.length == 0) {
            return 0;
        }
        // Handle cascading logical deletes for file relations linked to these records
        aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(recordIds);

        return aiDataRecordMapper.deleteAiDataRecordByRecordIds(recordIds);
    }

    /**
     * 删除AI数据记录信息
     *
     * @param recordId AI数据记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiDataRecordByRecordId(Long recordId)
    {
        return deleteAiDataRecordByRecordIds(new Long[]{recordId});
    }

    /**
     * 根据TableID批量删除AI数据记录
     *
     * @param tableIds 表ID集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiDataRecordByTableIds(Long[] tableIds)
    {
        if (tableIds == null || tableIds.length == 0) {
            return 0;
        }
        // First, get all recordIds for these tableIds to ensure cascading delete of file relations
        List<AiDataRecord> recordsToDelete = new java.util.ArrayList<>();
        for (Long tableId : tableIds) {
            AiDataRecord filter = new AiDataRecord();
            filter.setTableId(tableId);
            recordsToDelete.addAll(aiDataRecordMapper.selectAiDataRecordList(filter));
        }

        if (recordsToDelete.isEmpty()) {
            return 0;
        }

        Long[] recordIdsToDelete = recordsToDelete.stream().map(AiDataRecord::getRecordId).toArray(Long[]::new);

        // Call the existing method to delete these records and their related file relations
        // This will take care of aiRecordFileRelationService.deleteAiRecordFileRelationByRecordIds(recordIdsToDelete);
        deleteAiDataRecordByRecordIds(recordIdsToDelete);

        // The mapper method aiDataRecordMapper.deleteAiDataRecordByTableIds(tableIds) could also be called directly
        // if the cascading logic for file relations was handled differently (e.g. DB trigger or if recordIds were passed to it).
        // For consistency with field deletion, we ensure file relations are handled via their specific service method call first.
        // In this case, deleteAiDataRecordByRecordIds already handles it.
        return aiDataRecordMapper.deleteAiDataRecordByTableIds(tableIds); // This performs the actual deletion of records by tableIds.
    }
}

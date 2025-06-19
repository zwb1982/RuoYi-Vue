package com.ruoyi.workspace.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.workspace.mapper.AiTableFieldMapper;
import com.ruoyi.workspace.domain.AiTableField;
import com.ruoyi.workspace.service.IAiTableFieldService;
import com.ruoyi.workspace.service.IAiRecordFileRelationService; // To delete related file relations

/**
 * AI数据表字段定义Service业务层处理
 *
 * @author Jules
 * @date 2025-06-19
 */
@Service
public class AiTableFieldServiceImpl implements IAiTableFieldService
{
    @Autowired
    private AiTableFieldMapper aiTableFieldMapper;

    @Autowired
    private IAiRecordFileRelationService aiRecordFileRelationService; // Injected for cascading delete

    /**
     * 查询AI数据表字段定义
     *
     * @param fieldId AI数据表字段定义主键
     * @return AI数据表字段定义
     */
    @Override
    public AiTableField selectAiTableFieldByFieldId(Long fieldId)
    {
        return aiTableFieldMapper.selectAiTableFieldByFieldId(fieldId);
    }

    /**
     * 查询AI数据表字段定义列表
     *
     * @param aiTableField AI数据表字段定义
     * @return AI数据表字段定义
     */
    @Override
    public List<AiTableField> selectAiTableFieldList(AiTableField aiTableField)
    {
        return aiTableFieldMapper.selectAiTableFieldList(aiTableField);
    }

    /**
     * 根据表ID查询AI数据表字段定义列表
     *
     * @param tableId 表ID
     * @return AI数据表字段定义集合
     */
    @Override
    public List<AiTableField> selectAiTableFieldListByTableId(Long tableId)
    {
        return aiTableFieldMapper.selectAiTableFieldListByTableId(tableId);
    }

    /**
     * 新增AI数据表字段定义
     *
     * @param aiTableField AI数据表字段定义
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiTableField(AiTableField aiTableField)
    {
        aiTableField.setCreateBy(SecurityUtils.getUsername());
        aiTableField.setCreateTime(DateUtils.getNowDate());
        aiTableField.setDelFlag("0");
        aiTableField.setStatus("0"); // Default status
        // Ensure sortOrder has a default if not provided
        if (aiTableField.getSortOrder() == null) {
            aiTableField.setSortOrder(0);
        }
        if (aiTableField.getIsRequired() == null) {
            aiTableField.setIsRequired(0); // Default to not required
        }
        return aiTableFieldMapper.insertAiTableField(aiTableField);
    }

    /**
     * 批量新增AI数据表字段定义
     *
     * @param aiTableFields AI数据表字段定义列表
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiTableFieldBatch(List<AiTableField> aiTableFields)
    {
        int successCount = 0;
        if (aiTableFields == null || aiTableFields.isEmpty()) {
            return 0;
        }
        for (AiTableField aiTableField : aiTableFields) {
            // It's important that each field has a tableId set before calling this
            aiTableField.setCreateBy(SecurityUtils.getUsername());
            aiTableField.setCreateTime(DateUtils.getNowDate());
            aiTableField.setDelFlag("0");
            aiTableField.setStatus("0");
            if (aiTableField.getSortOrder() == null) {
                aiTableField.setSortOrder(0);
            }
            if (aiTableField.getIsRequired() == null) {
                aiTableField.setIsRequired(0);
            }
            successCount += aiTableFieldMapper.insertAiTableField(aiTableField);
        }
        return successCount;
        // Consider a true batch insert if performance is critical for very large lists
        // For now, iterating and calling individual inserts is simpler and often sufficient.
    }

    /**
     * 修改AI数据表字段定义
     *
     * @param aiTableField AI数据表字段定义
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAiTableField(AiTableField aiTableField)
    {
        aiTableField.setUpdateBy(SecurityUtils.getUsername());
        aiTableField.setUpdateTime(DateUtils.getNowDate());
        return aiTableFieldMapper.updateAiTableField(aiTableField);
    }

    /**
     * 批量删除AI数据表字段定义
     *
     * @param fieldIds 需要删除的AI数据表字段定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiTableFieldByFieldIds(Long[] fieldIds)
    {
        if (fieldIds == null || fieldIds.length == 0) {
            return 0;
        }
        // Handle cascading logical deletes for file relations linked to these fields
        aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds(fieldIds);

        return aiTableFieldMapper.deleteAiTableFieldByFieldIds(fieldIds);
    }

    /**
     * 删除AI数据表字段定义信息
     *
     * @param fieldId AI数据表字段定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiTableFieldByFieldId(Long fieldId)
    {
        return deleteAiTableFieldByFieldIds(new Long[]{fieldId});
    }

    /**
     * 根据TableID批量删除AI数据表字段定义
     *
     * @param tableIds 表ID集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiTableFieldByTableIds(Long[] tableIds)
    {
        if (tableIds == null || tableIds.length == 0) {
            return 0;
        }
        // First, get all fieldIds for these tableIds
        List<AiTableField> fieldsToDelete = new java.util.ArrayList<>();
        for (Long tableId : tableIds) {
            fieldsToDelete.addAll(aiTableFieldMapper.selectAiTableFieldListByTableId(tableId));
        }

        if (fieldsToDelete.isEmpty()) {
            return 0;
        }

        Long[] fieldIdsToDelete = fieldsToDelete.stream().map(AiTableField::getFieldId).toArray(Long[]::new);

        // Call the existing method to delete these fields and their related file relations
        deleteAiTableFieldByFieldIds(fieldIdsToDelete);

        // The mapper method directly deletes fields by tableIds, which is more efficient
        // However, we needed fieldIds for cascading delete of file relations.
        // If aiRecordFileRelationService.deleteAiRecordFileRelationByFieldIds is efficient,
        // then using aiTableFieldMapper.deleteAiTableFieldByTableIds(tableIds) directly after it would be fine.
        // For consistency in cascading logic, we get field IDs first.
        // Alternative: the mapper could do this join, or a DB trigger.
        // Let's stick to this explicit service-layer cascade for now.
        // The mapper `deleteAiTableFieldByTableIds` is already available and will be called by this.
        // The above logic ensures file relations are deleted *before* fields are marked deleted.
        return aiTableFieldMapper.deleteAiTableFieldByTableIds(tableIds);
    }
}

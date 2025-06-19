package com.ruoyi.workspace.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.workspace.mapper.AiRecordFileRelationMapper;
import com.ruoyi.workspace.domain.AiRecordFileRelation;
import com.ruoyi.workspace.service.IAiRecordFileRelationService;

/**
 * AI记录文件关联Service业务层处理
 *
 * @author Jules
 * @date 2025-06-19
 */
@Service
public class AiRecordFileRelationServiceImpl implements IAiRecordFileRelationService
{
    @Autowired
    private AiRecordFileRelationMapper aiRecordFileRelationMapper;

    /**
     * 查询AI记录文件关联
     *
     * @param relationId AI记录文件关联主键
     * @return AI记录文件关联
     */
    @Override
    public AiRecordFileRelation selectAiRecordFileRelationByRelationId(Long relationId)
    {
        return aiRecordFileRelationMapper.selectAiRecordFileRelationByRelationId(relationId);
    }

    /**
     * 查询AI记录文件关联列表
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return AI记录文件关联
     */
    @Override
    public List<AiRecordFileRelation> selectAiRecordFileRelationList(AiRecordFileRelation aiRecordFileRelation)
    {
        return aiRecordFileRelationMapper.selectAiRecordFileRelationList(aiRecordFileRelation);
    }

    /**
     * 新增AI记录文件关联
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiRecordFileRelation(AiRecordFileRelation aiRecordFileRelation)
    {
        aiRecordFileRelation.setCreateBy(SecurityUtils.getUsername());
        aiRecordFileRelation.setCreateTime(DateUtils.getNowDate());
        // uploadTime is set by DB default or should be set explicitly before calling this
        if (aiRecordFileRelation.getUploadTime() == null) {
            aiRecordFileRelation.setUploadTime(DateUtils.getNowDate());
        }
        aiRecordFileRelation.setDelFlag("0");
        return aiRecordFileRelationMapper.insertAiRecordFileRelation(aiRecordFileRelation);
    }

    /**
     * 修改AI记录文件关联
     *
     * @param aiRecordFileRelation AI记录文件关联
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAiRecordFileRelation(AiRecordFileRelation aiRecordFileRelation)
    {
        aiRecordFileRelation.setUpdateBy(SecurityUtils.getUsername());
        aiRecordFileRelation.setUpdateTime(DateUtils.getNowDate());
        return aiRecordFileRelationMapper.updateAiRecordFileRelation(aiRecordFileRelation);
    }

    /**
     * 批量删除AI记录文件关联
     *
     * @param relationIds 需要删除的AI记录文件关联主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiRecordFileRelationByRelationIds(Long[] relationIds)
    {
        if (relationIds == null || relationIds.length == 0) {
            return 0;
        }
        return aiRecordFileRelationMapper.deleteAiRecordFileRelationByRelationIds(relationIds);
    }

    /**
     * 删除AI记录文件关联信息
     *
     * @param relationId AI记录文件关联主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiRecordFileRelationByRelationId(Long relationId)
    {
        return aiRecordFileRelationMapper.deleteAiRecordFileRelationByRelationId(relationId);
    }

    /**
     * 根据RecordID批量删除AI记录文件关联
     *
     * @param recordIds 记录ID集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiRecordFileRelationByRecordIds(Long[] recordIds)
    {
        if (recordIds == null || recordIds.length == 0) {
            return 0;
        }
        return aiRecordFileRelationMapper.deleteAiRecordFileRelationByRecordIds(recordIds);
    }

    /**
     * 根据FieldID批量删除AI记录文件关联
     *
     * @param fieldIds 字段ID集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAiRecordFileRelationByFieldIds(Long[] fieldIds)
    {
        if (fieldIds == null || fieldIds.length == 0) {
            return 0;
        }
        return aiRecordFileRelationMapper.deleteAiRecordFileRelationByFieldIds(fieldIds);
    }
}

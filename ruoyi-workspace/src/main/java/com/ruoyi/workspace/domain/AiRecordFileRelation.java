package com.ruoyi.workspace.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI记录文件关联对象 ai_record_file_relation
 *
 * @author Jules
 * @date 2025-06-19
 */
public class AiRecordFileRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long relationId;

    /** 数据记录ID */
    @Excel(name = "数据记录ID")
    private Long recordId;

    /** 字段ID */
    @Excel(name = "字段ID")
    private Long fieldId;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;


    public void setRelationId(Long relationId)
    {
        this.relationId = relationId;
    }

    public Long getRelationId()
    {
        return relationId;
    }
    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getRecordId()
    {
        return recordId;
    }
    public void setFieldId(Long fieldId)
    {
        this.fieldId = fieldId;
    }

    public Long getFieldId()
    {
        return fieldId;
    }
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }
    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize()
    {
        return fileSize;
    }
    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFileType()
    {
        return fileType;
    }
    public void setUploadTime(Date uploadTime)
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime()
    {
        return uploadTime;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("relationId", getRelationId())
            .append("recordId", getRecordId())
            .append("fieldId", getFieldId())
            .append("filePath", getFilePath())
            .append("fileName", getFileName())
            .append("fileSize", getFileSize())
            .append("fileType", getFileType())
            .append("uploadTime", getUploadTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

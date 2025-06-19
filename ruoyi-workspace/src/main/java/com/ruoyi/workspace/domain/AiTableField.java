package com.ruoyi.workspace.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI数据表字段定义对象 ai_table_field
 *
 * @author Jules
 * @date 2025-06-19
 */
public class AiTableField extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字段ID */
    private Long fieldId;

    /** 所属数据表ID */
    @Excel(name = "所属数据表ID")
    private Long tableId;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段显示标签 */
    @Excel(name = "字段显示标签")
    private String fieldLabel;

    /** 字段类型 */
    @Excel(name = "字段类型")
    private String fieldType;

    /** 字段描述 */
    @Excel(name = "字段描述")
    private String fieldDescription;

    /** 是否必填（0否 1是） */
    @Excel(name = "是否必填", readConverterExp = "0=否,1=是")
    private Integer isRequired;

    /** 默认值 */
    @Excel(name = "默认值")
    private String defaultValue;

    /** 字段选项配置 (JSON) */
    @Excel(name = "字段选项配置")
    private String fieldOptions; // Store JSON as String

    /** AI抽取提示词 */
    @Excel(name = "AI抽取提示词")
    private String extractionPrompt;

    /** 排序顺序 */
    @Excel(name = "排序顺序")
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setFieldId(Long fieldId)
    {
        this.fieldId = fieldId;
    }

    public Long getFieldId()
    {
        return fieldId;
    }
    public void setTableId(Long tableId)
    {
        this.tableId = tableId;
    }

    public Long getTableId()
    {
        return tableId;
    }
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }
    public void setFieldLabel(String fieldLabel)
    {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldLabel()
    {
        return fieldLabel;
    }
    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }

    public String getFieldType()
    {
        return fieldType;
    }
    public void setFieldDescription(String fieldDescription)
    {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldDescription()
    {
        return fieldDescription;
    }
    public void setIsRequired(Integer isRequired)
    {
        this.isRequired = isRequired;
    }

    public Integer getIsRequired()
    {
        return isRequired;
    }
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }
    public void setFieldOptions(String fieldOptions)
    {
        this.fieldOptions = fieldOptions;
    }

    public String getFieldOptions()
    {
        return fieldOptions;
    }
    public void setExtractionPrompt(String extractionPrompt)
    {
        this.extractionPrompt = extractionPrompt;
    }

    public String getExtractionPrompt()
    {
        return extractionPrompt;
    }
    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
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
            .append("fieldId", getFieldId())
            .append("tableId", getTableId())
            .append("fieldName", getFieldName())
            .append("fieldLabel", getFieldLabel())
            .append("fieldType", getFieldType())
            .append("fieldDescription", getFieldDescription())
            .append("isRequired", getIsRequired())
            .append("defaultValue", getDefaultValue())
            .append("fieldOptions", getFieldOptions())
            .append("extractionPrompt", getExtractionPrompt())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

package com.ruoyi.workspace.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI数据表定义对象 ai_data_table
 *
 * @author Jules
 * @date 2025-06-19
 */
public class AiDataTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据表ID */
    private Long tableId;

    /** 所属工作空间ID */
    @Excel(name = "所属工作空间ID")
    private Long workspaceId;

    /** 数据表名称 */
    @Excel(name = "数据表名称")
    private String tableName;

    /** 数据表描述 */
    @Excel(name = "数据表描述")
    private String tableDescription;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setTableId(Long tableId)
    {
        this.tableId = tableId;
    }

    public Long getTableId()
    {
        return tableId;
    }
    public void setWorkspaceId(Long workspaceId)
    {
        this.workspaceId = workspaceId;
    }

    public Long getWorkspaceId()
    {
        return workspaceId;
    }
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return tableName;
    }
    public void setTableDescription(String tableDescription)
    {
        this.tableDescription = tableDescription;
    }

    public String getTableDescription()
    {
        return tableDescription;
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
            .append("tableId", getTableId())
            .append("workspaceId", getWorkspaceId())
            .append("tableName", getTableName())
            .append("tableDescription", getTableDescription())
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

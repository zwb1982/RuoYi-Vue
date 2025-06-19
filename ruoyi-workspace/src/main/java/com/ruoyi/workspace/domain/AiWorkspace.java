package com.ruoyi.workspace.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI工作空间对象 ai_workspace
 *
 * @author Jules
 * @date 2025-06-19
 */
public class AiWorkspace extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工作空间ID */
    private Long workspaceId;

    /** 工作空间名称 */
    @Excel(name = "工作空间名称")
    private String workspaceName;

    /** 工作空间描述 */
    @Excel(name = "工作空间描述")
    private String description;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setWorkspaceId(Long workspaceId)
    {
        this.workspaceId = workspaceId;
    }

    public Long getWorkspaceId()
    {
        return workspaceId;
    }
    public void setWorkspaceName(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

    public String getWorkspaceName()
    {
        return workspaceName;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
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
            .append("workspaceId", getWorkspaceId())
            .append("workspaceName", getWorkspaceName())
            .append("description", getDescription())
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

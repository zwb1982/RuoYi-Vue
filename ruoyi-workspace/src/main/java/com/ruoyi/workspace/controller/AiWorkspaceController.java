package com.ruoyi.workspace.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workspace.domain.AiWorkspace;
import com.ruoyi.workspace.service.IAiWorkspaceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI工作空间Controller
 *
 * @author Jules
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/workspace/workspace") // Matched module path from typical RuoYi generation
public class AiWorkspaceController extends BaseController
{
    @Autowired
    private IAiWorkspaceService aiWorkspaceService;

    /**
     * 查询AI工作空间列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiWorkspace aiWorkspace)
    {
        startPage();
        List<AiWorkspace> list = aiWorkspaceService.selectAiWorkspaceList(aiWorkspace);
        return getDataTable(list);
    }

    /**
     * 导出AI工作空间列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:export')")
    @Log(title = "AI工作空间", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiWorkspace aiWorkspace)
    {
        List<AiWorkspace> list = aiWorkspaceService.selectAiWorkspaceList(aiWorkspace);
        ExcelUtil<AiWorkspace> util = new ExcelUtil<AiWorkspace>(AiWorkspace.class);
        util.exportExcel(response, list, "AI工作空间数据");
    }

    /**
     * 获取AI工作空间详细信息
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:query')")
    @GetMapping(value = "/{workspaceId}")
    public AjaxResult getInfo(@PathVariable("workspaceId") Long workspaceId)
    {
        return success(aiWorkspaceService.selectAiWorkspaceByWorkspaceId(workspaceId));
    }

    /**
     * 新增AI工作空间
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:add')")
    @Log(title = "AI工作空间", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiWorkspace aiWorkspace)
    {
        return toAjax(aiWorkspaceService.insertAiWorkspace(aiWorkspace));
    }

    /**
     * 修改AI工作空间
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:edit')")
    @Log(title = "AI工作空间", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiWorkspace aiWorkspace)
    {
        return toAjax(aiWorkspaceService.updateAiWorkspace(aiWorkspace));
    }

    /**
     * 删除AI工作空间
     */
    @PreAuthorize("@ss.hasPermi('workspace:workspace:remove')")
    @Log(title = "AI工作空间", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workspaceIds}")
    public AjaxResult remove(@PathVariable Long[] workspaceIds)
    {
        return toAjax(aiWorkspaceService.deleteAiWorkspaceByWorkspaceIds(workspaceIds));
    }
}

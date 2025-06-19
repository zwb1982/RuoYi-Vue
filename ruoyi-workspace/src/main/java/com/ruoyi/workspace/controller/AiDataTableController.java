package com.ruoyi.workspace.controller;

import java.util.List;
import java.util.Map; // For request body of AI methods
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
import com.ruoyi.workspace.domain.AiDataTable;
import com.ruoyi.workspace.domain.AiTableField; // For AI suggestion response
import com.ruoyi.workspace.service.IAiDataTableService;
import com.ruoyi.workspace.service.IAiService; // To be created
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI数据表定义Controller
 *
 * @author Jules
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/workspace/table") // Path from design document for this entity
public class AiDataTableController extends BaseController
{
    @Autowired
    private IAiDataTableService aiDataTableService;

    @Autowired
    private IAiService aiService; // AI service for suggestions and descriptions

    /**
     * 查询AI数据表定义列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:list')") // Assuming permission convention
    @GetMapping("/list")
    public TableDataInfo list(AiDataTable aiDataTable)
    {
        startPage();
        List<AiDataTable> list = aiDataTableService.selectAiDataTableList(aiDataTable);
        return getDataTable(list);
    }

    /**
     * 导出AI数据表定义列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:export')")
    @Log(title = "AI数据表定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiDataTable aiDataTable)
    {
        List<AiDataTable> list = aiDataTableService.selectAiDataTableList(aiDataTable);
        ExcelUtil<AiDataTable> util = new ExcelUtil<AiDataTable>(AiDataTable.class);
        util.exportExcel(response, list, "AI数据表定义数据");
    }

    /**
     * 获取AI数据表定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:query')")
    @GetMapping(value = "/{tableId}")
    public AjaxResult getInfo(@PathVariable("tableId") Long tableId)
    {
        return success(aiDataTableService.selectAiDataTableByTableId(tableId));
    }

    /**
     * 新增AI数据表定义 for a given workspace
     * Path: /workspace/table/workspace/{workspaceId}
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:add')")
    @Log(title = "AI数据表定义", businessType = BusinessType.INSERT)
    @PostMapping("/workspace/{workspaceId}")
    public AjaxResult add(@PathVariable Long workspaceId, @RequestBody AiDataTable aiDataTable)
    {
        aiDataTable.setWorkspaceId(workspaceId);
        return toAjax(aiDataTableService.insertAiDataTable(aiDataTable));
    }

    /**
     * 修改AI数据表定义
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:edit')")
    @Log(title = "AI数据表定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiDataTable aiDataTable)
    {
        // Ensure workspaceId is not changed if it's part of the body, or handle appropriately
        return toAjax(aiDataTableService.updateAiDataTable(aiDataTable));
    }

    /**
     * 删除AI数据表定义
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:remove')")
    @Log(title = "AI数据表定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{tableIds}")
    public AjaxResult remove(@PathVariable Long[] tableIds)
    {
        return toAjax(aiDataTableService.deleteAiDataTableByTableIds(tableIds));
    }

    /**
     * AI生成表描述
     * Path: /workspace/table/{tableId}/ai-generate-description
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:ai')") // Custom permission for AI features
    @PostMapping("/{tableId}/ai-generate-description")
    public AjaxResult generateDescription(@PathVariable Long tableId, @RequestBody Map<String, String> params) {
        // tableId might be used to fetch existing context if needed, or could be 0 for a new table
        String tableName = params.get("tableName");
        if (tableName == null || tableName.trim().isEmpty()) {
            return AjaxResult.error("Table name is required to generate description.");
        }
        String description = aiService.generateTableDescription(tableName);
        return success(description);
    }

    /**
     * AI建议字段
     * Path: /workspace/table/{tableId}/field/ai-suggest
     */
    @PreAuthorize("@ss.hasPermi('workspace:table:ai')")
    @PostMapping("/{tableId}/field/ai-suggest")
    public AjaxResult suggestFields(@PathVariable Long tableId, @RequestBody Map<String, String> params) {
        // tableId might be used to fetch existing context if needed
        String tableName = params.get("tableName");
        String tableDescription = params.get("tableDescription");
        if (tableName == null || tableName.trim().isEmpty()) {
            return AjaxResult.error("Table name is required to suggest fields.");
        }
        List<AiTableField> suggestions = aiService.generateFieldSuggestions(tableName, tableDescription);
        return success(suggestions);
    }
}

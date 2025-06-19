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
import com.ruoyi.workspace.domain.AiTableField;
import com.ruoyi.workspace.service.IAiTableFieldService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI数据表字段定义Controller
 *
 * @author Jules
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/workspace/field") // Tentative path for field management
public class AiTableFieldController extends BaseController
{
    @Autowired
    private IAiTableFieldService aiTableFieldService;

    /**
     * 查询AI数据表字段定义列表 (typically for a specific table)
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiTableField aiTableField) // Pass tableId in AiTableField object for filtering
    {
        startPage(); // Handles pagination
        List<AiTableField> list = aiTableFieldService.selectAiTableFieldList(aiTableField);
        return getDataTable(list);
    }

    /**
     * 导出AI数据表字段定义列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:export')")
    @Log(title = "AI数据表字段定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiTableField aiTableField)
    {
        List<AiTableField> list = aiTableFieldService.selectAiTableFieldList(aiTableField);
        ExcelUtil<AiTableField> util = new ExcelUtil<AiTableField>(AiTableField.class);
        util.exportExcel(response, list, "AI数据表字段定义数据");
    }

    /**
     * 获取AI数据表字段定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:query')")
    @GetMapping(value = "/{fieldId}")
    public AjaxResult getInfo(@PathVariable("fieldId") Long fieldId)
    {
        return success(aiTableFieldService.selectAiTableFieldByFieldId(fieldId));
    }

    /**
     * 新增AI数据表字段定义
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:add')")
    @Log(title = "AI数据表字段定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiTableField aiTableField) // tableId must be set in the request body
    {
        if (aiTableField.getTableId() == null) {
            return AjaxResult.error("TableId is required to add a field.");
        }
        return toAjax(aiTableFieldService.insertAiTableField(aiTableField));
    }

    /**
     * 批量新增AI数据表字段定义 for a specific table
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:add')")
    @Log(title = "AI数据表字段定义", businessType = BusinessType.INSERT)
    @PostMapping("/batch/{tableId}")
    public AjaxResult addBatch(@PathVariable Long tableId, @RequestBody List<AiTableField> aiTableFields)
    {
        if (aiTableFields == null || aiTableFields.isEmpty()) {
            return AjaxResult.error("Field list cannot be empty.");
        }
        for (AiTableField field : aiTableFields) {
            field.setTableId(tableId); // Ensure all fields are associated with the given tableId
        }
        return toAjax(aiTableFieldService.insertAiTableFieldBatch(aiTableFields));
    }


    /**
     * 修改AI数据表字段定义
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:edit')")
    @Log(title = "AI数据表字段定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiTableField aiTableField)
    {
         if (aiTableField.getFieldId() == null) {
            return AjaxResult.error("FieldId is required to update a field.");
        }
        // tableId should not be changed during an update of a field, or handled carefully
        return toAjax(aiTableFieldService.updateAiTableField(aiTableField));
    }

    /**
     * 删除AI数据表字段定义
     */
    @PreAuthorize("@ss.hasPermi('workspace:field:remove')")
    @Log(title = "AI数据表字段定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fieldIds}")
    public AjaxResult remove(@PathVariable Long[] fieldIds)
    {
        return toAjax(aiTableFieldService.deleteAiTableFieldByFieldIds(fieldIds));
    }
}

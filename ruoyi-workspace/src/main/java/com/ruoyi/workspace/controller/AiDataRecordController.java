package com.ruoyi.workspace.controller;

import java.util.List;
import java.util.Map; // For AI extract params
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
import com.ruoyi.workspace.domain.AiDataRecord;
import com.ruoyi.workspace.service.IAiDataRecordService;
import com.ruoyi.workspace.service.IAiService; // To be created/used
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI数据记录Controller
 *
 * @author Jules
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/workspace/record") // Path from design document
public class AiDataRecordController extends BaseController
{
    @Autowired
    private IAiDataRecordService aiDataRecordService;

    @Autowired
    private IAiService aiService; // For AI data extraction

    /**
     * 查询AI数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:list')") // Assuming permission convention
    @GetMapping("/list")
    public TableDataInfo list(AiDataRecord aiDataRecord) // Pass tableId in AiDataRecord for filtering
    {
        startPage();
        List<AiDataRecord> list = aiDataRecordService.selectAiDataRecordList(aiDataRecord);
        return getDataTable(list);
    }

    /**
     * 导出AI数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:export')")
    @Log(title = "AI数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiDataRecord aiDataRecord)
    {
        List<AiDataRecord> list = aiDataRecordService.selectAiDataRecordList(aiDataRecord);
        ExcelUtil<AiDataRecord> util = new ExcelUtil<AiDataRecord>(AiDataRecord.class);
        util.exportExcel(response, list, "AI数据记录数据");
    }

    /**
     * 获取AI数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(aiDataRecordService.selectAiDataRecordByRecordId(recordId));
    }

    /**
     * 新增AI数据记录 for a given table
     * Path: /workspace/record/table/{tableId}
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:add')")
    @Log(title = "AI数据记录", businessType = BusinessType.INSERT)
    @PostMapping("/table/{tableId}")
    public AjaxResult add(@PathVariable Long tableId, @RequestBody AiDataRecord aiDataRecord)
    {
        if (aiDataRecord.getTableId() != null && !aiDataRecord.getTableId().equals(tableId)) {
            return AjaxResult.error("Table ID in path does not match Table ID in body.");
        }
        aiDataRecord.setTableId(tableId);
        return toAjax(aiDataRecordService.insertAiDataRecord(aiDataRecord));
    }

    /**
     * 修改AI数据记录
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:edit')")
    @Log(title = "AI数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiDataRecord aiDataRecord)
    {
        if (aiDataRecord.getRecordId() == null) {
            return AjaxResult.error("RecordId is required to update a record.");
        }
        // tableId should generally not be changed, or requires specific handling
        return toAjax(aiDataRecordService.updateAiDataRecord(aiDataRecord));
    }

    /**
     * 删除AI数据记录
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:remove')")
    @Log(title = "AI数据记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(aiDataRecordService.deleteAiDataRecordByRecordIds(recordIds));
    }

    /**
     * AI从文本中提取数据
     * Path: /workspace/record/ai-extract
     */
    @PreAuthorize("@ss.hasPermi('workspace:record:ai')") // Custom permission for AI features
    @PostMapping("/ai-extract")
    public AjaxResult extractData(@RequestBody Map<String, Object> params) {
        String text = (String) params.get("text");
        Object tableIdObj = params.get("tableId");

        if (text == null || text.trim().isEmpty()) {
            return AjaxResult.error("Text for extraction is required.");
        }
        if (tableIdObj == null) {
            return AjaxResult.error("TableId is required for context.");
        }

        Long tableId;
        try {
            tableId = Long.valueOf(tableIdObj.toString());
        } catch (NumberFormatException e) {
            return AjaxResult.error("Invalid TableId format.");
        }

        Map<String, Object> extractedData = aiService.extractDataFromText(text, tableId);
        return success(extractedData);
    }
}

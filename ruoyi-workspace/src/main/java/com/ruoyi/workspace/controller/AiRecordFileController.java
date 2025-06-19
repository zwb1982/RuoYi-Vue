package com.ruoyi.workspace.controller;

import java.util.List;
import java.util.Date; // For uploadTime
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.config.RuoYiConfig; // For upload path
import com.ruoyi.workspace.domain.AiRecordFileRelation;
import com.ruoyi.workspace.service.IAiRecordFileRelationService;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * AI记录文件Controller
 *
 * @author Jules
 * @date 2025-06-19
 */
@RestController
@RequestMapping("/workspace/file")
public class AiRecordFileController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiRecordFileController.class);

    @Autowired
    private IAiRecordFileRelationService aiRecordFileRelationService;

    /**
     * Common file upload endpoint
     * This endpoint will save the file and create an AiRecordFileRelation.
     * The client needs to provide recordId and fieldId to associate the file.
     */
    @PreAuthorize("@ss.hasPermi('workspace:file:upload')") // Generic permission for file upload
    @Log(title = "AI记录文件上传", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("recordId") Long recordId,
                                 @RequestParam("fieldId") Long fieldId)
    {
        if (file.isEmpty())
        {
            return AjaxResult.error("上传文件不能为空");
        }
        if (recordId == null || fieldId == null) {
            return AjaxResult.error("RecordId and FieldId are required to associate the file.");
        }

        try
        {
            // Upload file to the default RuoYi path (e.g., /profile/upload)
            String filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);

            // Create AiRecordFileRelation entry
            AiRecordFileRelation fileRelation = new AiRecordFileRelation();
            fileRelation.setRecordId(recordId);
            fileRelation.setFieldId(fieldId);
            fileRelation.setFileName(file.getOriginalFilename());
            fileRelation.setFilePath(filePath); // Path returned by FileUploadUtils
            fileRelation.setFileSize(file.getSize());
            fileRelation.setFileType(file.getContentType());
            fileRelation.setUploadTime(new Date());
            // CreateBy and CreateTime will be set by the service

            aiRecordFileRelationService.insertAiRecordFileRelation(fileRelation);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", file.getOriginalFilename());
            ajax.put("url", filePath); // Or a URL accessible by client if needed
            ajax.put("relationId", fileRelation.getRelationId());
            return ajax;
        }
        catch (Exception e)
        {
            log.error("文件上传失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 查询AI记录文件关联列表 (e.g., for a specific record or field)
     */
    @PreAuthorize("@ss.hasPermi('workspace:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiRecordFileRelation aiRecordFileRelation)
    {
        startPage();
        List<AiRecordFileRelation> list = aiRecordFileRelationService.selectAiRecordFileRelationList(aiRecordFileRelation);
        return getDataTable(list);
    }

    /**
     * 获取AI记录文件关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('workspace:file:query')")
    @GetMapping(value = "/{relationId}")
    public AjaxResult getInfo(@PathVariable("relationId") Long relationId)
    {
        return success(aiRecordFileRelationService.selectAiRecordFileRelationByRelationId(relationId));
    }


    /**
     * 删除AI记录文件关联 (and potentially the file itself, though this example only deletes the DB record)
     */
    @PreAuthorize("@ss.hasPermi('workspace:file:remove')")
    @Log(title = "AI记录文件关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relationIds}")
    public AjaxResult remove(@PathVariable Long[] relationIds)
    {
        // Note: This only deletes the database record.
        // Actual file deletion from disk would require additional logic here or in the service.
        // For example, iterate through relations, get filePath, and delete FileUploadUtils.delete(filePath)
        // This should be done carefully and typically within the service layer if complex.
        return toAjax(aiRecordFileRelationService.deleteAiRecordFileRelationByRelationIds(relationIds));
    }
}

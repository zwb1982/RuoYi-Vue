import request from '@/utils/request'

// Query AiRecordFileRelation list
export function listFileRelation(query) {
  return request({
    url: '/workspace/file/list', // Matches AiRecordFileController @GetMapping("/list")
    method: 'get',
    params: query
  })
}

// Query AiRecordFileRelation details
export function getFileRelation(relationId) {
  return request({
    url: '/workspace/file/' + relationId, // Matches @GetMapping(value = "/{relationId}")
    method: 'get'
  })
}

// Delete AiRecordFileRelation
export function delFileRelation(relationIds) { // relationIds can be a single ID or an array of IDs
  const ids = Array.isArray(relationIds) ? relationIds.join(',') : relationIds;
  return request({
    url: '/workspace/file/' + ids, // Matches @DeleteMapping("/{relationIds}")
    method: 'delete'
  })
}

// Note: Upload is handled directly by the AiRecordFileController's @PostMapping("/upload")
// and typically uses <el-upload> component which has its own action prop.
// Exporting metadata would be via a standard export endpoint if added to controller.

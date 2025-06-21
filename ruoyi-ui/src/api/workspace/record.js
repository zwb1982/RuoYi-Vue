import request from '@/utils/request'

// Query AiDataRecord list
export function listRecord(query) {
  return request({
    url: '/workspace/record/list', // Matches AiDataRecordController @GetMapping("/list")
    method: 'get',
    params: query
  })
}

// Query AiDataRecord details
export function getRecord(recordId) {
  return request({
    url: '/workspace/record/' + recordId, // Matches @GetMapping(value = "/{recordId}")
    method: 'get'
  })
}

// Delete AiDataRecord
export function delRecord(recordIds) { // recordIds can be a single ID or an array of IDs
  const ids = Array.isArray(recordIds) ? recordIds.join(',') : recordIds;
  return request({
    url: '/workspace/record/' + ids, // Matches @DeleteMapping("/{recordIds}")
    method: 'delete'
  })
}

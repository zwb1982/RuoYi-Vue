import request from '@/utils/request'

// Query AiDataTable list
export function listTable(query) {
  return request({
    url: '/workspace/table/list', // Matches AiDataTableController @GetMapping("/list")
    method: 'get',
    params: query
  })
}

// Query AiDataTable details
export function getTable(tableId) {
  return request({
    url: '/workspace/table/' + tableId, // Matches @GetMapping(value = "/{tableId}")
    method: 'get'
  })
}

// Delete AiDataTable
export function delTable(tableIds) { // tableIds can be a single ID or an array of IDs
  const ids = Array.isArray(tableIds) ? tableIds.join(',') : tableIds;
  return request({
    url: '/workspace/table/' + ids, // Matches @DeleteMapping("/{tableIds}")
    method: 'delete'
  })
}

// Note: Add/Edit for tables are typically done via the User Frontend in this design.
// The admin panel primarily lists, views details, and can delete/export.
// If add/edit were needed here, their API functions would be added.

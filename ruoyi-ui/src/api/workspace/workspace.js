import request from '@/utils/request' // RuoYi's standard request utility

// Query AiWorkspace list
export function listWorkspace(query) {
  return request({
    url: '/workspace/workspace/list', // Matches AiWorkspaceController @GetMapping("/list")
    method: 'get',
    params: query
  })
}

// Query AiWorkspace details
export function getWorkspace(workspaceId) {
  return request({
    url: '/workspace/workspace/' + workspaceId, // Matches @GetMapping(value = "/{workspaceId}")
    method: 'get'
  })
}

// Add AiWorkspace
export function addWorkspace(data) {
  return request({
    url: '/workspace/workspace', // Matches @PostMapping
    method: 'post',
    data: data
  })
}

// Update AiWorkspace
export function updateWorkspace(data) {
  return request({
    url: '/workspace/workspace', // Matches @PutMapping
    method: 'put',
    data: data
  })
}

// Delete AiWorkspace
export function delWorkspace(workspaceIds) { // workspaceIds can be a single ID or an array of IDs
  const ids = Array.isArray(workspaceIds) ? workspaceIds.join(',') : workspaceIds;
  return request({
    url: '/workspace/workspace/' + ids,
    method: 'delete'
  })
}

// Export AiWorkspace list
// RuoYi's export often uses POST for complex queries, but here AiWorkspaceController uses POST /export
// and expects AiWorkspace object. The `this.download` method in component handles this.
// So, no specific API function needed here if using `this.download`.
// If a dedicated export API function was preferred, it would look like:
// export function exportWorkspace(query) {
//   return request({
//     url: '/workspace/workspace/export',
//     method: 'post', // Or 'get' if backend supports it with query params
//     params: query // Or data: query for POST
//   })
// }

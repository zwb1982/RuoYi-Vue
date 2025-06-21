import request from '@/utils/request'

// Query AiCallLog list
export function listAiLog(query) {
  // This endpoint is conceptual as it's not yet implemented in the backend
  return request({
    url: '/workspace/aiLog/list',
    method: 'get',
    params: query
  })
}

// Query AiCallLog details
export function getAiLog(logId) {
  // This endpoint is conceptual
  return request({
    url: '/workspace/aiLog/' + logId,
    method: 'get'
  })
}

// Export AiCallLog list
// export function exportAiLog(query) {
//   return request({
//     url: '/workspace/aiLog/export',
//     method: 'post', // Or 'get'
//     params: query // Or data: query
//   })
// }
// For consistency, export will be handled by this.download in component

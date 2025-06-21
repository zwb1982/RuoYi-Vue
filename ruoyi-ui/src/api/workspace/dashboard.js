import request from '@/utils/request'

// Query Dashboard Statistics
export function getDashboardStats() {
  // This endpoint is conceptual as it's not yet implemented in the backend
  return request({
    url: '/workspace/dashboard/stats',
    method: 'get'
  })
}

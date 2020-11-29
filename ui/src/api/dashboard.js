import request from '@/utils/request'

export function getOs() {
  return request({
    url: '/statistics/os',
    method: 'get'
  })
}

export function getHardwareHistory() {
  return request({
    url: '/statistics/hardware/history',
    method: 'get'
  })
}
export function getHardwareLast() {
  return request({
    url: '/statistics/hardware/last',
    method: 'get'
  })
}

export function getSoftware() {
  return request({
    url: '/statistics/software',
    method: 'get'
  })
}

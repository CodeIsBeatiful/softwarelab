import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/softwarelab-ui/table/list',
    method: 'get',
    params
  })
}

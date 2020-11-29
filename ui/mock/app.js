import Mock from 'mockjs'

const data = Mock.mock({
  'items|6': [{
    id: '@guid',
    'author|1': ['John', 'Tom', 'Julia', 'Leehom', 'Black star', 'Admin'],
    'type|1': ['db', 'ai', 'bi', 'mq', 'iot'],
    'name|1': ['kafka', 'postgres', 'metadata', 'thingsboard'],
    stars: '@integer(1,50000)',
    description: '@cparagraph(1,3)',
    createTime: '@datetime',
    updateTime: '@datetime',
    additionalInfo: '{imageName:postgres:9.6}',
    'isCollect|1': [true, false],
    'logo|1': ['app/kafka.png', 'app/postgres.png']
  }]
})

export default [
  {
    url: '/softwarelab-ui/apps/list',
    type: 'get',
    response: config => {
      const items = data.items
      return {
        code: 20000,
        data: {
          total: items.length,
          items: items
        }
      }
    }
  }
]

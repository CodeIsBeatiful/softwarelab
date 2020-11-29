import Mock from 'mockjs'

const List = []
const count = 100

for (let i = 0; i < count; i++) {
  List.push(Mock.mock({
    id: '@increment',
    createTime: +Mock.Random.date('T'),
    operationTime: +Mock.Random.date('T'),
    'image|1': ['PG:9.6', 'Kafka:2.11_2.2.0'],
    title: '@title(2, 4)',
    'imageType|1': ['Util', 'DataBase'],
    'status|1': ['start', 'stop']
  }))
}

export default [
  {
    url: '/vue-element-admin/instances/list',
    type: 'get',
    response: config => {
      const { image, imageType, title, page = 1, limit = 20, sort } = config.query

      let mockList = List.filter(item => {
        if (imageType && imageType !== 'All' && item.imageType !== imageType) return false
        if (image && item.image !== image) return false
        if (title && item.title.indexOf(title) < 0) return false
        return true
      })

      if (sort === '-id') {
        mockList = mockList.reverse()
      }

      const pageList = mockList.filter((item, index) => index < limit * page && index >= limit * (page - 1))

      return {
        code: 20000,
        data: {
          total: mockList.length,
          items: pageList
        }
      }
    }
  },

  {
    url: '/vue-element-admin/instances/create',
    type: 'post',
    response: _ => {
      return {
        code: 20000,
        data: 'success'
      }
    }
  }
]


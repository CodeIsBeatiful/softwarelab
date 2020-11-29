<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="listQuery.imageType" @change="handleImageTypeSelect" placeholder="APP TYPE" clearable class="filter-item" style="width: 130px">
        <el-option v-for="item in calendarTypeOptions" :key="item.key" :label="item.key" :value="item.value" />
      </el-select>
      <el-autocomplete v-model="listQuery.image" class="filter-item" style="width: 200px" :fetch-suggestions="imageQuerySearch" placeholder="APP NAME" @select="handleImageSelect" />
      <el-input v-model="listQuery.name" placeholder="KEYWORD" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        QUERY
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        ADD
      </el-button>
      <el-button v-waves :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">
        EXPORT
      </el-button>
      <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">
        ADVANCED
      </el-checkbox>
    </div>
    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%;"
      @sort-change="sortChange"
    >
<!--      <el-table-column label="ID" prop="id" sortable="custom" align="center" width="300" :class-name="getSortClass('id')">-->
<!--        <template slot-scope="{row}">-->
<!--          <span>{{ row.id }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="NAME" prop="name" sortable="custom" min-width="100px" align="center" :class-name="getSortClass('name')">
        <template slot-scope="{row}">
          <span class="link-type" @click="handleDetail(row)">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="APP NAME" width="250px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.appName }}</span>
<!--          <el-tag>{{ row.imageType | typeFilter }}</el-tag>-->
        </template>
      </el-table-column>
      <el-table-column label="VERSION" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.appVersion }}</span>
        </template>
      </el-table-column>
      <el-table-column label="RUNNING STATUS" prop="runningStatus" align="center" sortable="custom" width="100" :class-name="getSortClass('runningStatus')">
        <template slot-scope="{row}">
          <el-button :type="row.runningStatus | statusTypeFilter" :icon="row.runningStatus | statusFilter" size="mini" circle />
        </template>
      </el-table-column>
      <el-table-column label="CREATE TIME" width="180px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="OPERATION TIME" prop="updateTime" sortable="custom" width="180px" align="center" :class-name="getSortClass('updateTime')">
        <template slot-scope="{row}">
          <span>{{ row.updateTime | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Actions" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleDetail(row)">
            Detail
          </el-button>
          <el-button v-if="row.runningStatus===0" size="mini" type="success" @click="handleModifyStatus(row,1)">
            Start
          </el-button>
          <el-button v-if="row.runningStatus===1" size="mini" type="warning" @click="handleModifyStatus(row,0)">
            Stop
          </el-button>
          <el-button v-if="row.runningStatus===0" size="mini" type="danger" @click="handleDelete(row,$index)">
            Remove
          </el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="showReviewer" label="Terminal" width="110px" align="center">
        <template slot-scope="{row}">
          <el-link v-if="row.runningStatus===1" target="_blank" :href="'#/terminal/terminal?id='+ row.id" style="height: 2em">
            <svg-icon icon-class="command" style="width: 2em; height: 2em" />
          </el-link>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="550px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="150px" style="width: 400px; margin-left:50px;">
        <template v-if="temp.additionalInfo.home">
          <el-form-item label="Home Page">
            <el-link type="primary" :href="temp.additionalInfo.home" target="_blank">{{temp.additionalInfo.home}}</el-link>
          </el-form-item>
        </template>
        <el-form-item label="Name" prop="name">
          <el-input v-model="temp.name" :disabled="dialogStatus==='detail'" />
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="App Type" prop="appType">
          <el-select v-model="temp.appType" @change="handleAppTypeSelectInDialog" :disabled="dialogStatus==='detail'" class="filter-item" placeholder="Please select">
            <el-option v-for="item in calendarTypeOptions" :key="item.key" :label="item.key" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="App Name" prop="appName">
          <el-select v-if="dialogStatus==='create'" v-model="temp.appName" @change="handleAppSelectInDialog" class="filter-item" placeholder="Please select">
            <el-option v-for="item in tempImages" :key="item.key" :label="item.key" :value="item.value" />
          </el-select>
          <el-input v-if="dialogStatus==='detail'" v-model="temp.appName" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="App Version" prop="appVersion">
          <el-select v-if="dialogStatus==='create'" v-model="temp.appVersion" @change="handleAppVersionSelectInDialog" class="filter-item" placeholder="Please select">
            <el-option v-for="item in tempVersions" :key="item.key" :label="item.key" :value="item.value" />
          </el-select>
          <el-input v-if="dialogStatus==='detail'" v-model="temp.appVersion" :disabled="true"></el-input>
        </el-form-item>
        <template v-if="temp.additionalInfo.imageName">
          <el-form-item label="Image Name">
            <el-input v-model="temp.additionalInfo.imageName" :disabled="true" />
          </el-form-item>
        </template>
        <template v-for="portSetting in temp.additionalInfo.ports">
          <el-form-item :key="portSetting.port" :label="portSetting.entrance | portSettingLabelFilter">
            <el-switch v-if="dialogStatus==='create'" v-model="portSetting.autoGen" active-color="#13ce66" inactive-color="#ff4949"/>
            <el-input v-model="portSetting.targetPort" :disabled="portSetting.autoGen || dialogStatus==='detail'" style="width:205px;font-size: 0.7em" >
              <template slot="prepend">{{portSetting.type}}</template>
              <template slot="append">:{{portSetting.port}}</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-for="envSetting in temp.additionalInfo.envs">
          <el-form-item :key="envSetting.key" :label="envSetting.label">
            <el-input v-model="envSetting.value" :disabled="dialogStatus==='detail'" style="width:205px;font-size: 0.7em" >
            </el-input>
          </el-form-item>
        </template>
        <el-form-item v-if="dialogStatus==='detail'" label="Create Time" prop="timestamp">
          <el-date-picker v-model="temp.createTime" :disabled="dialogStatus==='detail'" type="datetime" placeholder="Please pick a date" />
        </el-form-item>
        <el-form-item v-if="dialogStatus==='detail'" label="Operation Time" prop="timestamp">
          <el-date-picker v-model="temp.updateTime" :disabled="dialogStatus==='detail'" type="datetime" placeholder="Please pick a date" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dialogStatus==='create'" @click="dialogFormVisible = false">
          Cancel
        </el-button>
        <el-button v-if="dialogStatus==='create'" type="primary" @click="createData()">
          Confirm
        </el-button>
      </div>
    </el-dialog>

<!--    <el-dialog :visible.sync="dialogPvVisible" title="Reading statistics">-->
<!--      <el-table :data="pvData" border fit highlight-current-row style="width: 100%">-->
<!--        <el-table-column prop="key" label="Channel" />-->
<!--        <el-table-column prop="pv" label="Pv" />-->
<!--      </el-table>-->
<!--      <span slot="footer" class="dialog-footer">-->
<!--        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>-->
<!--      </span>-->
<!--    </el-dialog>-->
  </div>
</template>

<script>
import { fetchList, createInstance, removeInstance, startInstance, stopInstance } from '@/api/instance'
import { getTypes, getNamesByType, getVersionsByAppName, getVersionByAppNameAndVersion } from '@/api/app'
import waves from '@/directive/waves' // waves directive
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination

// const calendarTypeOptions = [
//   { key: 'All', display_name: '' },
//   { key: 'Util', display_name: 'Util' },
//   { key: 'DataBase', display_name: 'DataBase' },
//   { key: 'BI', display_name: 'BI' }
// ]

export default {
  name: 'ComplexTable',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        1: 'el-icon-check',
        0: 'el-icon-close'
      }
      return statusMap[status]
    },
    statusTypeFilter(status) {
      const statusMap = {
        1: 'success',
        0: 'warning'
      }
      return statusMap[status]
    },
    typeFilter(type) {
      return this.calendarTypeKeyValue[type]
    },
    portSettingLabelFilter(entrance) {
      if (entrance) {
        return 'Port Mapping(E)'
      } else {
        return 'Port Mapping'
      }
    }
  },
  data() {
    return {
      images: [],
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 20,
        image: undefined,
        imageType: undefined,
        name: undefined,
        sort: undefined
      },
      calendarTypeOptions: [],
      calendarTypeKeyValue: {},
      statusOptions: ['start', 'stop'],
      showReviewer: false,
      tempImages: [],
      tempVersions: [],
      temp: {
        id: undefined,
        appName: undefined,
        appVersion: undefined,
        appType: undefined,
        name: undefined,
        createTime: undefined,
        operationTIme: undefined,
        additionalInfo: { },
        status: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        detail: 'Detail',
        create: 'Create'
      },
      // dialogPvVisible: false,
      // pvData: [],
      rules: {
        appName: [{ required: true, message: 'app name is required', trigger: 'blur' }],
        appVersion: [{ required: true, message: 'app version is required', trigger: 'blur' }],
        name: [{ required: true, message: 'instance name is required', trigger: 'blur' }]
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
    this.getOptions()
  },
  mounted() {
    // do nothing
  },
  methods: {
    imageQuerySearch(queryString, cb) {
      const images = this.images
      const results = queryString ? images.filter(this.createImageFilter(queryString)) : images
      // callback data list
      cb(results)
    },
    createImageFilter(queryString) {
      return (image) => {
        return (image.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    handleImageTypeSelect(item) {
      this.images = []
      this.listQuery.image = undefined
      getNamesByType(item).then(response => {
        const data = response.data
        for (let i = 0; i < data.length; i++) {
          this.images[i] = { 'value': data[i] }
        }
      })
    },
    handleAppTypeSelectInDialog(item) {
      this.tempImages = []
      this.temp.appName = undefined
      this.tempVersions = []
      this.temp.appVersion = undefined
      this.temp.additionalInfo = {}
      getNamesByType(item).then(response => {
        const data = response.data
        for (let i = 0; i < data.length; i++) {
          this.tempImages.push({ 'key': data[i], 'value': data[i] })
        }
      })
    },
    handleAppSelectInDialog(item) {
      this.tempVersions = []
      this.temp.appVersion = undefined
      this.temp.additionalInfo = {}
      getVersionsByAppName(item).then(response => {
        const data = response.data
        for (let i = 0; i < data.length; i++) {
          if (data[i].downloadStatus === 2) {
            this.tempVersions.push({ 'key': data[i].version, 'value': data[i].version })
          }
        }
      })
    },
    handleAppVersionSelectInDialog(item) {
      this.temp.additionalInfo = {}
      getVersionByAppNameAndVersion(this.temp.appName, item).then(response => {
        this.temp.additionalInfo = JSON.parse(response.data.additionalInfo)
        if (this.temp.additionalInfo) {
          this.temp.additionalInfo.ports.forEach(portSetting => {
            this.$set(portSetting, 'autoGen', true)
          })
        }
      })
    },
    handleImageSelect(item) {
      console.log(item)
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total

        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    getOptions() {
      getTypes().then(response => {
        this.calendarTypeOptions = response.data
        // arr to obj, such as { CN : "China", US : "USA" }
        this.calendarTypeKeyValue = this.calendarTypeOptions.reduce((acc, cur) => {
          acc[cur.key] = cur.value
          return acc
        }, {})
      })
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleModifyStatus(row, status) {
      if (status === 1) {
        startInstance(row.id).then(response => {
          this.$message({
            message: 'start ' + row.name + ' success !',
            type: 'success'
          })
          this.getList()
        })
      } else if (status === 0) {
        stopInstance(row.id).then(response => {
          this.$message({
            message: 'stop ' + row.name + ' success !',
            type: 'success'
          })
          this.getList()
        })
      } else {
        // do nothing
      }
    },
    sortChange(data) {
      const { prop, order } = data
      if (prop === 'name') {
        this.sortByName(order)
      } else if (prop === 'runningStatus') {
        this.sortByRunningStatus(order)
      } else if (prop === 'updateTime') {
        this.sortByUpdateTime(order)
      }
    },
    sortByName(order) {
      if (order === 'ascending') {
        this.listQuery.sort = '+name'
      } else {
        this.listQuery.sort = '-name'
      }
      this.handleFilter()
    },
    sortByRunningStatus(order) {
      if (order === 'ascending') {
        this.listQuery.sort = '+runningStatus'
      } else {
        this.listQuery.sort = '-runningStatus'
      }
      this.handleFilter()
    },
    sortByUpdateTime(order) {
      if (order === 'ascending') {
        this.listQuery.sort = '+updateTime'
      } else {
        this.listQuery.sort = '-updateTime'
      }
      this.handleFilter()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        createTime: undefined,
        operationTime: undefined,
        name: undefined,
        status: undefined,
        appName: undefined,
        appVersion: undefined,
        additionalInfo: { },
        appType: undefined
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          // todo envs check
          // this.temp.id = parseInt(Math.random() * 100) + 1024 // mock a id
          // format to json string
          this.temp.additionalInfo = JSON.stringify(this.temp.additionalInfo)
          createInstance(this.temp).then((response) => {
            this.list.unshift(response.data)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDetail(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.temp.additionalInfo = JSON.parse(this.temp.additionalInfo)
      this.temp.timestamp = new Date(this.temp.timestamp)
      this.dialogStatus = 'detail'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleDelete(row, index) {
      removeInstance(row.id).then((response) => {
        this.list.splice(index, 1)
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['NAME', 'APP NAME', 'APP VERSION', 'RUNNING STATUS', 'CREATE TIME', 'OPERATION TIME']
        const filterVal = ['name', 'appName', 'appVersion', 'runningStatus', 'createTime', 'operationTime']
        const data = this.formatJson(filterVal)
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: 'software-list'
        })
        this.downloadLoading = false
      })
    },
    formatJson(filterVal) {
      return this.list.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    },
    getSortClass: function(key) {
      const sort = this.listQuery.sort
      return sort === `+${key}` ? 'ascending' : 'descending'
    }
  }
}
</script>

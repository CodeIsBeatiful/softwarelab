<template>
  <div class="store-container">
    <el-row :gutter="20">
      <el-col :span="4" :offset="20">
        <el-button type="success" icon="el-icon-download" size="small" @click="upgrade()">Upgrade</el-button>
        <el-button type="success" icon="el-icon-refresh-left" size="small" @click="reload()">Reload</el-button>
      </el-col>
    </el-row>
<!--    <div class="filter-container">-->
<!--      -->
<!--            <el-select v-model="listQuery.imageType" @change="handleImageTypeSelect" placeholder="APP TYPE" clearable class="filter-item" style="width: 130px">-->
<!--              <el-option v-for="item in calendarTypeOptions" :key="item.key" :label="item.key" :value="item.value" />-->
<!--            </el-select>-->
<!--            <el-autocomplete v-model="listQuery.image" class="filter-item" style="width: 200px" :fetch-suggestions="imageQuerySearch" placeholder="APP NAME" @select="handleImageSelect" />-->
<!--            <el-input v-model="listQuery.name" placeholder="KEYWORD" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />-->
<!--            <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">-->
<!--              QUERY-->
<!--            </el-button>-->
<!--            <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">-->
<!--              ADD-->
<!--            </el-button>-->
<!--            <el-button v-waves :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">-->
<!--              EXPORT-->
<!--            </el-button>-->
<!--            <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">-->
<!--              ADVANCED-->
<!--            </el-checkbox>-->

<!--    </div>-->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-divider content-position="left"><svg-icon icon-class="hot"></svg-icon></el-divider>
      </el-col>
      <el-col v-for="app in topList" :key="app.name" :span="6">
        <div class="grid-content bg-purple-light">
          <div class="store-app-logo">
            <img :src="'data:image/png;base64,'+app.logo" style="width: 96px;height: 96px;margin: 5px; border-radius: 10px;">
          </div>
          <div class="store-app-desc">
            <p class="store-app-desc-p">
              {{ app.name }} <el-button size="mini" round type="success">{{ app.usedCount }}</el-button>
            </p>
            <p class="store-app-desc-p" style="color: #5a5a5a;font-size: 0.8em; width: 200px">{{ app.description | ellipsis(100) }}</p>
            <p>
            </p>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-divider content-position="left"><svg-icon icon-class="app"></svg-icon></el-divider>
      </el-col>
      <el-col v-for="app in list" :key="app.name" :span="6">
        <div class="grid-content bg-purple-light">
          <div class="store-app-logo">
            <img :src="'data:image/png;base64,'+app.logo" style="width: 96px;height: 96px;margin: 5px; border-radius: 10px;">
          </div>
          <div class="store-app-desc">
            <p class="store-app-desc-p">{{ app.name }}</p>
            <p class="store-app-desc-p" style="color: #5a5a5a;font-size: 0.8em;">{{ app.description | ellipsis }}</p>
            <p>
              <el-button icon="el-icon-info" type="primary" size="small" :disabled="app.isCollect" @click="showDialog($event, app.name)">detail</el-button>
              <el-dropdown size="small" trigger="click" split-button @visible-change="handleDownloadDropdownChange($event,app.name)" @command="requestDownload(app.name,$event)">
                Download
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item disabled>Choose Version</el-dropdown-item>
                  <template v-for="version in appVersions">
                    <el-dropdown-item :key="version.version" :command="version.version" :disabled="version.downloadStatus === 2" :icon=" version.downloadStatus | downloadIconFilter">
                      {{ version.version }}
                    </el-dropdown-item>
                  </template>
                </el-dropdown-menu>
              </el-dropdown>
            </p>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog title="Tips" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
      <h4>{{ curApp.name }}</h4>
      <span>{{ curApp.description }}</span>
    </el-dialog>
  </div>
</template>

<script>
import { getTop, getList, getVersionsByAppName } from '@/api/app'
import { getToken } from '@/utils/auth'
import { getAddress } from '@/utils'

export default {
  filters: {
    ellipsis(value, length) {
      if (length === undefined) {
        length = 23
      }
      if (!value) return ''
      if (value.length > length) {
        return value.slice(0, length) + '...'
      }
      return value
    },
    downloadIconFilter(downloadStatus) {
      if (downloadStatus === 2) {
        return 'el-icon-check'
      } else if (downloadStatus === 1) {
        return 'el-icon-loading'
      } else if (downloadStatus === 0) {
        return 'el-icon-download'
      } else {
        return ''
      }
    }
  },
  data() {
    return {
      dialogVisible: false,
      list: null,
      topList: null,
      listLoading: true,
      curApp: {},
      appVersions: [],
      socket: null,
      token: getToken()
    }
  },
  created() {
    this.fetchData()
  },
  mounted() {
    this.socket = new WebSocket('ws://' + getAddress() + `/api/ws/message?token=${this.token}`)
    const that = this
    this.socket.addEventListener('message', function(event) {
      that.$notify({
        title: 'Success',
        message: event.data,
        type: 'success',
        duration: 2000
      })
    })
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getTop(10).then(response => {
        this.topList = response.data
      })
      getList({
        pageNum: 0,
        pageSize: 10 }).then(response => {
        this.list = response.data.records
        this.listLoading = false
      })
    },
    findData(name) {
      for (let i = 0; i < this.list.length; ++i) {
        if (this.list[i].name === name) {
          return this.list[i]
        }
      }
      return null
    },
    handleClose(done) {
      done()
    },
    showDialog(event, name) {
      this.dialogVisible = true
      this.curApp = this.findData(name)
    },
    handleDownloadDropdownChange(flag, name) {
      if (flag) {
        getVersionsByAppName(name).then(response => {
          this.appVersions = response.data
        })
      } else {
        this.appVersions = []
      }
    },
    requestDownload(app, version) {
      this.socket.send(JSON.stringify({
        type: 'image',
        operate: 'download',
        content: app + ':' + version
      }))
    },
    upgrade() {
      // todo pop
      this.socket.send(JSON.stringify({
        type: 'app',
        operate: 'upgrade'
      }))
    },
    reload() {
      // todo pop
      this.socket.send(JSON.stringify({
        type: 'app',
        operate: 'reload'
      }))
    }

  }
}
</script>

<style lang="scss" scoped>
  .store {
    &-container {
      margin: 30px;
    }
    &-app {
      width: auto;
      height: 110px;
      float: left;
      margin-top: 15px;

      &-logo {
        width: 110px;
        height: 110px;
        float: left;
      }

      &-desc {
        height: 110px;
        margin-left: 20px;
        float: left;
        &-p {
          margin-top: 0.5em;
          margin-bottom: 0.5em;
          /*margin: 10px;*/
        }
      }
    }
  }
  .el-row {
    margin-bottom: 20px;
    &:last-child {
      margin-bottom: 0;
    }
  }
  .el-col {
    /*border-radius: 4px;*/
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #d3dce6;
    margin-bottom: 15px;
  }
  .bg-purple-light {
    /*background: #e5e9f2;*/
    margin-bottom: 15px;
    border-bottom: #99a9bf   1px dashed;
  }
  .grid-content {
    /*border-radius: 4px;*/
    min-height: 110px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
</style>


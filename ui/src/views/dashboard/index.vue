<template>
  <div class="dashboard-container">

    <el-row :gutter="40" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-os">
            <svg-icon icon-class="os" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">
              OS
            </div>
            <span class="card-panel-num">{{os.name}}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-software">
            <svg-icon icon-class="app" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">
              App Store Version
            </div>
            <span class="card-panel-num">{{software.appStoreVersion}}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-software">
            <svg-icon icon-class="software" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">
              App Total
            </div>
            <count-to :start-val="0" :end-val="software.appTotal" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-software">
            <svg-icon icon-class="software" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">
              Instance
            </div>
            <count-to :start-val="0" :end-val="software.runningInstanceTotal" :duration="2600" class="card-panel-num" />/
            <count-to :start-val="0" :end-val="software.instanceTotal" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import LineChart from '@/views/dashboard/components/LineChart'
import CountTo from 'vue-count-to'
import { getHardwareHistory, getHardwareLast, getOs, getSoftware } from '@/api/dashboard'

export default {
  name: 'Dashboard',
  components: { LineChart, CountTo },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  data() {
    return {
      os: { },
      software: { },
      lineChartData: {
        expectedData: [],
        actualData: []
      }
    }
  },
  created() {
    getOs().then(response => {
      this.os = response.data
    })
    getSoftware().then(response => {
      this.software = response.data
    })
    const that = this
    getHardwareHistory().then(response => {
      const data = response.data
      if (data.length > 0) {
        for (let i = 0; i < data.length; i++) {
          const cpu = data[i].cpu
          const memory = data[i].memory
          const ts = data[i].ts
          that.lineChartData.expectedData.push([ts, cpu])
          that.lineChartData.actualData.push([ts, memory])
          if (that.lineChartData.expectedData.length > 50) {
            that.lineChartData.expectedData.shift()
          }
          if (that.lineChartData.actualData.length > 50) {
            that.lineChartData.actualData.shift()
          }
        }
      }
      this.hardwareInterval = setInterval(function() {
        getHardwareLast().then((response) => {
          const cpu = response.data.cpu
          const memory = response.data.memory
          const ts = response.data.ts
          that.lineChartData.expectedData.push([ts, cpu])
          that.lineChartData.actualData.push([ts, memory])
          if (that.lineChartData.expectedData.length > 50) {
            that.lineChartData.expectedData.shift()
          }
          if (that.lineChartData.actualData.length > 50) {
            that.lineChartData.actualData.shift()
          }
        })
      }, 3000)
    })
  },
  methods: {
  },
  beforeDestroy() {
    clearInterval(this.hardwareInterval)
  }
}
</script>

<style lang="scss" scoped>
/*.dashboard {*/
/*  &-container {*/
/*    margin: 30px;*/
/*  }*/
/*  &-text {*/
/*    font-size: 30px;*/
/*    line-height: 46px;*/
/*  }*/
/*}*/
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-os {
        background: #40c9c6;
      }

      .icon-software {
        background: #36a3f7;
      }
    }

    .icon-os {
      color: #40c9c6;
    }

    .icon-software {
      color: #36a3f7;
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>

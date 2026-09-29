<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {searchByGroupReport, sendJob} from "@/api/dispatch";
import schedulingConfig from '@/views/taskConfig/index.vue'
import schedulingTask from '@/views/dispatch/index.vue'
import schedulingLog from '@/views/dispatch/dispatchingLog/index.vue'
import {ElNotification} from "element-plus";

const props = defineProps({
  rowData: {
    default: {}
  }
})
let tableRow = ref({})//修改使用
const state = reactive({
  list: []
})
let loading = ref(true)
onMounted(() => {
  getData()
})
const getData = async () => {
  let params = {}
  let res = await searchByGroupReport(props.rowData)
  if (res) {
    let data = res.data
    let arr = []
    for (const key in data) {
      let obj = {
        label: key,
        list: []
      }
      for (const childKey in data[key]) {
        let childObj = {
          label: childKey,
          objList: []
        }
        data[key][childKey].forEach((item) => {
          childObj.objList.push(item)
        })
        obj.list.unshift(childObj)
      }
      arr.push(obj)
    }
    state.list = arr
    state.list.forEach((item)=>{
      let total=0
      item?.list.forEach((ret)=>{
        total+=ret.objList.length
      })
      item.total=total
    })

  }
  loading.value = false
  console.log(props.rowData, 'rowData')
}


const schedulingConfigDialog = ref({
  show: false,
  showFooter: false,
});
const schedulingTaskDialog = ref({
  show: false,
  showFooter: false,
});
const schedulingLogDialog = ref({
  show: false,
  showFooter: false,
});

const handle = (type, row) => {
  row.showVisible = false
  tableRow.value.environment = props.rowData.environment

  console.log(row, 'row')
  if (type == 1) {
    tableRow.value.type = 'dispatchConfig' //查询配置
    tableRow.value.jobId = row.jobId
    Object.assign(schedulingConfigDialog.value, {
      show: true,
      title: "调度配置",
      width: '80%',
      okText: false,
      cancelText: false,
    })
  } else if (type == 2) {
    tableRow.value.type = 'dispatch'
    tableRow.value.JOB_ID = row.jobId
    Object.assign(schedulingTaskDialog.value, {
      show: true,
      title: "调度任务",
      width: '80%',
      okText: false,
      cancelText: false,
    })
  } else {
    let environment = props.rowData.environment
    tableRow.value.type = 'dispatchingLog'
    tableRow.value.jobIds = row.jobId
    Object.assign(schedulingLogDialog.value, {
      show: true,
      title: "调度日志",
      width: '80%',
      okText: false,
      cancelText: false,
    })
  }
}
//批量调度
const batchScheduling = async (list) => {
  let filteredList = list.filter(data => data.endStatus !== 1);

  if (filteredList.length === 0) {
    ElNotification({
      title: '提示',
      message: '没有可调度的任务',
      type: 'warning',
      duration: 2000,
      offset: 80
    });
  } else {
    let promises = filteredList.map(data => sendJob({jobId: data.jobId}));
    try {
      let results = await Promise.all(promises);
      if (results) {
        ElNotification({
          title: '调度完成',
          message: '批量调度完成',
          type: 'success',
          duration: 2000,
          offset: 80
        });
      }
    } catch (error) {
      // 处理错误
    }
  }


}
const jobName = (row) => {
}
</script>

<template>
  <div v-loading="loading" style="height: 300px" v-if="loading">
  </div>
  <div v-else style="position: relative">
    <div v-if="state.list?.length>0">
      <el-tabs type="border-card" v-loading="loading">
        <el-tab-pane v-for="(item,index) in state.list" :key="index">
          <template #label>
            <div>{{ item.label }}</div>
            <span v-if="item.list?.length>0"><el-badge :value="item.total" class="item"></el-badge></span>
          </template>
          <el-table :data="item.list" border :show-header="false">
            <el-table-column align="center">
              <template #default="scope">
                {{ scope.row.label }}
              </template>
            </el-table-column>
            <el-table-column align="center">
              <template #default="scope">
                <div v-for="(list,i) in scope.row.objList" :key="i" style="padding: 8px">
                  <el-popover
                      placement="top"
                      title=""
                      v-model:visible="list.showVisible"
                      :width="323"
                      trigger="click"
                  >
                    <div style="display: flex">
                      <el-button class="global-btn-second" @click="handle(1,list)">调度配置</el-button>
                      <el-button class="global-btn-second" @click="handle(2,list)" style="padding: 0 10px">调度任务
                      </el-button>
                      <el-button class="global-btn-second" @click="handle(3,list)">调度日志</el-button>
                    </div>
                    <template #reference>
                      <el-button @click="jobName(list)" class="m-2" type="success" v-if="list.endStatus==1">
                        {{ list.jobName }}
                      </el-button>
                      <el-button @click="jobName(list)" class="m-2" color="#586cb1" v-else>{{ list.jobName }}</el-button>
                    </template>
                  </el-popover>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center">
              <template #default="scope">
                <div style="padding:4px">
                  {{ scope.row.objList[0].solution }}
                </div>
                <!--              <div v-for="(list,i) in scope.row.objList" :key="i">-->
                <!--                {{list.solution}}-->
                <!--              </div>-->
                <div>
                  <el-button class="global-btn-second" @click="batchScheduling(scope.row.objList)"
                  >{{ ('批量调度') }}
                  </el-button>
                </div>
              </template>

            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div v-else>
      <el-table :data="list" border :show-header="false">
        <el-table-column align="center">
        </el-table-column>
      </el-table>
    </div>
  </div>

  <div class="g-Grey">
    <y9Dialog v-model:config="schedulingConfigDialog">
      <schedulingConfig :tableRow="tableRow"></schedulingConfig>
    </y9Dialog>
  </div>

  <div class="g-Grey">
    <y9Dialog v-model:config="schedulingTaskDialog">
      <schedulingTask :tableRow="tableRow"></schedulingTask>
    </y9Dialog>
  </div>

  <div class="g-Grey">
    <y9Dialog v-model:config="schedulingLogDialog">
      <schedulingLog :tableRow="tableRow"></schedulingLog>
    </y9Dialog>
  </div>
</template>

<style scoped lang="scss">
.global-btn-second {
  background-color: transparent;
}

.g-Grey {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-primary-light-9);
  }
}

.h-White {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-white)!important;
  }
}

.tips {
  position: absolute;
  left: 0;
  top: 0;
}
</style>
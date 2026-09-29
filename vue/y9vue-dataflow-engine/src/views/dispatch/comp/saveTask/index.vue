<script setup lang="ts">
import tooltips from './comp/tooltips.vue'
import {globalDataTask} from "@/views/dispatch/comp/saveTask/data";
import {globalData, taskSetForm} from '@/views/taskConfig/comps/data'
import {onMounted, reactive, ref} from "vue";
import {getDataFindAll, getEnvironmentAll, saveJob} from "@/api/dispatch";
import {getFindAll} from "@/api/taskConfig";
import {setTreeData} from "@/utils/object";
import veCorn from "./comp/ve-cron/index.vue";
import newTask from '@/views/taskConfig/comps/newTask.vue';
import newTask2 from '@/views/taskConfig/comps/newTask2.vue';
import { ElNotification } from 'element-plus';

const props = defineProps({
  setTaskShow: {
    default: false,
    type: Boolean
  },
  tableRow: {
    default: {},
    type: Object
  }
})
const visible = ref(false)
const _cron = ref("* * * * * ? *")
const handleChangeCron = (cron: string) => {
  _cron.value = cron
}

const tableDataValue = ref([
  {id: 1, seconds: '*', minutes: '*', Hour: '*', day: '*', month: '*', weeks: '?', years: ''},
])
const tableDataValues = ref([
  {id: 1, seconds: '*', minutes: '*', Hour: '*', day: '*', month: '*', weeks: '?', years: ''},
])
const handleTabChange = (params) => {
  for (const key in tableDataValue.value[0]) {
    if (key == params.type) {
      tableDataValue.value[0][key] = params.value
    }
  }
}

const dispatchTypeChange = (e) => {
  if (e == 'cron') {
    globalDataTask.tableData.speed = _cron.value
  }

}
//保存
const saveCorn = () => {
  let cron = _cron.value.split(' ')
  if (cron[3] === "?" && cron[5] === "?") {
    ElNotification({
      title: '错误',
      message: '日期与星期不可以同时为“不指定”',
      type: 'error',
      duration: 2000,
      offset: 80
    });
    return
  }
  if (cron[3] !== "?" && cron[5] !== "?") {
    ElNotification({
      title: '错误',
      message: '日期与星期必须有一个为“不指定”',
      type: 'error',
      duration: 2000,
      offset: 80
    });
    return;
  }
  globalDataTask.tableData.speed = _cron.value
  visible.value = false
}
let loading = ref(true)
onMounted(() => {
  getTree() //获取树形结构
  initData()
  ruleFormRef?.value?.resetFields()
  if (globalDataTask.type == 'add') {
    globalDataTask.tableData = JSON.parse(JSON.stringify(globalDataTask.initTableData))
  } else {
    getList(props.tableRow.jobType, '3')

  }
  if (props.setTaskShow) { //直接设置定时任务
    globalDataTask.tableData.args = [globalData.saveForm.id]
    globalDataTask.tableData.jobType = globalData.saveForm.businessId
  } else {
    //新增任务
  }
  setTimeout(()=>{
    loading.value = false
  },500)

})
const selectTime = () => {
  visible.value = !visible.value
  let speed = JSON.parse(JSON.stringify(globalDataTask.tableData.speed))
  let speedSplit = speed.split(' ')
  tableDataValue.value[0].seconds = speedSplit[0]
  tableDataValue.value[0].minutes = speedSplit[1]
  tableDataValue.value[0].Hour = speedSplit[2]
  tableDataValue.value[0].day = speedSplit[3]
  tableDataValue.value[0].month = speedSplit[4]
  tableDataValue.value[0].weeks = speedSplit[5]
  tableDataValue.value[0].years = speedSplit[6]
  _cron.value = globalDataTask.tableData.speed
}
const getTree = async () => {
  let res = await getFindAll()
  if (res) {
    if (res.data) {
      state.treeData = setTreeData(res.data)
    }
  }
}
const treeChange = (e) => {
  if (!props.setTaskShow) {
    globalDataTask.tableData.args = []
    globalDataTask.tableData.args = []
    getList(e, '2')
  }
}
const initData = async () => {
  let res = await getEnvironmentAll()
  if (res) {
    globalDataTask.environmentAll = res.data
  }
}
const getList = async (e, type) => {
  let res = await getDataFindAll({businessId: e})
  if (res) {
    res.data?.forEach((item) => {
      item.value = item.id
    })
    state.dataList = res.data
    if (type == '1') {
      let arr = state.dataList.filter(obj => globalDataTask.tableData.args.includes(obj.id)).map(obj => obj.id);
      if (arr.length == 0) {
        globalDataTask.tableData.args = []
      } else {
        globalDataTask.tableData.args = arr
      }
    } else if (type == '2') {
      globalDataTask.tableData.args = []
    } else if (type == '3') { //3是编辑需要赋值
      for (const key in globalDataTask.tableData) {
        for (const rowKey in props.tableRow) {
          if (key == rowKey) {
            globalDataTask.tableData[key] = props.tableRow[rowKey]
          }
        }
      }
      globalDataTask.tableData.args = props.tableRow.args.split(',')
    }
  }
}


const taskList = computed(() => {
  return state.dataList.filter(item => globalDataTask.tableData?.args?.includes(item.id));
})
const emits = defineEmits(['close'])
const state = reactive({
  treeData: [],
  dataList: [],
  businessId: null,
  defaultProps: {
    children: 'children',
    label: 'name',
  },
  dataSource: [],//数据源
})
const rules = {
  jobType: [
    {required: true, message: '请选择业务分类', trigger: ['blur', 'change']}
  ],
  args: [
    {required: true, message: '请选择任务', trigger: ['blur', 'change']}
  ],
  speed: [
    {required: true, message: '不能为空', trigger: ['blur', 'change']}
  ],
  name: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  description: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  sourceTimeOut: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  dispatchType: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  blockingStrategy: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  timeOut: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  errorCount: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  environment: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
}

const saveY9Table = async () => {
  let validate = await ruleFormRef.value.validate()
  if (validate) {
    let data = JSON.parse(JSON.stringify(globalDataTask.tableData))

    if (globalDataTask.type == 'edit') {
      data.id = props.tableRow.id
    }

    data.args = data.args?.toString()
    data.type='job'
    let res = await saveJob(data)
    if (res) {
      if (res.success == true) {
        ElNotification({
          title: ('成功'),
          message: ('保存成功'),
          type: 'success',
          duration: 2000,
          offset: 80
        });
      }
      emits('close', 3); //3在调度是查列表 在任务配置是关闭

    }
  }
}
const handleTask = (row) => {
  globalData.row = {};
  globalData.allData = {};
  if(props.setTaskShow){
    let obj={
      id:globalData.saveForm?.id,
      businessId:globalData.saveForm?.businessId
    }
    jobId.value=JSON.parse(JSON.stringify(globalData.saveForm?.businessId))
    row=obj
  }else {
    jobId.value = row.businessId //点击的任务id
  }
  globalData.row = row;
  globalData.type = 'edit';
  if(row.taskType == 1) {
      Object.assign(dialogConfig2.value, {
        show: true,
        title: '编辑',
        width: '65%',
        okText: false,
        cancelText: false
      });
  }else if(row.taskType == 2) {
      taskSetForm.tableData.activeName = '1';
      taskSetForm.tableData.showSave = true;
      Object.assign(dialogConfig.value, {
        show: true,
        title: '编辑',
        width: '65%',
        okText: false,
        cancelText: false
      });
  }
}

// 表单配置
const dialogConfig = ref({
  show: false
});
const dialogConfig2 = ref({
  show: false
});
const close = () => {
  emits('close', 2)
}
let jobId = ref('')  //点击的分类id
const closeTask = (type) => {
  if(type == 1){
    dialogConfig.value.show = false
  }else{
    dialogConfig2.value.show = false
  }
  getList(jobId.value, '1')
}
let ruleFormRef = ref()
</script>

<template>
  <!-- 表单 -->
  <y9Dialog v-model:config="dialogConfig">
    <newTask v-if="dialogConfig.show" @close="closeTask(1)"></newTask>
  </y9Dialog>
  <y9Dialog v-model:config="dialogConfig2">
    <newTask2 v-if="dialogConfig2.show" @close="closeTask(2)"></newTask2>
  </y9Dialog>
  <el-form
      v-loading="loading"
      ref="ruleFormRef"
      :model="globalDataTask.tableData"
      :rules="rules">
    <div class="base">
      <el-divider content-position="left" class="title">基础配置</el-divider>
    </div>
    <el-descriptions border :column="1">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>业务分类</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div v-if="setTaskShow">
          {{ globalData.saveForm.businessName }}
        </div>
        <div v-else>
          <el-form-item prop="jobType">
            <el-tree-select
                @change="treeChange"
                node-key="id"
                clearable
                check-strictly
                :props="state.defaultProps"
                v-model="globalDataTask.tableData.jobType"
                :data="state.treeData"
                style="width: 100%"
            />
          </el-form-item>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>业务名称</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div v-if="setTaskShow">
          {{ globalData.saveForm.name }}
        </div>
        <div v-else>
          <el-form-item prop="args">
            <el-tree-select
                v-model="globalDataTask.tableData.args"
                :data="state.dataList"
                multiple
                :props="state.defaultProps"
                filterable
                :render-after-expand="false"
                check-strictly
                check-on-click-node
                show-checkbox
            />
          </el-form-item>
        </div>
      </el-descriptions-item>
    </el-descriptions>
    <div class="task-list" v-if="setTaskShow">
      <div v-for="item in globalDataTask.tableData.args" class="box">
        <y9Card title="我是标题" :showHeader="false" class="card" @click="handleTask(item)">
          <div class="name"> {{ globalData.saveForm.name }}</div>
          <!--          <div class="cate">业务分类</div>-->
          <div class="des">
            <tooltips :lineClamp="1" :tipText="globalData.saveForm.description">{{
                globalData.saveForm.description
              }}
            </tooltips>
          </div>
        </y9Card>
      </div>
    </div>
    <div class="task-list" v-else>
      <div v-for="item in taskList" class="box">
        <y9Card title="我是标题" :showHeader="false" class="card" @click="handleTask(item)">
          <div class="name">{{ item.name }}</div>
          <!--          <div class="cate">业务分类</div>-->
          <div class="des">
            <tooltips :lineClamp="1" :tipText="item.description">{{ item.description }}</tooltips>
          </div>
        </y9Card>
      </div>
    </div>

    <el-divider content-position="left" class="title">调度配置</el-divider>

    <el-descriptions border :column="2">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>环境选择</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="environment">
          <el-select
              v-model="globalDataTask.tableData.environment"
              class="m-2"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in globalDataTask.environmentAll"
                :key="item.name"
                :label="item.description"
                :value="item.name"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>任务名称</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="name">
          <el-input placeholder="请输入" v-model="globalDataTask.tableData.name"></el-input>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>描述</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="description">
          <el-input placeholder="请输入" v-model="globalDataTask.tableData.description"></el-input>
        </el-form-item>
      </el-descriptions-item>

      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>来源</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item>
          <el-input placeholder="请输入" v-model="globalDataTask.tableData.jobSource"></el-input>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>调度类型</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="dispatchType">
          <el-select
              v-model="globalDataTask.tableData.dispatchType"
              class="m-2"
              @change="dispatchTypeChange"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in globalDataTask.dispatchTypeOptions"
                :key="item.id"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <div v-if="globalDataTask.tableData.dispatchType">
        <el-descriptions-item label-align="center" v-if="globalDataTask.tableData.dispatchType=='cron'">
          <template #label>
            <div>
              <span>cron</span>
              <span class="y9-required-icon">*</span>
            </div>
          </template>
          <el-popover
              :visible="visible"
              :width="850"
              placement="top"
              trigger="click">
            <!--            v-if="visible"-->
            <div class="corn-popper" v-if="visible">
              <veCorn :value="_cron" @change="handleChangeCron" @tabChange="handleTabChange"/>
              <div>
                <el-table :data="tableDataValue" border style="width: 100%">
                  <el-table-column prop="seconds" label="秒" align="center" style="font-weight: bold"/>
                  <el-table-column prop="minutes" label="分" align="center"/>
                  <el-table-column prop="Hour" label="時" align="center"/>
                  <el-table-column prop="day" label="日" align="center"/>
                  <el-table-column prop="month" label="月" align="center"/>
                  <el-table-column prop="weeks" label="周" align="center"/>
                  <el-table-column prop="years" label="年" align="center"/>
                </el-table>
              </div>
            </div>
            <div style="text-align: center;margin-top: 15px">
              <el-button type="primary" @click="visible=false"><span>取消</span></el-button>
              <el-button type="primary" @click="saveCorn"><span>确定</span></el-button>
            </div>
            <template #reference>
              <el-form-item prop="speed" @click="selectTime">
                <el-input readonly placeholder="请选择" v-model="globalDataTask.tableData.speed"></el-input>
              </el-form-item>
            </template>
          </el-popover>

        </el-descriptions-item>
        <el-descriptions-item label-align="center" label="fetchSize" v-else>
          <template #label>
            <div>
              <span>速度(单位秒)</span>
              <span class="y9-required-icon">*</span>
            </div>
          </template>
          <div class="fetch">
            <div class="fetch-w">
              <el-form-item prop="speed">
                <el-input-number class="number-input" placeholder="请输入" :controls="false"
                                 style="width: 100%" v-model="globalDataTask.tableData.speed"/>
              </el-form-item>
            </div>
          </div>
        </el-descriptions-item>
      </div>

    </el-descriptions>
    <el-divider content-position="left" class="title">其他配置</el-divider>
    <el-descriptions border :column="2">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>阻塞策略</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="blockingStrategy">
          <el-select
              v-model="globalDataTask.tableData.blockingStrategy"
              class="m-2"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in globalDataTask.blockingOptions"
                :key="item.id"
                :label="item.label"
                :value="item.label"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>子任务id</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item>
          <el-input placeholder="请输入" v-model="globalDataTask.tableData.child"></el-input>
        </el-form-item>
      </el-descriptions-item>

      <el-descriptions-item label-align="center" label="fetchSize">
        <template #label>
          <div>
            <span>调度超时时间(秒)</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item prop="sourceTimeOut">
              <el-input-number class="number-input" placeholder="请输入" :controls="false"
                               style="width: 100%" v-model="globalDataTask.tableData.sourceTimeOut"/>
            </el-form-item>
          </div>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="fetchSize">
        <template #label>
          <div>
            <span>调度总超时时间(秒)</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item prop="timeOut">
              <el-input-number class="number-input" placeholder="请输入" :controls="false"
                               style="width: 100%" v-model="globalDataTask.tableData.timeOut"/>
            </el-form-item>
          </div>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="fetchSize">
        <template #label>
          <div>
            <span>重试次数</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item prop="errorCount">
              <el-input-number class="number-input" placeholder="请输入" :controls="false"
                               style="width:calc( 50% - 100px)" v-model="globalDataTask.tableData.errorCount"/>
            </el-form-item>
          </div>
        </div>
      </el-descriptions-item>
    </el-descriptions>
  </el-form>
  <div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 25px;">
    <el-button type="primary" @click="saveY9Table"><i class="ri-save-line"></i><span>保存</span></el-button>
    <el-button type="primary" @click="close"><i class="ri-close-line"></i><span>关闭</span></el-button>
  </div>
</template>

<style scoped lang="scss">
.task-list {
  cursor: pointer;
  width: 100%;
  display: flex;
  flex-wrap: wrap;

  .box {
    display: flex;
    width: 25%;

    .card {
      margin: 25px 20px 10px 0;
      width: 100%;

      .name {
        font-size: 18px;
      }

      .cate {
        margin: 5px 0;
      }

      .des {

      }
    }
  }
}

.corn-popper {
  min-height: 450px;
  padding: 0 30px;
}

.base {
  margin-top: -16px;

  .is-left {
    margin-top: 6px;
  }

  :deep(.el-divider__text.is-left) {
    margin-top: 6px;
  }
}

:deep(.el-tag__content) {
  color: var(--el-color-primary) !important;
}
</style>
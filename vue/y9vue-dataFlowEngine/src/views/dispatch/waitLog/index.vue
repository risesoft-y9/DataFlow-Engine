<template>
  <!-- 表格 -->
  <y9Table
      :config="tableConfig"
      :filterConfig="filterConfig"
      @on-curr-page-change="onCurrentChange"
      @on-page-size-change="onPageSizeChange"
  >
    <template #environment>
      <div class="y9select-task">
        <div class="label">任务环境</div>
        <el-select
            clearable
            @change="envChange"
            v-model="state.form.environment"
            class="m-2"
            placeholder="请选择"
        >
          <el-option
              v-for="item in state.environmentAll"
              :key="item.id"
              :label="item.description"
              :value="item.name"
          />
        </el-select>
      </div>
    </template>
    <template #select>
      <div class="y9select-task">
        <div class="label">业务分类</div>
        <el-tree-select
            node-key="id"
            clearable
            check-strictly
            :props="state.defaultProps"
            v-model="state.form.jobType"
            :data="state.treeData"
            style="width: 100%"
        />
      </div>
    </template>
    <template #dispatchType>
      <div class="y9select-task">
        <div class="label">调度服务</div>
        <el-input  clearable placeholder="请输入" v-model="state.form.dispatchSource"></el-input>
      </div>
    </template>
    <template #statusType>
      <div class="y9select-task">
        <div class="label">状态</div>
        <el-select
            clearable
            v-model="state.form.status"
            class="m-2"
            placeholder="请选择"
        >
          <el-option
              v-for="item in state.statusList"
              :key="item.id"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </div>
    </template>
    <template #queryFun>
      <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
      ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
      </el-button>
    </template>
    <template #typeName="{ row, column, index }">
      <template>{{ row.typeName }}</template>
    </template>
    <template #className="{ row, column, index }">
      <template>{{ row.name }}</template>
    </template>
    <template #description="{ row, column, index }">
      <template>{{ row.description }}</template>
    </template>
    <template #status="{ row ,column, index}">
      <div class="statusBtn" @click="handle(2,row)">
        <el-tag size="large"  effect="dark"  type="warning"  v-if="row.STATUS==-1">等待中</el-tag>
        <el-tag size="large" style="color: #fff" effect="dark" v-else-if="row.STATUS==1">成功</el-tag>
        <el-tag size="large"  effect="dark" type="warning" v-else-if="row.STATUS==0">执行中</el-tag>
        <el-tag size="large" effect="dark"  type="danger" v-else-if="row.STATUS==2">失败</el-tag>
      </div>
    </template>
    <template #RESULT="{ row ,column, index}">
      <div class="fields" style="cursor: pointer;color:var(--el-color-primary)" @click="handle(1,row)">
        <span v-if="row.STATUS==0">执行中...</span>
        <span>{{row.RESULT}}</span>

      </div>
    </template>
    <template #operation="{ row ,column, index}">
      <div class="fields" @click="handle(2,row)">
        <i class="ri-book-open-line"></i>
        <span class="text">{{ ('详情') }}</span>
      </div>
    </template>
  </y9Table>


  <!-- 详情 -->
  <div class="c-Grey">
    <y9Dialog v-model:config="dialogConfig" >
      <dispatchInfo :tableRow="tableRow" v-if="dialogConfig.show" ></dispatchInfo>
    </y9Dialog>
  </div>
  <!-- 结果 -->
  <div class="d-White">
    <y9Dialog v-model:config="dialogConfigLog" >
      <result :tableRow="tableRow" v-if="dialogConfigLog.show" ></result>
    </y9Dialog>
  </div>

</template>

<script lang="ts" setup>
import {ref, onMounted, computed, inject, reactive} from 'vue';
import {useI18n} from 'vue-i18n';
import {getFindAll} from "@/api/taskConfig";

import {getDataSearch, getEnvironmentAll, getLogSearch} from "@/api/dispatch";
import {setTreeData} from "@/utils/object";
import router from '@/router';
import moment from "moment/moment";
import dispatchInfo from '../index.vue'
import result from '../comp/result/index.vue'
const {t} = useI18n();
const fontSizeObj: any = inject('sizeObjInfo');
import {useRoute} from 'vue-router'
import { ElNotification } from 'element-plus';

const props = defineProps({
  tableRow: {
    default: {},
  }
})

const route = useRoute()

const state = reactive({
  treeData: [],
  defaultProps: {
    children: 'children',
    label: 'name',
  },
  form: {
    environment: '',//环境id
    dispatchType: null,//调度类型
    status: -1,//等待中
    jobType: null,
    // jobId: null,
    jobIds: null, //任务集合id
    serviceId: null,
    jobName: null,
    dispatchSource: null,//被调度的服务
  },
  statusList: [
    {label: '全部', value: '', id: 1},
    {label: '执行中', value: 0, id: 2},
    {label: '等待中', value: -1, id: 3},
    {label: '失败', value: 2, id: 4},
    {label: '成功', value: 1, id: 5},
  ],
  dispatchTypeOptions: [ // todo 后续查看服务接口
    {label: '全部', value: '', id: 1},
  ],
  environmentAll: [],//环境下拉
  businessId: null,
  dataSource: [],//数据源
})


// 搜索的filter条件变量
let filterData = ref({});

// 表格列表配置
let tableConfig = ref({
  pageConfig: {
    currentPage: 1, //当前页数，支持 v-model 双向绑定
    pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
    total: 0,
    pageSizeOpts:[10,15,30,60,120,240]
  },
  loading: false,
  border: false,
  headerBackground: true,
  columns: [
    {
      type: 'index',
      width: 80,
      fixed: 'left',
      title: computed(() => t('序号'))
    },
    {
      title: computed(() => t('任务名称')),
      key: 'JOB_NAME',
    },
    {
      title: computed(() => t('业务分类')),
      key: 'JOB_TYPE_NAME',
    },
    {
      title: computed(() => t('调度服务')),
      key: 'DISPATCH_SOURCE',
      width: 290,
    },

    {
      title: computed(() => t('调度时间')),
      key: 'DISPATCH_TIME',
      width: 170,
      render(row) {
        if(row.DISPATCH_TIME){
          return h("div", {}, moment(row.DISPATCH_TIME).format("YYYY-MM-DD HH:mm:ss"));
        }
      }
    },
    {
      title: computed(() => t('结束时间')),
      key: 'END_TIME',
      width: 170,
      render(row) {
        if(row?.END_TIME){
          return h("div", {}, moment(row?.END_TIME).format("YYYY-MM-DD HH:mm:ss"));
        }

      }
    },
    {
      title: computed(() => t('结果')),
      key: 'RESULT',
      slot: 'RESULT',
      width: 320,
    },
    {
      title: computed(() => t('状态')),
      width: 150,
      key: 'status',
      slot: 'status'
    },
    // {
    //   title: computed(() => t('操作')),
    //   width: 150,
    //   key: 'operation',
    //   slot: 'operation'
    // }
  ],
  tableData: []
});
// 详情
const dialogConfig = ref({
  show: false,
  showFooter: false,
});
// 日志
const dialogConfigLog = ref({
  show: false,
  showFooter: false,
});

let tableRow = ref()
const handle = (type,row) => {
  tableRow.value = row
  console.log(row, '查看详情')
  if(type==1){
    Object.assign(dialogConfigLog.value, {
      show: true,
      title: "日志",
      width: '50%',
      okText: false,
      cancelText: false,
      appendToBody:true
    })
  }else{
    row.type = 'dispatch'
    row.environment = state.form.environment
    Object.assign(dialogConfig.value, {
      show: true,
      title: "调度管理",
      width: '80%',
      okText: false,
      cancelText: false,
    })
  }

}
// 表格条件筛选配置
let filterConfig = ref({
  itemList: [
    {
      type: 'slot',
      value: '',
      slotName: 'environment',
      span: 4,
    },
    {
      type: 'slot',
      value: '',
      slotName: 'dispatchType',
      span: 4,
    },
    {
      type: 'input',
      key: 'jobName',
      span: 4,
      label: computed(() => t('任务名称'))
    },
    {
      type: 'slot',
      value: '',
      slotName: 'select',
      span: 4,
    },
    {
      type: 'slot',
      value: '',
      slotName: 'statusType',
      span: 4,
    },
    {
      type: 'slot',
      span: 4,
      slotName: 'queryFun'
    }
  ],
  fangDouTime: 0,//防抖时间
  filtersValueCallBack: (filters) => {
    console.log('过滤值', filters);
    filterData.value = filters;
  }
});


onMounted(() => {
  initParams() //弹窗数据
  getEnvironment();
  //initTableData();
  getTree()
});

const envChange=()=>{
  tableConfig.value.pageConfig.currentPage = 1;
  initTableData();
}

const initParams = () => {
  // let jobIds = route?.query?.jobIds
  // let environment = route?.query?.environment
  // if (jobIds) {
  //   state.form.environment = environment
  //   state.form.jobIds = jobIds
  // }
  if (props?.tableRow?.type == 'dispatchingLog') { //调度日志的弹窗
    tableConfig.value.pageConfig.pageSize = 10
    
    state.form.environment = props?.tableRow.environment
    if(props?.tableRow.jobIds){
      state.form.jobIds = props.tableRow.jobIds
    }else{
      state.form.jobIds = props.tableRow.id
    }

    tableConfig.value.columns.forEach((item, index) => {
      if (item.key == 'operation') {
        tableConfig.value.columns.splice(index, 1)
      }
    })

    filterConfig.value.itemList.forEach((item, index) => {
      item.span = 4
      if (item.slotName == 'environment') {
        filterConfig.value.itemList.splice(index, 1)
      }
    })
    filterConfig.value.itemList.forEach((item, index) => {
      item.span = 4
    })
  }
}

const getTree = async () => {
  let res = await getFindAll()
  if (res) {
    if (res.data) {
      state.treeData = setTreeData(res.data)
    }
  }
}

//获取环境
const getEnvironment = async () => {
  let res = await getEnvironmentAll();
  if (res) {
    state.environmentAll = res.data;
    state.form.environment = res.data[0].name;
    initTableData();
  }
}

// init 数据
async function initTableData() {
  tableConfig.value.loading = true;
  // 接口参数
  let params = {
    environment: state.form.environment,
    dispatchType: state.form.dispatchType,
    name: state.form.name,
    // jobId:state.form.jobId,
    jobIds: state.form.jobIds,
    // serviceId:state.form.serviceId,
    // jobName:state.form.jobName,
    dispatchSource: state.form.dispatchSource,//被调的服务
    jobType: state.form.jobType,
    status: state.form.status,
    pageNo: tableConfig.value.pageConfig.currentPage,
    pageSize: tableConfig.value.pageConfig.pageSize,
    ...filterData.value
  };

  // 请求接口
  let res = await getLogSearch(params);

  if (res.code == 0) {
    // 对返回的接口数据进行赋值与处理
    tableConfig.value.tableData = res.data.content
    tableConfig.value.pageConfig.total = res.data.total;
  }else {
    tableConfig.value.tableData = [];
    tableConfig.value.pageConfig.total = 0;
    ElNotification({
      title: '提示',
      message: res?.msg,
      type: 'error',
      duration: 2000,
      offset: 80
    });
  }

  tableConfig.value.loading = false;
}

// 搜索
function handleClickQuery() {
  tableConfig.value.pageConfig.currentPage = 1;
  initTableData();
}

// 分页操作
function onCurrentChange(currPage) {
  console.log(currPage, 'currPage')
  tableConfig.value.pageConfig.currentPage = currPage;
  initTableData();
}

function onPageSizeChange(pageSize) {
  tableConfig.value.pageConfig.pageSize = pageSize;
  initTableData();
}

</script>

<style lang="scss" scoped>
.fields {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 5px;

  i {
    margin-right: 2px;
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
}
.operation {
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;

  .fields {
    cursor: pointer;
    display: flex;
    align-items: center;
    margin: 5px;

    i {
      margin-right: 2px;
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
  }

  .delete {
    margin: 5px;
    display: flex;
    align-items: center;

    i {
      margin-right: 2px;
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
  }
}

.y9select-task {
  display: flex;
  align-items: center;
  width: 100%;

  .label {
    margin-right: 10px;
    color: #606266;
    font-size: inherit;

    display: flex;
    align-items: center;
  }
}

.statusBtn {
  :deep(.el-tag>span) {
    color: var(--el-color-white) !important;
  }
  :deep(.el-tag__content){
    color: var(--el-color-white) !important;
  }
  :deep(.el-tag){
    color: var(--el-color-white) !important;
  }
}


//:deep(.el-tag__content){
//  color: var(--el-color-white) !important;
//}
//:deep(.el-tag>span) {
//  color: var(--el-color-white) !important;
//}
.c-Grey {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-primary-light-9);
  }
}

.d-White {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-white)!important;
  }
}
.fields {
  display: flex;
  align-items: center;
  margin: 5px;

  i {
    margin-right: 2px;
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
}

</style>

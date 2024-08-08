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
            @change="envChange"
            clearable
            v-model="state.form.environment"
            class="m-2"
            placeholder="请选择"
        >
          <el-option
              v-for="item in state.environmentAll"
              :key="item.id"
              :label="item.name"
              :value="item.name"
          />
        </el-select>
      </div>
    </template>
    <template #startDate>
      <div class="y9select-task">
        <div class="label">开始时间</div>
        <el-date-picker
            v-model="state.form.startDate"
            type="date"
            placeholder="开始时间"
            value-format="YYYY-MM-DD"
        />
      </div>
    </template>

    <template #endDate>
      <div class="y9select-task">
        <div class="label">结束时间</div>
        <el-date-picker
            v-model="state.form.endDate"
            type="date"
            placeholder="结束时间"
            value-format="YYYY-MM-DD"
        />
      </div>
    </template>


    <template #queryFun>
      <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
      ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
      </el-button>
      <div class="right-btn">
        <el-button class="global-btn-second" @click="logReport"
        >{{ ('日志分析报告') }}
        </el-button>
      </div>
    </template>

    <template #operation="{ row ,column, index}">
      <div class="fields" @click="handle(row)">
        <i class="ri-search-eye-line"></i>
        <span class="text">{{ ('查看失败日志') }}</span>
      </div>
    </template>
  </y9Table>


  <div class="e-Grey">
    <y9Dialog v-model:config="dialogLogInfo">
      <logInfo :tableRow="tableRow"></logInfo>
    </y9Dialog>
  </div>

  <div class="f-White">
    <y9Dialog v-model:config="dialogLogInfoAll">
      <logAnalysisReport :rowData="rowData"></logAnalysisReport>
    </y9Dialog>
  </div>
</template>

<script lang="ts" setup>
import {ref, onMounted, computed, inject, reactive} from 'vue';
import {useI18n} from 'vue-i18n';
import {getFindAll} from "@/api/taskConfig";
import {ElMessageBox, ElNotification, ElMessage} from 'element-plus';
import {getDataSearch, getEnvironmentAll, getLogSearch, getLogSearchByGroup} from "@/api/dispatch";
import {setTreeData} from "@/utils/object";
import router from '@/router';
import moment from "moment/moment";
import logInfo from '../failLog/index.vue'

const {t} = useI18n();
const fontSizeObj: any = inject('sizeObjInfo');
import logAnalysisReport from './comp/logAnalysisReport/index.vue'

const state = reactive({
  treeData: [],
  defaultProps: {
    children: 'children',
    label: 'name',
  },
  form: {
    environment: 'Public',//环境id
    status: 2,//
    startDate: '',
    endDate: ''
  },

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
      width: 100,
      fixed: 'left',
      title: computed(() => t('序号'))
    },
    {
      title: computed(() => t('分组/任务名称')),
      key: 'job_name',
    },
    {
      title: computed(() => t('失败次数')),
      key: 'count',
    },

    {
      title: computed(() => t('最新调度时间')),
      key: 'dispatch_time',
      render(row) {
        if (row.dispatch_time) {
          return h("div", {}, moment(row?.dispatch_time).format("YYYY-MM-DD HH:mm:ss"));
        }
      }
    },
    {
      title: computed(() => t('调度失败日志')),
      width: 200,
      key: 'operation',
      slot: 'operation'
    }
  ],
  tableData: []
});
const dialogLogInfo = ref({
  show: false,
  showFooter: false,
});

const dialogLogInfoAll = ref({
  show: false,
  showFooter: false,
});
let tableRow = ref()
const handle = (row) => {
  row.environment = state.form.environment
  tableRow.value = row
  tableRow.value.type = 'dispatchingLog'
  tableRow.value.id = row.job_id
  Object.assign(dialogLogInfo.value, {
    show: true,
    title: "调度日志",
    width: '80%',
    okText: false,
    cancelText: false,
  })
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
      slotName: 'startDate',
      key: 'startDate',
      span: 4,
      label: computed(() => t('开始时间'))
    },
    {
      type: 'slot',
      key: 'endDate',
      slotName: 'endDate',
      span: 4,
      label: computed(() => t('结束时间'))
    },
    {
      type: 'input',
      key: 'jobName',
      span: 4,
      label: computed(() => t('任务名称'))
    },

    {
      type: 'slot',
      span: 8,
      slotName: 'queryFun'
    },

  ],
  fangDouTime: 0,//防抖时间
  filtersValueCallBack: (filters) => {
    console.log('过滤值', filters);
    filterData.value = filters;
  }
});


onMounted(() => {
  // 获取前一天日期
  const startDate = moment().subtract(1, 'months').format('YYYY-MM-DD');
  // 获取当天日期
  const endDate = moment().format('YYYY-MM-DD');
  state.form.startDate = startDate
  state.form.endDate = endDate
  getEnvironment()

  initTableData();
});
const envChange = () => {
  tableConfig.value.pageConfig.currentPage = 1;
  initTableData();
}

const getTree = async () => {
  let res = await getFindAll()
  if (res) {
    if (res.data) {
      state.treeData = setTreeData(res.data)
    }
  }
}
//获取环境 todo 后续改成获取用户
const getEnvironment = async () => {
  let res = await getEnvironmentAll()
  if (res) {
    state.environmentAll = res.data
  }
}
let rowData = ref({})
const logReport = () => {
  rowData.value = {
    startDate: state.form.startDate,
    endDate: state.form.endDate,
    environment: state.form.environment,
    ...filterData.value
  }
  Object.assign(dialogLogInfoAll.value, {
    show: true,
    title: "日志分析报告",
    width: '90%',
    okText: false,
    cancelText: false,
  })
}

// init 数据
async function initTableData() {
  tableConfig.value.loading = true;
  // 接口参数
  let params = {
    environment: state.form.environment,
    startDate: state.form.startDate,
    endDate: state.form.endDate,
    pageNo: tableConfig.value.pageConfig.currentPage,
    pageSize: tableConfig.value.pageConfig.pageSize,
    ...filterData.value
  };

  // 请求接口
  let res = await getLogSearchByGroup(params);

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


.global-btn-second {
  background-color: transparent;
}

.right-btn {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.e-Grey {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-primary-light-9);
  }
    :deep(.d-White .y9-dialog-body .y9-dialog-content) {
      background: var(--el-color-white)!important;
    }
}

:deep(.g-Grey .y9-dialog-body .y9-dialog-content) {
  background: var(--el-color-primary-light-9);
}

.f-White {
  :deep(.y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-white);
  }

  :deep(.g-Grey .y9-dialog-body .y9-dialog-content) {
    background: var(--el-color-primary-light-9);
  }


}


</style>

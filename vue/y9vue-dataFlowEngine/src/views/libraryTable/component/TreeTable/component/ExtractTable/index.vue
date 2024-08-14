<script setup lang="ts">
import {useSettingStore} from '@/store/modules/settingStore';

import {computed, reactive, ref, onMounted} from 'vue';
import {currNode} from '@/views/libraryTable/component';
import {ExtractTableInfo, getExtractTable} from '@/api/libraryTable';
import {findByTypeList} from "@/api/dataSource";
import {getPage} from "@/views/libraryTable/component/DataSheet/data";

const tableHeight = ref(useSettingStore().getWindowHeight - 520);
let baseIdList=ref([])
onMounted(() => {
  getData()
  handleClickQuery()
});
const getData=async ()=>{
  let options=await findByTypeList({type:0})
  let arr=[]
  options.data.forEach((data)=>{
    let obj={label: "【"+data.baseType+"】"+data.baseName, value: data.id};
    arr.push(obj);
  })
  state.filterConfig.itemList[0].props.options = arr;
  if (currNode.value.id) {
    if (arr.some(obj => obj.value == currNode.value?.id)) {
      state.filterConfig.itemList[0].value = currNode.value?.id;
    } else {
      state.filterConfig.itemList[0].value = null
    }
  }
}
const state = reactive({
  dialogConfig: {
    show: false
  },
  baseIdList: [],
  loading: false,
  // 过滤 数值
  currSearchValue: {},
  //  表格的过滤 条件
  filterConfig: {
    filtersValueCallBack: (filters) => {
      console.log(filters, 'filters');
      //过滤值回调
      state.currSearchValue = filters;
    },
    fangDouTime:0,//防抖时间
    itemList: [
      {
        type: 'select',
        key: 'baseId',
        span: 9,
        value: '', //值
        label: computed(() => '数据源名称'),
        props: {
          type: 'select',
          options:baseIdList.value
          // render: () => {//text类型渲染的内容
          // render: () => {//text类型渲染的内容
          //   return h('span', '牛逼')
          // }
        }
      },
      {
        type: 'input',
        value: '',
        key: 'tableName',
        label: '表名称',
        labelWith: '120px',
        props: {
          placeholder: '请输入表名称'
        },
        span: 8
      },
      {
        type: 'slot',
        slotName: 'bnts',
        span: 7
      }
    ],
    showBorder: true
  },
  // 个人权限 表格的 配置条件
  tableConfig: {
    height: tableHeight,
    columns: [
      {
        type: 'index',
        title: computed(() => '序号'),
        fixed: 'left'
      },
      {
        title: computed(() => '数据表名称'),
        key: 'name'
      },
      // {
      //   title: computed(() => '表中文名称'),
      //   key: 'cname'
      // },
      {
        title: computed(() => '操作'),
        fixed: 'right',
        key: 'operation',
        slot: 'operation'
      }
    ],
    tableData: [],
    pageConfig:false
  }
});
let y9TableRef=ref()
// 搜索表数据
const handleClickQuery = async () => {
  // console.log(y9TableRef.value)
  // console.log(y9TableRef.value.elTableFilterRef?.y9FilterRef)
  // console.log('state.',state.currSearchValue)
  // if (!state.currSearchValue.baseId || !state.currSearchValue?.tableName) {
  //   ElMessage({
  //     type: 'error',
  //     message: '两个搜索条件都不能为空',
  //     offset: 65
  //   });
  // } else {
  //
  // }
  state.loading = true;
  let result = await getExtractTable({
    // page: state.tableConfig.pageConfig.currentPage,
    // size: state.tableConfig.pageConfig.pageSize,
      ...state.currSearchValue});
  if (result.code == 0) {
    // 赋值
    state.tableConfig.tableData = result.data;
    // state.tableConfig.pageConfig.total = result?.count;
  }
};
const onPageSizeChange = (pageSize) => {
  state.tableConfig.pageConfig.pageSize = pageSize;
  handleClickQuery();
};
const onCurrPageChange = (currPage) => {
  state.tableConfig.pageConfig.currentPage = currPage;
  handleClickQuery();
};

const handle=async (row)=>{
  console.log(row,555)
  let params={
    baseId:row.baseId,
    tableName:row.name
  }
  let res=await ExtractTableInfo(params)
  if (res.success == true) {
    ElNotification({
      title: ('成功'),
      message:('提取成功'),
      type: 'success',
      duration: 2000,
      offset: 80
    });
    handleClickQuery();
    getPage();
  }
}
</script>

<template>
  <y9Table
      ref="y9TableRef"
      v-loading="state.loading"
      :config="state.tableConfig"
      :filterConfig="state.filterConfig"
      @on-curr-page-change="onCurrPageChange"
      @on-page-size-change="onPageSizeChange"
  >
    <template v-slot:bnts>
      <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
      ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
      </el-button>
    </template>
    <template v-slot:operation="{ row }">
      <div class="operation">
        <div class="fields" @click="handle(row)">
          <i class="ri-edit-line"></i>
          <span class="text">{{ $t('提取') }}<span v-if="row.status==1">(已提取)</span></span>
        </div>
      </div>
    </template>
  </y9Table>
</template>

<style scoped lang="scss"></style>

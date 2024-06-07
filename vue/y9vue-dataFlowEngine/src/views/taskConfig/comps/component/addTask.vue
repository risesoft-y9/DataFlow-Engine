<script setup lang="ts">
import {ref, onMounted, reactive} from "vue";

const emits = defineEmits(['next', 'top', 'goBack', 'goBackSet','setTask','close'])
import {
  addTaskForm,
  addTaskFormRef, addTaskLoading,
  getTableFieldListAll,
  getTableListAll,
  globalData, goalTableForm,
  radioChange,
  tableSetForm
} from '../data'
import {getTableList} from "@/api/businessClassify";
import {getDataById, getFindAll} from "@/api/taskConfig";
import {setTreeData} from "@/utils/object";
import {findByTypeList} from "@/api/dataSource";
//去除警告
const other=()=>{
  emits('next');
  emits('top');
  emits('goBack');
  emits('goBackSet');
  emits('setTask');
  emits('close');
}
const state = reactive({
  treeData: [],
  defaultProps: {
    children: 'children',
    label: 'name',
  },
  dataSource: [],//数据源
})
const next = async () => {
  let validate = await addTaskFormRef.value.validate()
  if (validate) {
    emits('next');
  }
}
let loading=ref(true)
onMounted(() => {
  addTaskLoading.value=true
  setInitData()
  getData()
})
const setInitData = () => {
  addTaskFormRef?.value?.resetFields()
  //重置所有数据
  if (globalData.type == 'add') {
    for (let key in addTaskForm.tableData) {
      addTaskForm.tableData[key] = null;
    }
  } else {

  }

}
const getData = async () => {
  await getTree() //获取树形结构
  await getDataSource()
  if (globalData.type == 'edit') {
    await getDataAll()
  }
  setTimeout(()=>{
    addTaskLoading.value=false
  },800)
}
const getDataAll = async () => {
  let res = await getDataById({id: globalData.row.id})
  if (res) {
    let ret = res.data
    ret.taskConfigModel.sourceCloumn=ret.taskConfigModel?.sourceCloumn?.split(',')
    ret.taskConfigModel.targetCloumn=ret.taskConfigModel?.targetCloumn?.split(',')
    ret.taskConfigModel.updateField=ret.taskConfigModel?.updateField?.split(',')
    if(ret.taskConfigModel.precise){
      tableSetForm.tableData.radios=1 //精准切分
    }

    if(ret.taskConfigModel.tableNumber!=null&&ret.taskConfigModel.tableNumber!=''&&ret.taskConfigModel.tableNumber!=undefined){
      tableSetForm.tableData.radios=2 //平均切分
      tableSetForm.tableData.tableNumber=ret.taskConfigModel.tableNumber
    }

    if(ret.taskConfigModel.splitFactor!=null&&ret.taskConfigModel.splitFactor!=''&&ret.taskConfigModel.splitFactor!=undefined){
      tableSetForm.tableData.radios=3 //执行数切分
      tableSetForm.tableData.splitFactor=ret.taskConfigModel.splitFactor
    }

    //查询表格下拉
    await getTableListAll(ret.taskConfigModel.sourceId,ret.taskConfigModel.targetId)
    await getTableFieldListAll(ret.taskConfigModel.sourceTable,ret.taskConfigModel.targetTable) //获取字段详情
    await setOtherData(ret)

  }
}
const  setOtherData=(ret)=>{
  for (const key in ret) {
    for (const keyKey in addTaskForm.tableData) {
      if(keyKey==key){
        addTaskForm.tableData[keyKey]=ret[key]
      }
    }
    for (const keyKey in tableSetForm.tableData) {
      if(keyKey==key){
        tableSetForm.tableData[keyKey]=ret[key]
      }
    }
    for (const keyKey in goalTableForm.tableData) {
      if(keyKey==key){
        goalTableForm.tableData[keyKey]=ret[key]
      }
    }
  }
  for (const key in ret.taskConfigModel) {
    for (const keyKey in addTaskForm.tableData) {
      if(keyKey==key){
        addTaskForm.tableData[keyKey]=ret.taskConfigModel[key]
      }
    }
    for (const keyKey in tableSetForm.tableData) {
      if(keyKey==key){
        tableSetForm.tableData[keyKey]=ret.taskConfigModel[key]
      }
    }
    for (const keyKey in goalTableForm.tableData) {
      if(keyKey==key){
        goalTableForm.tableData[keyKey]=ret.taskConfigModel[key]
      }
    }
  }
  //设置differentField 和convertField
  if(ret?.convertField?.length==0||!ret?.convertField){
    goalTableForm.tableData.convertField=[{}]
  }
  if(ret?.differentField?.length==0||!ret?.differentField){
    goalTableForm.tableData.differentField=[{}]
  }
  if(ret?.dateField?.length==0||!ret?.dateField){
    goalTableForm.tableData.dateField=[{}]
  }
  globalData.allData=ret
}
const getDataSource = async () => {
  let res = await findByTypeList({type: 0})
  if (res) {
    state.dataSource = res.data
  }
}

const getTree = async () => {
  let res = await getFindAll()
  if (res) {
    if (res.data) {
      addTaskForm.treeData=res.data
      state.treeData = setTreeData(res.data)
    }
  }
}

const rules = {
  name: [
    {required: true, message: '请输入业务名称', trigger: ['blur', 'change']}
  ],
  businessId: [
    {required: true, message: '请选择业务分类', trigger: ['blur', 'change']}
  ],
  sourceId: [
    {required: true, message: '请选择数据源', trigger: ['blur', 'change']}
  ],
  targetId: [
    {required: true, message: '请选择目的源', trigger: ['blur', 'change']}
  ]
}
let ruleFormRef = ref(addTaskFormRef)

</script>
<template>
  <el-form
      v-loading="addTaskLoading"
      ref="ruleFormRef"
      :model="addTaskForm.tableData"
      :rules="rules">

    <el-descriptions border :column="1">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>任务名称</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="name">
          <el-input placeholder="请输入" v-model="addTaskForm.tableData.name"></el-input>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>业务分类</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="businessId">
          <el-tree-select
              node-key="id"
              check-strictly
              :props="state.defaultProps"
              v-model="addTaskForm.tableData.businessId"
              :data="state.treeData"
          />
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>描述</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item>
          <el-input placeholder="请输入" show-word-limit type="textarea"
                    v-model="addTaskForm.tableData.description"></el-input>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>选择数据源</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="sourceId">
          <el-radio-group v-model="addTaskForm.tableData.sourceId">
            <el-radio @change="radioChange('input')" v-for="item in state.dataSource" :label="item.id">
              【{{ item.baseType }}】{{ item.baseName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>选择目的源</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="targetId">
          <el-radio-group v-model="addTaskForm.tableData.targetId">
            <el-radio @change="radioChange('output')" v-for="item in state.dataSource" :label="item.id">
              【{{ item.baseType }}】{{ item.baseName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-descriptions-item>
    </el-descriptions>

  </el-form>
  <div class="btn">
    <el-button type="primary" @click="next" class="global-btn-main">下一步</el-button>
  </div>
</template>

<style scoped lang="scss">
.btn {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}


</style>

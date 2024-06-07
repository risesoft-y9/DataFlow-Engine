<script setup lang="ts">
import addTask from './component/addTask.vue'
import tableSet from './component/tableSet.vue'
import goalTable from './component/goalTable.vue'
import taskSet from './component/taskSet.vue'
import successTask from './component/successTask.vue'
import saveTask from '../../dispatch/comp/saveTask/index.vue'
import {shallowRef, watch} from 'vue'
import {nextTick, onMounted} from "vue";
import {
  addTaskForm,
  addTaskFormRef,
  goalTableForm, goalTableRef,
  tableSetForm,
  tableSetRef,
  taskSetForm
} from "@/views/taskConfig/comps/data";

const emits=defineEmits(['close'])
const active = ref(0)
let showTab = ref(0)
const next = (e) => {
  if (active.value++ > 4) active.value = 0
  showTab.value = active.value
}
const top = () => {
  active.value--
  showTab.value = active.value
}
const goBack = () => {
  console.log('点击返回')
  active.value = 0
  showTab.value = active.value
}
const goBackSet = () => {
  active.value = 5
  showTab.value = 3
}
watch(() => showTab.value, (newValue) => {
  if (newValue == 0) {
    componentName.value = addTask
  } else if (newValue == 1) {
    componentName.value = tableSet
  } else if (newValue == 2) {
    componentName.value = goalTable
  } else if (newValue == 3) {
    componentName.value = taskSet
  } else if (newValue == 4) {
    componentName.value = successTask
  }
}, {
  immediate: false
})
let setTaskShow=ref(false)
let taskInfo=ref()
const setTask=()=>{
  setTaskShow.value=true
}
const close=(type)=>{
  if(type==1){ //新增或者修改任务时查询列表
    emits('close',1)
  }else{ //设置定时任务关闭
    emits('close')
  }
}
let componentName = shallowRef(addTask)
</script>

<template>
  <div v-if="setTaskShow">
    <saveTask @close="close" :setTaskShow="setTaskShow"></saveTask>
  </div>
  <div v-else>
    <el-steps :active="active" finish-status="success" align-center>
      <el-step title="新建任务"/>
      <el-step title="源头表设置"/>
      <el-step title="目的表设置"/>
      <el-step title="任务设置"/>
      <el-step title="完成"/>
    </el-steps>
    <div class="main">
      <keep-alive>
        <component @close="close" :is="componentName" @setTask="setTask" @next="next" @top="top" @goBack="goBack" @goBackSet="goBackSet"></component>
      </keep-alive>
    </div>
  </div>

</template>

<style scoped lang="scss">
.main {
  margin-top: 40px;
}

:deep(.el-step__title.is-success) {
  color: var(--el-color-primary);
}

:deep(.el-step__head.is-success) {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}
</style>

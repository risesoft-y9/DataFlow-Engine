<script setup lang="ts">
import {reactive} from "vue";
import {getServiceByName, sendJob} from "@/api/dispatch";
import {ElNotification} from "element-plus";

const emits = defineEmits(['close'])
const props = defineProps({
  tableRow: {
    default: {},
    type: Object
  }
})
const state = reactive({
  form: {
    server: null
  },
  serveList: []
})
onMounted(() => {
  getData()
})
const getData = async () => {
  let environment = props.tableRow.environment
  let res = await getServiceByName({name: 'data-transfer', environment})
  if (res) {
    state.serveList = res.data
  }
}
const rules = {
  jobType: [
    {required: true, message: '请选择业务分类', trigger: ['blur', 'change']}
  ],
}
const startTask = async () => {
  let res = await sendJob({jobId: props.tableRow.id,server:state.form.server})
  if (res) {
    ElNotification({
      title: res?.success ? ('任务调度成功') : ('任务调度失败'),
      message: res?.msg,
      type: res?.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
    emits('close')
  }
}
const close = () => {
  emits('close')
}
let ruleFormRef = ref()
</script>

<template>
  <el-form
      ref="ruleFormRef"
      :model="state.form"
      :rules="rules">
    <el-descriptions border :column="1">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>指定服务器</span>
            <span class="y9-required-icon"></span>
          </div>
        </template>
        <el-form-item prop="environment">
          <el-select
              v-model="state.form.server"
              class="m-2"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in state.serveList"
                :key="item.name"
                :label="item.instanceId"
                :value="item.instanceId"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
    </el-descriptions>
  </el-form>
  <div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 25px;">
    <el-button type="primary" @click="startTask"><i class="ri-save-line"></i><span>执行</span></el-button>
    <el-button type="primary" @click="close"><i class="ri-close-line"></i><span>关闭</span></el-button>
  </div>
</template>

<style scoped lang="scss">

</style>
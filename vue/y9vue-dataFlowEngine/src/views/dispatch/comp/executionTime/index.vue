<script setup lang="ts">
import {nextExecutorTime} from "@/api/dispatch";
import {onMounted, reactive} from "vue";

const props = defineProps({
  tableRow: {
    default: {},
    type: Object
  }
})
let loading=ref(true)
onMounted(() => {
  getData()
})
const state = reactive({
  list: []
})
const getData = async () => {
  let res = await nextExecutorTime({jobId: props.tableRow.id})
  if (res) {
    state.list = res.data
  }
  loading.value=false
}
</script>

<template>
  <div class="main" v-loading="loading">
    <div v-if="state.list.length>0">
      <div v-for="item in state.list" class="font">{{ item }}</div>
    </div>
    <div v-else>
      <el-empty description="暂无数据"/>
    </div>
  </div>
</template>

<style scoped lang="scss">
.main {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.font {
  color: #606266;
}
</style>
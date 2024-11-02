<script setup lang="ts">
import {onMounted,reactive} from "vue";
import {getConsole} from "@/api/dispatch";

const props = defineProps({
  tableRow: {
    default: {},
  }
})
let loading=ref(true)
const state = reactive({
  data: ''
})
onMounted(() => {
  getData()
})
const getData = async () => {
  let res = await getConsole({
    id: props.tableRow.ID
  })
  if (res) {
    state.data = res.data
    state.data= state.data.replace(/\n/g, "<br>");
    loading.value=false
  }
}
</script>

<template>
  <div v-loading="loading" style="height: 100px" v-if="loading"></div>
 <div class="main">
   <div class="result">
     <div v-html="state.data" style="white-space: pre-wrap;"></div>
   </div>

 </div>
</template>


<style scoped lang="scss">
.main{
  //margin-left: -50px;
  //margin-top: -26px;
  //width: calc(100% + 100px);
  //height: calc(100% + 52px);
  //background: #fff;
  .result{
    //padding: 26px 50px;
  }
}
</style>
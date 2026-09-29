<script lang="ts" setup>
import {ref, watch} from 'vue'
import Seconds from './seconds/index.vue'
import Minutes from './minutes/index.vue'
import Hour from './hour/index.vue'
import Day from './day/index.vue'
import Month from './month/index.vue'
import Weeks from './weeks/index.vue'
import Years from './years/index.vue'

const props = defineProps({
  value: {
    type: String,
    required: false,
    default: () => "* * * * * ? *"
  }
})

watch(() => props.value, () => {
  cron.value = props.value.split(' ')
})
let showTab = ref(false)
onMounted(() => {
  console.log(props.value, 'props.value')
  cron.value = props.value.split(' ')
  showTab.value = true
})
const activeName = ref('seconds')
const emits = defineEmits(['change', 'tabChange'])

const cron = ref(['*', '*', '*', '*', '*', '?', ''])


const handleChange = (index: number, value: string) => {
  cron.value[index] = value
  console.log(cron.value,'cron555',index)
  if (cron.value[3] === "?" && cron.value[5] === "?") {
    ElNotification({
      title: '错误',
      message: '日期与星期不可以同时为“不指定”',
      type: 'error',
      duration: 2000,
      offset: 80
    });
  }
  if (cron.value[3] !== "?" && cron.value[5] !== "?") {
    console.log(cron.value,'cron555')
    ElNotification({
      title: '错误',
      message: '日期与星期必须有一个为“不指定”',
      type: 'error',
      duration: 2000,
      offset: 80
    });
  }
  emits('change', cron.value.join(' ').trim())
  let params = {
    type: activeName.value,
    value
  }
  emits('tabChange', params)
}

</script>

<template>
  <div v-if="showTab">
    <el-tabs v-model="activeName">
      <el-tab-pane label="秒" name="seconds">
        <seconds v-if="props" :value="cron[0]" @change="handleChange(0, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="分钟" name="minutes">
        <minutes :value="cron[1]" @change="handleChange(1, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="小时" name="Hour">
        <hour :value="cron[2]" @change="handleChange(2, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="日" name="day">
        <day :value="cron[3]" @change="handleChange(3, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="月份" name="month">
        <month :value="cron[4]" @change="handleChange(4, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="周（星期）" name="weeks">
        <weeks :value="cron[5]" @change="handleChange(5, $event)"/>
      </el-tab-pane>
      <el-tab-pane label="年份" name="years">
        <years :value="cron[6]" @change="handleChange(6, $event)"/>
      </el-tab-pane>
    </el-tabs>
  </div>

</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import {onMounted, reactive, ref, watch} from "vue";
import {Cycle} from "@/components/ve-cron/Cycle";
import {InitiationCycle} from "@/components/ve-cron/InitiationCycle";

const props = defineProps({
  value: {
    type: String,
    required: false,
    default: () => "*"
  }
})
const _value = ref('*')

watch(() => props.value, () => {
  _value.value = props.value
})
onMounted(() => {
  updateVal()
})
const updateVal = () => {
  if (!props.value) {
    return
  }
  if (props.value === '?') { //不指定
    radio.value = 2
  } else if (props.value.indexOf('-') !== -1) { // 2周期
    if (props.value.split('-').length === 2) {
      radio.value = 2
      _cycle.start = Number(props.value.split('-')[0])
      _cycle.end = Number(props.value.split('-')[1])
    }
  } else if (props.value.indexOf('/') !== -1) { // 3循环
    if (props.value.split('/').length === 2) {
      radio.value = 3
      _initiationCycle.initiation = Number(props.value.split('/')[0])
      _initiationCycle.cycle = Number(props.value.split('/')[1])
    }
  } else if (props.value.indexOf('*') !== -1) { // 1每
    radio.value = 1
  } else if (props.value.indexOf('L') !== -1) { // 6最后
    radio.value = 6
  } else if (props.value.indexOf('W') !== -1) { // 8工作日
    radio.value = 5
    // workingDay.value = props.value
  } else { // *
    radio.value = 4
    const stringArray = props.value.split(',');
    checkList.value = stringArray
  }
}
const radio = ref(1)

const _cycle = reactive<Cycle>({
  end: 2, start: 1
})

const _initiationCycle = reactive<InitiationCycle>({
  cycle: 1,
  initiation: 0
})

const checkList = ref([0])
const emits = defineEmits(['change'])

const handleChange = (value: number) => {
  switch (value) {
    case 1:
      checkList.value=[]
      _value.value = '*'
      break
    case 2:
      checkList.value=[]
      _value.value = `${_cycle.start}-${_cycle.end}`
      break
    case 3:
      checkList.value=[]
      _value.value = `${_initiationCycle.initiation}/${_initiationCycle.cycle}`
      break
    case 4:
      if(checkList.value.length==0){
        checkList.value=['0']
      }
      _value.value = checkList.value.sort((a, b) => a - b).join(',')
      break
  }
  emits('change', _value.value)
}

const handleChangeCheckbox = () => {
  radio.value=4
  if (radio.value === 4) {
    handleChange(4)
  }
}
</script>

<template>
  <el-radio-group v-model="radio" @change="handleChange">
    <el-radio :label="1">每分钟 允许的通配符[, - * /]</el-radio>
    <el-radio :label="2">
      <el-space>
        <el-text>周期 从</el-text>
        <el-input-number
            v-model="_cycle.start"
            :max="59"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>-</el-text>
        <el-input-number
            v-model="_cycle.end"
            :max="59"
            :min="2"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>分钟</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="3">
      <el-space>
        <el-text>循环 从</el-text>
        <el-input-number
            v-model="_initiationCycle.initiation"
            :max="59"
            :min="0"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>分钟开始,每</el-text>
        <el-input-number
            v-model="_initiationCycle.cycle"
            :max="59"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>分钟执行一次</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="4">指定</el-radio>
    <el-checkbox-group v-model="checkList" @change="handleChangeCheckbox">
      <el-checkbox v-for="index in 60" :key="index" :label="(index-1).toString()"/>
    </el-checkbox-group>
  </el-radio-group>
</template>

<style lang="scss" scoped>
.el-radio {
  width: 100%;
}

.el-input-number {
  width: 80px;
}

.el-checkbox {
  width: 40px;
  margin-right: 10px;
}
</style>

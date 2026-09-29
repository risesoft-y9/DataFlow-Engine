<script lang="ts" setup>
import {reactive, ref, watch, onMounted} from "vue";

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
  } else if (props.value.indexOf('-') !== -1) { // 3周期
    if (props.value.split('-').length === 2) {
      radio.value = 3
      _cycle.start = Number(props.value.split('-')[0])
      _cycle.end = Number(props.value.split('-')[1])
    }
  } else if (props.value.indexOf('/') !== -1) { // 4循环
    if (props.value.split('/').length === 2) {
      radio.value = 4
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
    radio.value = 7
    const stringArray = props.value.split(',');
    checkList.value = stringArray
  }
}
const radio = ref(1)

const _cycle = reactive({
  end: 2, start: 1
})

const _initiationCycle = reactive({
  cycle: 1,
  initiation: 1
})
const workingDay = ref(1)
// const checkList = ref([1])
const checkList = ref([])

const emits = defineEmits(['change'])

const handleChange = (value: number) => {
  switch (value) {
    case 1:
      checkList.value=[]
      _value.value = '*'
      break
    case 2:
      checkList.value=[]
      _value.value = '?'
      break
    case 3:
      checkList.value=[]
      _value.value = `${_cycle.start}-${_cycle.end}`
      break
    case 4:
      checkList.value=[]
      _value.value = `${_initiationCycle.initiation}/${_initiationCycle.cycle}`
      break
    case 5:
      checkList.value=[]
      _value.value = `${workingDay.value}W`
      break
    case 6:
      checkList.value=[]
      _value.value = `L`
      break
    case 7:
      if(checkList.value.length==0){
        checkList.value=['1']
      }
      _value.value = checkList.value.sort((a, b) => a - b).join(',')
      break
  }
  emits('change', _value.value)
}

const handleChangeCheckbox = () => {
  radio.value = 7
  if (radio.value === 7) {
    handleChange(7)
  }
}
</script>

<template>
  <el-radio-group v-model="radio" @change="handleChange">
    <el-radio :label="1">每日 允许的通配符[, - * / L W]</el-radio>
    <el-radio :label="2">不指定</el-radio>
    <el-radio :label="3">
      <el-space>
        <el-text>周期 从</el-text>
        <el-input-number
            v-model="_cycle.start"
            :max="31"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>-</el-text>
        <el-input-number
            v-model="_cycle.end"
            :max="31"
            :min="2"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>日</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="4">
      <el-space>
        <el-text>循环 从</el-text>
        <el-input-number
            v-model="_initiationCycle.initiation"
            :max="31"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>日开始,每</el-text>
        <el-input-number
            v-model="_initiationCycle.cycle"
            :max="31"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>天执行一次</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="5">
      <el-space>
        <el-text>每月</el-text>
        <el-input-number
            v-model="workingDay"
            :max="31"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>号最近的那个工作日</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="6">本月最后一天</el-radio>
    <el-radio :label="7">指定</el-radio>
    <el-checkbox-group v-model="checkList" @change="handleChangeCheckbox">
      <el-checkbox v-for="index in 31" :key="index" :label="index.toString()"/>
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

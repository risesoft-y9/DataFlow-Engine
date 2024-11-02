<script lang="ts" setup>
import {onMounted, reactive, ref, watch} from "vue";
import {Cycle} from "@/components/ve-cron/Cycle";
import {InitiationCycle} from "@/components/ve-cron/InitiationCycle";

const props = defineProps({
  value: {
    type: String,
    required: false,
    default: () => "?"
  }
})
const _value = ref('?')

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
      _cycle.start =Number(props.value.split('-')[0])
      _cycle.end = Number(props.value.split('-')[1])
    }
  } else if (props.value.indexOf('/') !== -1) { // 7循环
    if (props.value.split('/').length === 2) {
      radio.value = 7
      _initiationCycle.loopStart = Number(props.value.split('/')[0])
      _initiationCycle.loopEnd = Number(props.value.split('/')[1])
    }
  } else if (props.value.indexOf('*') !== -1) { // 1每
    radio.value = 1
  }else if (props.value.indexOf("#") !== -1) {
    // 7指定周
    if (props.value.split("#").length === 2) {
      radio.value = 4
      _initiationCycle.initiation = Number(props.value.split("#")[0])
      _initiationCycle.cycle =Number(props.value.split("#")[1])
    }// workingDay.value = props.value
  }else if (props.value.indexOf("L") !== -1) {
    // 6最后
    radio.value = 5
  } else { // *
    radio.value = 6
    const stringArray = props.value.split(',');
    checkList.value = stringArray
  }
}
const radio = ref(2)

const _cycle = reactive<Cycle>({
  end: 2, start: 1,
  loopEnd: 1, loopStart: 1
})

const _initiationCycle = reactive<InitiationCycle>({
  cycle: 1,
  initiation: 1
})

const workingDay = ref(1)

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
      _value.value = `${workingDay.value}L`
      break
    case 6:
      if(checkList.value.length==0){
        checkList.value=['1']
      }
      _value.value = checkList.value.sort((a, b) => a - b).join(',')
      break
    case 7:
      checkList.value=[]
      _value.value = `${_cycle.loopStart}/${_cycle.loopEnd}`
      break
  }
  emits('change', _value.value)
}

const handleChangeCheckbox = () => {
  if (radio.value === 6) {
    handleChange(6)
  }
}
</script>

<template>
  <el-radio-group v-model="radio" @change="handleChange">
    <el-radio :label="1">每周 允许的通配符[, - * / L W]</el-radio>
    <el-radio :label="2">不指定</el-radio>
    <el-radio :label="3">
      <el-space>
        <el-text>周期 从星期</el-text>
        <el-input-number
            v-model="_cycle.start"
            :max="7"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>-</el-text>
        <el-input-number
            v-model="_cycle.end"
            :max="7"
            :min="2"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
      </el-space>
    </el-radio>
    <el-radio :label="7">
      <el-space>
        <el-text>循环 从星期</el-text>
        <el-input-number
            v-model="_cycle.loopStart"
            :max="7"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>开始,每</el-text>
        <el-input-number
            v-model="_cycle.loopEnd"
            :max="7"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>天执行一次</el-text>
      </el-space>
    </el-radio>
    <el-radio :label="4">
      <el-space>
        <el-text>指定周 本月第</el-text>
        <el-input-number
            v-model="_initiationCycle.initiation"
            :max="4"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>周,星期</el-text>
        <el-input-number
            v-model="_initiationCycle.cycle"
            :max="7"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
      </el-space>
    </el-radio>
    <el-radio :label="5">
      <el-space>
        <el-text>本月最后一个星期</el-text>
        <el-input-number
            v-model="workingDay"
            :max="7"
            :min="1"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
      </el-space>
    </el-radio>
    <el-radio :label="6">指定</el-radio>
    <el-checkbox-group v-model="checkList" @change="handleChangeCheckbox">
      <el-checkbox v-for="index in 7" :key="index" :label="index.toString()"/>
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

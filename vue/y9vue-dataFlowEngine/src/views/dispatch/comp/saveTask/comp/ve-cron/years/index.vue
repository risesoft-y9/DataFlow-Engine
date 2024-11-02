<script lang="ts" setup>
import {onMounted, reactive, ref, watch} from "vue";
import {Cycle} from "@/components/ve-cron/Cycle";

const props = defineProps({
  value: {
    type: String,
    required: false,
    default: () => ""
  }
})
const _value = ref('')

watch(() => props.value, () => {
  _value.value = props.value
})

const radio = ref(1)

const _cycle = reactive<Cycle>({
  end: 2024, start: 2025
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
  }
}
const emits = defineEmits(['change'])

const handleChange = (value: number) => {
  switch (value) {
    case 1:
      _value.value = ''
      break
    case 2:
      _value.value = '?'
      break
    case 3:
      _value.value = `${_cycle.start}-${_cycle.end}`
      break
  }
  emits('change', _value.value)
}
</script>

<template>
  <el-radio-group v-model="radio" @change="handleChange">
    <el-radio :label="1">每年</el-radio>
    <el-radio :label="2">不指定 允许的通配符[, - * /] 非必填</el-radio>
    <el-radio :label="3">
      <el-space>
        <el-text>周期 从</el-text>
        <el-input-number
            v-model="_cycle.start"
            :max="3000"
            :min="2013"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
        <el-text>-</el-text>
        <el-input-number
            v-model="_cycle.end"
            :max="3000"
            :min="2014"
            controls-position="right"
            size="small"
            @change="handleChange(radio)"/>
      </el-space>
    </el-radio>
  </el-radio-group>
</template>

<style lang="scss" scoped>
.el-radio {
  width: 100%;
}

.el-input-number {
  width: 100px;
}

.el-checkbox {
  width: 40px;
  margin-right: 10px;
}
</style>

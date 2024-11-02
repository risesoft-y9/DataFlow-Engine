<template>
  <el-tooltip :content="tipText?.toString()" placement="top" style="width: 100%" :disabled="disabled" popper-class="tooltips">
    <div
        class="sllipsis-wraper"
        :style="{ '-webkit-line-clamp': lineClamp,
                'overflow': 'hidden',
                'text-overflow': 'ellipsis',
                'display':' -webkit-box',
                '-webkit-box-orient': 'vertical ',
                'word-break': 'break-all',

      }"
        @mouseenter="mouseenter($event)"
        @mouseleave="mouseleave($event)"
    >
      <slot>{{ tipText }}</slot>
    </div>
  </el-tooltip>
</template>

<script>
export default {
  name: 'Ellipsis',
  props: {
    lineClamp: {
      // 展示几行
      type: Number,
      default: 1
    },
    tipText: {
      // 提示值
      type: [Number, String],
      default: ''
    }
  },
  data() {
    return {
      disabled: true

    }
  },
  computed: {},
  created() {
  },
  methods: {
    mouseenter(e) {
      // 鼠标移入
      if (!this.tipText.length) return
      let target = e.target
      let containerHeight = target.clientHeight + 1
      let textHeight = target.scrollHeight

      if (textHeight > containerHeight) {
        // 溢出
        this.disabled = false
      } else {
        // 未溢出
        this.disabled = true
      }
    },
    mouseleave(e) {
      // 鼠标移开
      this.disabled = true
    }

  }
}
</script>

<style lang="scss">
.tooltips {
  max-width: 50vw;
}
</style>




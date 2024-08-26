<template>
    <el-form ref="paramsForm" label-width="100px">
        <template v-for="item of params">
            <el-form-item :label="item.paramName">
                <el-input v-model="item.value" clearable></el-input>
            </el-form-item>
        </template>
        <el-form-item label="返回结果">
            <el-input v-model="resData" :readonly="false" resize="none" rows="5" type="textarea" />
        </el-form-item>
    </el-form>
</template>
<script lang="ts" setup>
    import { defineProps, onMounted, reactive } from 'vue';

    const props = defineProps({
        row: {
            type: Object,
            default: () => {
                return {};
            }
        },
        params: {
            type: Array,
            default: () => {
                return [];
            }
        }
    });
    const data = reactive({
        paramsForm: {},
        resData: ''
    });

    let { paramsForm, resData } = toRefs(data);

    defineExpose({
        resData
    });

    onMounted(() => {});
</script>

<style lang="scss"></style>

<script setup lang="ts">
import { globalDataTask } from '@/views/dispatch/comp/saveTask/data';

const emits = defineEmits(['next', 'top', 'goBack', 'goBackSet', 'setTask', 'close']);
//去除警告
const other = () => {
    emits('next');
    emits('top');
    emits('goBack');
    emits('goBackSet');
    emits('setTask');
    emits('close');
};

const goBack = () => {
    emits('goBack');
};
const setTimeTask = () => {
    globalDataTask.type = 'add'; //新增定时任务
    emits('setTask');
};
</script>

<template>
    <el-col>
        <el-result icon="success" title="成功">
            <template #extra>
                <div style="display: flex">
                    <div>
                        <el-button @click="goBack" type="primary">查看任务</el-button>
                    </div>
                    <div style="margin-left: 20px" v-if="globalData.type == 'add'">
                        <el-button @click="setTimeTask" type="primary">设置定时任务</el-button>
                    </div>
                </div>
            </template>
        </el-result>
    </el-col>
</template>

<style scoped lang="scss">
</style>

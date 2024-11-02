<script setup lang="ts">
    import { onMounted } from 'vue';
    import { environmentAll, getData, initChat, initSchedulingInfo } from './data';

    onMounted(() => {
        window.onresize = function () {
            initSchedulingInfo();
        };
    });
</script>

<template>
    <div id="schedulingInfo" class="main"></div>
</template>

<style scoped lang="scss">
    .main {
        width: 100%;
        height: 450px;
    }
</style>

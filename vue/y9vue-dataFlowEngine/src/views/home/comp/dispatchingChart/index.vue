<script setup lang="ts">
    import { showJobView } from '@/api/dispatch';
    import { environmentAll, getData, initChat } from './data';
    import { onMounted } from 'vue';

    onMounted(() => {
        window.onresize = function () {
            initChat();
        };
    });
</script>

<template>
    <div id="main" class="main"></div>
</template>

<style scoped lang="scss">
    .main {
        width: 100%;
        height: 450px;
    }
</style>

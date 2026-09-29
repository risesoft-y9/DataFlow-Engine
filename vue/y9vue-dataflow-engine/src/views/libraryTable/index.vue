<template>
    <TreeTable></TreeTable>
</template>
<script lang="ts" setup>
import TreeTable from './component/TreeTable/index.vue';
import { onMounted } from 'vue';
import { currNode } from '@/views/libraryTable/component';
onMounted(() => {
    currNode.value = {};
});
</script>
<style scoped lang="scss">
.table-box {
    display: flex;
}
</style>

<template>
    <fixedTreeModule ref="fixedTreeRef" :showNodeDelete="false" :treeApiObj="treeApiObj" @onNodeClick="onNodeClick">
        <template #treeHeaderRight> </template>

        <!-- 右边卡片 -->
        <template v-slot:rightContainer v-if="currNode.id">
            <div>
                <y9Card :title="`${currNode.name}`">
                    <userAuthTable :currNode="currNode"></userAuthTable>
                </y9Card>
            </div>
        </template>
    </fixedTreeModule>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { reactive, ref } from 'vue';
    import { getRolesList } from '@/api/systemManagement/roleManagement';
    import userAuthTable from './comps/userAuthTable.vue';
    const loading = ref(false);
    //固定树组件实例
    const fixedTreeRef = ref(null);
    //当前节点数据
    const currNode = ref({});

    //tree接口对象
    const treeApiObj = reactive({
        topLevel: async () => {
            let result = await getRolesList({ pageNo: 1, pageSize: 20 });
            return result.data.content;
        },
        flag: 'RoleTable'
    });

    //点击节点时
    const onNodeClick = (node) => {
        currNode.value = node;
    };
</script>

<style lang="scss" scoped>
    :deep(.y9-filter) {
        .y9-filter-item {
            .el-input {
                .el-input__wrapper {
                    box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color)) inset !important;
                }
            }
        }
    }
</style>

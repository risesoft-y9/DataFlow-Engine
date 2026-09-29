<script setup lang="ts">
import { treeApiObj, onDeleteTree, onNodeClick, currNode, onRefreshTree } from '../index';
import { reactive, onMounted, inject } from 'vue';
import { ElMessage } from 'element-plus';
import DataSheet from '../DataSheet/index.vue';
import { useI18n } from 'vue-i18n';
import ExtractTable from './component/ExtractTable/index.vue';
import EditorForm from '@/views/libraryTable/component/DataSheet/component/EditorForm.vue';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');
const state = reactive({
    dialogConfig: {
        show: false
    },
    addConfig: {
        show: false
    },
    row: {}
});
const extractTable = () => {
    Object.assign(state.dialogConfig, {
        show: true,
        okText: false,
        cancelText: false,
        width: '55%',
        // loading: true,
        title: '提取表'
    });
};

const addTable = (row) => {
    if (row.baseType == 'elasticsearch') {
        ElMessage({
            type: 'error',
            message: t('暂不支持'),
            offset: 65
        });
        return;
    }
    state.row = row;
    Object.assign(state.addConfig, {
        show: true,
        okText: false,
        cancelText: false,
        showFooter: false,
        width: '60%',
        // loading: true,
        title: '新增表'
    });
};

const close = () => {
    state.addConfig.show = false;
};
</script>

<template>
    <y9Dialog v-model:config="state.dialogConfig">
        <ExtractTable></ExtractTable>
    </y9Dialog>
    <y9Dialog v-model:config="state.addConfig" :okText="true" appendToBody class="newOrModifyTable">
        <EditorForm :row="state.row" :type="'add'" @close="close"></EditorForm>
    </y9Dialog>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        :hiddenSearch="true"
        :showNodeDelete="false"
        :lazy="false"
        @onDeleteTree="onDeleteTree"
        @onNodeClick="onNodeClick"
    >
        <template #treeHeaderRight="treeHeaderRight">
            <el-button
                @click="extractTable"
                class="global-btn-main"
                type="primary"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                :size="fontSizeObj.buttonSize"
            >
                <i class="ri-add-line"></i>{{ $t('提取表') }}
            </el-button>
        </template>
        <template #actions="actions">
            <div @click="addTable(actions.item)" class="add" v-if="actions.item.$level !== 1">
                <i class="ri-add-line"></i>
                <span class="text">{{ $t('新增表') }}</span>
            </div>
        </template>
        <template #rightContainer>
            <DataSheet></DataSheet>
        </template>
    </fixedTreeModule>
</template>

<style scoped lang="scss">
.add {
    display: flex;
    align-items: center;
    .text {
        color: var(--el-color-primary);
    }
}
.newOrModifyTable .el-dialog__body {
    padding-bottom: -10px;
}
.y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-footer {
    padding: 0;
}
</style>

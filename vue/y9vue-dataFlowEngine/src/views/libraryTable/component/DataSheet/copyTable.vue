<script setup lang="ts">
import { useSettingStore } from '@/store/modules/settingStore';

import { computed, reactive, ref, onMounted } from 'vue';
import { currNode } from '@/views/libraryTable/component';
import { copyTableInfo, getExtractTable } from '@/api/libraryTable';
import { findByTypeList } from '@/api/dataSource';
import { getPage } from '@/views/libraryTable/component/DataSheet/data';
import { ElMessage, ElNotification } from 'element-plus';

const tableHeight = ref(useSettingStore().getWindowHeight - 520);
let baseIdList = ref([]);
onMounted(() => {
    getData();
    handleClickQuery();
});
const getData = async () => {
    let options = await findByTypeList({ type: 0 });
    let arr = [];
    options.data.forEach((data) => {
        if (currNode.value.id) {
            if (data.id != currNode.value?.id) {
                let obj = { label: '【' + data.baseType + '】' + data.baseName, value: data.id };
                arr.push(obj);
            }
        }
    });
    state.filterConfig.itemList[0].props.options = arr;
};
const state = reactive({
    dialogConfig: {
        show: false
    },
    baseIdList: [],
    loading: false,
    // 过滤 数值
    currSearchValue: {},
    //  表格的过滤 条件
    filterConfig: {
        filtersValueCallBack: (filters) => {
            //过滤值回调
            state.currSearchValue = filters;
        },
        fangDouTime: 0, //防抖时间
        itemList: [
            {
                type: 'select',
                key: 'baseId',
                span: 9,
                value: '', //值
                label: computed(() => '数据源名称'),
                props: {
                    type: 'select',
                    options: baseIdList.value
                }
            },
            {
                type: 'input',
                value: '',
                key: 'tableName',
                label: '表名称',
                labelWith: '120px',
                props: {
                    placeholder: '请输入表名称'
                },
                span: 8
            },
            {
                type: 'slot',
                slotName: 'bnts',
                span: 7
            }
        ],
        showBorder: true
    },
    // 个人权限 表格的 配置条件
    tableConfig: {
        height: tableHeight,
        columns: [
            {
                type: 'index',
                title: computed(() => '序号'),
                fixed: 'left'
            },
            {
                title: computed(() => '数据表名称'),
                key: 'name'
            },
            // {
            //   title: computed(() => '表中文名称'),
            //   key: 'cname'
            // },
            {
                title: computed(() => '操作'),
                fixed: 'right',
                key: 'operation',
                slot: 'operation'
            }
        ],
        tableData: [],
        pageConfig: false
    }
});
let y9TableRef = ref();
// 搜索表数据
const handleClickQuery = async () => {
    state.loading = true;
    let result = await getExtractTable({
        ...state.currSearchValue
    });
    if (result.code == 0) {
        let arr = [];
        result.data.forEach((data) => {
            if (data.status == 1) {
                arr.push(data);
            }
        });
        // 赋值
        state.tableConfig.tableData = arr;
    }
};

const handle = async (row) => {
    let params = {
        baseId: row.baseId,
        tableName: row.name,
        id: currNode.value?.id
    };
    let res = await copyTableInfo(params);
    if (res.success) {
        ElMessage({ type: 'success', message: res.msg, offset: 65 });
        handleClickQuery();
        getPage();
    } else {
        ElMessage({ message: res.msg, type: 'error', offset: 65 });
    }
};
</script>

<template>
    <y9Table
        ref="y9TableRef"
        v-loading="state.loading"
        :config="state.tableConfig"
        :filterConfig="state.filterConfig"
    >
        <template v-slot:bnts>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
            </el-button>
        </template>
        <template v-slot:operation="{ row }">
            <div class="operation">
                <div class="fields" @click="handle(row)">
                    <i class="ri-edit-line"></i>
                    <span class="text">{{ $t('复制') }}</span>
                </div>
            </div>
        </template>
    </y9Table>
</template>

<style scoped lang="scss"></style>

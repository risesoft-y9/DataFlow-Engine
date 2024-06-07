<template>
    <div class="options">
        <el-button type="primary" @click="handleClickData">提交</el-button>
    </div>
    <y9Table
        v-model:selectedVal="selectedCheckboxVal"
        :config="tableConfig"
        uniqueIdent="JOBTYPE"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
    </y9Table>
</template>

<script lang="ts" setup>
    import { computed, ref, watch, onMounted } from 'vue';
    import { getTableList } from '@/api/businessClassify/index';
    import { getStoragePageSize } from '@/utils/index';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    const props = defineProps({
        selectData: {
            type: Object,
            default: () => []
        }
    });

    const emits = defineEmits(['save-list']);

    // 表格列表配置
    let tableConfig = ref({
        loading: false,
        border: false,
        rowKey: 'name',
        headerBackground: true,
        columns: [
            {
                type: 'selection',
                width: 60
            },
            {
                type: 'index',
                title: computed(() => t('序号')),
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('名称')),
                key: 'name'
            },
            {
                title: computed(() => t('时间')),
                key: 'createTime'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('JOBTYPE', 5),
            total: 0
        }
    });

    let selectedCheckboxVal = ref<any>([]);
    watch(
        () => props.selectData,
        async (newList) => {
            await initTableData();
            selectedCheckboxVal.value = newList;
        },
        {
            deep: true,
            immediate: true
        }
    );

    // init 数据
    async function initTableData() {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize
        };

        // console.log(params, '9999');

        // 请求接口
        let result = await getTableList(params);

        if (result.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = result.rows;
            tableConfig.value.pageConfig.total = result.total;
        }

        tableConfig.value.loading = false;
    }

    // 分页操作
    function onCurrentChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        initTableData();
    }

    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        initTableData();
    }

    // 返回数据
    function handleClickData() {
        let dataList = selectedCheckboxVal.value?.map((item) => {
            return { name: item.name, id: item.id };
        });
        emits('save-list', dataList);
    }
</script>

<style lang="scss" scoped>
    .options {
        :deep(.el-button) {
            margin-bottom: 10px;
        }
    }
</style>

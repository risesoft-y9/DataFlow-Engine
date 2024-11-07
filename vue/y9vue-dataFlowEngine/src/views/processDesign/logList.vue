<template>
    <y9Table
        :config="tableConfig"
        uniqueIdent="logList"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
    <template #message="{ row, column, index }">
        <div class="fields" style="cursor: pointer;color:var(--el-color-primary)" @click="handle(row)">
            <span>{{row.message}}</span>
        </div>
    </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfigLog" >
        <result :tableRow="tableRow" v-if="dialogConfigLog.show" ></result>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, ref, onMounted, h } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getStoragePageSize } from '@/utils/index';
    import { getLogList } from '@/api/processAdmin/processModel';
    import result from '../dispatch/comp/result/index.vue'
    import moment from 'moment';
    const { t } = useI18n();

    const props = defineProps({
        arrangeId: {
            type: String,
            default: ''
        }
    });

    let tableRow = ref({});

    // 表格列表配置
    let tableConfig = ref<any>({
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('logList', 10),
            total: 0
        },
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            // {
            //     type: 'index',
            //     width: 100,
            //     fixed: 'left',
            //     title: computed(() => t('序号'))
            // },
            {
                title: computed(() => t('执行标识符')),
                width: 120,
                key: 'identifier'
            },
            {
                title: computed(() => t('任务名称')),
                width: 300,
                key: 'jobName'
            },
            {
                title: computed(() => t('执行结果')),
                key: 'message',
                slot: 'message'
            },
            {
                title: computed(() => t('执行时间')),
                width: 200,
                key: 'createTime',
                render(row) {
                    return h('div', {}, moment(row.createTime).format('YYYY-MM-DD HH:mm:ss'));
                }
            },
        ],
        tableData: []
    });

    onMounted(() => {
        initTableData();
    });

    // init表格数据
    async function initTableData() {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            id: props.arrangeId
        };

        // 请求接口
        let result = await getLogList(params);
        if (result.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = result.rows;
            tableConfig.value.pageConfig.total = result.total;
        }

        tableConfig.value.loading = false;
    }

    const dialogConfigLog = ref({
        show: false,
        showFooter: false,
    });

    const handle = (row) => {
        if(row.logId != '') {
            tableRow.value.ID = row.logId;
            Object.assign(dialogConfigLog.value, {
                show: true,
                title: "日志",
                width: '50%',
                okText: false,
                cancelText: false,
                appendToBody:true
            })
        }
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
    
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';

    .fields {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 5px;
    }
</style>

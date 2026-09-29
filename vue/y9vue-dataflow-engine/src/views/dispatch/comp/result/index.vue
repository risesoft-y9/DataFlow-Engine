<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { getConsole, getLogByError } from '@/api/dispatch';
import moment from 'moment';
import { ElNotification } from 'element-plus';

const props = defineProps({
    tableRow: {
        default: {}
    }
});

let loading = ref(true);
const activeName = ref('first');
const state = reactive({
    data: ''
});

// 表格列表配置
let tableConfig = ref({
    pageConfig: {
        currentPage: 1, //当前页数，支持 v-model 双向绑定
        pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
        total: 0,
        pageSizeOpts: [10, 15, 30, 60, 120, 240]
    },
    border: false,
    headerBackground: true,
    columns: [
        {
            type: 'index',
            width: 80,
            fixed: 'left',
            title: '序号'
        },
        {
            title: '数据内容',
            key: 'record'
        },
        {
            title: '异常信息',
            key: 'msg'
        },
        {
            title: '执行时间',
            key: 'date',
            width: 170,
            render(row) {
                if (row.date) {
                    return moment(row.date).format('YYYY-MM-DD HH:mm:ss');
                }
            }
        }
    ],
    tableData: []
});

onMounted(() => {
    getData();
    getLogData();
});

const getData = async () => {
    let res = await getConsole({
        id: props.tableRow.ID
    });
    if (res) {
        state.data = res.data;
        state.data = state.data.replace(/\n/g, '<br>');
        loading.value = false;
    }
};

const getLogData = async () => {
    let params = {
        jobId: props.tableRow.ID,
        page: tableConfig.value.pageConfig.currentPage,
        size: tableConfig.value.pageConfig.pageSize
    };

    // 请求接口
    let res = await getLogByError(params);
    if (res.code == 0) {
        // 对返回的接口数据进行赋值与处理
        tableConfig.value.tableData = res.rows;
        tableConfig.value.pageConfig.total = res.total;
    } else {
        tableConfig.value.tableData = [];
        tableConfig.value.pageConfig.total = 0;
        ElNotification({
            title: '提示',
            message: res?.msg,
            type: 'error',
            duration: 2000,
            offset: 80
        });
    }
}

// 分页操作
function onCurrentChange(currPage) {
    tableConfig.value.pageConfig.currentPage = currPage;
    getLogData();
}

function onPageSizeChange(pageSize) {
    tableConfig.value.pageConfig.pageSize = pageSize;
    getLogData();
}
</script>

<template>
    <div v-loading="loading" style="height: 100px" v-if="loading"></div>
    <div class="main">
        <el-tabs v-model="activeName">
            <el-tab-pane label="日志信息" name="first">
                <div v-html="state.data" style="white-space: pre-wrap"></div>
            </el-tab-pane>
            <el-tab-pane label="脏数据" name="second">
                <!-- 表格 -->
                <y9Table
                    :config="tableConfig"
                    @on-curr-page-change="onCurrentChange"
                    @on-page-size-change="onPageSizeChange"
                >
                </y9Table>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<style scoped lang="scss">

</style>
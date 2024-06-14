<template>
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="authManagement"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
        </template>
    </y9Table>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { computed, ref, watch } from 'vue';
    import { searchUsersList, userEmpower, revokeUserEmpower } from '@/api/systemManagement/authManagement';
    import { getStoragePageSize } from '@/utils/index';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    const props = defineProps({
        currNode: {
            type: Object,
            default: () => {}
        }
    });

    // 全局loading
    let loading = ref(false);

    // 搜索的filter条件变量
    let filterData = ref({
        isNot: false
    });

    // 存放解除授权或授权的 判断
    let isNotFlag = ref(false);

    // 表格列表配置
    let tableConfig = ref({
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'index',
                title: computed(() => t('序号')),
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('用户名')),
                key: 'userName',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('账号')),
                key: 'account',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                render: (row) => {
                    let auth = [
                        h(
                            'span',
                            {
                                onclick: async () => {
                                    // 用户id row.id
                                    // 角色id props.currNode.id
                                    ElMessageBox.confirm(`${t('是否确定给用户授权')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            try {
                                                let result = await userEmpower({
                                                    roleId: props.currNode.id,
                                                    userId: row.id
                                                });
                                                if (result.code == 0) {
                                                    initTableData(props.currNode.id);
                                                }
                                                ElNotification({
                                                    title: result.success ? t('授权成功') : t('授权失败'),
                                                    message: result.msg,
                                                    type: result.success ? 'success' : 'error',
                                                    duration: 2000,
                                                    offset: 80
                                                });
                                            } catch (err) {}

                                            loading.value = false;
                                        })
                                        .catch(() => {
                                            ElMessage({
                                                type: 'info',
                                                message: t('已取消授权'),
                                                offset: 65
                                            });
                                        });
                                }
                            },
                            t('授权')
                        )
                    ];
                    let revokeAuth = [
                        h(
                            'span',
                            {
                                onclick: async () => {
                                    ElMessageBox.confirm(`${t('是否确定给用户解除授权')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            try {
                                                let result = await revokeUserEmpower({
                                                    roleId: props.currNode.id,
                                                    userId: row.id
                                                });
                                                if (result.code == 0) {
                                                    initTableData(props.currNode.id);
                                                }
                                                ElNotification({
                                                    title: result.success ? t('解除授权成功') : t('解除授权失败'),
                                                    message: result.msg,
                                                    type: result.success ? 'success' : 'error',
                                                    duration: 2000,
                                                    offset: 80
                                                });
                                            } catch (err) {}

                                            // console.log('0000');

                                            loading.value = false;
                                        })
                                        .catch(() => {
                                            ElMessage({
                                                type: 'info',
                                                message: t('已取消解除授权'),
                                                offset: 65
                                            });
                                        });
                                },
                                style: {
                                    margin: '0 10px'
                                }
                            },
                            t('取消授权')
                        )
                    ];
                    return isNotFlag.value ? auth : revokeAuth;
                }
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('authManagement', 15),
            pageSizeOpts: [10, 15, 30, 60, 120, 240],
            total: 0
        }
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'select',
                key: 'isNot',
                value: false,
                label: computed(() => t('授权用户')),
                props: {
                    options: [
                        { label: '是', value: false },
                        { label: '否', value: true }
                    ],
                    clearable: false
                }
            },
            {
                type: 'input',
                key: 'userName',
                label: computed(() => t('用户名'))
            },
            {
                type: 'input',
                key: 'account',
                label: computed(() => t('账号'))
            },
            {
                type: 'slot',
                slotName: 'queryFun'
            }
        ],
        filtersValueCallBack: (filters) => {
            // console.log('过滤值', filters);
            filterData.value = filters;
        }
    });

    watch(
        () => props.currNode.id,
        (newId) => {
            initTableData(newId);
        },
        {
            immediate: true
        }
    );

    // init 数据
    async function initTableData(id) {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            pageNo: tableConfig.value.pageConfig.currentPage,
            pageSize: tableConfig.value.pageConfig.pageSize,
            ...filterData.value,
            roleId: id
        };

        // console.log(params, '9999');

        isNotFlag.value = params.isNot;

        // 请求接口
        let result = await searchUsersList(params);

        if (result.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = result.data.content;
            tableConfig.value.pageConfig.total = result.data.total;
        }

        tableConfig.value.loading = false;
    }

    // 搜索
    function handleClickQuery() {
        tableConfig.value.pageConfig.currentPage = 1;
        initTableData(props.currNode.id);
    }

    // 分页操作
    function onCurrentChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        initTableData(props.currNode.id);
    }

    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        initTableData(props.currNode.id);
    }
</script>

<style lang="scss" scoped></style>

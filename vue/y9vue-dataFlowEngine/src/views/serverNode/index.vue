<template>
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="serviceNode"
        v-loading.fullscreen.lock="loading"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
        </template>
        <template #status="{ row }">
            <el-select v-if="modifyStatus[row.instanceId]" v-model="row.status" placeholder="请选择服务器状态">
                <el-option
                    v-for="item in serviceStatusList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                ></el-option>
            </el-select>
            <div v-else>{{ statusNum[row.status] }}</div>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { ref, computed, onMounted } from 'vue';
    import {
        getServicesList,
        modifyServiceStatus,
        restartService,
        checkServiceExample,
        deleteService
    } from '@/api/serverNode/index';
    import { $formatTime } from '@/utils/object';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    // 服务器状态对象
    let statusNum = {
        1: '等待',
        0: '正常',
        2: '异常',
        3: '暂停'
    };

    // 环境id与名称的对应
    let environmentName = {
        dev: '测试环境',
        Public: '默认环境'
    };

    // 服务器状态的数组
    let serviceStatusList = [
        { label: '等待', value: 1 },
        { label: '正常', value: 0 },
        { label: '异常', value: 2 },
        { label: '暂停', value: 3 }
    ];

    // 修改状态情况
    let modifyStatus = ref({});

    // 服务器实例数据
    let loading = ref(false);

    // 表格列表配置
    let tableConfig = ref({
        pageConfig: false,
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'index',
                width: 80,
                fixed: 'left',
                title: computed(() => t('序号'))
            },
            {
                title: computed(() => t('状态')),
                slot: 'status'
            },
            {
                title: computed(() => t('所属环境')),
                key: 'environment',
                render: (row) => {
                    return environmentName[row.environment];
                }
            },
            {
                title: computed(() => t('节点标识')),
                key: 'instanceId',
                width: 300
            },
            {
                title: computed(() => t('版本号')),
                key: 'version'
            },
            {
                title: computed(() => t('描述')),
                key: 'description'
            },
            {
                title: computed(() => t('更新时间')),
                key: 'updateTime',
                width: 138,
                render: (row) => {
                    return $formatTime(row.updateTime).longDateTime;
                }
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                width: 300,
                render: (row) => {
                    // 修改状态场景
                    let modifyscene = [
                        h(
                            'span',
                            {
                                onclick: async () => {
                                    // 请求修改状态接口
                                    try {
                                        let result = await modifyServiceStatus(row.instanceId, row.status);
                                        ElNotification({
                                            title: result.success ? t('修改成功') : t('修改失败'),
                                            message: result.msg,
                                            type: result.success ? 'success' : 'error',
                                            duration: 2000,
                                            offset: 80
                                        });
                                        modifyStatus.value[row.instanceId] = false;
                                    } catch (err) {}
                                },
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-edit-2-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('修改'))
                            ]
                        ),
                        h(
                            'span',
                            {
                                style: {
                                    margin: '0 10px',
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                },
                                onclick: () => {
                                    modifyStatus.value[row.instanceId] = false;
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-close-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('取消'))
                            ]
                        )
                    ];
                    // 一般场景
                    let normalscene = [
                        h(
                            'span',
                            {
                                onclick: () => {
                                    modifyStatus.value[row.instanceId] = true;
                                },
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-edit-2-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('修改状态'))
                            ]
                        ),
                        // h(
                        //     'span',
                        //     {
                        //         style: {
                        //             margin: '0 10px',
                        //             display: 'inline-flex',
                        //             alignItems: 'center'
                        //         },
                        //         onclick: async () => {
                        //             ElMessageBox.confirm(`${t('是否确定重启服务器节点')} ?`, t('提示'), {
                        //                 confirmButtonText: t('确定'),
                        //                 cancelButtonText: t('取消'),
                        //                 type: 'info'
                        //             })
                        //                 .then(async () => {
                        //                     loading.value = true;
                        //                     try {
                        //                         await restartService(row.instanceId);
                        //                     } catch (err) {
                        //                         ElNotification({
                        //                             title: t('成功'),
                        //                             message: t('重启成功'),
                        //                             type: 'success',
                        //                             duration: 2000,
                        //                             offset: 80
                        //                         });
                        //                     }
                        //                     loading.value = false;
                        //                 })
                        //                 .catch(() => {
                        //                     ElMessage({
                        //                         type: 'info',
                        //                         message: t('已取消重启'),
                        //                         offset: 65
                        //                     });
                        //                 });
                        //         }
                        //     },
                        //     [
                        //         h('i', {
                        //             class: 'ri-history-fill',
                        //             style: {
                        //                 marginRight: '1px'
                        //             }
                        //         }),
                        //         h('span', t('重启'))
                        //     ]
                        // ),
                        h(
                            'span',
                            {
                                style: {
                                    margin: '0px 10px',
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                },
                                onclick: async () => {
                                    let result = await checkServiceExample({ id: row.instanceId });
                                    ElNotification({
                                        title: result.success ? t('检查成功') : t('检查失败'),
                                        message: result.msg,
                                        type: result.success ? 'success' : 'error',
                                        duration: 2000,
                                        offset: 80
                                    });
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-survey-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('检查实例'))
                            ]
                        ),
                        h(
                            'span',
                            {
                                onclick: () => {
                                    ElMessageBox.confirm(`${t('是否确定删除服务器节点')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                    .then(async () => {
                                        loading.value = true;
                                        let result = await deleteService({ id: row.instanceId });
                                        ElNotification({
                                            title: result.success ? t('成功') : t('失败'),
                                            message: result.msg,
                                            type: result.success ? 'success' : 'error',
                                            duration: 2000,
                                            offset: 80
                                        });
                                        loading.value = false;
                                    })
                                    .catch(() => {
                                        ElMessage({
                                            type: 'info',
                                            message: t('已取消删除'),
                                            offset: 65
                                        });
                                    });
                                },
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-delete-bin-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('删除'))
                            ]
                        )
                    ];
                    return modifyStatus.value[row.instanceId] ? modifyscene : normalscene;
                }
            }
        ],
        tableData: []
    });

    const filterData = ref({});
    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'select',
                key: 'environment',
                label: computed(() => t('环境')),
                span: 6,
                props: {
                    options: [
                        { label: '默认环境', value: 'Public' },
                        { label: '测试环境', value: 'dev' }
                    ]
                }
            },
            {
                type: 'select',
                key: 'status',
                label: computed(() => t('状态')),
                span: 6,
                props: {
                    options: serviceStatusList
                }
            },
            {
                type: 'slot',
                slotName: 'queryFun',
                span: 12
            }
        ],
        filtersValueCallBack: (filters) => {
            // console.log('过滤值', filters);
            filterData.value = filters;
        }
    });

    onMounted(getServicesData);

    async function getServicesData() {
        let result = await getServicesList(filterData.value);
        tableConfig.value.tableData = result.data;
    }

    // 搜索
    function handleClickQuery() {
        getServicesData();
    }
</script>

<style lang="scss" scoped></style>

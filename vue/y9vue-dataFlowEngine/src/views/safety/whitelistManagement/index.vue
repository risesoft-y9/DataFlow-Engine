<template>
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="whitelist"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
            <div class="right-btn">
                <el-button type="primary" class="global-btn-main" @click="handleAddWhite"
                    ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
                >
            </div>
        </template>
    </y9Table>
    <!-- 表单 -->
    <y9Dialog v-model:config="dialogConfig">
        <y9Form :config="formConfig" ref="y9TreeFormRef"></y9Form>
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { computed, onMounted, ref } from 'vue';
    import { searchWhiteList, saveOrUpdateWhite, deleteWhiteById } from '@/api/safety/whitelistManagement';
    import { getEnvironmentAll } from '@/api/dispatch';
    import { getStoragePageSize } from '@/utils/index';
    import { $validCheck } from '@/utils/validate';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    let loading = ref(false);

    // 搜索的filter条件变量
    let filterData = ref({});

    // 环境id与名称的对应
    let environmentName = {
        dev: '测试环境',
        Public: '默认环境'
    };

    // 白名单id
    let whiteId = ref('');

    onMounted(() => {
        initEnvironment();
    });

    // 获取环境id数据，并赋值表单或筛选条件中
    async function initEnvironment() {
        let result = await getEnvironmentAll();
        if (result.code == 0) {
            let dataList = result.data?.map((item) => {
                return { label: item.description, value: item.name };
            });
            filterConfig.value.itemList?.map((item) => {
                if (item.key == 'environment') {
                    item.props.options = dataList;
                }
            });
            formConfig.value.itemList?.map((item) => {
                if (item.prop == 'environment') {
                    item.props.options = dataList;
                }
            });
        }
    }

    // 表格列表配置
    let tableConfig = ref({
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'index',
                width: 80,
                title: computed(() => t('序号')),
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('环境名称')),
                key: 'environment',
                showOverflowTooltip: false,
                render: (row) => {
                    return environmentName[row.environment];
                }
            },
            {
                title: computed(() => t('ip')),
                key: 'ipMatch',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('描述')),
                key: 'description',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('服务')),
                key: 'service',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                render: (row) => {
                    return [
                        h(
                            'span',
                            {
                                onclick: () => {
                                    formConfig.value.model = {
                                        environment: row.environment,
                                        ipMatch: row.ipMatch,
                                        description: row.description,
                                        service: row.service
                                    };
                                    whiteId.value = row.id;
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('编辑白名单'))
                                    });
                                }
                            },
                            t('编辑')
                        ),
                        h(
                            'span',
                            {
                                onclick: async () => {
                                    ElMessageBox.confirm(`${t('是否确定删除')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                        // loading: true,
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await deleteWhiteById({ id: row.id });
                                            if (result.code == 0) {
                                                tableConfig.value.pageConfig.currentPage = 1;
                                                initTableData();
                                            }
                                            loading.value = false;
                                            ElNotification({
                                                title: result.success ? t('删除成功') : t('删除失败'),
                                                message: result.msg,
                                                type: result.success ? 'success' : 'error',
                                                duration: 2000,
                                                offset: 80
                                            });
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
                                    margin: '0 10px'
                                }
                            },
                            t('删除')
                        )
                    ];
                }
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('whitelist', 15),
            pageSizeOpts: [10, 15, 30, 60, 120, 240],
            total: 0
        }
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'input',
                key: 'description',
                label: computed(() => t('描述')),
                span: 8
            },
            {
                type: 'select',
                key: 'environment',
                filterable: true,
                label: computed(() => t('环境名称')),
                props: {
                    options: []
                },
                span: 8
            },
            {
                type: 'slot',
                slotName: 'queryFun',
                span: 8
            }
        ],
        filtersValueCallBack: (filters) => {
            console.log('过滤值', filters);
            filterData.value = filters;
        }
    });

    // 表单配置
    const y9TreeFormRef = ref(null);
    const dialogConfig = ref({
        show: false,
        title: '',
        width: '42%',
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let valid = await y9TreeFormRef.value?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (!valid) {
                    reject();
                    return;
                }
                // 接口参数
                let params = {
                    id: whiteId.value ? whiteId.value : null,
                    ...y9TreeFormRef.value?.model
                };

                console.log(params, 'params');

                // 进行接口操作
                let result = await saveOrUpdateWhite(params);
                if (result.code == 0) {
                    // 请求接口
                    initTableData();
                }
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                resolve();
            });
        }
    });

    //可注册ip的筛选
    const mobileValidator = (rule, value, callback) => {
        let result = $validCheck('ipMatch', value, true);
        if (!result.valid) {
            callback(new Error(t(result.msg)));
        } else {
            callback();
        }
    };

    const formConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1, //1列模式
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {
            //表单属性
            environment: '',
            ipMatch: '',
            description: '',
            service: '**'
        },
        rules: {
            //表单验证规则
            ipMatch: [
                { required: true, message: '请输入可注册IP', trigger: 'blur' },
                // { validator: mobileValidator, trigger: 'blur' }
            ],
            environment: [{ required: true, message: '请选择环境id', trigger: 'blur' }],
            description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
        },
        itemList: [
            //表单显示列表
            {
                type: 'select',
                prop: 'environment',
                label: computed(() => t('环境名称')),
                props: {
                    options: []
                },
                required: true
            },
            {
                type: 'input',
                prop: 'ipMatch',
                label: computed(() => t('可注册ip')),
                required: true
            },
            {
                type: 'input',
                prop: 'description',
                label: computed(() => t('白名单描述')),
                required: true
            },
            {
                type: 'input',
                prop: 'service',
                label: computed(() => t('可注册服务'))
            }
        ]
    });

    // 增加
    function handleAddWhite() {
        whiteId.value = '';
        formConfig.value.model = { environment: '', ipMatch: '', description: '', service: '**' };
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('新增白名单'))
        });
    }

    onMounted(() => {
        initTableData();
    });
    // init 数据
    async function initTableData() {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            pageNo: tableConfig.value.pageConfig.currentPage,
            pageSize: tableConfig.value.pageConfig.pageSize,
            ...filterData.value
        };

        // 请求接口
        let result = await searchWhiteList(params);

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
        initTableData();
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
    .right-btn {
        width: 100%;
        display: flex;
        justify-content: flex-end;
    }
</style>
<!-- @/api/safety/whitelistManagement -->

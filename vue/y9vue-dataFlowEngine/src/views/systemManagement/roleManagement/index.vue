<template>
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="roleManagement"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
            <div class="right-btn">
                <el-button type="primary" class="global-btn-main" @click="handleAddUser"
                    ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
                >
            </div>
        </template>
    </y9Table>
    <!-- 表单 -->
    <y9Dialog v-model:config="dialogConfig">
        <y9Form :config="formConfig" ref="y9TreeFormRef">
            <template #JOBTYPES>
                <div class="job-types">
                    <el-tag
                        v-for="(item, index) in formConfig.model.jobTypes"
                        :key="index"
                        closable
                        @close="handleClose(item)"
                    >
                        {{ item }}
                    </el-tag>
                    <el-tag @click="dialogJOBTYPESConfig.show = true" class="button-new-tag" type="info">
                        +添加
                    </el-tag>
                </div>
            </template>
        </y9Form>
    </y9Dialog>

    <!-- 业务分类表单 -->
    <y9Dialog v-model:config="dialogJOBTYPESConfig">
        <JobTypeDialog :selectData="formConfig.model.jobTypes" @save-list="handleGetData" />
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { computed, onMounted, ref } from 'vue';
    import { getRolesList, saveOrUpdateRole, deleteRoleById } from '@/api/systemManagement/roleManagement';
    import { getEnvironmentAll } from '@/api/dispatch';
    import { getStoragePageSize } from '@/utils/index';
    import JobTypeDialog from './comps/JobTypeDialog.vue';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 搜索的filter条件变量
    let filterData = ref({});

    let loading = ref(false);

    // 表格返回数据的回显
    let environmentName = {
        dev: '测试环境',
        Public: '默认环境'
    };

    // 表格列表配置
    let tableConfig = ref({
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'index',
                title: computed(() => t('序号')),
                showOverflowTooltip: false,
                width: 90
            },
            {
                title: computed(() => t('角色名')),
                key: 'name',
                width: 120,
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('环境名称')),
                key: 'environments',
                showOverflowTooltip: false,
                render: (row) => {
                    let environmentsList = row.environments.split(',')?.map((item) => environmentName[item]);
                    return environmentsList.join('，');
                }
            },
            {
                title: computed(() => t('任务类型')),
                key: 'jobTypes',
                showOverflowTooltip: false,
                render: (row) => {
                    return row.jobTypes.replace(/,/g, '，');
                }
            },
            {
                title: computed(() => t('是否有系统管理员权限')),
                key: 'systemManager',
                render: (row) => {
                    return row.systemManager == 1 ? '是' : '否';
                }
            },
            {
                title: computed(() => t('是否有安全管理员权限')),
                key: 'userManager',
                render: (row) => {
                    return row.userManager == 1 ? '是' : '否';
                }
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                render: (row) => {
                    return [
                        h(
                            'span',
                            {
                                onclick: async () => {
                                    let rowParams = Object.assign({}, row, {
                                        environments: row.environments ? row.environments.split(',') : [],
                                        jobTypes: row.jobTypes ? row.jobTypes.split(',') : []
                                    });
                                    formConfig.value.model = rowParams;
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('修改角色信息'))
                                    });
                                },
                                style: {
                                    margin: '0 10px'
                                }
                            },
                            t('编辑')
                        ),
                        h(
                            'span',
                            {
                                onclick: () => {
                                    ElMessageBox.confirm(`${t('是否确定删除该节点')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await deleteRoleById({ id: row.id });
                                            if (result.code == 0) {
                                                initTableData();
                                            }
                                            ElNotification({
                                                title: result.success ? t('删除成功') : t('删除失败'),
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
            pageSize: getStoragePageSize('roleManagement', 15),
            pageSizeOpts: [10, 15, 30, 60, 120, 240],
            total: 0
        }
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'input',
                key: 'name',
                span: 6,
                label: computed(() => t('角色名'))
            },
            {
                type: 'select',
                key: 'userManager',
                label: computed(() => t('安全管理员权限')),
                span: 6,
                props: {
                    options: [
                        { label: '是', value: 1 },
                        { label: '否', value: 0 }
                    ]
                }
            },
            {
                type: 'select',
                key: 'systemManager',
                span: 6,
                label: computed(() => t('系统管理员权限')),
                props: {
                    options: [
                        { label: '是', value: 1 },
                        { label: '否', value: 0 }
                    ]
                }
            },
            {
                type: 'slot',
                slotName: 'queryFun',
                span: 6
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
                let params = Object.assign({}, y9TreeFormRef.value.model, {
                    environments: y9TreeFormRef.value.model.environments?.join(','),
                    jobTypes: y9TreeFormRef.value.model.jobTypes?.join(','),
                    id: formConfig.value.model.id ? formConfig.value.model.id : null
                });

                // 进行接口操作
                try {
                    let result = await saveOrUpdateRole(params);
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
                } catch (err) {
                    reject(err);
                }
            });
        }
    });

    // 业务分类表单
    const dialogJOBTYPESConfig = ref({
        show: false,
        title: '任务类型列表',
        width: '60%',
        showFooter: false
    });

    // 拿到表格返回的数据
    function handleGetData(data) {
        dialogJOBTYPESConfig.value.show = false;
        formConfig.value.model.jobTypes = data?.map((item) => item.name);
    }

    // 删除tag标签
    function handleClose(value) {
        formConfig.value.model.jobTypes = formConfig.value.model.jobTypes.filter((item) => item !== value);
    }

    const formConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1, //1列模式
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {
            //表单属性
            name: '',
            userManager: '',
            systemManager: '',
            environments: [],
            jobTypes: [],
            id: ''
        },
        rules: {
            //表单验证规则
            name: { required: true, message: computed(() => t('请输入用户名')), trigger: 'blur' },
            userManager: { required: true, message: computed(() => t('请输入账号')), trigger: 'blur' },
            systemManager: { required: true, message: computed(() => t('请输入密码')), trigger: 'blur' }
        },
        itemList: [
            //表单显示列表
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('角色名')),
                required: true
            },
            {
                type: 'select',
                prop: 'environments',
                label: computed(() => t('环境名称')),
                props: {
                    multiple: true,
                    options: [],
                    events: {
                        change: (value) => {
                            formConfig.value.model.environments = value;
                        }
                    }
                }
            },
            {
                type: 'slot',
                prop: 'jobTypes',
                label: computed(() => t('任务类型')),
                props: {
                    slotName: 'JOBTYPES'
                }
            },
            {
                type: 'select',
                prop: 'userManager',
                label: computed(() => t('用户管理操作')),
                required: true,
                props: {
                    options: [
                        { label: '是', value: 1 },
                        { label: '否', value: 0 }
                    ]
                }
            },
            {
                type: 'select',
                prop: 'systemManager',
                label: computed(() => t('系统管理操作')),
                required: true,
                props: {
                    options: [
                        { label: '是', value: 1 },
                        { label: '否', value: 0 }
                    ]
                }
            }
        ]
    });

    onMounted(() => {
        initTableData();
        // 请求环境接口
        getEnvironment();
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
        let result = await getRolesList(params);

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

    // 增加
    async function handleAddUser() {
        formConfig.value.model = {
            name: '',
            userManager: '',
            systemManager: '',
            environments: [],
            jobTypes: [],
            id: ''
        };
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('增加角色'))
        });
    }

    // 获取所有环境
    //获取环境 todo 后续改成获取用户
    const getEnvironment = async () => {
        let res = await getEnvironmentAll();
        if (res.code == 0) {
            let dataList = res.data?.map((item) => {
                return { label: item.description, value: item.name };
            });
            formConfig.value.itemList?.map((item) => {
                if (item.prop == 'environments') {
                    item.props.options = dataList;
                }
            });
        }
    };

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

    .job-types {
        :deep(.el-tag) {
            margin-right: 10px;
        }
    }
</style>

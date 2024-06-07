<template>
    <!-- 表格 -->
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="businessTableOne"
        @expand-change="handleExpandChange"
        @on-curr-page-change="(currPage) => onCurrentChange(currPage, 'one')"
        @on-page-size-change="(pageSize) => onPageSizeChange(pageSize, 'one')"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
            <el-button type="primary" class="global-btn-main" @click="handleClickAdd"
                ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
            >
        </template>
        <template #childContent="props">
            <div style="padding: 20px">
                <y9Table
                    :key="props.index"
                    :uniqueIdent="`businessTableTwo${props.index}`"
                    :config="secondLevelTable[props.index]"
                    @on-curr-page-change="(currPage) => onCurrentChange(currPage, 'second', props.index, props.row)"
                    @on-page-size-change="(pageSize) => onPageSizeChange(pageSize, 'second', props.index, props.row)"
                ></y9Table>
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
    import { ref, computed, onMounted } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getTableList, saveBusinessType, deleteBusinessType } from '@/api/businessClassify/index';
    import { getStoragePageSize } from '@/utils/index';
    const { t } = useI18n();

    const loading = ref(false);
    // 当前操作的row数据
    const currentRowData = ref({});

    let tableConfig = ref({
        rowKey: 'id', //懒加载必须设置rowKey
        lazy: true, //懒加载
        loading: false,
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('businessTableOne', 5),
            total: 0
        },
        border: false,
        headerBackground: true,
        expandRowKeys: [],
        columns: [
            {
                type: 'expand',
                slot: 'childContent',
                width: 60
            },
            {
                type: 'index',
                width: 100,
                fixed: 'left',
                title: computed(() => t('序号'))
                // key: 'id'
            },

            {
                title: computed(() => t('名称')),
                key: 'name',
                align: 'left'
            },
            {
                title: computed(() => t('时间')),
                key: 'createTime'
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                width: 200,
                render: (row) => {
                    return [
                        h(
                            'span',
                            {
                                onclick: () => {
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('新增树节点')),
                                        type: 'add'
                                    });
                                    formConfig.value.model = { name: '' };
                                    currentRowData.value = row;
                                },
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-add-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('新增'))
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
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('编辑树节点')),
                                        type: 'edit'
                                    });
                                    formConfig.value.model = row;
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-edit-2-line',
                                    style: {
                                        marginRight: '1px'
                                    }
                                }),
                                h('span', t('编辑'))
                            ]
                        ),
                        h(
                            'span',
                            {
                                onclick: () => {
                                    ElMessageBox.confirm(
                                        `${t('是否确定删除该节点，该节点下的所有子节点将会一并删除')} ?`,
                                        t('提示'),
                                        {
                                            confirmButtonText: t('确定'),
                                            cancelButtonText: t('取消'),
                                            type: 'info'
                                            // loading: true,
                                        }
                                    )
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await deleteBusinessType({ id: row.id });
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
                }
            }
        ],
        tableData: []
    });

    let secondLevelTable = ref<Array<any>>([]);

    let filterData = ref({});
    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'name',
                label: computed(() => t('名称')),
                span: 6
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

    const y9TreeFormRef = ref(null);

    const dialogConfig = ref({
        show: false,
        title: '',
        type: '',
        width: '42%',
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let valid = await y9TreeFormRef.value?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (!valid) {
                    reject();
                    return;
                }
                // 将新增的所需的一些属性设置
                let params = {
                    name: y9TreeFormRef.value?.model.name,
                    parentId: currentRowData.value ? currentRowData.value.id : null,
                    id: newConfig.type == 'add' ? null : formConfig.value.model.id
                };

                await saveBusinessType(params)
                    .then(async (result) => {
                        if (result.code == 0) {
                            if (currentRowData.value?.id) {
                                let id = currentRowData.value?.id;
                                tableConfig.value.expandRowKeys = [id];
                                handleExpandChange(currentRowData.value, true);
                            } else {
                                tableConfig.value.pageConfig.currentPage = 1;
                                tableConfig.value.expandRowKeys = [];
                                initTableData();
                            }
                        }
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                    })
                    .catch((err) => console.log(err));

                resolve();
            });
        }
    });

    const formConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1, //1列模式
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {
            //表单属性
            name: ''
        },
        rules: {
            //表单验证规则
            name: { required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }
        },
        itemList: [
            //表单显示列表
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('名称')),
                required: true
            }
        ]
    });

    onMounted(() => {
        initTableData();
    });

    // 初始化表格数据
    async function initTableData() {
        tableConfig.value.loading = true;
        let params = {
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            ...filterData.value
        };
        let result = await getTableList(params);
        if (result.code == 0) {
            tableConfig.value.tableData = result.rows;
            tableConfig.value.pageConfig.total = result.total;
            // 赋值子级表格初始化
            secondLevelTable.value = initSecondTableConfig(result.rows.length);
        }
        tableConfig.value.loading = false;
    }

    // 搜索
    function handleClickQuery() {
        tableConfig.value.pageConfig.currentPage = 1;
        initTableData();
    }

    // 分页操作
    function onCurrentChange(currPage, type, index?, row?) {
        switch (type) {
            case 'one':
                tableConfig.value.pageConfig.currentPage = currPage;
                initTableData();
                break;

            default:
                secondLevelTable.value[index].pageConfig.currentPage = currPage;
                secondInitTableData(row, index);
                break;
        }
    }

    function onPageSizeChange(pageSize, type, index?, row?) {
        switch (type) {
            case 'one':
                tableConfig.value.pageConfig.pageSize = pageSize;
                initTableData();
                break;

            default:
                secondLevelTable.value[index].pageConfig.pageSize = pageSize;
                secondInitTableData(row, index);
                break;
        }
    }

    // 行展开与关闭
    async function handleExpandChange(row, expanded) {
        let tableIndex;
        await tableConfig.value.tableData?.map((item, index) => {
            if (item.id == row.id) {
                tableIndex = index;
            }
        });
        if (expanded) {
            await secondInitTableData(row, tableIndex);
        }
    }

    // 子级表格的初始化数据
    async function secondInitTableData(row, index) {
        secondLevelTable.value[index].loading = true;
        let params = {
            page: secondLevelTable.value[index].pageConfig.currentPage,
            size: secondLevelTable.value[index].pageConfig.pageSize,
            ...filterData.value,
            parentId: row.id
        };
        let result = await getTableList(params);
        if (result.code == 0) {
            secondLevelTable.value[index].tableData = result.rows;
            secondLevelTable.value[index].pageConfig.total = result.total;
        }
        secondLevelTable.value[index].loading = false;
    }

    // 新增
    function handleClickAdd() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('新增树节点')),
            type: 'add'
        });
        formConfig.value.model = { name: '' };
        currentRowData.value = {};
    }

    function initSecondTableConfig(length) {
        return new Array(length).fill(0).map((item, index) => ({
            pageConfig: {
                currentPage: 1,
                pageSize: getStoragePageSize(`businessTableTwo${index}`, 5),
                total: 0
            },
            // border: false,
            loading: false,
            // headerBackground: true,
            columns: [
                {
                    type: 'index',
                    width: 100,
                    fixed: 'left',
                    title: computed(() => t('序号'))
                    // key: 'id'
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name'
                },
                {
                    title: computed(() => t('时间')),
                    key: 'createTime'
                },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    width: 200,
                    render: (row, params) => {
                        return [
                            h(
                                'span',
                                {
                                    style: {
                                        margin: '0 10px',
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onclick: () => {
                                        Object.assign(dialogConfig.value, {
                                            show: true,
                                            title: computed(() => t('编辑树节点')),
                                            type: 'edit'
                                        });
                                        formConfig.value.model = row;
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-edit-2-line',
                                        style: {
                                            marginRight: '1px'
                                        }
                                    }),
                                    h('span', t('编辑'))
                                ]
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

                                                let result = await deleteBusinessType({ id: row.id });
                                                if (result.code == 0) {
                                                    await secondInitTableData(row, params.$index);
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
                    }
                }
            ],
            tableData: []
        }));
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
</style>

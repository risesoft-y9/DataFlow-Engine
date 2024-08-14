<template>
    <!-- 表格 -->
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="engineConfig"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
            >
            <div class="right-btn">
                <el-button type="primary" class="global-btn-main" @click="handleClickAdd"
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

    <!-- 参数值表单 -->
    <y9Dialog v-model:config="paramsDialogConfig">
        <paramsList :currParamsId="currParamsId"></paramsList>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { ref, onMounted, computed, h } from 'vue';
    import { ElMessageBox, ElNotification, ElMessage } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import paramsList from './comps/paramsList.vue';
    import { getStoragePageSize } from '@/utils/index';
    import { getTableList, saveConfigureMapping, deleteConfigureMapping } from '@/api/engineConfig/index';
    const { t } = useI18n();

    interface formDataType {
        typeName?: string;
        className?: string;
        description?: string;
        createTime?: string;
        funcType?: string;
        onlyOne?: number;
    }

    // loading
    let loading = ref(false);

    // 表格的每行列的参数对象
    let formData = ref<formDataType>({});

    // 点击参数值 赋值当前的id值 用于传递给组件内部
    let currParamsId = ref('');

    // 数据处理类型合集
    let dataProcessing = ['executor', 'exchange', 'channel', 'plugs'];

    // 类别名称的遍历数组
    let typeNameList = [
        { label: '全部', value: '' },
        { label: '输入/输出线程池(executor)', value: 'executor' },
        { label: '数据闸口(exchange)', value: 'exchange' },
        { label: '输入/输出通道(channel)', value: 'channel' },
        { label: '其它插件(plugs)', value: 'plugs' },
        { label: 'mysql', value: 'mysql' },
        { label: 'oracle', value: 'oracle' },
        { label: 'postgresql', value: 'postgresql' },
        { label: 'kingbase', value: 'kingbase' },
        { label: 'dm', value: 'dm' },
        { label: 'ftp', value: 'ftp' },
        { label: 'elasticsearch', value: 'elasticsearch' }
    ];

    // 功能类型的遍历数组
    let funcTypeList = [
        { label: '输入(input)', value: 'input' },
        { label: '输出(output)', value: 'output' },
        { label: '数据闸口(exchange)', value: 'exchange' },
        { label: '日志(printLog)', value: 'printLog' },
        { label: '脏数据(dirtyData)', value: 'dirtyData' }
    ];

    // 搜索的filter条件变量
    let filterData = ref({});

    // 表格列表配置
    let tableConfig = ref({
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
                title: computed(() => t('类别名称')),
                key: 'typeName',
                width: 120,
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('类别')),
                key: 'type',
                width: 150,
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('类名称')),
                key: 'className'
            },
            {
                title: computed(() => t('功能类型')),
                key: 'funcType',
                width: 100
            },
            {
                title: computed(() => t('描述')),
                key: 'description'
            },
            {
                title: computed(() => t('唯一')),
                key: 'onlyOne',
                width: 80,
                render: (row) => {
                    if (row.onlyOne == 1) {
                        return '是';
                    } else {
                        return '否';
                    }
                }
            },
            {
                title: computed(() => t('创建时间')),
                key: 'createTime',
                width: 200,
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                width: 200,
                render: (row) => {
                    let btn;
                    // 数据处理类型，有参数值
                    if (dataProcessing.filter((item) => item === row.typeName).length) {
                        btn = h(
                            'span',
                            {
                                onclick: () => {
                                    paramsDialogConfig.value.show = true;
                                    // 赋值当前id 用于传递给组件 用于接口请求
                                    currParamsId.value = row.id;
                                }
                            },
                            t('参数值')
                        );
                    }
                    let initActions = [
                        btn,
                        h(
                            'span',
                            {
                                onclick: () => {
                                    // 赋值
                                    formConfig.value.model = row;
                                    // 弹窗出现
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('编辑'))
                                    });
                                },
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center',
                                    margin: '0 10px'
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
                                onclick: async () => {
                                    ElMessageBox.confirm(`${t('是否确定要删除此数据')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                        // loading: true,
                                    })
                                        .then(async () => {
                                            loading.value = true;

                                            // 请求接口 row数据
                                            let result = await deleteConfigureMapping({ id: row.id });
                                            if (result.code == 0) {
                                                // 请求接口
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
                    return h('span', initActions);
                }
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('engineConfig', 15),
            total: 0,
            pageSizeOpts:[10,15,30,60,120,240]
        }
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'select',
                value: '',
                key: 'typeName',
                label: computed(() => t('类别名称')),
                span: 6,
                props: {
                    options: typeNameList
                }
            },
            {
                type: 'input',
                key: 'className',
                span: 6,
                label: computed(() => t('类名称'))
            },
            {
                type: 'slot',
                slotName: 'queryFun',
                span: 12
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
                    ...y9TreeFormRef.value?.model
                };
                // 进行接口操作
                let result = await saveConfigureMapping(params);
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

    const formConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1, //1列模式
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {
            //表单属性
            id: '',
            typeName: '',
            className: '',
            description: '',
            funcType: '',
            onlyOne: 1
        },
        rules: {
            //表单验证规则
            typeName: { required: true, message: computed(() => t('请输入类别名称')), trigger: 'blur' },
            className: { required: true, message: computed(() => t('请输入class类')), trigger: 'blur' },
            funcType: { required: true, message: computed(() => t('请输入功能类型')), trigger: 'blur' }
        },
        itemList: [
            //表单显示列表
            {
                type: 'select',
                prop: 'typeName',
                label: computed(() => t('类别名称')),
                required: true,
                props: {
                    options: typeNameList?.filter((item) => item.value !== '')
                }
            },
            {
                type: 'input',
                prop: 'className',
                label: computed(() => t('CLASS类')),
                required: true
            },
            {
                type: 'select',
                prop: 'funcType',
                label: computed(() => t('功能类型')),
                required: true,
                props: {
                    options: funcTypeList?.filter((item) => item.value !== '')
                }
            },
            {
                type: 'input',
                prop: 'description',
                label: computed(() => t('描述'))
                // required: true
            },
            {
                type: 'radio',
                prop: 'onlyOne',
                label: '唯一',
                props: {
                    options: [
                        {
                            value: 1,
                            label: '是'
                        },
                        {
                            value: 0,
                            label: '否'
                        }
                    ]
                }
            }
        ]
    });

    function handleClickAdd() {
        // 表单初始化
        formConfig.value.model = { id: '', typeName: '', className: '', description: '', funcType: '', onlyOne: 1 };
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('新增'))
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
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            ...filterData.value
        };

        // 请求接口
        let result = await getTableList(params);

        if (result.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = await returnDataHandle(result.rows);
            tableConfig.value.pageConfig.total = result.total;
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

    // 对接口返回的数据 添加一个类别的栏目
    // data 为数组
    async function returnDataHandle(data) {
        // 对接口返回的数据 进行type的赋值
        await data.map((item) =>
            dataProcessing.filter((type) => type == item.typeName).length
                ? (item.type = '数据处理类插件')
                : (item.type = '数据类插件')
        );
        // // 找到updateTime时间最新的对象
        // const mostRecentObj = data.reduce((latest, obj) => {
        //     return new Date(latest.createTime) > new Date(obj.createTime) ? latest : obj;
        // }, data[0]);

        // // 判断该对象的type
        // const isDataProcessingType = mostRecentObj.type === '数据处理型组件';

        // // 根据判断结果对数组进行排序
        // // 如果是数据处理型组件，就按照升序排列
        // // 如果是关系型数据库组件，就按照降序排列
        // data.sort((a, b) => {
        //     if (isDataProcessingType) {
        //         // 对type长度升序排列
        //         return a.type.length - b.type.length;
        //     } else {
        //         // 对type长度降序排列
        //         return b.type.length - a.type.length;
        //     }
        // });

        return data;
    }

    // 参数值弹框
    let paramsDialogConfig = ref({
        show: false,
        title: computed(() => t('参数列表')),
        width: '62%',
        showFooter: false
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    .right-btn {
        width: 100%;
        display: flex;
        justify-content: flex-end;
    }
</style>

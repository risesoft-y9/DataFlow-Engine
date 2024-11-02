<template>
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="paramsList"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #queryFun>
            <el-button type="primary" class="global-btn-main" @click="handleClickAdd"
                ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
            >
        </template>
    </y9Table>

    <!-- 表单 -->
    <y9Dialog v-model:config="dialogConfig">
        <y9Form :config="formConfig" ref="y9TreeFormRef">
            <template #paramsName>
                <el-radio-group v-model="filterParams.dataType" @change="handleChange" :required="true">
                    <el-radio label="1">{{ $t('一级参数 ') }}</el-radio>
                    <el-radio label="2"> {{ $t('二级参数') }} </el-radio>
                </el-radio-group>
            </template>
        </y9Form>
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { computed, ref, onMounted } from 'vue';
    import { ElMessageBox, ElNotification, ElMessage } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { getStoragePageSize } from '@/utils/index';
    import { getMappingParams, saveMappingParams, deleteMappingParams } from '@/api/engineConfig/index';
    const { t } = useI18n();
    let loading = ref(false);

    const props = defineProps({
        currParamsId: {
            type: String,
            default: ''
        }
    });

    // 表单中的单选radio和当前对象的唯一标识id
    let filterParams = ref({
        dataType: '1',
        id: ''
    });

    // 表格列表配置
    let tableConfig = ref<any>({
        pageConfig: {
            currentPage: 1,
            pageSize: getStoragePageSize('paramsList', 20),
            total: 0
        },
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'index',
                width: 100,
                fixed: 'left',
                title: computed(() => t('序号'))
            },
            {
                title: computed(() => t('参数名称')),
                render: (row) => {
                    return row.upName ? `${row.upName}.${row.name}` : row.name;
                }
            },
            {
                title: computed(() => t('描述')),
                key: 'description'
            },
            {
                title: computed(() => t('缺省值')),
                key: 'defaultValue'
            },
            {
                title: computed(() => t('参数类型')),
                key: 'type'
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
                                    // 赋值
                                    formConfig.value.model = row;
                                    filterParams.value.dataType = row.upName ? '2' : '1';
                                    filterParams.value.id = row.id;
                                    handleChange(filterParams.value.dataType);
                                    // 弹窗出现
                                    Object.assign(dialogConfig.value, {
                                        show: true,
                                        title: computed(() => t('编辑'))
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
                                    ElMessageBox.confirm(`${t('是否确定要删除此数据')} ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                        // loading: true,
                                    })
                                        .then(async () => {
                                            loading.value = true;

                                            // 请求接口 row数据
                                            let result = await deleteMappingParams({ id: row.id });
                                            if (result.code == 0) {
                                                // 重新请求接口
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
                                }
                            },
                            t('删除')
                        )
                    ];
                }
            }
        ],
        tableData: []
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'slot',
                slotName: 'queryFun'
            }
        ]
    });

    // 新增的表单弹窗
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
                    ...y9TreeFormRef.value?.model,
                    ...filterParams.value,
                    mappingId: props.currParamsId
                };

                // 如果修改为一级菜单，将上级名称设置为空
                if (filterParams.value.dataType == '1') {
                    params.upName = '';
                }
                // saveMappingParams
                console.log(params, '000');
                // 进行接口操作
                await saveMappingParams(params)
                    .then((result) => {
                        if (result.code == 0) {
                            // 重新请求列表
                            initTableData();
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

    // 表单
    const formConfig = ref<any>({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1, //1列模式
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {
            //表单属性
            name: '',
            type: 'string',
            defaultValue: '',
            description: '',
            upName: ''
        },
        rules: {
            //表单验证规则
            name: { required: true, message: computed(() => t('请输入参数名称')), trigger: 'blur' },
            type: { required: true, message: computed(() => t('请输入类别名称')), trigger: 'blur' },
            upName: { required: true, message: computed(() => t('请输入上一级名称')), trigger: 'blur' }
            // defaultValue: { required: true, message: computed(() => t('请输入缺省值')), trigger: 'blur' }
            // description: { required: true, message: computed(() => t('请输入描述')), trigger: 'blur' }
        },
        itemList: [
            //表单显示列表
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('参数名称')),
                required: true
            },
            {
                type: 'slot',
                label: computed(() => t('类别名称')),
                required: true,
                props: {
                    slotName: 'paramsName'
                }
            },
            {
                type: 'select',
                prop: 'type',
                label: computed(() => t('类别名称')),
                required: true,
                props: {
                    options: [
                        {
                            label: computed(() => t('字符串')),
                            value: 'string'
                        },
                        {
                            label: computed(() => t('数字')),
                            value: 'integer'
                        },
                        {
                            label: computed(() => t('布尔类型')),
                            value: 'boolean'
                        }
                    ]
                }
            },
            {
                type: 'input',
                prop: 'defaultValue',
                label: computed(() => t('缺省值'))
                // required: true
            },
            {
                type: 'input',
                prop: 'description',
                label: computed(() => t('描述'))
                // required: true
            }
        ]
    });

    // 类别名称单选框的逻辑处理
    function handleChange(value) {
        if (value == '1') {
            // 设置为一级就删除表单的 上级名称栏目
            formConfig.value.itemList?.map((item, index) => {
                if (item.prop == 'upName') {
                    formConfig.value.itemList.splice(index, 1);
                }
            });
        } else {
            // '2'
            // 查找数组中是否有prop为'upName'的对象
            const hasUpName = formConfig.value.itemList.some((item) => item.prop === 'upName');

            // 如果没有找到，那么在type为'slot'的元素后面添加新对象
            if (!hasUpName) {
                // 遍历数组时记录索引，以便后续插入新元素
                for (let i = 0; i < formConfig.value.itemList.length; i++) {
                    // 当找到type为'slot'的元素时，在其后面插入新对象
                    if (formConfig.value.itemList[i].type === 'slot') {
                        // 利用splice进行插入（参数1：插入位置，参数2：删除个数，参数3：插入的元素）
                        formConfig.value.itemList.splice(i + 1, 0, {
                            type: 'input',
                            prop: 'upName',
                            label: '上一级名称',
                            required: true
                        });
                        // 假设只需要在第一个slot后面添加，添加完成后退出循环
                        // 如果需要在每个slot后面都添加，那么不要break，同时要更新i的值
                        // i += 1; // 当前位置后移
                        break;
                    }
                }
            }
        }
    }

    function handleClickAdd() {
        // 表单初始化
        formConfig.value.model = { name: '', type: 'string', defaultValue: '', description: '', upName: '' };
        filterParams.value = { dataType: '1', id: '' };
        handleChange('1');
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('新增'))
        });
    }

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
            mappingId: props.currParamsId
        };

        // 请求接口
        let result = await getMappingParams(params);
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
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
</style>

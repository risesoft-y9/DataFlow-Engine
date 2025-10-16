<template>
    <!-- 表格 -->
    <y9Table
        v-if="showTable"
        v-model:selectedVal="selectedCheckboxVal"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #environment>
            <div class="y9select-task">
                <div class="label">任务环境</div>
                <el-select
                    @change="envChange"
                    clearable
                    v-model="state.form.environment"
                    class="m-2"
                    placeholder="请选择"
                >
                    <el-option
                        v-for="item in state.environmentAll"
                        :key="item.id"
                        :label="item.description"
                        :value="item.name"
                    />
                </el-select>
            </div>
        </template>
        <template #businessId>
            <div class="y9select-task">
                <div class="label">业务分类</div>
                <el-tree-select
                    node-key="id"
                    clearable
                    check-strictly
                    :props="state.defaultProps"
                    v-model="state.form.jobType"
                    :data="state.treeData"
                />
            </div>
        </template>
        <template #dispatchType>
            <div class="y9select-task">
                <div class="label">调度类型</div>
                <el-select clearable v-model="state.form.dispatchType" class="m-2" placeholder="请选择">
                    <el-option
                        v-for="item in state.dispatchTypeOptions"
                        :key="item.id"
                        :label="item.label"
                        :value="item.label"
                    />
                </el-select>
            </div>
        </template>
        <template #select>
            <div class="y9select-task">
                <div class="label">业务分类</div>
                <el-tree-select
                    node-key="id"
                    clearable
                    check-strictly
                    :props="state.defaultProps"
                    v-model="state.form.jobType"
                    :data="state.treeData"
                    style="width: 100%"
                />
            </div>
        </template>
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
            </el-button>
            <div class="right-btn" v-if="props?.tableRow?.type != 'dispatch'">
                <el-button class="global-btn-second" @click="lookLog">{{ '查询日志' }} </el-button>
                <el-button class="global-btn-second" @click="schedule">{{ '调度一次' }} </el-button>
                <el-button type="primary" class="global-btn-main" @click="addTask"
                    ><i class="ri-add-line"></i>{{ $t('新增') }}
                </el-button>
            </div>
        </template>

        <template #tableStatus="{ row, column, index }">
            <el-switch
                :active-value="1"
                :inactive-value="0"
                :validate-event="false"
                @change="switchChange(row, $event)"
                v-model="row.status"
                inline-prompt
                style="color: #fff"
            />
        </template>

        <template #operation="{ row, column, index }">
            <div>
                <div class="operation">
                    <div class="fields" @click="handle(1, row)" v-if="props?.tableRow?.type != 'dispatch'">
                        <i class="ri-search-eye-line"></i>
                        <span class="text">{{ '查询日志' }}</span>
                    </div>
                    <div class="fields" @click="handle(2, row)">
                        <i class="ri-calendar-schedule-line"></i>
                        <span class="text">{{ '下次执行时间' }}</span>
                    </div>
                    <div class="delete" @click="handle(3, row)">
                        <i class="ri-task-line"></i>
                        <span class="text">{{ '调度一次' }}</span></div
                    >
                    <div class="fields" @click="handle(4, row)">
                        <i class="ri-delete-bin-line"></i>
                        <span class="text">{{ '删除等待的任务' }}</span>
                    </div>
                    <div class="fields" @click="handle(5, row)">
                        <i class="ri-edit-line"></i>
                        <span class="text">{{ '修改' }}</span>
                    </div>
                    <div class="delete" @click="handle(6, row)">
                        <i class="ri-delete-bin-line"></i>
                        <span class="text">{{ '删除' }}</span>
                    </div>
                </div>
            </div>
        </template>
    </y9Table>

    <div class="b-White">
        <!-- 表单 -->
        <y9Dialog v-model:config="dialogConfig">
            <saveTask :tableRow="tableRow" v-if="dialogConfig.show" @close="close"></saveTask>
        </y9Dialog>
    </div>
    <div class="b-White">
        <!-- 表单 -->
        <y9Dialog v-model:config="dialog">
            <scheduleOnce @close="closeTask" :tableRow="tableRow"></scheduleOnce>
        </y9Dialog>
    </div>

    <div class="b-White">
        <y9Dialog v-model:config="dialogTime">
            <executionTime @close="closeTask" :tableRow="tableRow"></executionTime>
        </y9Dialog>
    </div>

    <div class="a-Grey">
        <y9Dialog v-model:config="dialogLogInfo">
            <logInfo @close="closeTask" :tableRow="tableRow"></logInfo>
        </y9Dialog>
    </div>
</template>

<script lang="ts" setup>
    import { ref, onMounted, computed, inject, reactive, h } from 'vue';
    import { ElMessageBox, ElNotification, ElMessage } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { getFindAll, getFindPage } from '@/api/taskConfig';
    import saveTask from '@/views/dispatch/comp/saveTask/index.vue';
    import scheduleOnce from './comp/scheduleOnce/index.vue';
    import executionTime from './comp/executionTime/index.vue';
    import { globalDataTask } from '@/views/dispatch/comp/saveTask/data';
    import { dataDelete, getDataSearch, getEnvironmentAll, killAwaitJob, sendJob, setStatus } from '@/api/dispatch';
    import { setTreeData } from '@/utils/object';
    import router from '@/router';
    import moment from 'moment/moment';
    import logInfo from './dispatchingLog/index.vue';

    const { t } = useI18n();
    const fontSizeObj: any = inject('sizeObjInfo');

    let selectedCheckboxVal = ref([]);
    const props = defineProps({
        tableRow: {
            default: {}
        }
    });
    // loading
    let loading = ref(false);

    // 表格的每行列的参数对象
    let formData = ref({});

    const state = reactive({
        treeData: [],
        defaultProps: {
            children: 'children',
            label: 'name'
        },
        form: {
            environment: '', //环境id
            dispatchType: null, //调度类型
            status: 0, //开关
            jobType: null,
            name: null,
            id: null
        },
        dispatchTypeOptions: [
            { label: 'cron', value: 'cron', id: 1 },
            { label: '固定速度', value: '固定速度', id: 2 }
        ],
        environmentAll: [], //环境下拉
        businessId: null,
        dataSource: [] //数据源
    });
    // 点击编辑时当前的对象唯一标识id
    let editId = ref(null);

    // 搜索的filter条件变量
    let filterData = ref({});

    // 表格列表配置
    let tableConfig = ref({
        pageConfig: {
            currentPage: 1,
            pageSize: 15,
            total: 0,
            pageSizeOpts: [10, 15, 30, 60, 120, 240]
        },
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            {
                type: 'selection',
                key: 'selection',
                width: 60
            },
            {
                type: 'index',
                width: 60,
                fixed: 'left',
                title: computed(() => t('序号'))
            },
            {
                title: computed(() => t('任务名称')),
                key: 'name'
            },
            {
                title: computed(() => t('描述')),
                key: 'description',
                width: 150
            },
            {
                title: computed(() => t('创建时间')),
                key: 'createDate',
                width: 160,
                render(row) {
                    return h('div', {}, moment(row.createDate).format('YYYY-MM-DD HH:mm:ss'));
                }
            },
            {
                title: computed(() => t('调度类型')),
                key: 'dispatchType',
                width: 90
            },
            {
                title: computed(() => t('业务分类')),
                key: 'jobTypeName'
            },
            {
                title: computed(() => t('调度表达式')),
                key: 'speed'
            },
            {
                title: computed(() => t('来源')),
                key: 'jobSource'
            },
            {
                title: computed(() => t('状态')),
                key: 'status',
                slot: 'tableStatus',
                width: 90
            },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                width: 540,
                key: 'operation',
                slot: 'operation'
            }
        ],
        tableData: []
    });

    // 表格条件筛选配置
    let filterConfig = ref({
        itemList: [
            {
                type: 'slot',
                value: '',
                slotName: 'environment',
                span: 4
            },
            // {
            //   type: 'slot',
            //   value: '',
            //   slotName: 'businessId',
            //   span: 4,
            // },

            {
                type: 'input',
                key: 'name',
                span: 4,
                label: computed(() => t('任务名称'))
            },
            {
                type: 'slot',
                value: '',
                slotName: 'select',
                span: 4
            },
            {
                type: 'slot',
                value: '',
                slotName: 'dispatchType',
                span: 4
            },
            {
                type: 'slot',
                span: 8,
                slotName: 'queryFun'
            }
        ],
        fangDouTime: 0, //防抖时间
        filtersValueCallBack: (filters) => {
            //console.log('过滤值', filters);
            filterData.value = filters;
        }
    });

    // 表单配置
    const dialogConfig = ref({
        show: false,
        showFooter: false
    });

    // 表单配置
    const dialog = ref({
        show: false,
        showFooter: false
    });

    const dialogTime = ref({
        show: false,
        showFooter: false
    });

    const dialogLogInfo = ref({
        show: false,
        showFooter: false,
        appendToBody: true
    });

    const close = (type) => {
        if (type == 3) {
            initTableData();
        }
        dialogConfig.value.show = false;
    };

    const closeTask = () => {
        dialog.value.show = false;
    };

    let tableRow = ref({}); //修改使用
    const handle = (type, row) => {
        tableRow.value = row;
        if (type == 1) {
            //日志
            tableRow.value.type = 'dispatchingLog';
            Object.assign(dialogLogInfo.value, {
                show: true,
                title: '调度日志',
                width: '80%',
                okText: false,
                cancelText: false
            });
            // router.push({path: 'dispatching', query: {userId: '123'}})
        } else if (type == 5) {
            //修改
            globalDataTask.type = 'edit';
            Object.assign(dialogConfig.value, {
                show: true,
                title: '修改',
                width: '65%',
                okText: false,
                cancelText: false
            });
        } else if (type == 2) {
            //下次执行时间
            Object.assign(dialogTime.value, {
                show: true,
                title: '下次执行时间',
                width: '50%',
                okText: false,
                cancelText: false
            });
        } else if (type == 3) {
            //调度一次
            Object.assign(dialog.value, {
                show: true,
                title: '调度一次',
                width: '40%',
                okText: false,
                cancelText: false
            });
        } else if (type == 4) {
            //删除等待的任务
            ElMessageBox.confirm(`${t('是否删除等待')}【${row.name}】 任务?`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
                .then(async () => {
                    let res = await killAwaitJob({ jobId: row.id });
                    ElNotification({
                        title: res?.success ? t('删除成功') : t('删除失败'),
                        message: res?.msg,
                        type: res?.success ? 'success' : 'error',
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
        } else if (type == 5) {
            //编辑
            globalDataTask.type = 'edit';
            Object.assign(dialogConfig.value, {
                show: true,
                title: '编辑',
                width: '65%',
                okText: false,
                showFooter: false,
                cancelText: false
            });
        } else if (type == 6) {
            ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 任务?`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
                .then(async () => {
                    let res = await dataDelete({ id: row.id });
                    ElNotification({
                        title: res?.success ? t('删除成功') : t('删除失败'),
                        message: res?.msg,
                        type: res?.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (res.success == true) {
                        initTableData();
                    }
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: t('已取消删除'),
                        offset: 65
                    });
                });
        } else {
            Object.assign(dialog.value, {
                show: true,
                title: '详情',
                width: '65%',
                okText: false,
                cancelText: false
            });
        }
    };
    const lookLog = () => {
        if (selectedCheckboxVal.value.length == 0) {
            ElNotification({
                title: '查询失败',
                message: '请先选择一个任务',
                type: 'warning',
                duration: 2000,
                offset: 80
            });
            return;
        }
        const jobIds = selectedCheckboxVal.value.map((obj) => obj.id).join(',');
        let environment = state.form.environment;
        tableRow.value.type = 'dispatchingLog';
        tableRow.value.environment = environment;
        tableRow.value.jobIds = jobIds;
        console.log(tableRow.value, 'tableRow');
        Object.assign(dialogLogInfo.value, {
            show: true,
            title: '调度日志',
            width: '80%',
            okText: false,
            cancelText: false
        });
    };

    //批量调度一次
    const schedule = async () => {
        if (selectedCheckboxVal.value.length == 0) {
            ElNotification({
                title: '调度失败',
                message: '请先选择一个任务',
                type: 'warning',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let promises = selectedCheckboxVal.value.map((data) => sendJob({ jobId: data.id }));
        try {
            let results = await Promise.all(promises);
            if (results) {
                ElNotification({
                    title: '调度成功',
                    message: '任务调度成功',
                    type: 'success',
                    duration: 2000,
                    offset: 80
                });
            }
        } catch (error) {
            // ElNotification({
            //   title: '调度失败',
            //   message: '调度失败',
            //   type: 'error',
            //   duration: 2000,
            //   offset: 80
            // });
        }
    };

    const addTask = () => {
        globalDataTask.type = 'add';

        Object.assign(dialogConfig.value, {
            show: true,
            title: '新增',
            width: '65%',
            okText: false,
            cancelText: false
        });
    };
    let showTable = ref(false);
    onMounted(() => {
        initParams(); //弹窗数据
        showTable.value = true;
        getEnvironment();
        getTree(); //获取树形结构
        //initTableData();
    });

    const envChange = () => {
        tableConfig.value.pageConfig.currentPage = 1;
        initTableData();
    };
    const initParams = () => {
        if (props?.tableRow?.type == 'dispatch') {
            //调度日志的弹窗
            tableConfig.value.pageConfig.pageSize = 10;
            state.form.environment = props?.tableRow.environment;
            state.form.id = props.tableRow.JOB_ID;

            tableConfig.value.columns.forEach((item, index) => {
                if (item.key == 'status' || item.key == 'selection') {
                    tableConfig.value.columns.splice(index, 1);
                }
            });
            filterConfig.value.itemList.forEach((item, index) => {
                item.span = 5;
                if (item.slotName == 'environment') {
                    filterConfig.value.itemList.splice(index, 1);
                }
            });
            filterConfig.value.itemList.forEach((item, index) => {
                item.span = 5;
            });
        }
    };
    const getTree = async () => {
        let res = await getFindAll();
        if (res) {
            if (res.data) {
                state.treeData = setTreeData(res.data);
            }
        }
    };

    //获取环境
    const getEnvironment = async () => {
        let res = await getEnvironmentAll();
        if (res) {
            state.environmentAll = res.data;
            state.form.environment = res.data[0].name;
            initTableData();
        }
    };

    // init 数据
    async function initTableData() {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            environment: state.form.environment,
            dispatchType: state.form.dispatchType,
            name: state.form.name,
            id: state.form.id,
            jobType: state.form.jobType,
            pageNo: tableConfig.value.pageConfig.currentPage,
            pageSize: tableConfig.value.pageConfig.pageSize,
            ...filterData.value
        };

        // 请求接口
        let res = await getDataSearch(params);

        if (res.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = res.data.content;
            tableConfig.value.pageConfig.total = res.data.total;
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

    //关闭状态
    const switchChange = async (row, e) => {
        if (!row.id) return;

        let res = await setStatus({ id: row.id, status: e });
        if (res) {
            ElNotification({
                title: res?.success ? '修改状态成功' : '修改失败',
                message: res?.msg,
                type: res?.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        }
    };
</script>

<style lang="scss" scoped>
    .operation {
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;

        .fields {
            display: flex;
            align-items: center;
            margin: 5px;

            i {
                margin-right: 2px;
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }

        .delete {
            margin: 5px;
            display: flex;
            align-items: center;

            i {
                margin-right: 2px;
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }

    .y9select-task {
        display: flex;
        align-items: center;
        width: 100%;

        .label {
            margin-right: 10px;
            color: #606266;
            font-size: inherit;

            display: flex;
            align-items: center;
        }
    }

    .right-btn {
        width: 100%;
        display: flex;
        justify-content: flex-end;
    }

    // 时间选择器
    :deep(.el-time-panel) {
        .el-time-spinner__item,
        .el-time-panel__btn {
            font-size: v-bind("'inherit'") !important;
        }
    }

    // 日期选择器
    :deep(.el-picker-panel__body) {
        .el-date-picker__header {
            button,
            .el-date-picker__header-label {
            }
        }

        .el-picker-panel__content {
            tr {
            }
        }
    }

    :deep(.y9-required-icon) {
        color: var(--el-color-error);
        margin-left: 4px;
    }

    :deep(.el-descriptions__header) {
        margin-bottom: 16px;
    }

    :deep(.el-descriptions__label) {
        width: 180px;
        font-weight: normal !important;
    }

    :deep(.el-descriptions__cell) {
        font-weight: normal !important;
        font-size: inherit;

        .is-error {
            margin-bottom: 16px !important;
        }

        .el-form-item {
            margin: 5px 0;
        }
    }

    :deep(.el-form-item__content) {
        width: 180px;

        .el-form-item__error {
            font-size: 14px;
        }

        div,
        .el-radio__label,
        .el-radio-button__inner,
        .el-checkbox__label,
        .el-checkbox-button__inner {
            font-size: inherit;
        }

        .el-input-group__prepend,
        .el-input-group__append {
            color: var(--el-color-primary);
        }

        .el-tag,
        .el-tag__close {
            color: var(--el-color-primary);
        }

        .el-date-editor {
            width: 100%;

            .el-input__wrapper {
                width: calc(100% - 22px);
            }
        }

        .el-select {
            width: 100%;
        }
    }

    :deep(.el-input__wrapper) {
        height: 30px;
    }

    :deep(.el-form-item__label) {
        font-size: inherit;
    }

    :deep(.y9-form-item-checkbox) {
        display: flex;
    }

    :deep(.el-upload-list__item-custom-icon) {
        font-size: inherit;
    }

    :deep(.noSelect) {
        .el-upload {
            display: none;
        }
    }

    :deep(.custom-img) {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    :deep(.el-upload--picture-card) {
        width: auto;
        height: auto;
        border: none;

        i {
            color: #ffffff;
        }
    }

    :deep(.el-input-number .el-input__inner) {
        text-align: left !important;
        margin-left: -3px;
    }

    .a-Grey {
        :deep(.y9-dialog-body .y9-dialog-content) {
            background: var(--el-color-primary-light-9);
        }
    }

    .b-White {
        :deep(.y9-dialog-body .y9-dialog-content) {
            background: var(--el-color-white) !important;
        }
    }

    .global-btn-second {
        background-color: transparent;
    }
</style>

<template>
    <!-- 表格 -->
    <y9Table
        :config="tableConfig"
        :filterConfig="filterConfig"
        uniqueIdent="businessCategory"
        @on-curr-page-change="onCurrentChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #select>
            <div class="y9select-task">
                <div class="label">业务分类</div>
                <el-tree-select
                    node-key="id"
                    clearable
                    check-strictly
                    :props="state.defaultProps"
                    v-model="state.businessId"
                    :data="state.treeData"
                    style="width: 100%"
                />
            </div>
        </template>
        <template #queryFun>
            <el-button type="primary" @click="handleClickQuery" class="global-btn-main">
                <i class="ri-search-2-line"></i>{{ $t('搜索') }}
            </el-button>
            <div class="right-btn" v-if="!props.tableRow.type">
                <el-button type="primary" class="global-btn-main" @click="addTask2">
                    <i class="ri-add-line"></i>{{ $t('新建单节点任务') }}
                </el-button>
                <el-button type="primary" class="global-btn-main" @click="addTask">
                    <i class="ri-add-line"></i>{{ $t('新建同步任务') }}
                </el-button>
            </div>
        </template>

        <template #operation="{ row, column, index }">
            <div class="operation">
                <div class="fields" @click="handle(3, row)">
                    <i class="ri-book-open-line"></i>
                    <span class="text">{{ '详情' }}</span>
                </div>
                <div class="fields" @click="handle(1, row)">
                    <i class="ri-edit-line"></i>
                    <span class="text">{{ '编辑' }}</span>
                </div>
                <div class="delete" @click="handle(2, row)">
                    <i class="ri-delete-bin-line"></i>
                    <span class="text">{{ '删除' }}</span></div
                >
            </div>
        </template>
    </y9Table>

    <!-- 表单 -->
    <div class="j-White">
        <y9Dialog v-model:config="dialogConfig">
            <newTask v-if="dialogConfig.show" @close="close"></newTask>
        </y9Dialog>
    </div>
    <div class="j-White">
        <y9Dialog v-model:config="dialogConfig2">
            <newTask2 v-if="dialogConfig2.show" @close="close"></newTask2>
        </y9Dialog>
    </div>
    <div class="j-White">
        <y9Dialog v-model:config="dialog">
            <taskInfo v-if="dialog.show"></taskInfo>
        </y9Dialog>
    </div>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, inject, reactive, h } from 'vue';
import { ElMessageBox, ElNotification, ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { getStoragePageSize } from '@/utils/index';
import moment from 'moment';

const { t } = useI18n();
const fontSizeObj: any = inject('sizeObjInfo');
import newTask from './comps/newTask.vue';
import newTask2 from './comps/newTask2.vue';
import taskInfo from './comps/taskInfo.vue';
import { deleteDataId, getDataById, getFindAll, getFindPage } from '@/api/taskConfig';
import { addTaskForm, globalData, taskSetForm } from '@/views/taskConfig/comps/data';
import { setTreeData } from '@/utils/object';
import { deleteTableField } from '@/api/libraryTable';

// loading
let loading = ref(false);
const props = defineProps({
    tableRow: {
        default: {}
    }
});

const state = reactive({
    treeData: [],
    defaultProps: {
        children: 'children',
        label: 'name'
    },
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
        pageSize: getStoragePageSize('businessCategory', 15),
        total: 0,
        pageSizeOpts: [10, 15, 30, 60, 120, 240]
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
            title: computed(() => t('任务名称')),
            key: 'name',
            width: 220
        },
        {
            title: computed(() => t('描述')),
            key: 'description'
        },
        {
            title: computed(() => t('业务分类')),
            width: 180,
            key: 'business'
        },
        {
            title: computed(() => t('任务状态')),
            width: 120,
            key: 'status'
        },
        {
            title: computed(() => t('任务类型')),
            width: 100,
            key: 'taskType',
            render(row) {
                if (row.taskType == 1) {
                    return h('div', {}, '单任务');
                } else {
                    return h('div', {}, '同步任务');
                }
            }
        },
        {
            title: computed(() => t('创建者')),
            width: 100,
            key: 'user'
        },
        {
            title: computed(() => t('创建时间')),
            key: 'createTime',
            width: 150,
            render(row) {
                return h('div', {}, moment(row.createTime).format('YYYY-MM-DD HH:mm:ss'));
            }
        },
        {
            title: computed(() => t('操作')),
            fixed: 'right',
            width: 200,
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
            slotName: 'select',
            span: 6
        },
        {
            type: 'input',
            key: 'name',
            span: 6,
            label: computed(() => t('任务名称'))
        },
        {
            type: 'slot',
            span: 12,
            slotName: 'queryFun'
        }
    ],
    fangDouTime: 0, //防抖时间
    filtersValueCallBack: (filters) => {
        filterData.value = filters;
    }
});

// 表单配置
const dialogConfig = ref({
    show: false
});
const dialogConfig2 = ref({
    show: false
});
const dialog = ref({
    show: false
});

const close = (type) => {
    if (type == 1) {
        tableConfig.value.pageConfig.currentPage = 1;
        initTableData();
    } else if (type == 2) {
        dialogConfig2.value.show = false;
        tableConfig.value.pageConfig.currentPage = 1;
        initTableData();
    } else {
        dialogConfig.value.show = false;
    }
};
const handle = async (type, row) => {
    globalData.row = {};
    globalData.allData = {};
    globalData.row = row;
    if (type == 1) {
        globalData.type = 'edit';
        if (row.taskType == 1) {
            Object.assign(dialogConfig2.value, {
                show: true,
                title: '编辑',
                width: '65%',
                okText: false,
                cancelText: false
            });
        } else if (row.taskType == 2) {
            taskSetForm.tableData.activeName = '1';
            taskSetForm.tableData.showSave = true;
            Object.assign(dialogConfig.value, {
                show: true,
                title: '编辑',
                width: '65%',
                okText: false,
                cancelText: false
            });
        }
    } else if (type == 2) {
        ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 任务?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                let res = await deleteDataId({ id: row.id });
                if (res.success == true) {
                    ElNotification({
                        title: res?.success ? t('删除成功') : t('删除失败'),
                        message: res?.msg,
                        type: res?.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
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
        if (row.taskType == 1) {
            globalData.type = 'detail';
            Object.assign(dialogConfig2.value, {
                show: true,
                title: '详情',
                width: '55%',
                okText: false,
                cancelText: false
            });
        } else if (row.taskType == 2) {
            Object.assign(dialog.value, {
                show: true,
                title: '详情',
                width: '65%',
                okText: false,
                cancelText: false
            });
        }
    }
};

const addTask = () => {
    globalData.type = 'add';
    globalData.row = {};
    globalData.allData = {};
    taskSetForm.tableData.activeName = '1';
    taskSetForm.tableData.showSave = true;

    Object.assign(dialogConfig.value, {
        show: true,
        title: '新增同步任务',
        width: '65%',
        okText: false,
        cancelText: false
    });
};

const addTask2 = () => {
    globalData.type = 'add';
    globalData.row = {};
    globalData.allData = {};

    Object.assign(dialogConfig2.value, {
        show: true,
        title: '新增单任务',
        width: '65%',
        okText: false,
        cancelText: false
    });
};

onMounted(() => {
    getTree(); //获取树形结构
    initTableData();
});
const getTree = async () => {
    let res = await getFindAll();
    if (res) {
        if (res.data) {
            state.treeData = setTreeData(res.data);
        }
    }
};

// init 数据
async function initTableData() {
    tableConfig.value.loading = true;
    // 接口参数
    let params = {
        page: tableConfig.value.pageConfig.currentPage,
        size: tableConfig.value.pageConfig.pageSize,
        businessId: state.businessId,
        jobId: props.tableRow.jobId,
        ...filterData.value
    };

    // 请求接口
    let res = await getFindPage(params);

    if (res.code == 0) {
        // 对返回的接口数据进行赋值与处理
        tableConfig.value.tableData = res.rows;
        tableConfig.value.pageConfig.total = res.total;
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
.operation {
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;

    .fields {
        display: flex;
        align-items: center;
        margin-right: 10px;

        i {
            margin-right: 2px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    .delete {
        display: flex;
        align-items: center;

        i {
            margin-right: 2px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
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
    min-height: 30px;
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

.right-btn {
    width: 100%;
    display: flex;
    justify-content: flex-end;
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
.j-White {
    :deep(.y9-dialog-body .y9-dialog-content) {
        background: var(--el-color-white) !important;
    }
}
</style>

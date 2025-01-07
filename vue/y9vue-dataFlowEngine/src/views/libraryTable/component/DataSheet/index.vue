<script setup lang="ts">
    import { reactive, onMounted, inject, computed, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    // 调整表格高度适应屏幕
    import EditorForm from './component/EditorForm.vue';
    import CopyTable from './copyTable.vue';
    import { state, getPage, tableHeight } from '@/views/libraryTable/component/DataSheet/data';
    import { currNode } from '@/views/libraryTable/component';
    import { buildTable, deleteTableInfo, getTableJob } from '@/api/libraryTable';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';

    const table = reactive({
        // 个人权限 表格的 配置条件
        tableConfig: {
            columns: [
                {
                    type: 'index',
                    title: computed(() => t('序号')),
                    width: 100,
                    fixed: 'left'
                },
                {
                    title: computed(() => t('数据表名称')),
                    width: 220,
                    key: 'name'
                },
                {
                    title: computed(() => t('表中文名称')),
                    key: 'cname'
                },
                {
                    title: computed(() => t('数据源名称')),
                    key: 'baseName',
                    showOverflowTooltip: true
                },
                {
                    title: computed(() => t('数据源类型')),
                    key: 'baseType'
                },
                {
                    title: computed(() => t('数据量')),
                    key: 'dataNum'
                },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    width: 280,
                    key: 'operation',
                    slot: 'operation'
                }
            ]
        }
    });

    // 初始化列表 请求
    onMounted(() => {
        currNode.value = {};
        state.tableConfig.columns = table.tableConfig.columns;
        getPage();
    });

    const gridData = ref([]);// 关联关系数据
    // 获取关联关系
    async function getData(tableId) {
        gridData.value = [];
        dialogTableVisible.value = true;
        let res = await getTableJob({tableId: tableId});
        if (res.code == 0) {
            gridData.value = res.data;
        } else {
            ElMessage.error(res.msg);
        }
    }

    const handle = (type, row) => {
        if(type == 4) {
            getData(row.id);
        } else if(type == 1) {
            state.row = row;
            Object.assign(state.dialogConfig, {
                show: true,
                okText: false,
                cancelText: false,
                width: '60%',
                // loading: true,
                title: '字段管理'
            });
        } else if(type == 2) {
            ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 数据表?`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
            .then(async () => {
                let res = await deleteTableInfo({ id: row.id });
                if (res) {
                    ElNotification({
                        title: res?.success ? t('删除成功') : t('删除失败'),
                        message: res?.msg,
                        type: res?.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    getPage();
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
            ElMessageBox.confirm(`确定是否更新库表结构`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
            .then(async () => {
                let res = await buildTable({ tableId: row.id });
                if (res) {
                    ElNotification({
                        title: res?.success ? t('更新成功') : t('更新失败'),
                        message: res?.msg,
                        type: res?.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    getPage();
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消更新'),
                    offset: 65
                });
            });
        }
    };
    const onPageSizeChange = (pageSize) => {
        state.tableConfig.pageConfig.pageSize = pageSize;
        getPage();
    };
    const onCurrPageChange = (currPage) => {
        state.tableConfig.pageConfig.currentPage = currPage;
        getPage();
    };

    const handleClickQuery = () => {
        getPage();
    };

    const dialogTableVisible = ref(false);

    const copyTablePage = () => {
        Object.assign(state.copyTabledialogConfig, {
            show: true,
            okText: false,
            cancelText: false,
            width: '55%',
            title: "复制表",
        })
    }
</script>

<template>
    <y9Dialog v-model:config="state.dialogConfig">
        <EditorForm :type="'edit'" :row="state.row"></EditorForm>
    </y9Dialog>
    <y9Dialog v-model:config="state.copyTabledialogConfig">
        <CopyTable></CopyTable>
    </y9Dialog>
    <y9Card :title="currNode.cname ? currNode.cname : $t('库表结构管理')">
        <y9Table
            v-loading="state.loading"
            :config="state.tableConfig"
            uniqueIdent="dataSheet"
            :filterConfig="state.filterConfig"
            @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange"
            @window-height-change="windowHeightChange"
        >
            <template v-slot:bnts>
                <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                    ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
                </el-button>
                <el-button type="primary" @click="copyTablePage" class="global-btn-main" v-if="state.copyBtn"
                    ><i class="ri-file-copy-2-line"></i>{{ $t('复制表') }}
                </el-button>
            </template>
            <template v-slot:operation="{ row }">
                <div class="operation">
                    <div class="fields" @click="handle(3, row)" v-if="row.status==0">
                        <i class="ri-edit-line"></i>
                        <span class="text">{{ $t('生成表') }}</span>
                    </div>
                    <div class="fields" @click="handle(4, row)">
                        <i class="ri-mastercard-line"></i>
                        <span class="text">{{ $t('关联关系') }}</span>
                    </div>
                    <div class="fields" @click="handle(1, row)">
                        <i class="ri-edit-line"></i>
                        <span class="text">{{ $t('字段管理') }}</span>
                    </div>
                    <div class="delete" @click="handle(2, row)">
                        <i class="ri-delete-bin-line"></i>
                        <span class="text">{{ $t('删除') }}</span>
                    </div>
                </div>
            </template>
        </y9Table>
    </y9Card>
    <el-dialog v-model="dialogTableVisible" title="数据表关联关系" width="1200">
        <el-table :data="gridData">
            <el-table-column property="taskName" label="任务名称" width="300" />
            <el-table-column property="sourceTable" label="源表" width="300" />
            <el-table-column property="sourceTableNum" label="源表数据量" />
            <el-table-column property="targetTable" label="目的表" width="300" />
            <el-table-column property="targetTableNum" label="目的表数据量" />
        </el-table>
    </el-dialog>
</template>

<style scoped lang="scss">
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
</style>

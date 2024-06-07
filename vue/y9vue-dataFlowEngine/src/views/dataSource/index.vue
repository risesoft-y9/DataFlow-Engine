<!--
 * @Author: fuyu
 * @Date: 2024-01-09 16:43:02
 * @LastEditors: fuyu
 * @LastEditTime: 2024-01-09 16:43:02
 * @Description: 数据源管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        @onRemoveNode="onRemoveNode"
        @onAddNode="onAddNode"
        @onNodeClick="onNodeClick"
    >
        <template #treeHeaderRight>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="onAddDataSourceType"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('新增数据源类型') }}</span>
            </el-button>
        </template>

        <!-- 右边卡片 -->
        <template v-slot:rightContainer v-if="currNode.id">
            <div v-if="currNode.$level === 1">
                <y9Card :title="`${currNode.name}`">
                    <dataSourceType
                        :flxedTree="fixedTreeRef"
                        :currNode="currNode"
                        :changeLoading="changeLoading"
                    ></dataSourceType>
                </y9Card>
            </div>
            <div v-else-if="currNode.$level === 2">
                <y9Card>
                    <template #title>
                        <div class="card-title">
                            {{ currNode.name }}
                            <span class="card-title-status" v-loading="statusLoading">
                                <i class="ri-check-line" v-if="dataStatus"></i>
                                <i class="ri-close-line" v-else></i>
                            </span>
                        </div>
                    </template>
                    <DataSource
                        :flxedTree="fixedTreeRef"
                        :currNode="currNode"
                        :changeLoading="changeLoading"
                    ></DataSource>
                </y9Card>
            </div>
        </template>
    </fixedTreeModule>

    <Dialog ref="dialogRef" :flxedTree="fixedTreeRef" :currNode="currNode"></Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import {
        getDataSourceType,
        getDataSourceByType,
        searchDataSource,
        deleteDataSource,
        deleteDataSourceType,
        checkDataStatus
    } from '@/api/dataSource';
    import { FormType } from './enums';
    import { inject, ref, reactive } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import Dialog from './comps/dialog/index.vue';
    import DataSource from './comps/dataSource/dataForm.vue';
    import dataSourceType from './comps/dataSourceType/index.vue';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const loading = ref(false);
    const changeLoading = (status: boolean) => {
        console.log('status==', status);
        loading.value = status;
    };

    //固定树组件实例
    const fixedTreeRef = ref(null);

    //弹窗组件实例
    const dialogRef = ref(null);

    //tree接口对象
    const treeApiObj = reactive({
        topLevel: getDataSourceType,
        childLevel: {
            api: getDataSourceByType,
            paramsUseNodeField: {
                category: 'name'
            }
        },
        search: {
            api: searchDataSource,
            searchKeyName: 'baseName'
        },
        flag: 'dataSource'
    });

    //当前节点数据
    const currNode = ref({});

    //新增节点-新增源数据
    const onAddNode = (node) => {
        // let FormTypeFlag = '';
        // switch (node.name) {
        //     case 'elastic':
        //         FormTypeFlag = FormType.DATA_SOURCE_ELASTIC;
        //         break;
        //     case 'ftp':
        //         FormTypeFlag = FormType.DATA_SOURCE_FTP;
        //         break;
        //     default:
        //         FormTypeFlag = FormType.DATA_SOURCE;
        //         break;
        // }
        //打开弹窗
        dialogRef.value.assginDialogConfig({
            show: true,
            okText: t('确认'),
            title: t('新增数据源'),
            resetText: t('重置'),
            flag: FormType.DATA_SOURCE,
            params: {
                type: node.name
            }
        });
    };

    //删除节点
    const onRemoveNode = (node) => {
        ElMessageBox.confirm(`${t('是否删除')}【${node.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                console.log('删除节点', node);
                let result = { success: false, msg: '' };
                //删除数据源类型
                if (node.$level === 1) {
                    loading.value = true;
                    //请求删除数据源类型的接口
                    let result = await deleteDataSourceType({ id: node.id });
                    loading.value = false;
                    //刷新树
                    fixedTreeRef.value?.onRefreshTree && fixedTreeRef.value.onRefreshTree();
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                }
                //删除数据源
                else if (node.$level === 2) {
                    loading.value = true;
                    //请求删除数据源的接口
                    let result = await deleteDataSource({ id: node.id });
                    loading.value = false;
                    //刷新树
                    fixedTreeRef.value?.onRefreshTree && fixedTreeRef.value.onRefreshTree();
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                }
            })
            .catch((e) => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
        //这里请求删除数据源接口
        //成功后刷新树
    };

    //点击节点时
    const onNodeClick = (node) => {
        currNode.value = node;
        if (currNode.value.$level === 2) {
            getDataStatus();
        }
    };

    //新增数据源类型
    const onAddDataSourceType = () => {
        //打开弹窗
        dialogRef.value.assginDialogConfig({
            show: true,
            title: t('新增数据源类型'),
            okText: t('确认'),
            resetText: false,
            flag: FormType.DATA_SOURCE_TYPE
        });
    };

    let dataStatus = ref();
    let statusLoading = ref(false);
    async function getDataStatus() {
        statusLoading.value = true;
        let result = await checkDataStatus({ sourceId: currNode.value.id });
        if (result.code == 0) dataStatus.value = result.data;
        statusLoading.value = false;
    }
</script>

<style lang="scss" scoped>
    .card-title {
        display: flex;
        align-items: center;
        .card-title-status {
            display: inline-block;
            width: 20px;
            height: 20px;
            color: var(--el-color-white);
            line-height: 20px;
            text-align: center;
            margin-left: 6px;
            > i {
                border-radius: 50%;
            }
            :deep(.el-loading-mask) {
                .el-loading-spinner .circular {
                    width: 25px;
                }
            }
        }
        .ri-check-line {
            background-color: var(--el-color-success);
        }
        .ri-close-line {
            background-color: var(--el-color-danger);
        }
    }
</style>

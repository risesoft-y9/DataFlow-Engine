<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        @onNodeClick="onNodeClick"
        @onAddNode="onAddNode"
        @onRemoveNode="onRemoveNode"
    >
        <template #treeHeaderRight>
            <el-button type="primary" class="global-btn-main" @click="handleAddTop"
                ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
            >
        </template>

        <!-- 右边卡片 -->
        <template v-slot:rightContainer v-if="currNode.id">
            <div>
                <y9Card :title="`${currNode.name}`">
                    <div v-if="isEditState">
                        <el-button :loading="loading" class="global-btn-main" type="primary" @click="onActions('save')">
                            <i class="ri-save-line"></i>
                            <span>{{ $t('保存') }}</span>
                        </el-button>

                        <el-button class="global-btn-second" @click="isEditState = false">
                            <i class="ri-close-line"></i>
                            <span> {{ $t('取消') }}</span>
                        </el-button>
                    </div>

                    <div v-else style="display: flex; justify-content: space-between; text-align: right">
                        <el-button class="global-btn-main" type="primary" @click="onActions('edit')">
                            <i class="ri-edit-line"></i>
                            <span>{{ $t('编辑') }}</span>
                        </el-button>
                    </div>
                    <baseInfoForm ref="baseInfoFormRef" :currInfo="currNode" :isEditState="isEditState"></baseInfoForm>
                </y9Card>
            </div>
        </template>
    </fixedTreeModule>
    <y9Dialog v-model:config="dialogConfig">
        <y9Form :config="formConfig" ref="y9TreeFormRef"></y9Form>
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { ref, computed, reactive } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getTableList, saveBusinessType, deleteBusinessType } from '@/api/businessClassify/index';
    import baseInfoForm from '@/views/businessClassify/treeIndex/comps/baseInfoForm.vue';
    const { t } = useI18n();

    const loading = ref(false);
    //固定树组件实例
    const fixedTreeRef = ref(null);
    //当前节点数据
    const currNode = ref({});
    // 编辑状态
    const isEditState = ref(false);
    //tree接口对象
    const treeApiObj = reactive({
        topLevel: getTableList,
        childLevel: {
            api: getTableList,
            params: {
                page: 1,
                size: 20
            }
        },
        search: {
            api: getTableList,
            searchKeyName: 'name',
            params: {
                page: 1,
                size: 20
            }
        },
        flag: 'businessClass'
    });

    const y9TreeFormRef = ref(null);
    let baseInfoFormRef = ref(); //表单

    const dialogConfig = ref({
        show: false,
        title: '新增',
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
                    parentId: currNode.value ? currNode.value.id : null,
                    id: null
                };

                console.log(params, 'params');

                await saveBusinessType(params)
                    .then(async (result) => {
                        if (result.code == 0) {
                            //刷新树
                            fixedTreeRef.value?.onRefreshTree && fixedTreeRef.value.onRefreshTree();
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

    const handleAddTop = () => {
        currNode.value = {};
        dialogConfig.value.show = true;
    };

    //点击节点时
    const onNodeClick = (node) => {
        currNode.value = node;
    };

    const onAddNode = (node) => {
        currNode.value = node;
        dialogConfig.value.show = true;
    };

    const onRemoveNode = (node) => {
        const deleteText =
            node.children.length > 0 ? '是否确定删除该节点，该节点下的所有子节点将会一并删除' : '是否确定删除该节点';
        ElMessageBox.confirm(`${t(deleteText)} ?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
            // loading: true,
        })
            .then(async () => {
                loading.value = true;
                let result = await deleteBusinessType({ id: node.id });
                if (result.code == 0) {
                    //刷新树
                    fixedTreeRef.value?.onRefreshTree && fixedTreeRef.value.onRefreshTree();
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
    };

    //操作按钮
    async function onActions(type) {
        if (type == 'edit') {
            //编辑
            isEditState.value = true;
        } else if (type == 'save') {
            //保存
            loading.value = true;
            let valid = await baseInfoFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
            if (valid) {
                // 将新增的所需的一些属性设置
                let params = {
                    name: baseInfoFormRef.value?.y9FormRef.model.name,
                    parentId: currNode.value.parentId,
                    id: currNode.value.id
                };

                let result = await saveBusinessType(params);
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    //修改后的数据更新到对应的树数据当中
                    await handAssginNode(result.data, currNode.value.id); //手动更新节点到tree

                    //取消编辑状态
                    isEditState.value = false;
                }
            }
            loading.value = false;
        }
    }
    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    //获取tree数据
    function getTreeData() {
        return fixedTreeRef.value.getTreeData();
    }
    /**手动更新节点信息
     * @param {Object} obj 需要合并的字段
     * @param {String} targetId 需要更新的节点id
     * @param {String} postChildId 请求的子节点id，如果存在该字段就请求子节点
     */
    async function handAssginNode(obj, targetId, postChildId?) {
        if (postChildId) {
            const childData = await postNode({ id: postChildId }); //重新请求当前节点的子节点，获取格式化后的子节点信息
            obj.children = childData;
        }

        //1.更新当前节点的信息
        const currNode = typeof targetId == 'object' ? targetId : findNode(getTreeData(), targetId); //找到树节点对应的节点信息
        if (currNode) {
            Object.assign(currNode, obj); //合并节点信息

            //2.手动设置点击当前节点
            handClickNode(currNode); //手动设置点击当前节点
        } else {
            refreshTree();
        }
    }

    // 刷新 组织架构树
    function refreshTree() {
        fixedTreeRef.value.onRefreshTree();
    }

    /**手动点击树节点
     * @param {Boolean} isExpand 是否展开节点
     */
    function handClickNode(node, isExpand?) {
        console.log(node, 'node11');

        fixedTreeRef.value?.handClickNode(node, isExpand);
    }
    //在树数据中根据id找到对应的节点并返回
    function findNode(treeData, targetId) {
        return fixedTreeRef.value.findNode(treeData, targetId);
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
</style>

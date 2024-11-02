<template>
    <div id="fixedDiv" class="fixed">
        <slot name="leftFixed">
            <y9Card>
                <template #header>
                    <div class="custom-left">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onRefreshTree"
                        >
                            <i class="ri-refresh-line"></i>
                            <span>{{ $t('刷新') }}</span>
                        </el-button>
                        <input autocomplete="new-password" hidden type="password" />
                        <el-input
                            v-if="!hiddenSearch"
                            v-model="apiSearchKey"
                            :placeholder="$t('请搜索')"
                            :size="fontSizeObj.buttonSize"
                            autocomplete
                            type="search"
                            @input="onSearchChange"
                        >
                            <template #prefix>
                                <i class="ri-search-line"></i>
                            </template>
                        </el-input>
                    </div>
                    <div class="custom-right">
                        <slot name="treeHeaderRight"></slot>
                    </div>
                </template>

                <y9Tree
                    ref="y9TreeRef"
                    v-loading="TreeLoading"
                    :data="alreadyLoadTreeData"
                    :lazy="lazy"
                    :load="onTreeLazyLoad"
                    @node-click="onNodeClick"
                    @node-expand="onNodeExpand"
                >
                    <template #title="{ item }">
                        <slot :item="item" name="title">
                            <i :class="item.title_icon"></i>
                            <span>{{ item[nodeLabel] }}</span>
                        </slot>
                    </template>
                    <template #actions="{ item }">
                        <slot :item="item" name="actions">
                            <i v-if="item.add_icon" class="ri-add-line" @click="onAddNode(item)"></i>
                            <i v-if="item.delete_icon" class="ri-delete-bin-7-line" @click="onRemoveNode(item)"></i>
                        </slot>
                    </template>
                </y9Tree>
            </y9Card>
        </slot>
    </div>

    <div class="right-container">
        <slot name="rightContainer"></slot>
    </div>
</template>

<script lang="ts" setup>
    import { useSettingStore } from '@/store/modules/settingStore';
    import { computed, inject, nextTick, onMounted, ref, watch } from 'vue';
    import y9_storage from '@/utils/storage';

    const props = defineProps({
        treeApiObj: {
            //tree接口对象,参数名称请严格按照以下注释进行传参。
            /**
			 {
				 topLevel:treeInterface,//一级接口，可以直接返回接口，也可以返回一个函数，函数里返回tree数据。
				 childLevel:{//子级（二级及二级以上）tree接口
					 api:getTreeItemById,//可以直接返回接口，也可以返回一个函数，函数里返回tree数据，如果返回函数，params就不需要传入。
					 params:{}//请求参数，
					 paramsUseNodeField:{//使用上级节点的某个字段作为参数，默认使用父节点的id，作为二级接口请求参数parentId的参数值。
						 parentId:'id'
					 }
				 },
				 search: {//搜索接口及参数
					 api:getTreeItemById,
					 params:{}//请求参数，
					 searchKeyName:"key"//搜索接口的参数key名称，默认是key
				 },
				 flag:"",//在此组件统一处理数据的标识
			 }
	        */
            type: Object
        },

        treeLoading: {
            //树加载状态
            type: Boolean,
            default: false
        },

        hiddenSearch: {
            //是否隐藏搜索条件
            type: Boolean,
            default: false
        },

        nodeLabel: {
            //显示的节点属性
            type: String,
            default: 'name'
        },

        showNodeDelete: {
            //是否显示删除icon
            type: Boolean,
            default: true
        }
    });

    const TreeLoading = ref(false);

    const emits = defineEmits(['onTreeClick', 'onDeleteTree', 'onNodeExpand', 'onRefreshTree']);

    //已经加载的tree数据
    const alreadyLoadTreeData = ref([]);

    //点击节点
    const onNodeClick = (node) => {
        emits('onNodeClick', node);
    };

    //节点被展开时触发的事件
    const onNodeExpand = (node) => {
        emits('onNodeExpand', node);
    };

    //移除节点
    const onRemoveNode = (node) => {
        emits('onRemoveNode', node);
    };

    //新增节点
    const onAddNode = (node) => {
        emits('onAddNode', node);
    };

    //格式化懒加载的数据
    async function formatLazyTreeData(data, isTopLevel?) {
        const treeType = props.treeApiObj?.childLevel?.params?.treeType; //二级接口的请求参数treeType
        // const parentId = y9_storage.getObjectItem('ssoUserInfo', 'parentId');
        // const isGlobalManager = y9_storage.getObjectItem('ssoUserInfo', 'globalManager');
        // const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

        for (let i = 0; i < data.length; i++) {
            const item = data[i];
            item.delete_icon = props.showNodeDelete; //是否显示删除icon

            const flag = props.treeApiObj?.flag;

            if (flag == 'libraryTableType' && isTopLevel && item.name == 'ftp') {
                data.splice(i, 1);
                continue;
            }

            if (flag === 'dataSource' || flag == 'libraryTableType') {
                if (isTopLevel) {
                    item.add_icon = true;
                    item.$level = 1;
                } else {
                    item.name = item.baseName;
                    item.isLeaf = true;
                    item.$level = 2;
                }
            }
            if (flag == 'libraryTableType') {
                if (isTopLevel) {
                    item.add_icon = false;
                    item.$level = 1;
                } else {
                    item.name = item.baseName;
                    item.isLeaf = true;
                    item.$level = 2;
                }
            }
            if (flag === 'libraryTable' || flag == 'RoleTable') {
                if (isTopLevel) {
                    item.isLeaf = true;
                }
            }

            if (flag === 'businessClass') {
                if (isTopLevel) {
                    item.add_icon = true;
                }
            }
        }
    }

    //tree实例
    const y9TreeRef = ref();

    //懒加载
    const onTreeLazyLoad = async (node, resolve: () => void) => {
        if (node.$level === 0) {
            //1.获取数据
            let data = [];
            let res;
            const flag = props.treeApiObj?.flag;
            if (flag != 'libraryTable' || flag == 'RoleTable') {
                res = await props.treeApiObj?.topLevel(); //请求一级节点接口
            } else {
                res = await props.treeApiObj?.topLevel({ type: 0 }); //请求一级节点接口
            }

            data = res.data || res.rows || res;

            //2.格式化数据
            await formatLazyTreeData(data, true);

            //初始化数据后，默认选中第一个节点、设置第一个节点展开，模拟点击第一个节点
            const callBack = () => {
                if (data.length > 0) {
                    const flag = props.treeApiObj?.flag;
                    if (flag != 'libraryTable') {
                        y9TreeRef.value.setCurrentKey(data[0].id); //设置第一个节点为高亮节点
                        y9TreeRef.value.setExpandKeys([data[0].id]); //设置第一个节点展开
                        onNodeClick(data[0]); //模拟点击第一个节点
                    }
                }
            };
            return resolve(data, callBack); //返回一级节点数据
        } else {
            //1.获取数据
            let data = [];
            if (node.children && node.children.length > 0) {
                //如果有传入的数据就取传入的数据
                data = node.children;
            } else {
                //如果没有则取接口数据
                //整合参数
                let params = {};
                const childLevelParams = props.treeApiObj?.childLevel?.params;

                if (childLevelParams) {
                    params = childLevelParams;
                }

                //使用上级节点的某个字段作为参数
                const paramsUseNodeField = props.treeApiObj?.childLevel?.paramsUseNodeField;
                if (paramsUseNodeField) {
                    for (let key in paramsUseNodeField) {
                        params[key] = node[paramsUseNodeField[key]];
                    }
                } else if (paramsUseNodeField !== false) {
                    params.parentId = node.id;
                }

                //请求接口
                const res = await props.treeApiObj?.childLevel?.api(params);
                data = res.data || res.rows || res;
            }

            //2.格式化数据
            await formatLazyTreeData(data, false);

            return resolve(data); //返回二级三级...节点数据
        }
    };

    //是否懒加载
    const lazy: boolean = ref(true);

    //搜索关键字
    const apiSearchKey = ref('');

    //防抖定时器
    let timer;
    const onSearchChange = (searchkey) => {
        clearTimeout(timer); //清空计时器
        timer = setTimeout(async () => {
            //重新计时
            if (searchkey) {
                //有值就请求搜索api

                //整合参数
                let params = {};
                const searchParams = props.treeApiObj?.search?.params;
                if (searchParams) {
                    params = searchParams;
                }

                const searchKeyName = props.treeApiObj?.search?.searchKeyName;
                if (searchKeyName) {
                    params[searchKeyName] = searchkey;
                } else {
                    params['key'] = searchkey;
                }

                TreeLoading.value = true;

                //请求搜索接口
                const res = await props.treeApiObj?.search?.api(params);
                if (res.code !== 0) {
                    ElMessage({
                        type: 'error',
                        message: res.msg,
                        offset: 65
                    });
                    TreeLoading.value = false;
                    return;
                }
                const data = res.data || res.rows;

                if (!data.length) {
                    alreadyLoadTreeData.value = [];
                    TreeLoading.value = false;
                    return;
                }

                const flag = props.treeApiObj?.flag;

                if (flag === 'dataSource' || flag === 'libraryTableType') {
                    //格式化tree数据
                    await formatLazyTreeData(data, true);

                    data.forEach(async (item) => {
                        for (let key in item.category) {
                            item[key] = item.category[key];
                        }
                        delete item.category;

                        if (item.children.length) {
                            await formatLazyTreeData(item.children, false);
                        }
                    });

                    alreadyLoadTreeData.value = data;
                } else {
                    //格式化tree数据
                    await formatLazyTreeData(data, true);
                    //根据搜索结果转换成tree结构显示出来
                    if (flag === 'businessClass') {
                        data.map((item) => {
                            if (item.parentId == '0') {
                                item.parentId = null;
                            }
                        });
                    }
                    alreadyLoadTreeData.value = await transformTreeBySearchResult(data);
                }

                TreeLoading.value = false;

                nextTick(() => {
                    y9TreeRef.value.setExpandAll();
                });
            } else {
                //没有就获取获取已经懒加载过的数据，并且设置默认选中第一个节点、默认展开第一个节点，模拟点击第一个节点

                alreadyLoadTreeData.value = y9TreeRef.value.getLazyTreeData(); //获取已经懒加载过的数据

                nextTick(() => {
                    if (alreadyLoadTreeData.value.length > 0) {
                        const flag = props.treeApiObj?.flag;
                        if (flag != 'libraryTable') {
                            y9TreeRef.value.setCurrentKey(alreadyLoadTreeData.value[0].id); //设置第一个节点为高亮节点
                            y9TreeRef.value.setExpandKeys([alreadyLoadTreeData.value[0].id]); //设置第一个节点展开
                            onNodeClick(alreadyLoadTreeData.value[0]); //模拟点击第一个节点
                        }
                    }
                });
            }
        }, 500);
    };

    //根据搜索结果转换成tree结构
    async function transformTreeBySearchResult(result) {
        const treeData = [];
        for (let i = 0; i < result.length; i++) {
            const item = result[i];
            if (!item.parentId) {
                //一级节点
                let node = item;
                const child = result.filter((resultItem) => resultItem.parentId === item.id);
                if (child.length > 0) {
                    //如果有子节点则递归子节点，进行组合
                    item.children = child;
                    const fn2 = async (data) => {
                        for (let j = 0; j < data.length; j++) {
                            const itemJ = data[j];
                            const childs = result.filter((resultItem) => resultItem.parentId === itemJ.id);
                            if (childs.length > 0) {
                                itemJ.children = childs;
                                if (itemJ.children.length > 0) {
                                    await fn2(itemJ.children);
                                }
                            }
                        }
                    };
                    await fn2(item.children); //递归子节点
                }
                treeData.push(item);
            }
        }

        return treeData;
    }

    /**
     * node 需要点击的节点
     * isExpand 点击后是否展开节点
     */
    function handClickNode(node, isExpand = true) {
        nextTick(() => {
            y9TreeRef.value?.setCurrentKey(node.id); //设置为高亮节点
            y9TreeRef.value?.handNodeClick(node); //重新点击节点，获取最新的节点信息
            if (isExpand) {
                y9TreeRef.value?.setExpandKeys([node.id]); //设置展开
            }
        });
    }

    //刷新tree
    function onRefreshTree() {
        const flag = props.treeApiObj?.flag;
        if (flag == 'libraryTable' || flag == 'RoleTable') {
            emits('onRefreshTree');
        }
        alreadyLoadTreeData.value = [];
        lazy.value = false;
        setTimeout(() => {
            lazy.value = true;
        }, 0);
    }

    //设置树数据
    async function setTreeData(data) {
        await formatLazyTreeData(data);
        alreadyLoadTreeData.value = data;
    }

    //获取树数据
    function getTreeData() {
        return alreadyLoadTreeData.value;
    }

    /**根据id查找tree节点信息
     * @param {Object} data 树的数据
     * @param {Object} targetId 目标节点id
     */
    function findNode(data, targetId) {
        let currNodeInfo = null;
        const fun = (data, targetId) => {
            for (let i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.id === targetId) {
                    currNodeInfo = item;
                    break;
                }
                if (item.children && item.children.length > 0) {
                    fun(item.children, targetId);
                }
            }
        };
        fun(data, targetId);
        return currNodeInfo;
    }

    defineExpose({
        onRefreshTree, //刷新tree
        y9TreeRef,
        onTreeLazyLoad,
        setTreeData,
        getTreeData,
        findNode,
        handClickNode
    });

    //固定模块样式
    // 在 fiexHeader 变化时 监听滚动事件，改变className
    const settingStore = useSettingStore();
    const layout = computed(() => settingStore.getLayout);
    const isFiexHeader = computed(() => settingStore.getFixedHeader);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    function listener() {
        const elementId = 'fixedDiv';
        const scroll_Y = window.scrollY;
        if (scroll_Y > 100 && document.getElementById(elementId)) {
            document.getElementById(elementId).className = 'fixed-after-scroll';
        }
        if (scroll_Y < 100 && document.getElementById(elementId)) {
            document.getElementById(elementId).className = 'fixed';
            if (layout.value === 'Y9Horizontal') {
                document.getElementById('fixedDiv').className = 'fixed fixed-herder-horizontal';
            }
        }
    }

    watch(isFiexHeader, (newV, oldV) => {
        if (!newV) {
            window.addEventListener('scroll', listener, false);
        } else {
            // 移除监听
            window.removeEventListener('scroll', listener, false);
        }
    });

    watch(layout, (newV, oldV) => {
        if (newV === 'Y9Horizontal') {
            document.getElementById('indexlayout').style.height = 'auto';
            document.getElementById('fixedDiv').className = 'fixed fixed-herder-horizontal';
        } else {
            document.getElementById('indexlayout').style.height = '';
            document.getElementById('fixedDiv').className = 'fixed';
        }
    });
    onMounted(() => {
        const layout = computed(() => settingStore.getLayout);
        if (layout.value === 'Y9Horizontal') {
            document.getElementById('indexlayout').style.height = 'auto';
            document.getElementById('fixedDiv').className = 'fixed fixed-herder-horizontal';
        } else {
            document.getElementById('indexlayout').style.height = '';
        }
        if (!isFiexHeader.value) {
            window.addEventListener('scroll', listener, false);
        }
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    @import '@/theme/global-vars.scss';
    @import '@/theme/global.scss';

    :deep(.node-title) {
        display: inline-flex;
        align-items: center;

        i {
            margin-right: 5px;
            font-weight: normal;
        }
    }

    :deep(.active-node) {
        .y9-tree-item-content {
            .y9-tree-item-content-div {
                .action-icons {
                    i {
                        color: var(--el-color-white) !important;
                    }
                }
            }
        }
    }

    :deep(.y9-card-header) {
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;

        .custom-left {
            display: flex;

            .el-button {
                margin-right: 16px;
            }

            .el-input__wrapper {
                padding: 0;
                border-radius: 30px;

                .el-input__prefix {
                    .el-input__prefix-inner {
                        & > :last-child {
                            margin-right: -34px;
                        }
                    }
                }
            }

            .el-input__inner {
                min-width: 220px;
                border-radius: 30px;
                box-shadow: 0 2px 4px #0000000d;
                padding: 10px 16px 10px 30px;
                line-height: v-bind('fontSizeObj.lineHeight') !important;

                font-size: v-bind('fontSizeObj.baseFontSize');
            }

            .ri-search-line {
                line-height: v-bind('fontSizeObj.lineHeight') !important;

                margin-left: 2px;
                font-size: v-bind('fontSizeObj.baseFontSize');
            }

            .el-input__clear {
                margin-left: 4px;
                line-height: v-bind('fontSizeObj.lineHeight') !important;
            }
        }
    }

    .right-container {
        margin-left: v-bind("settingStore.device === 'mobile'? '0px' : 'calc(26.6vw + 35px)'");
        :deep(.y9-card) {
            margin-bottom: 35px;
        }

        :deep(.y9-card:last-child) {
            margin-bottom: 0px;
        }
    }

    :deep(.el-col-12) {
        padding-left: 25px !important;
    }

    /* 固定左侧树 */
    .fixed {
        position: v-bind("settingStore.device === 'mobile'? 'static' : 'fixed'");
        width: v-bind("settingStore.device === 'mobile'? '100%' : '26.6vw'");
        animation: moveTop-2 0.2s;
        animation-fill-mode: forwards;
        margin-bottom: v-bind("settingStore.device === 'mobile'? '20px' : '0px'");

        :deep(.y9-card) {
            height: calc(100vh - #{$headerHeight} - #{$headerBreadcrumbHeight} - 35px);
            overflow: hidden;
            min-width: 345px;
        }
    }

    .fixed-herder-horizontal {
        margin-top: 60px;

        :deep(.y9-card) {
            height: calc(100vh - #{$headerHeight} - #{$headerBreadcrumbHeight} - 95px);
            overflow: hidden;
        }
    }

    .fixed-after-scroll {
        position: fixed;
        width: 26.6vw;
        animation: moveTop 0.2s;
        animation-fill-mode: forwards;

        :deep(.y9-card) {
            height: calc(100vh - 35px - 35px);
            overflow: auto;
            scrollbar-width: none;
        }
    }

    @keyframes moveTop {
        0% {
            top: calc(#{$headerHeight} + #{$headerBreadcrumbHeight});
        }
        100% {
            top: 35px;
        }
    }

    @keyframes moveTop-2 {
        0% {
            top: 35px;
        }
        100% {
            top: calc(#{$headerHeight} + #{$headerBreadcrumbHeight});
        }
    }

    @media screen and (max-width: 1578px) {
        .custom-left {
            margin-bottom: 10px;
        }
    }
</style>

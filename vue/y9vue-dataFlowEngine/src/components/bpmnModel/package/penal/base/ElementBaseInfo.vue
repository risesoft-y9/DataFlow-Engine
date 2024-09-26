<template>
    <div class="panel-tab__content">
        <el-form label-width="90px" size="small" @submit.prevent>
            <el-form-item label="ID">
                <el-input
                    v-model="elementBaseInfo.id"
                    :disabled="idEditDisabled"
                    clearable
                    maxlength="25"
                    show-word-limit
                    @change="updateBaseInfo('id')"
                />
            </el-form-item>
            <div v-show="elementBaseInfo.$type === 'bpmn:UserTask'">
                <el-form-item label="绑定任务">
                    <!-- <el-input v-model="elementBaseInfo.name" clearable @change="updateBaseInfo('name')" /> -->
                    <el-select
                        v-model="taskId"
                        placeholder="Select"
                        filterable
                        style="width: 240px"
                        ref="taskSelectRef"
                    >
                        <el-option
                            v-for="item in taskOptions"
                            :key="item.taskId"
                            :label="item.taskName"
                            :value="'' + item.taskId"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="执行节点">
                    <el-select v-model="executNode" placeholder="Select" filterable @change="changeExecutNode">
                        <el-option
                            v-for="item in bpmnStore.getExecutNode"
                            :key="item.instanceId"
                            :label="item.instanceId"
                            :value="'' + item.instanceId"
                        />
                    </el-select>
                </el-form-item>
                <!-- <el-form-item label="任务过滤参数"></el-form-item> -->
                <div
                    style="
                        margin-bottom: 25px;
                        font-size: 14px;
                        font-weight: bolder;
                        line-height: 17px;
                        display: flex;
                        align-items: center;
                    "
                    ><i class="ri-find-replace-line" style="font-size: 17px; margin: 0 8px 0 -8px"></i>任务搜索条件</div
                >
                <el-form-item label="执行环境">
                    <el-select
                        v-model="environment"
                        placeholder="Select"
                        filterable
                        style="width: 240px"
                        @change="changeEnvironment"
                    >
                        <el-option
                            v-for="item in bpmnStore.getEnvironmentResult"
                            :key="item.id"
                            :label="item.id"
                            :value="'' + item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="速度类型">
                    <el-select
                        v-model="dispatchType"
                        placeholder="Select"
                        style="width: 240px"
                        @change="changeDispatchType"
                    >
                        <el-option value="cron">cron</el-option>
                        <el-option value="固定速度">固定速度</el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="任务类型">
                    <el-tree-select
                        node-key="id"
                        clearable
                        check-strictly
                        :props="jobTypeProps"
                        v-model="jobType"
                        :data="bpmnStore.getJobTypeResult"
                        @change="changeJobType"
                    />
                </el-form-item>
                <el-form-item label="任务搜索">
                    <el-input
                        v-model="taskName"
                        @change="changeTaskName"
                        style="width: 240px"
                        placeholder="Please input"
                    />
                </el-form-item>
                <div style="display: flex; justify-content: center">
                    <el-button type="primary" @click="searchByParams">查询条件结果</el-button>
                </div>
            </div>
        </el-form>
        <br />
        <!-- 表格 -->
        <y9Table
            v-if="showTable"
            :config="tableConfig"
            @on-curr-page-change="onCurrentChange"
            @on-page-size-change="onPageSizeChange"
        >
        </y9Table>
    </div>
</template>
<script lang="ts" setup>
    import { forEach } from 'lodash';
    import { ref, defineProps, nextTick, toRefs, onMounted, watch, reactive, watchEffect } from 'vue';
    import { useBpmnStore } from '@/store/modules/bpmnStore';
    import { templateRef } from '@vueuse/core';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    const bpmnStore = useBpmnStore();
    bpmnStore.setInitData();
    const bpmnModelerXML = ref('');
    const props = defineProps({
        businessObject: Object,
        type: String,
        id: String,
        idEditDisabled: {
            type: Boolean,
            default: true
        },
        updateId: Boolean
    });
    // 表格配置
    let showTable = ref(false);
    onMounted(() => {
        showTable.value = true;
    });
    let tableConfig = ref({
        pageConfig: {
            currentPage: 1,
            pageSize: 15,
            total: 0,
            pageSizeOpts: [10, 15, 30, 60, 120, 240]
        },
        loading: false,
        // border: false,
        // headerBackground: true,
        columns: [
            {
                type: 'index',
                width: 60,
                fixed: 'left',
                title: computed(() => t('序号'))
            },
            {
                title: computed(() => t('任务名称')),
                key: 'name'
            }
        ],
        tableData: []
    });
    // 分页操作
    function onCurrentChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        bpmnStore.setTaskApiParams({
            pageNo: currPage
        });
        initTableData();
    }
    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        initTableData();
    }
    // init 数据
    async function initTableData() {
        tableConfig.value.loading = true;
        let res = await getDataSearch(bpmnStore.getTaskApiParams);
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
        // 请求接口
        tableConfig.value.loading = false;
    }
    const data = reactive({
        elementBaseInfo: {},
        bpmnElement: null
    });
    // 绑定任务的 id
    const taskId = ref('');
    // 执行节点
    const executNode = ref('');
    // 环境参数
    const environment = ref('Public');
    const changeEnvironment = () => {
        bpmnStore.setTaskApiParams({
            environment: environment.value
        });
    };
    // 速度的类型参数
    const dispatchType = ref('');
    const changeDispatchType = () => {
        bpmnStore.setTaskApiParams({
            dispatchType: dispatchType.value
        });
    };
    // 任务名称参数
    const taskName = ref('');
    const changeTaskName = () => {
        bpmnStore.setTaskApiParams({
            name: taskName.value
        });
    };
    // 任务类型参数
    const jobType = ref('');
    const jobTypeProps = {
        children: 'children',
        label: 'name'
    };
    const changeJobType = () => {
        bpmnStore.setTaskApiParams({
            jobType: jobType.value
        });
    };
    const taskOptions = ref(bpmnStore.taskResult);
    watch(
        () => bpmnStore.taskResult,
        () => {
            taskOptions.value = bpmnStore.taskResult;
        }
    );
    // 查询条件结果
    const taskSelectRef = ref();
    const searchByParams = async () => {
        const loading = ElLoading.service({ lock: true, text: '正在查询中', background: 'rgba(0, 0, 0, 0.3)' });
        await bpmnStore.setSearchResult();
        loading.close();
        nextTick(() => {
            // console.log(taskSelectRef.value);
            taskSelectRef.value?.toggleMenu();
        });
    };

    // const taskOptions = reactive([
    //     {
    //         taskName: '任务-1',
    //         taskId: 'id-1'
    //     },
    //     {
    //         taskName: '任务-2',
    //         taskId: 'id-2'
    //     },
    //     {
    //         taskName: '任务-3',
    //         taskId: 'id-3'
    //     }
    // ]);
    watch(taskId, (newVal) => {
        taskOptions.value.map((item, index) => {
            if (item.taskId === newVal) {
                elementBaseInfo.value.name = item.taskName;
                elementBaseInfo.value.taskId = item.taskId;
                updateBaseInfo('taskId');
                updateBaseInfo('name');
            }
        });
    });
    watch(executNode, (newVal) => {
        bpmnStore.getExecutNode.map((item, index) => {
            if (item.instanceId === newVal) {
                elementBaseInfo.value.executNode = executNode.value;
                updateBaseInfo('executNode');
            }
        });
    });

    let { elementBaseInfo, bpmnElement } = toRefs(data);

    async function setTaskOptionValue() {
        await bpmnStore.currentBpmnModeler.saveXML({ format: true }).then(({ xml }) => {
            bpmnModelerXML.value = xml;
            // 使用DOMParser解析XML字符串;
            let parser = new DOMParser();
            let xmlDoc = parser.parseFromString(xml, 'text/xml');
            let tagNameArray = Array.from(xmlDoc.getElementsByTagName('bpmn2:userTask'));
            forEach(tagNameArray, (item) => {
                let id = item.getAttribute('id');
                // console.log(id);
                if (id === props.id) {
                    if (item.getAttribute('taskId')) {
                        taskId.value = item.getAttribute('taskId');
                    } else {
                        taskId.value = '';
                    }
                    if (item.getAttribute('executNode')) {
                        executNode.value = item.getAttribute('executNode');
                    } else {
                        executNode.value = '';
                    }
                }
            });
        });
    }
    watch(
        () => props.id,
        (newVal) => {
            if (newVal) {
                nextTick(() => {
                    resetBaseInfo();
                });
            }
        },
        { deep: true }
    );

    watch(
        () => props.updateId,
        (newVal) => {
            if (props.updateId != null) {
                nextTick(() => {
                    if (props.updateId) {
                        elementBaseInfo.value.id = 'skip_' + elementBaseInfo.value.id;
                    } else {
                        if (elementBaseInfo.value.id.startsWith('skip_')) {
                            elementBaseInfo.value.id = elementBaseInfo.value.id.split('skip_')[1];
                        }
                    }
                    updateBaseInfo('id');
                });
            }
        },
        { deep: true }
    );

    function resetBaseInfo() {
        bpmnElement.value = window?.bpmnInstances?.bpmnElement || {};
        elementBaseInfo.value = JSON.parse(JSON.stringify(bpmnElement.value?.businessObject));

        if (elementBaseInfo.value && elementBaseInfo.value.$type === 'bpmn:SubProcess') {
            elementBaseInfo.value['isExpanded'] = elementBaseInfo.value.di?.isExpanded;
        }

        //UserTask和结束节点，修改排他网管路由表达式
        if (
            window?.bpmnInstances?.bpmnElement.type == 'bpmn:UserTask' ||
            window?.bpmnInstances?.bpmnElement.type == 'bpmn:EndEvent'
        ) {
            if (
                window?.bpmnInstances?.bpmnElement.businessObject.incoming != null &&
                window?.bpmnInstances?.bpmnElement.businessObject.incoming != undefined
            ) {
                //获取路由路线
                window?.bpmnInstances?.bpmnElement.businessObject.incoming.forEach((element) => {
                    let bpmnElementF = window.bpmnInstances.elementRegistry.get(element.id);
                    if (element.sourceRef.$type == 'bpmn:ExclusiveGateway') {
                        let body = '${routeToTaskId=="' + window?.bpmnInstances?.bpmnElement.id + '"}'; //设置表达式
                        let condition = window.bpmnInstances.moddle.create('bpmn:FormalExpression', { body });
                        window.bpmnInstances.modeling.updateProperties(bpmnElementF, {
                            conditionExpression: condition
                        });
                    }
                });
            }
        }
        setTaskOptionValue();
    }

    function updateBaseInfo(key) {
        if (key === 'id') {
            if (elementBaseInfo.value[key] == '') {
                return;
            }
            window.bpmnInstances.modeling.updateProperties(window?.bpmnInstances?.bpmnElement, {
                id: elementBaseInfo.value[key],
                di: { id: `${elementBaseInfo.value[key]}_di` }
            });

            //UserTask和结束节点修改id，修改排他网管路由表达式
            if (
                window?.bpmnInstances?.bpmnElement.type == 'bpmn:UserTask' ||
                window?.bpmnInstances?.bpmnElement.type == 'bpmn:EndEvent'
            ) {
                if (
                    window?.bpmnInstances?.bpmnElement.incoming != null &&
                    window?.bpmnInstances?.bpmnElement.incoming != undefined
                ) {
                    //获取路由路线
                    window?.bpmnInstances?.bpmnElement.incoming.forEach((element) => {
                        let bpmnElementF = window.bpmnInstances.elementRegistry.get(element.id);
                        if (element.source.type == 'bpmn:ExclusiveGateway') {
                            let body = '${routeToTaskId=="' + window?.bpmnInstances?.bpmnElement.id + '"}'; //设置表达式
                            let condition = window.bpmnInstances.moddle.create('bpmn:FormalExpression', { body });
                            window.bpmnInstances.modeling.updateProperties(bpmnElementF, {
                                conditionExpression: condition
                            });
                        }
                    });
                }
            }
            return;
        }
        if (key === 'isExpanded') {
            window?.bpmnInstances?.modeling.toggleCollapse(window?.bpmnInstances?.bpmnElement);
            return;
        }
        const attrObj = Object.create(null);
        attrObj[key] = elementBaseInfo.value[key];
        window.bpmnInstances.modeling.updateProperties(window?.bpmnInstances?.bpmnElement, attrObj);
    }
</script>

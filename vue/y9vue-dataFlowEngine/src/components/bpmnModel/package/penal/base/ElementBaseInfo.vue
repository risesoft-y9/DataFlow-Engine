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
            <el-form-item label="绑定任务" v-show="elementBaseInfo.$type === 'bpmn:UserTask'">
                <!-- <el-input v-model="elementBaseInfo.name" clearable @change="updateBaseInfo('name')" /> -->
                <el-select v-model="taskId" placeholder="Select" filterable style="width: 240px">
                    <el-option
                        v-for="item in taskOptions"
                        :key="item.taskName"
                        :label="item.taskName"
                        :value="item.taskId"
                    />
                </el-select>
            </el-form-item>
        </el-form>
    </div>
</template>
<script lang="ts" setup>
    import { forEach } from 'lodash';
    import { ref, defineProps, nextTick, toRefs, onMounted, watch, reactive } from 'vue';
    import { useBpmnStore } from '@/store/modules/bpmnStore';

    const bpmnStore = useBpmnStore();
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
    const data = reactive({
        elementBaseInfo: {},
        bpmnElement: null
    });
    const taskId = ref('');
    const taskOptions = reactive([
        {
            taskName: '任务-1',
            taskId: 'id-1'
        },
        {
            taskName: '任务-2',
            taskId: 'id-2'
        },
        {
            taskName: '任务-3',
            taskId: 'id-3'
        }
    ]);
    watch(taskId, (newVal) => {
        console.log(elementBaseInfo);
        taskOptions.map((item, index) => {
            if (item.taskId === newVal) {
                elementBaseInfo.value.name = item.taskName;
                elementBaseInfo.value.taskId = item.taskId;
                updateBaseInfo('taskId');
                updateBaseInfo('name');
                // 绑定一个任务后，删除该任务选项
                // taskOptions.splice(index, 1);
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

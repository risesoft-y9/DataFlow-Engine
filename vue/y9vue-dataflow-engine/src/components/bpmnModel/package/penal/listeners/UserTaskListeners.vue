<template>
    <div class="panel-tab__content">
        <el-table :data="elementListenersList" border size="small">
            <el-table-column align="center" label="序号" type="index" width="50px" />
            <el-table-column
                :formatter="(row) => listenerEventTypeObject[row.event]"
                align="center"
                label="事件类型"
                min-width="80px"
                show-overflow-tooltip
            />
            <el-table-column align="center" label="事件id" min-width="80px" prop="id" show-overflow-tooltip />
            <el-table-column
                :formatter="(row) => listenerTypeObject[row.listenerType]"
                align="center"
                label="监听器类型"
                min-width="80px"
                show-overflow-tooltip
            />
            <el-table-column align="center" label="操作" width="70px">
                <template v-slot="{ row, $index }">
                    <i class="ri-edit-2-line" title="编辑" @click="openListenerForm(row, $index)"></i>
                    <el-divider direction="vertical" />
                    <i
                        class="ri-delete-bin-line"
                        style="color: #ff4d4f"
                        title="移除"
                        @click="removeListener(row, $index)"
                    ></i>
                </template>
            </el-table-column>
        </el-table>
        <div class="element-drawer__button">
            <el-button :icon="Plus" size="small" type="primary" @click="openListenerForm(null)">添加监听器</el-button>
        </div>

        <!-- 监听器 编辑/创建 部分 -->
        <el-drawer
            v-model="listenerFormModelVisible"
            append-to-body
            destroy-on-close
            style="width: 480px"
            title="任务监听器"
        >
            <el-form ref="listenerFormRef" :model="listenerForm" label-width="96px" size="small" @submit.prevent>
                <el-form-item :rules="{ required: true, trigger: ['blur', 'change'] }" label="事件类型" prop="event">
                    <el-select v-model="listenerForm.event">
                        <el-option
                            v-for="i in Object.keys(listenerEventTypeObject)"
                            :key="i"
                            :label="listenerEventTypeObject[i]"
                            :value="i"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item :rules="{ required: true, trigger: ['blur', 'change'] }" label="监听器ID" prop="id">
                    <el-input v-model="listenerForm.id" clearable />
                </el-form-item>
                <el-form-item
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="监听器类型"
                    prop="listenerType"
                >
                    <el-select v-model="listenerForm.listenerType">
                        <el-option
                            v-for="i in Object.keys(listenerTypeObject)"
                            :key="i"
                            :label="listenerTypeObject[i]"
                            :value="i"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item
                    v-if="listenerForm.listenerType === 'classListener'"
                    key="listener-class"
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="Java类"
                    prop="class"
                >
                    <el-input v-model="listenerForm.class" clearable />
                </el-form-item>
                <el-form-item
                    v-if="listenerForm.listenerType === 'expressionListener'"
                    key="listener-expression"
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="表达式"
                    prop="expression"
                >
                    <el-input v-model="listenerForm.expression" clearable />
                </el-form-item>
                <el-form-item
                    v-if="listenerForm.listenerType === 'delegateExpressionListener'"
                    key="listener-delegate"
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="代理表达式"
                    prop="delegateExpression"
                >
                    <el-input v-model="listenerForm.delegateExpression" clearable />
                </el-form-item>
                <template v-if="listenerForm.listenerType === 'scriptListener'">
                    <el-form-item
                        key="listener-script-format"
                        :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写脚本格式' }"
                        label="脚本格式"
                        prop="scriptFormat"
                    >
                        <el-input v-model="listenerForm.scriptFormat" clearable />
                    </el-form-item>
                    <el-form-item
                        key="listener-script-type"
                        :rules="{ required: true, trigger: ['blur', 'change'], message: '请选择脚本类型' }"
                        label="脚本类型"
                        prop="scriptType"
                    >
                        <el-select v-model="listenerForm.scriptType">
                            <el-option label="内联脚本" value="inlineScript" />
                            <el-option label="外部脚本" value="externalScript" />
                        </el-select>
                    </el-form-item>
                    <el-form-item
                        v-if="listenerForm.scriptType === 'inlineScript'"
                        key="listener-script"
                        :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写脚本内容' }"
                        label="脚本内容"
                        prop="value"
                    >
                        <el-input v-model="listenerForm.value" clearable />
                    </el-form-item>
                    <el-form-item
                        v-if="listenerForm.scriptType === 'externalScript'"
                        key="listener-resource"
                        :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写资源地址' }"
                        label="资源地址"
                        prop="resource"
                    >
                        <el-input v-model="listenerForm.resource" clearable />
                    </el-form-item>
                </template>

                <template v-if="listenerForm.event === 'timeout'">
                    <el-form-item key="eventDefinitionType" label="定时器类型" prop="eventDefinitionType">
                        <el-select v-model="listenerForm.eventDefinitionType">
                            <el-option label="日期" value="date" />
                            <el-option label="持续时长" value="duration" />
                            <el-option label="循环" value="cycle" />
                            <el-option label="无" value="null" />
                        </el-select>
                    </el-form-item>
                    <el-form-item
                        v-if="!!listenerForm.eventDefinitionType && listenerForm.eventDefinitionType !== 'null'"
                        key="eventTimeDefinitions"
                        :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写定时器配置' }"
                        label="定时器"
                        prop="eventTimeDefinitions"
                    >
                        <el-input v-model="listenerForm.eventTimeDefinitions" clearable />
                    </el-form-item>
                </template>
            </el-form>

            <el-divider />
            <p class="listener-filed__title">
                <span
                    ><el-icon><Menu /></el-icon>注入字段：</span
                >
                <el-button size="small" type="primary" @click="openListenerFieldForm(null)">添加字段</el-button>
            </p>
            <el-table :data="fieldsListOfListener" border fit max-height="240" size="small" style="flex: none">
                <el-table-column align="center" label="序号" type="index" width="50px" />
                <el-table-column align="center" label="字段名称" min-width="100px" prop="name" />
                <el-table-column
                    :formatter="(row) => fieldTypeObject[row.fieldType]"
                    align="center"
                    label="字段类型"
                    min-width="80px"
                    show-overflow-tooltip
                />
                <el-table-column
                    :formatter="(row) => row.string || row.expression"
                    align="center"
                    label="字段值/表达式"
                    min-width="100px"
                    show-overflow-tooltip
                />
                <el-table-column align="center" label="操作" width="70px">
                    <template v-slot="{ row, $index }">
                        <i class="ri-edit-2-line" title="编辑" @click="openListenerFieldForm(row, $index)"></i>
                        <el-divider direction="vertical" />
                        <i
                            class="ri-delete-bin-line"
                            style="color: #ff4d4f"
                            title="移除"
                            @click="removeListenerField(row, $index)"
                        ></i>
                    </template>
                </el-table-column>
            </el-table>

            <div class="element-drawer__button">
                <el-button size="small" @click="listenerFormModelVisible = false">取 消</el-button>
                <el-button size="small" type="primary" @click="saveListenerConfig">保 存</el-button>
            </div>
        </el-drawer>

        <!-- 注入西段 编辑/创建 部分 -->
        <el-dialog
            v-model="listenerFieldFormModelVisible"
            append-to-body
            destroy-on-close
            title="字段配置"
            width="600px"
        >
            <el-form
                ref="listenerFieldFormRef"
                :model="listenerFieldForm"
                label-width="96px"
                size="small"
                style="height: 136px"
                @submit.prevent
            >
                <el-form-item :rules="{ required: true, trigger: ['blur', 'change'] }" label="字段名称：" prop="name">
                    <el-input v-model="listenerFieldForm.name" clearable />
                </el-form-item>
                <el-form-item
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="字段类型："
                    prop="fieldType"
                >
                    <el-select v-model="listenerFieldForm.fieldType">
                        <el-option
                            v-for="i in Object.keys(fieldTypeObject)"
                            :key="i"
                            :label="fieldTypeObject[i]"
                            :value="i"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item
                    v-if="listenerFieldForm.fieldType === 'string'"
                    key="field-string"
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="字段值："
                    prop="string"
                >
                    <el-input v-model="listenerFieldForm.string" clearable />
                </el-form-item>
                <el-form-item
                    v-if="listenerFieldForm.fieldType === 'expression'"
                    key="field-expression"
                    :rules="{ required: true, trigger: ['blur', 'change'] }"
                    label="表达式："
                    prop="expression"
                >
                    <el-input v-model="listenerFieldForm.expression" clearable />
                </el-form-item>
            </el-form>
            <template v-slot:footer>
                <el-button size="small" @click="listenerFieldFormModelVisible = false">取 消</el-button>
                <el-button size="small" type="primary" @click="saveListenerFiled">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script lang="ts" setup>
    import { ref, defineProps, nextTick, toRefs, onMounted, watch, reactive } from 'vue';
    import { createListenerObject, updateElementExtensions } from '../../utils';
    import { initListenerForm, initListenerType, eventType, listenerType, fieldType } from './utilSelf';

    const props = defineProps({
        id: String,
        type: String
    });
    const data = reactive({
        elementListenersList: [],
        listenerEventTypeObject: eventType,
        listenerTypeObject: listenerType,
        listenerFormModelVisible: false,
        listenerForm: {},
        fieldTypeObject: fieldType,
        fieldsListOfListener: [],
        listenerFieldFormModelVisible: false, // 监听器 注入字段表单弹窗 显示状态
        editingListenerIndex: -1, // 监听器所在下标，-1 为新增
        editingListenerFieldIndex: -1, // 字段所在下标，-1 为新增
        listenerFieldForm: {}, // 监听器 注入字段 详情表单
        prefix: 'flowable',
        listenerFormRef: '',
        listenerFieldFormRef: '',
        otherExtensionList: [],
        bpmnElementListeners: []
    });

    let {
        elementListenersList,
        listenerEventTypeObject,
        listenerTypeObject,
        listenerFormModelVisible,
        listenerForm,
        fieldTypeObject,
        fieldsListOfListener,
        listenerFieldFormModelVisible,
        editingListenerIndex,
        editingListenerFieldIndex,
        listenerFieldForm,
        prefix,
        listenerFormRef,
        listenerFieldFormRef,
        otherExtensionList,
        bpmnElementListeners
    } = toRefs(data);

    watch(
        () => props.id,
        (newVal) => {
            if (newVal) {
                nextTick(() => {
                    resetListenersList();
                });
            }
        },
        { deep: true }
    );

    onMounted(() => {});

    function resetListenersList() {
        otherExtensionList.value = [];
        bpmnElementListeners.value =
            window.bpmnInstances.bpmnElement.businessObject?.extensionElements?.values?.filter(
                (ex) => ex.$type === `${prefix.value}:TaskListener`
            ) ?? [];
        elementListenersList.value = bpmnElementListeners.value.map((listener) => initListenerType(listener));
    }

    function openListenerForm(listener, index) {
        if (listener) {
            listenerForm.value = initListenerForm(listener);
            editingListenerIndex.value = index;
        } else {
            listenerForm.value = {};
            editingListenerIndex.value = -1; // 标记为新增
        }
        if (listener && listener.fields) {
            fieldsListOfListener.value = listener.fields.map((field) => ({
                ...field,
                fieldType: field.string ? 'string' : 'expression'
            }));
        } else {
            fieldsListOfListener.value = [];
            listenerForm.value['fields'] = [];
        }
        // 打开侧边栏并清楚验证状态
        listenerFormModelVisible.value = true;
        nextTick(() => {
            if (listenerFormRef.value) listenerFormRef.value.clearValidate();
        });
    }

    // 移除监听器
    function removeListener(listener, index) {
        ElMessageBox.confirm('确认移除该监听器吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                bpmnElementListeners.value.splice(index, 1);
                elementListenersList.value.splice(index, 1);
                updateElementExtensions(
                    window.bpmnInstances.bpmnElement,
                    otherExtensionList.value.concat(bpmnElementListeners.value)
                );
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '操作取消',
                    offset: 65
                });
            });
    }

    // 保存监听器
    async function saveListenerConfig() {
        let validateStatus = await listenerFormRef.value.validate();
        if (!validateStatus) return; // 验证不通过直接返回
        const listenerObject = createListenerObject(listenerForm.value, true, prefix.value);
        if (editingListenerIndex.value === -1) {
            bpmnElementListeners.value.push(listenerObject);
            elementListenersList.value.push(listenerForm.value);
        } else {
            bpmnElementListeners.value.splice(editingListenerIndex.value, 1, listenerObject);
            elementListenersList.value.splice(editingListenerIndex.value, 1, listenerForm.value);
        }
        // 保存其他配置
        otherExtensionList.value =
            window.bpmnInstances.bpmnElement.businessObject?.extensionElements?.values?.filter(
                (ex) => ex.$type !== `${prefix.value}:TaskListener`
            ) ?? [];
        updateElementExtensions(
            window.bpmnInstances.bpmnElement,
            otherExtensionList.value.concat(bpmnElementListeners.value)
        );
        // 4. 隐藏侧边栏
        listenerFormModelVisible.value = false;
        listenerForm.value = {};
    }

    // 打开监听器字段编辑弹窗
    function openListenerFieldForm(field, index) {
        listenerFieldForm.value = field ? JSON.parse(JSON.stringify(field)) : {};
        editingListenerFieldIndex.value = field ? index : -1;
        listenerFieldFormModelVisible.value = true;
        nextTick(() => {
            if (listenerFieldFormRef.value) listenerFieldFormRef.value.clearValidate();
        });
    }

    // 保存监听器注入字段
    async function saveListenerFiled() {
        let validateStatus = await listenerFieldFormRef.value.validate();
        if (!validateStatus) return; // 验证不通过直接返回
        if (editingListenerFieldIndex.value === -1) {
            fieldsListOfListener.value.push(listenerFieldForm.value);
            listenerForm.fields.value.push(listenerFieldForm.value);
        } else {
            fieldsListOfListener.value.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value);
            listenerForm.value.fields.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value);
        }
        listenerFieldFormModelVisible.value = false;
        nextTick(() => {
            listenerFieldForm.value = {};
        });
    }

    // 移除监听器字段
    function removeListenerField(field, index) {
        ElMessageBox.confirm('确认移除该字段吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                fieldsListOfListener.value.splice(index, 1);
                listenerForm.value.fields.splice(index, 1);
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '操作取消',
                    offset: 65
                });
            });
    }
</script>

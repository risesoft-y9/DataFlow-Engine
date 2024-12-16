<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { addTaskForm, globalData, goalTableForm, goalTableRef, tableSetForm } from '../data';
import { getApiField, getTableField } from '@/api/libraryTable';
import { v4 as uuidv4 } from 'uuid';

const emits = defineEmits(['next', 'top', 'goBack', 'goBackSet', 'setTask', 'close']);
//去除警告
const other = () => {
    emits('next');
    emits('top');
    emits('goBack');
    emits('goBackSet');
    emits('setTask');
    emits('close');
};

const next = async () => {
    let validate = await goalTableRef.value.validate();
    if (validate) {
        emits('next');
    }
};
const top = () => {
    emits('top');
};
const state = reactive({
    defaultProps: {
        children: 'children',
        label: 'label'
    }
});
onMounted(() => {
    setInitData();
});

const setInitData = () => {
    goalTableRef?.value?.resetFields();
    if (globalData.type == 'add') {
        for (let key in goalTableForm.tableData) {
            if (key == 'field') {
                goalTableForm.tableData[key] = [];
            } else if (key == 'targeName') {
                goalTableForm.tableData[key] = goalTableForm.tableData[key];
            } else if (key == 'writerType') {
                goalTableForm.tableData[key] = 'insert';
            } else if (key == 'differentField') {
                goalTableForm.tableData.differentField = [];
                let id = uuidv4();
                let obj = {
                    id,
                    value: '',
                    rules1: [],
                    rules2: []
                };
                goalTableForm.tableData.differentField.push(obj);
            } else if (key == 'convertField') {
                goalTableForm.tableData.convertField = [];
                let id = uuidv4();
                let obj = {
                    id,
                    value: ''
                };
                goalTableForm.tableData.convertField.push(obj);
            } else if (key == 'dateField') {
                goalTableForm.tableData.dateField = [];
                let id = uuidv4();
                let obj = {
                    id,
                    value: ''
                };
                goalTableForm.tableData.dateField.push(obj);
            } else {
                goalTableForm.tableData[key] = null;
            }
        }
    } else {
    }
};

const getTableFieldList = async () => {
    let res;
    if (addTaskForm.tableData.targetType == 'api') {
        res = await getApiField({ apiId: goalTableForm.tableData.targetTable });
    } else {
        res = await getTableField({ tableId: goalTableForm.tableData.targetTable });
    }
    if (res) {
        goalTableForm.tableFieldList = res.data;
        goalTableForm.tableData.targetCloumn = []; //清空字段详情勾选
        res.data.forEach((item) => {
            item.label = `${item.name}:${item.cname}(${item.fieldType})`;
            //设置勾选
            goalTableForm.tableData.targetCloumn.push(item.id);
        });
    }
};

const tableChange = () => {
    getTableFieldList();
};
//新增异字段
const addField = () => {
    let id = uuidv4();
    let obj = {
        id,
        value: '',
        rules1: [],
        rules2: []
    };
    goalTableForm.tableData.differentField.push(obj);
};

const deleteField = (item, index) => {
    goalTableForm.tableData.differentField.splice(index, 1);
    // goalTableForm.tableData.differentField.forEach((ret, i) => {
    //   if (ret.id == item.id) {
    //     goalTableForm.tableData.differentField.splice(i, 1)
    //   }
    // })
};
const dataAddFormatId = () => {
    let id = uuidv4();
    let obj = {
        id,
        value: ''
    };
    goalTableForm.tableData.dateField.push(obj);
};

const dataDeleteFormatId = (item, index) => {
    goalTableForm.tableData.dateField.splice(index, 1);
    // goalTableForm.tableData.convertField.forEach((ret, i) => {
    //   if (ret.id == item.id) {
    //     goalTableForm.tableData.convertField.splice(i, 1)
    //   }
    // })
};

const dataAddField = () => {
    let id = uuidv4();
    let obj = {
        id,
        value: ''
    };
    goalTableForm.tableData.convertField.push(obj);
};

const dataDeleteField = (item, index) => {
    goalTableForm.tableData.convertField.splice(index, 1);
    // goalTableForm.tableData.convertField.forEach((ret, i) => {
    //   if (ret.id == item.id) {
    //     goalTableForm.tableData.convertField.splice(i, 1)
    //   }
    // })
};

//源头字段
const filteredTableFieldList = computed(() => {
    return tableSetForm.tableFieldList.filter((item) => {
        return tableSetForm.tableData.sourceCloumn.includes(item.id);
    });
});
//目标字段
const targetFilteredTableFieldList = computed(() => {
    return goalTableForm.tableFieldList
        .filter((item) => {
            return goalTableForm.tableData.targetCloumn.includes(item.id);
        })
        .map((item) => {
            return {
                ...item,
                label: `${item.name}:${item.cname}(${item.fieldType})`
            };
        });
});

const fieldChange = (item, type, props) => {
    if (type == 1) {
        //源头
        if (item.source == '' || item.source == undefined) {
            item.rules2 = [];
            goalTableRef.value.clearValidate(props);
        } else {
            item.rules2 = [{ required: true, message: '请选择', trigger: ['blur', 'change'] }];
        }
    } else {
        //目标
        if (item.target == '' || item.target == undefined) {
            item.rules1 = [];
            goalTableRef.value.clearValidate(props);
        } else {
            item.rules1 = [{ required: true, message: '请选择', trigger: ['blur', 'change'] }];
        }
    }
};
const formatIdChange = (item, type, index) => {
    if (item.fieldName == '' || item.fieldName == undefined) {
        item.rules2 = [];
        goalTableRef.value.clearValidate(`dateField[${index}].format`);
    } else {
        item.rules2 = [{ required: true, message: '请输入', trigger: ['blur', 'change'] }];
    }

    if (item.format == '' || item.format == undefined) {
        item.rules1 = [];
        goalTableRef.value.clearValidate(`dateField[${index}].fieldName`);
    } else {
        item.rules1 = [{ required: true, message: '请选择', trigger: ['blur', 'change'] }];
    }
};
let rules = ref({
    targetTable: [{ required: true, message: '请输入', trigger: ['blur', 'change'] }],
    writerType: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
    updateField: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
    value1: [],
    oldData: []
});

const checkBoxChange = () => {
    console.log(goalTableForm.tableData.targetCloumn, 'goalTableForm.tableData.targetCloumn');
    const targetFieldSet = new Set(goalTableForm.tableData.targetCloumn);
    console.log(targetFieldSet, 'targetFieldSet');
    goalTableForm.tableData.differentField.forEach((item, index) => {
        if (!targetFieldSet.has(item.target)) {
            item.target = null;
            item.rules1 = [];
            goalTableRef.value.clearValidate(`differentField[${index}].target`);
            goalTableRef.value.clearValidate(`differentField[${index}].source`);
        }
    });

    goalTableForm.tableData.convertField.forEach((ret) => {
        if (!targetFieldSet.has(ret.fieldName)) {
            ret.fieldName = null;
            ret.oldData = null;
            ret.newData = null;
            ret.rules1 = [];
            ret.rules2 = [];
            ret.rules3 = [];
        }
    });
    goalTableForm.tableData.updateField.forEach((res, i) => {
        if (!targetFieldSet.has(res)) {
            goalTableForm.tableData?.updateField?.splice(i, 1);
        }
    });

    goalTableForm.tableData.dateField.forEach((item, index) => {
        if (!targetFieldSet.has(item.fieldName)) {
            item.fieldName = null;
            item.rules2 = [];
            goalTableRef.value.clearValidate(`dateField[${index}].fieldName`);
            goalTableRef.value.clearValidate(`dateField[${index}].format`);
        }
    });
};

const dataChange = (item, type, index) => {
    if (type == 1) {
        if (item.fieldName == '' || item.fieldName == undefined) {
            if (item.oldData == '' || item.oldData == undefined) {
                item.rules2 = [];
            }
            if (item.newData == '' || item.newData == undefined) {
                item.rules3 = [];
            }
            goalTableRef.value.clearValidate(`convertField[${index}].oldData`);
            goalTableRef.value.clearValidate(`convertField[${index}].newData`);
            if (item.oldData || item.newData) {
                item.rules1 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            } else {
                goalTableRef.value.clearValidate(`convertField[${index}].fieldName`);
                item.rules1 = [];
            }
        } else {
            item.rules2 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            item.rules3 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];

            if (item.oldData || item.newData) {
                item.rules1 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            }
        }
    } else if (type == 2) {
        if (item.oldData == '' || item.oldData == undefined) {
            if (item.fieldName == '' || item.fieldName == undefined) {
                item.rules1 = [];
            }
            if (item.newData == '' || item.newData == undefined) {
                item.rules3 = [];
            }
            goalTableRef.value.clearValidate(`convertField[${index}].fieldName`);
            goalTableRef.value.clearValidate(`convertField[${index}].newData`);
            if (item.fieldName || item.newData) {
                item.rules2 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            } else {
                goalTableRef.value.clearValidate(`convertField[${index}].oldData`);
                item.rules2 = [];
            }
        } else {
            item.rules1 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            item.rules3 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];

            if (item.fieldName || item.newData) {
                item.rules2 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            }
        }
    } else {
        if (item.newData == '' || item.newData == undefined) {
            if (item.fieldName == '' || item.fieldName == undefined) {
                item.rules1 = [];
            }
            if (item.oldData == '' || item.oldData == undefined) {
                item.rules2 = [];
            }

            goalTableRef.value.clearValidate(`convertField[${index}].fieldName`);
            goalTableRef.value.clearValidate(`convertField[${index}].oldData`);
            if (item.fieldName || item.oldData) {
                item.rules3 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            } else {
                goalTableRef.value.clearValidate(`convertField[${index}].newData`);
                item.rules3 = [];
            }
        } else {
            item.rules1 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            item.rules2 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];

            if (item.fieldName || item.oldData) {
                item.rules3 = [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }];
            }
        }
    }
};
const radioChange = (e) => {
    goalTableForm.tableData.updateField = '';
};
let ruleFormRef = ref(goalTableRef);
</script>
<template>
    <el-form ref="ruleFormRef" :model="goalTableForm.tableData" :rules="rules">
        <el-descriptions border :column="1">
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span v-if="addTaskForm.tableData.targetType == 'api'">接口名称</span>
                        <span v-else-if="addTaskForm.tableData.targetType == 'ftp'">文件目录</span>
                        <span v-else>表名称</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="targetTable">
                    <el-select
                        @change="tableChange"
                        v-model="goalTableForm.tableData.targetTable"
                        class="m-2"
                        placeholder="请选择"
                        filterable
                        v-if="addTaskForm.tableData.targetType != 'ftp'"
                    >
                        <el-option
                            v-for="item in goalTableForm.tableOptions"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                    <el-input
                        placeholder="填写存储的文件目录"
                        v-model="goalTableForm.tableData.targetTable"
                        v-else
                    ></el-input>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="字段详情" v-if="addTaskForm.tableData.targetType != 'ftp'">
                <template #label>
                    <div>
                        <span>字段详情</span>
                    </div>
                </template>
                <el-form-item>
                    <el-checkbox-group v-model="goalTableForm.tableData.targetCloumn" class="field">
                        <div v-for="item in goalTableForm.tableFieldList" class="type">
                            <el-checkbox @change="checkBoxChange" v-model="item.id" :label="item.id">
                                {{ item.name }}:{{ item.cname }}({{ item.fieldType }})
                            </el-checkbox>
                        </div>
                    </el-checkbox-group>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="执行类">
                <template #label>
                    <div>
                        <span>执行类</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="targeName">
                    <el-select
                        v-model="goalTableForm.tableData.targeName"
                        class="m-2"
                        value-key="id"
                        placeholder="请选择"
                    >
                        <el-option
                            v-for="item in goalTableForm.classListOptions"
                            :key="item.id"
                            :label="item.className"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
            </el-descriptions-item>
            <div v-if="addTaskForm.tableData.targetType == 'ftp'">
                <el-descriptions-item label-align="center" label="缓存大小">
                    <template #label>
                        <div>
                            <span>缓存大小</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item>
                                <el-input
                                    :controls="false"
                                    v-model="tableSetForm.tableData.tableNumber"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">缓存大小/kb，默认500</div>
                    </div>
                </el-descriptions-item>
            </div>
            <div v-else>
                <el-descriptions-item label-align="center" label="异字段同步">
                    <template #label>
                        <div>
                            <span>异字段同步</span>
                        </div>
                    </template>
                    <div class="sync" v-for="(items, index) in goalTableForm.tableData.differentField">
                        <div class="w100">
                            <el-form-item :rules="items.rules1" :prop="`differentField[${index}].source`">
                                <el-select
                                    v-model="items.source"
                                    value-key="id"
                                    clearable
                                    placeholder="请选择源头字段"
                                    filterable
                                    @change="fieldChange(items, 1, `differentField[${index}].target`)"
                                >
                                    <el-option
                                        v-for="item in filteredTableFieldList"
                                        :key="item.id"
                                        :label="`${item.name}:${item.cname}(${item.fieldType})`"
                                        :value="item.id"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div class="seg"> -</div>
                        <div class="w100">
                            <el-form-item :rules="items.rules2" :prop="`differentField[${index}].target`">
                                <el-select
                                    v-model="items.target"
                                    value-key="id"
                                    filterable
                                    placeholder="请选择目标字段"
                                    clearable
                                    @change="fieldChange(items, 2, `differentField[${index}].source`)"
                                >
                                    <el-option
                                        v-for="item in targetFilteredTableFieldList"
                                        :key="item.id"
                                        :label="`${item.name}:${item.cname}(${item.fieldType})`"
                                        :value="item.id"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div class="icon" @click="addField">
                            <el-icon size="20">
                                <CirclePlus />
                            </el-icon>
                        </div>
                        <div
                            class="icon"
                            v-if="goalTableForm.tableData.differentField.length > 1"
                            @click="deleteField(items, index)"
                        >
                            <el-icon size="20">
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" label="日期格式转换">
                    <template #label>
                        <div>
                            <span>日期格式转换</span>
                            <!--            <span class="y9-required-icon">*</span>-->
                        </div>
                    </template>
                    <div class="sync" v-for="(items, index) in goalTableForm.tableData.dateField">
                        <div class="w100">
                            <el-form-item :rules="items.rules1" :prop="`dateField[${index}].fieldName`">
                                <el-select
                                    clearable
                                    v-model="items.fieldName"
                                    value-key="id"
                                    placeholder="请选择转换字段"
                                    filterable
                                    @change="formatIdChange(items, 1, index)"
                                >
                                    <el-option
                                        v-for="item in filteredTableFieldList"
                                        :key="item.id"
                                        :label="`${item.name}:${item.cname}`"
                                        :value="item.id"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div class="seg">-</div>
                        <div class="w100">
                            <el-form-item :rules="items.rules2" :prop="`dateField[${index}].format`">
                                <el-input
                                    @change="formatIdChange(items, 2, index)"
                                    clearable
                                    placeholder="请输入"
                                    v-model="items.format"
                                ></el-input>
                            </el-form-item>
                        </div>
                        <div class="icon" @click="dataAddFormatId">
                            <el-icon size="20">
                                <CirclePlus />
                            </el-icon>
                        </div>
                        <div
                            class="icon"
                            v-if="goalTableForm.tableData.dateField.length > 1"
                            @click="dataDeleteFormatId(items, index)"
                        >
                            <el-icon size="20">
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" label="数据转换">
                    <template #label>
                        <div>
                            <span>数据转换</span>
                        </div>
                    </template>
                    <div class="sync" v-for="(items, index) in goalTableForm.tableData.convertField">
                        <div class="w100">
                            <el-form-item :rules="items.rules1" :prop="`convertField[${index}].fieldName`">
                                <el-select
                                    clearable
                                    v-model="items.fieldName"
                                    value-key="id"
                                    placeholder="请选择转换字段"
                                    filterable
                                    @change="dataChange(items, 1, index)"
                                >
                                    <el-option
                                        v-for="item in filteredTableFieldList"
                                        :key="item.id"
                                        :label="`${item.name}:${item.cname}`"
                                        :value="item.id"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div class="seg">:</div>
                        <div class="w100">
                            <el-form-item :rules="items.rules2" :prop="`convertField[${index}].oldData`">
                                <el-input
                                    @change="dataChange(items, 2, index)"
                                    clearable
                                    placeholder="原始数据"
                                    v-model="items.oldData"
                                ></el-input>
                            </el-form-item>
                        </div>
                        <div class="seg">-</div>
                        <div class="w100">
                            <el-form-item :rules="items.rules3" :prop="`convertField[${index}].newData`">
                                <el-input
                                    clearable
                                    @change="dataChange(items, 3, index)"
                                    placeholder="更新数据"
                                    v-model="items.newData"
                                ></el-input>
                            </el-form-item>
                        </div>
                        <div class="icon" @click="dataAddField">
                            <el-icon size="20">
                                <CirclePlus />
                            </el-icon>
                        </div>
                        <div
                            class="icon"
                            v-if="goalTableForm.tableData.convertField.length > 1"
                            @click="dataDeleteField(items, index)"
                        >
                            <el-icon size="20">
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </el-descriptions-item>
                <div v-if="addTaskForm.tableData.targetType != 'api'">
                    <el-descriptions-item label-align="center" label="输出类型">
                        <template #label>
                            <div>
                                <span>输出类型</span>
                                <span class="y9-required-icon">*</span>
                            </div>
                        </template>
                        <el-form-item prop="writerType">
                            <el-radio-group v-model="goalTableForm.tableData.writerType" @change="radioChange">
                                <el-radio :label="'insert'">新增</el-radio>
                                <el-radio :label="'update'">增量更新（仅更新发生变化的数据，未发生变化的不予更新）</el-radio>
                                <el-radio :label="'replace'">全量更新（不管有没有发生变化，都更新）</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-descriptions-item>
                    <el-descriptions-item 
                        label-align="center"
                        v-if="goalTableForm.tableData.writerType == 'update' || goalTableForm.tableData.writerType == 'replace'"
                    >
                        <template #label>
                            <div>
                                <span>更新查询字段</span>
                                <span class="y9-required-icon">*</span>
                            </div>
                        </template>
                        <el-form-item prop="updateField">
                            <el-tree-select
                                node-key="id"
                                v-model="goalTableForm.tableData.updateField"
                                :data="targetFilteredTableFieldList"
                                :props="state.defaultProps"
                                multiple
                                placeholder="请选择更新时的查询字段"
                                filterable
                                :render-after-expand="false"
                                check-strictly
                                check-on-click-node
                                show-checkbox
                            >
                            </el-tree-select>
                        </el-form-item>
                    </el-descriptions-item>
                </div>
            </div>
        </el-descriptions>
    </el-form>

    <div class="btn">
        <el-button @click="top" class="global-btn-second">上一步</el-button>
        <el-button type="primary" @click="next" class="global-btn-main">下一步</el-button>
    </div>
</template>

<style scoped lang="scss">
//webkit内核浏览器 滚动条大小
::-webkit-scrollbar {
    width: 4px;
    height: 2px;
    background-color: transparent;
}

.field {
    display: flex;
    width: 100%;
    flex-wrap: wrap;
    max-height: 120px;
    overflow-y: scroll;
    overflow-x: hidden;

    .type {
        width: 50%;
    }
}

.fetch {
    display: flex;
    align-items: center;

    .fetch-w {
        width: 35%;
    }

    .tips {
        margin-left: 20px;
        margin-top: 5px;
        color: #999999;
    }
}

.btn {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

.seg {
    margin: 0 10px;
}

.sync {
    width: 100%;
    display: flex;
    align-items: center;

    .w100 {
        width: 100%;
    }

    .icon {
        color: var(--el-color-primary);
        display: flex;
        font-weight: bold;
        align-items: center;
        margin-left: 10px;
        cursor: pointer;
        margin-top: 3px;
    }
}
</style>

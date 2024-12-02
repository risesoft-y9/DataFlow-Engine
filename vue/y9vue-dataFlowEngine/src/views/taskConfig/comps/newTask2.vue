<template>
    <el-form v-loading="addTaskLoading" :model="addTaskForm.tableData" :rules="rules" ref="taskFormRef">
        <el-descriptions border :column="1">
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span>任务名称</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="name">
                    <el-input
                        placeholder="请输入"
                        v-model="addTaskForm.tableData.name" 
                        :disabled="globalData.type == 'detail' ? true : false"
                    ></el-input>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span>业务分类</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="businessId">
                    <el-tree-select
                        node-key="id"
                        check-strictly
                        :props="state.defaultProps"
                        v-model="addTaskForm.tableData.businessId"
                        :data="state.treeData"
                        :disabled="globalData.type == 'detail' ? true : false"
                    />
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span>描述</span>
                    </div>
                </template>
                <el-form-item>
                    <el-input
                        placeholder="请输入"
                        show-word-limit
                        type="textarea"
                        v-model="addTaskForm.tableData.description"
                        :disabled="globalData.type == 'detail' ? true : false"
                    ></el-input>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span>选择数据源</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="sourceId">
                    <el-radio-group v-model="addTaskForm.tableData.sourceId" :disabled="globalData.type == 'detail' ? true : false">
                        <el-radio
                            @change="radioChange('input', item.baseType)"
                            v-for="item in state.dataSource"
                            :label="item.id"
                        >
                            【{{ item.baseType }}】{{ item.baseName }}
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-descriptions-item>

            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span v-if="addTaskForm.tableData.sourceType == 'api'">接口名称</span>
                        <span v-else>表名称</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="sourceTable">
                    <el-select
                        @change="tableChange"
                        v-model="addTaskForm.tableData.sourceTable"
                        class="m-2"
                        placeholder="请选择"
                        size="small"
                        :disabled="globalData.type == 'detail' ? true : false"
                    >
                        <el-option
                            v-for="item in addTaskForm.tableOptions"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="字段详情">
                <template #label>
                    <div>
                        <span>字段详情</span>
                    </div>
                </template>
                <el-form-item class="field">
                    <div v-for="item in addTaskForm.tableFieldList" class="type">
                        {{ item.name }}:{{ item.cname }}({{ item.fieldType }})
                    </div>
                </el-form-item>
            </el-descriptions-item>

            <div v-if="addTaskForm.tableData.sourceType == 'api'">
                <el-descriptions-item label-align="center" label="接口参数">
                    <template #label>
                        <div>
                            <span>接口参数</span>
                            <span class="y9-required-icon">*</span>
                        </div>
                    </template>
                    <el-form-item prop="whereSql" style="margin-bottom: 0px">
                        <el-input
                            placeholder="点击右边按钮生成参数，动态公式需要生成后手动修改"
                            show-word-limit
                            type="textarea"
                            rows="3"
                            v-model="addTaskForm.tableData.whereSql"
                            style="width: 97%"
                            :disabled="globalData.type == 'detail' ? true : false"
                        ></el-input>
                        <el-icon :size="20" title="自动生成接口参数" @click="addParams()" v-if="globalData.type != 'detail'" style="margin-left: 5px">
                            <Promotion />
                        </el-icon>
                    </el-form-item>
                </el-descriptions-item>
            </div>
            <div v-else>
                <el-descriptions-item label-align="center" label="输出类型">
                    <template #label>
                        <div>
                            <span>操作类型</span>
                            <span class="y9-required-icon">*</span>
                        </div>
                    </template>
                    <el-form-item prop="writerType">
                        <el-radio-group v-model="addTaskForm.tableData.writerType" :disabled="globalData.type == 'detail' ? true : false">
                            <el-radio :label="'delete'">删除</el-radio>
                            <el-radio :label="'update'">更新</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" label="where语句">
                    <template #label>
                        <div>
                            <span>执行语句</span>
                            <span class="y9-required-icon">*</span>
                        </div>
                    </template>
                    <el-form-item prop="whereSql" style="margin-bottom: 0px">
                        <el-input
                            placeholder="请输入"
                            show-word-limit
                            type="textarea"
                            rows="3"
                            v-model="addTaskForm.tableData.whereSql"
                            :disabled="globalData.type == 'detail' ? true : false"
                        ></el-input>
                    </el-form-item>
                    <el-collapse accordion>
                        <el-collapse-item>
                            <template #title>
                                说明<el-icon class="header-icon"><info-filled /></el-icon>
                            </template>
                            <div class="tip-text">
                                <p
                                    >1.只支持update和delete语句，只需填写表名称后面的SQL语句 <br />
                                    <span>update示例：SET column1 = value1, column2 = value2,... WHERE condition</span
                                    ><br />
                                    <span>delete示例：WHERE condition</span>
                                </p>
                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </el-descriptions-item>
            </div>
        </el-descriptions>
    </el-form>
    <div class="btn" v-if="globalData.type != 'detail'">
        <el-button type="primary" @click="saveData(taskFormRef)" class="global-btn-main">保存</el-button>
    </div>

    <y9Dialog v-model:config="dialogConfig">
        <EditParams v-if="dialogConfig.type == 'editParams'" ref="editParamsRef" :params="paramsList" />
    </y9Dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, toRefs } from 'vue';
import { globalData } from '@/views/taskConfig/comps/data';
import { getDataTableList, getFindAll, getSingleTaskById, saveSingleTask } from '@/api/taskConfig';
import { setTreeData } from '@/utils/object';
import { findByTypeList } from '@/api/dataSource';
import { getApiField, getTableField } from '@/api/libraryTable';
import { ElMessage, FormInstance } from 'element-plus';
import { apiTest, findParamsList } from '@/api/interface';
import EditParams from '@/views/interface/editParams.vue';

const emits = defineEmits(['close']);
const taskFormRef = ref<FormInstance>();
const addTaskLoading = ref(false);

const addTaskForm = reactive({
    tableData: {
        id: '',
        name: null, //任务名称
        businessId: null, //业务分类id
        description: '', //任务描述
        sourceId: null, //源头id
        sourceType: 'api', //类型
        sourceTable: '', //数据表id
        writerType: '', // 操作类型：update、delete
        whereSql: '' //sql语句
    },
    treeData: [], //业务分类列表
    tableOptions: [], //表名称列表
    tableFieldList: [] //字段详情列表
});

const state = reactive({
    treeData: [], // 业务分类数据
    defaultProps: {
        children: 'children',
        label: 'name'
    },
    dataSource: [] //数据源
});

onMounted(() => {
    addTaskLoading.value = true;
    getData();
    setTimeout(()=>{
        addTaskLoading.value = false;
    }, 800)
});

const getData = async () => {
    await getTree(); //获取业务分类
    await getDataSource();
    if (globalData.type != 'add') {
        await getDataAll();
    }
};

const getDataAll = async () => {
    let res = await getSingleTaskById({ id: globalData.row.id });
    if (res) {
        let ret = res.data;

        addTaskForm.tableData = ret;

        //查询表格下拉
        getTableList('');
        getTableFieldList(); //获取字段详情
    }
};

const getDataSource = async () => {
    let res = await findByTypeList({ type: 0 });
    if (res) {
        let apiData = {
            id: 'api',
            baseName: '接口数据源',
            baseType: 'api'
        };
        state.dataSource = res.data;
        state.dataSource.unshift(apiData);
        // 赋予初始值
        //addTaskForm.tableData.sourceId = 'api';
    }
};

const radioChange = async (type, baseType) => {
    addTaskLoading.value = true;
    addTaskForm.tableOptions = [];
    addTaskForm.tableData.sourceTable = '';
    addTaskForm.tableFieldList = []; //清空字段
    addTaskForm.tableData.sourceType = baseType;
    await getTableList(type); //获取表和执行类
    addTaskLoading.value = false;
};

const getTableList = async (type) => {
    let sourceId = addTaskForm.tableData.sourceId;
    if(sourceId == 'api') {
        type = 'input';
    }
    let params = {
        sourceId,
        type
    };
    let res = await getDataTableList(params);
    if (res) {
        addTaskForm.tableOptions = res.data.tableList;
    }
};

// 获取业务分类
const getTree = async () => {
    let res = await getFindAll();
    if (res) {
        if (res.data) {
            addTaskForm.treeData = res.data;
            state.treeData = setTreeData(res.data);
        }
    }
};

//表筛选
const tableChange = () => {
    getTableFieldList();
};

// 获取表字段
const getTableFieldList = async () => {
    let res;
    if (addTaskForm.tableData.sourceType == 'api') {
        res = await getApiField({ apiId: addTaskForm.tableData.sourceTable });
    } else {
        res = await getTableField({ tableId: addTaskForm.tableData.sourceTable });
    }
    if (res) {
        addTaskForm.tableFieldList = res.data;
    }
};

const rules = {
    name: [{ required: true, message: '请输入任务名称', trigger: ['blur', 'change'] }],
    businessId: [{ required: true, message: '请选择业务分类', trigger: ['blur', 'change'] }],
    sourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
    writerType: [{ required: true, message: '请选择操作类型', trigger: ['blur', 'change'] }],
    whereSql: [{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]
};

const data = reactive({
    //弹窗配置
    dialogConfig: {
        show: false,
        title: '',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                if (dialogConfig.value.type == 'editParams') {
                    return requestTest(resolve, reject);
                }
            });
        },
        onReset: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                return saveParams(resolve, reject);
            });
        },
        visibleChange: (visible) => {}
    },
    row: '',
    paramsList: [],
    editParamsRef: ''
});

let { row, dialogConfig, paramsList, editParamsRef } = toRefs(data);

async function addParams() {
    if (!addTaskForm.tableData.sourceTable) {
        ElMessage({ type: 'error', message: '请先选择接口', offset: 65 });
        return;
    }
    let res = await findParamsList({ parentId: addTaskForm.tableData.sourceTable, dataType: 0 });
    paramsList.value = res.data.params;
    row.value = res.data.interface;
    dialogConfig.value.resetText = false;
    Object.assign(dialogConfig.value, {
        show: true,
        width: '30%',
        title: '参数填写',
        showFooter: true,
        okText: '请求测试',
        type: 'editParams'
    });
}

let paramRef = ref('');
async function requestTest(resolve, reject) {
    let headObj = ref([]);
    let paramsObj = ref([]);
    let formData = new FormData();
    paramsList.value.forEach((item) => {
        if (item.reqType == 'Headers') {
            headObj.value.push({ name: item.paramName, value: item.paramValue });
        }
        if (item.reqType == 'Body') {
            formData.append(item.paramName, item.paramValue);
        }
        if (item.reqType == 'Params') {
            paramsObj.value.push({ name: item.paramName, value: item.paramValue });
        }
    });
    let res = await apiTest({
        method: row.value.requestType,
        url: row.value.interfaceUrl,
        headers: headObj.value,
        params: paramsObj.value,
        body: JSON.stringify(formData),
        contentType: row.value.contentType
    });
    if (res.success) {
        paramRef.value = JSON.stringify({
            method: row.value.requestType,
            url: row.value.interfaceUrl,
            headers: headObj.value,
            params: paramsObj.value,
            body: JSON.stringify(formData),
            contentType: row.value.contentType
        });
        dialogConfig.value.resetText = '生成请求参数';
    }
    editParamsRef.value.resData = res.data;
    reject();
}

async function saveParams(resolve, reject) {
    addTaskForm.tableData.whereSql = paramRef.value;
    dialogConfig.value.show = false;
}

const saveData = (refFrom) => {
    if (!refFrom) return;
    refFrom.validate(async (valid) => {
        if (valid) {
            addTaskLoading.value = true;
            let res = await saveSingleTask(addTaskForm.tableData);
            addTaskLoading.value = false;
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
                emits('close', 2);
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65 });
            }
        }
    });
};
</script>

<style scoped lang="scss">
.btn {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
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

.tip-text {
    color: #999999;
    p {
        margin: 0px;
    }
    span {
        padding: 0px 5px;
    }
}
</style>

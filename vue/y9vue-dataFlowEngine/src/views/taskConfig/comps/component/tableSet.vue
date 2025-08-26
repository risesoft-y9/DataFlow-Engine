<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, toRefs } from 'vue';

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
import EditParams from '@/views/interface/editParams.vue';
import { addTaskForm, globalData, goalTableForm, goalTableRef, tableSetForm, tableSetRef } from '../data';
import { getApiField, getTableField } from '@/api/libraryTable';
import { getDataTableList } from '@/api/taskConfig';
import { apiTest, findParamsList } from '@/api/interface';
import { ElMessage } from 'element-plus';

const setInitData = () => {
    tableSetRef.value.resetFields();
    if (globalData.type == 'add') {
        for (let key in tableSetForm.tableData) {
            if (key == 'sourceCloumn') {
                tableSetForm.tableData[key] = [];
            } else if (key == 'sourceName') {
                tableSetForm.tableData[key] = tableSetForm.tableData[key];
            } else if (key == 'sourceTable') {
                tableSetForm.tableData[key] = tableSetForm.tableData[key];
            } else {
                tableSetForm.tableData[key] = null;
            }
        }
    }
};

const next = async () => {
    let validate = await tableSetRef.value.validate();
    if (validate) {
        emits('next');
    }
};

const top = () => {
    emits('top');
};

const state = reactive({});

onMounted(() => {
    setInitData();
});

const getTableFieldList = async () => {
    let res;
    if (addTaskForm.tableData.sourceType == 'api') {
        res = await getApiField({ apiId: tableSetForm.tableData.sourceTable });
    } else {
        res = await getTableField({ tableId: tableSetForm.tableData.sourceTable });
    }
    if (res) {
        tableSetForm.tableFieldList = res.data;
        tableSetForm.tableData.sourceCloumn = []; //清空字段详情勾选
        res.data.forEach((item) => {
            item.label = `${item.name}:${item.cname}(${item.fieldType})`;
            //设置勾选
            tableSetForm.tableData.sourceCloumn.push(item.id);
        });
    }
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
    if (!tableSetForm.tableData.sourceTable) {
        ElMessage({ type: 'error', message: '请先选择接口', offset: 65 });
        return;
    }
    let res = await findParamsList({ parentId: tableSetForm.tableData.sourceTable, dataType: 0 });
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
    tableSetForm.tableData.whereSql = paramRef.value;
    dialogConfig.value.show = false;
}

const filteredTableFieldList = computed(() => {
    return tableSetForm.tableFieldList.filter((item) => {
        return tableSetForm.tableData.sourceCloumn.includes(item.id);
    });
});
const checkBoxChange = () => {
    let exist = tableSetForm.tableData.sourceCloumn.includes(tableSetForm.tableData.splitPk);
    if (!exist) {
        tableSetForm.tableData.splitPk = null;
    }
    const fieldSet = new Set(tableSetForm.tableData.sourceCloumn);
    goalTableForm?.tableData?.differentField?.forEach((item, index) => {
        if (!fieldSet.has(item.source)) {
            item.source = null;
            item.rules2 = [];
            goalTableRef.value.clearValidate(`differentField[${index}].target`);
            goalTableRef.value.clearValidate(`differentField[${index}].source`);
        }
    });
};
//表筛选
const tableChange = () => {
    if(tableSetForm.tableData.sourceTable == 'multi') {
        tableSetForm.tableData.sourceCloumn = [];
    } else {
        getTableFieldList();
    }
};
const rules = {
    sourceTable: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
    sourceName: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
    tableNumber: [{ required: true, message: '请输入', trigger: ['blur', 'change'] }],
    splitFactor: [{ required: true, message: '请输入', trigger: ['blur', 'change'] }]
};
const radioChange = (e) => {
    if (e == 1) {
        tableSetForm.tableData.splitFactor = null;
        tableSetForm.tableData.tableNumber = null;
    } else if (e == 2) {
        tableSetForm.tableData.splitFactor = null;
    } else {
        tableSetForm.tableData.tableNumber = null;
    }
};
let ruleFormRef = ref(tableSetRef);

const activeName = ref('1');
</script>
<template>
    <el-form ref="ruleFormRef" :model="tableSetForm.tableData" :rules="rules">
        <el-descriptions border :column="1">
            <el-descriptions-item label-align="center">
                <template #label>
                    <div>
                        <span v-if="addTaskForm.tableData.sourceType == 'api'">接口名称</span>
                        <span v-else-if="addTaskForm.tableData.sourceType == 'ftp'">文件目录</span>
                        <span v-else>表名称</span>
                        <span class="y9-required-icon">*</span>
                    </div>
                </template>
                <el-form-item prop="sourceTable">
                    <el-select
                        @change="tableChange"
                        v-model="tableSetForm.tableData.sourceTable"
                        class="m-2"
                        placeholder="请选择"
                        filterable
                        v-if="addTaskForm.tableData.sourceType != 'ftp'"
                    >
                        <el-option
                            v-for="item in tableSetForm.tableOptions"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                    <el-input
                        placeholder="填写需要读取的文件目录"
                        v-model="tableSetForm.tableData.sourceTable"
                        v-else
                    ></el-input>
                </el-form-item>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="字段详情" v-if="addTaskForm.tableData.sourceType != 'ftp'">
                <template #label>
                    <div>
                        <span v-if="tableSetForm.tableData.sourceTable == 'multi'">查询语句</span>
                        <span v-else>字段详情</span>
                    </div>
                </template>
                <el-form-item prop="sourceCloumn" v-if="tableSetForm.tableData.sourceTable == 'multi'">
                    <el-input
                        placeholder="请输入多表查询SQL，例如：select au.id as ID, au.name as NAME, ad.mobile as MOBILE from a_user au left join a_detail ad on au.id = ad.userId"
                        type="textarea"
                        rows="3"
                        v-model="tableSetForm.tableData.sourceCloumn"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="sourceCloumn" v-else>
                    <el-checkbox-group v-model="tableSetForm.tableData.sourceCloumn" class="field">
                        <div v-for="item in tableSetForm.tableFieldList" class="type">
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
                <el-form-item prop="sourceName">
                    <el-select
                        v-model="tableSetForm.tableData.sourceName"
                        class="m-2"
                        value-key="id"
                        placeholder="请选择"
                    >
                        <el-option
                            v-for="item in tableSetForm.classListOptions"
                            :key="item.id"
                            :label="item.className"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
            </el-descriptions-item>
            <div v-if="addTaskForm.tableData.sourceType == 'api'">
                <el-descriptions-item label-align="center" label="接口参数">
                    <template #label>
                        <div>
                            <span>接口参数</span>
                        </div>
                    </template>
                    <el-form-item prop="whereSql" style="margin-bottom: 0px">
                        <el-input
                            placeholder="点击右边按钮生成参数，动态公式需要生成后手动修改"
                            show-word-limit
                            type="textarea"
                            rows="3"
                            v-model="tableSetForm.tableData.whereSql"
                            style="width: 97%"
                        ></el-input>
                        <el-icon :size="20" title="自动生成接口参数" @click="addParams()" style="margin-left: 5px">
                            <Promotion />
                        </el-icon>
                    </el-form-item>
                    <el-collapse v-model="activeName" accordion>
                        <el-collapse-item name="1">
                            <template #title>
                                说明<el-icon class="header-icon"><info-filled /></el-icon>
                            </template>
                            <div class="tip-text">
                                <p
                                    >动态参数公式<br />
                                    <span
                                        >1.分页参数，自动轮询所有数据：①$page{num}：初始页数，$page公式名，num为值</span
                                    >
                                </p>
                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </el-descriptions-item>
            </div>
            <div v-else-if="addTaskForm.tableData.sourceType == 'ftp'">
                <el-descriptions-item label-align="center" label="文件名称">
                    <template #label>
                        <div>
                            <span>文件名称</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item>
                                <el-input
                                    :controls="false"
                                    v-model="tableSetForm.tableData.whereSql"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">模糊查询，只筛查目录下指定名称的文件</div>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" label="文件时间">
                    <template #label>
                        <div>
                            <span>文件时间</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item>
                                <el-input
                                    :controls="false"
                                    v-model="tableSetForm.tableData.splitPk"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">筛查目录下大于该时间的文件，格式：yyyy-MM-dd HH:mm:ss</div>
                    </div>
                </el-descriptions-item>
            </div>
            <div v-else>
                <el-descriptions-item label-align="center" label="where语句">
                    <template #label>
                        <div>
                            <span>where语句</span>
                        </div>
                    </template>
                    <el-form-item prop="whereSql" style="margin-bottom: 0px">
                        <el-input
                            placeholder="请输入"
                            show-word-limit
                            type="textarea"
                            rows="3"
                            v-model="tableSetForm.tableData.whereSql"
                        ></el-input>
                    </el-form-item>
                    <el-collapse accordion>
                        <el-collapse-item>
                            <template #title>
                                说明<el-icon class="header-icon"><info-filled /></el-icon>
                            </template>
                            <div class="tip-text" v-if="addTaskForm.tableData.sourceType == 'elasticsearch'">
                                <p
                                    >1.只支持elasticsearch查询语句<br />
                                    <span>示例：{"match":{"name":"张三"}}</span>
                                </p>
                            </div>
                            <div class="tip-text" v-else>
                                <p>动态指令公式</p>
                                <p>
                                    1.只支持sql语句：$sql{A(a)#B} <br />
                                    <span
                                        >①$sql：公式名，SQL查询；②A：sql函数；③a：参数（参数为字符串时在公式前后加上'
                                        '）；④B：查询对象（output-目标表，input-源头表）</span
                                    ><br />
                                    <span
                                        >示例公式：$sql{max(createtime)#output}
                                        意思是查询当前目标表createtime字段的最大值</span
                                    ><br />
                                    <span
                                        >示例功能：增量同步：createtime >= $sql{max(createtime)#output}
                                        根据目标表最大时间查询新增数据</span
                                    >
                                </p>
                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </el-descriptions-item>
                <el-descriptions-item
                    label-align="center"
                    label="fetchSize"
                    v-if="addTaskForm.tableData.sourceType != 'elasticsearch'"
                >
                    <template #label>
                        <div>
                            <span>fetchSize</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item>
                                <el-input-number
                                    class="number-input"
                                    placeholder="请填写数字,不填取默认值"
                                    :controls="false"
                                    v-model="tableSetForm.tableData.fetchSize"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">jdbc查询时ResultSet的每次读取记录数</div>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" label="切分字段">
                    <template #label>
                        <div>
                            <span>切分字段</span>
                        </div>
                    </template>
                    <el-form-item prop="splitPk">
                        <el-select
                            clearable
                            v-model="tableSetForm.tableData.splitPk"
                            class="m-2"
                            value-key="id"
                            placeholder="请选择"
                            filterable
                            v-if="tableSetForm.tableData.sourceTable != 'multi'"
                        >
                            <el-option
                                v-for="item in filteredTableFieldList"
                                :key="item.id"
                                :label="`${item.name}:${item.cname}(${item.fieldType})`"
                                :value="item.id"
                            />
                        </el-select>
                        <el-input
                            placeholder="填写多表查询SQL里返回的字段名称，当填写的字段名称是多张表里重复的字段名称时加上表别名前缀，例如：t.id"
                            v-model="tableSetForm.tableData.splitPk"
                            v-else
                        ></el-input>
                    </el-form-item>
                </el-descriptions-item>
                <!--      执行数基数  切分数量-->
                <el-descriptions-item label-align="center">
                    <template #label>
                        <div>
                            <span>切分模式</span>
                        </div>
                    </template>
                    <el-form-item>
                        <el-radio-group v-model="tableSetForm.tableData.radios" @change="radioChange">
                            <el-radio :label="1">精准切分</el-radio>
                            <el-radio :label="2">平均切分</el-radio>
                            <el-radio :label="3">执行数切分</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" v-if="tableSetForm.tableData.radios == 2">
                    <template #label>
                        <div>
                            <span>切分数量</span>
                            <span class="y9-required-icon">*</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item prop="tableNumber">
                                <el-input-number
                                    class="number-input"
                                    placeholder="请输入"
                                    :controls="false"
                                    v-model="tableSetForm.tableData.tableNumber"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">根据表数据量按数量平均切分：表数据量/切分数量</div>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label-align="center" v-if="tableSetForm.tableData.radios == 3">
                    <template #label>
                        <div>
                            <span>执行数基数</span>
                            <span class="y9-required-icon">*</span>
                        </div>
                    </template>
                    <div class="fetch">
                        <div class="fetch-w">
                            <el-form-item prop="splitFactor">
                                <el-input-number
                                    class="number-input"
                                    placeholder="请输入"
                                    :controls="false"
                                    v-model="tableSetForm.tableData.splitFactor"
                                />
                            </el-form-item>
                        </div>
                        <div class="tips">根据线程池线程设置大小切分：表数据量/(线程数*执行数基数)</div>
                    </div>
                </el-descriptions-item>
            </div>
        </el-descriptions>
    </el-form>
    <div class="demo-collapse" v-if="addTaskForm.tableData.sourceType != 'ftp'">
        <el-collapse accordion>
            <el-collapse-item>
                <template #title>
                    <div class="advanced">
                        <div class="title">高级设置</div>
                    </div>
                </template>
                <div>
                    <el-descriptions border :column="1">
                        <el-descriptions-item label-align="center" label="数据脱敏">
                            <template #label>
                                <div>
                                    <span>数据脱敏</span>
                                </div>
                            </template>
                            <div class="desensitization">
                                <div class="type">
                                    <el-form-item prop="maskFields">
                                        <el-input
                                            placeholder="请输入脱敏字段"
                                            v-model="tableSetForm.tableData.maskFields"
                                        ></el-input>
                                    </el-form-item>
                                </div>
                                <div class="tips">多个脱敏字段之间用英文逗号区分</div>
                            </div>
                        </el-descriptions-item>
                        <el-descriptions-item label-align="center" label="数据脱敏">
                            <template #label>
                                <div>
                                    <span>数据加密</span>
                                </div>
                            </template>
                            <div class="desensitization">
                                <div class="type">
                                    <el-form-item prop="jiami">
                                        <el-input
                                            placeholder="请输入加密字段"
                                            v-model="tableSetForm.tableData.encrypFields"
                                        ></el-input>
                                    </el-form-item>
                                </div>
                                <div class="tips">多个加密字段之间用英文逗号区分</div>
                            </div>
                        </el-descriptions-item>
                    </el-descriptions>
                </div>
            </el-collapse-item>
        </el-collapse>
    </div>

    <div class="btn">
        <el-button @click="top" class="global-btn-second">上一步</el-button>
        <el-button type="primary" @click="next" class="global-btn-main">下一步</el-button>
    </div>

    <y9Dialog v-model:config="dialogConfig">
        <EditParams v-if="dialogConfig.type == 'editParams'" ref="editParamsRef" :params="paramsList" />
    </y9Dialog>
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
        width: 20%;
    }

    .tips {
        margin-left: 20px;
        margin-top: 5px;
        color: #999999;
    }
}

.desensitization {
    display: flex;
    align-items: center;

    .type {
        width: 50%;
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

.demo-collapse {
    margin-top: 10px;

    .advanced {
        width: 100%;
        display: flex;
        background: #eee;

        .title {
            margin-left: 40px;
        }
    }

    :deep(.el-icon) {
        position: absolute;
        margin-left: 20px;
    }
}

.number-input {
    width: 100%;
}

.number-inputs {
    width: 20%;
}

:deep(.el-input-number .el-input__inner) {
    text-align: left !important;
    margin-left: -3px;
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

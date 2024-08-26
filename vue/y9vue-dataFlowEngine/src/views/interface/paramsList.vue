<template>
    <el-tabs v-model="activeName" style="height: 40px" @tab-click="tabclick">
        <el-tab-pane label="请求参数" name="Request"></el-tab-pane>
        <el-tab-pane label="响应参数" name="Response"></el-tab-pane>
    </el-tabs>
    <el-form ref="paramsRef" :model="formData" :rules="rules" class="paramsForm" style="margin-top: 10px">
        <y9Table
            :config="tableConfig"
            :filterConfig="filterConfig"
            class="interfaceParamsTable"
            @select="handlerSelectData"
            @select-all="handlerSelectData"
        >
            <template #addBtn>
                <el-button class="global-btn-main" type="primary" @click="addParams"
                    ><i class="ri-add-line"></i>新增
                </el-button>
                <el-button v-if="activeName == 'Request'" class="global-btn-main" type="primary" @click="handleTest"
                    ><i class="ri-links-line"></i>请求测试
                </el-button>
            </template>
            <template #paramType="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="paramType">
                    <el-select v-model="formData.paramType">
                        <el-option key="String" label="String" value="String"></el-option>
                        <el-option key="Integer" label="Integer" value="Integer"></el-option>
                        <el-option key="Boolean" label="Boolean" value="Boolean"></el-option>
                    </el-select>
                </el-form-item>
                <span v-else>{{ row.paramType }}</span>
            </template>
            <template #paramName="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="paramName">
                    <el-input v-model="formData.paramName" clearable />
                </el-form-item>
                <span v-else>{{ row.paramName }}</span>
            </template>
            <template #reqType="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="reqType">
                    <el-select v-model="formData.reqType">
                        <el-option key="Params" label="Params" value="Params"></el-option>
                        <el-option key="Headers" label="Headers" value="Headers"></el-option>
                        <el-option key="Body" label="Body" value="Body"></el-option>
                    </el-select>
                </el-form-item>
                <span v-else>{{ row.reqType }}</span>
            </template>
            <template #remark="{ row, column, index }">
                <el-form-item v-if="editIndex === index">
                    <el-input v-model="formData.remark" clearable />
                </el-form-item>
                <span v-else>{{ row.remark }}</span>
            </template>
            <template #opt_button="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-link type="primary" :underline="false" @click="saveData()"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-link>
                    <el-link type="primary" :underline="false" @click="cancalData()"
                        ><i class="ri-close-line"></i>取消
                    </el-link>
                </div>
                <div v-else>
                    <el-link type="primary" :underline="false" @click="editParams(row, index)"
                        ><i class="ri-edit-line"></i>修改
                    </el-link>
                    <el-link type="primary" :underline="false" @click="delParams(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-link>
                </div>
            </template>
        </y9Table>
    </el-form>
    <y9Dialog v-model:config="dialogConfig">
        <EditParams ref="editParamsRef" :params="tableConfig.tableData" :row="row" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import axios from 'axios';
    import { defineProps, reactive, ref } from 'vue';
    import { ElLoading, ElMessage, ElMessageBox, FormRules } from 'element-plus';
    import {
        findParamsList,
        deleteParams,
        saveResponseParams,
        saveParams,
        apiTest
    } from '@/api/interface';
    import EditParams from '@/views/interface/editParams.vue';

    const props = defineProps({
        row: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const rules = reactive<FormRules>({
        paramName: { required: true, message: '请输入参数名称', trigger: 'blur' },
        paramType: { required: true, message: '请输入参数类型', trigger: 'blur' }
    });
    const data = reactive({
        isEmptyData: false,
        editIndex: '',
        formData: { id: '', paramName: '', paramType: '', reqType: '', parentId: '' },
        isEdit: false,
        tableConfig: {
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '类型', key: 'reqType', width: '180', slot: 'reqType' },
                { title: '参数名称', key: 'paramName', slot: 'paramName' },
                { title: '参数类型', key: 'paramType', width: '180', slot: 'paramType' },
                { title: '参数备注', key: 'remark', width: '200', slot: 'remark' },
                { title: '添加时间', key: 'createTime', width: '180' },
                { title: '操作', width: '180', slot: 'opt_button' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 12,
                    slotName: 'addBtn'
                }
            ]
        },
        idsArr: [],
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    return requestTest(resolve, reject);
                });
            },
            onReset: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    return saveAllResParams(resolve, reject);
                });
            },
            visibleChange: (visible) => {}
        },
        paramsRef: '',
        editParamsRef: '',
        activeName: 'Request'
    });

    let {
        editIndex,
        isEmptyData,
        formData,
        isEdit,
        filterConfig,
        tableConfig,
        idsArr,
        dialogConfig,
        editParamsRef,
        activeName,
        paramsRef
    } = toRefs(data);

    async function getTableList() {
        if (activeName.value == 'Request') {
            let res = await findParamsList({parentId: props.row.id, dataType: 0});
            tableConfig.value.tableData = res.data;
        } else if (activeName.value == 'Response') {
            let res = await findParamsList({parentId: props.row.id, dataType: 1});
            tableConfig.value.tableData = res.data;
        }
    }

    getTableList();

    function tabclick(tab, event) {
        //页签切换
        activeName.value = tab.props.name;
        cancalData();
        if (activeName.value == 'Request') {
            //tableConfig.value.columns.splice(1, 1);
            tableConfig.value.columns.splice(1, 0, {
                title: '类型',
                key: 'reqType',
                width: '180',
                slot: 'reqType'
            });
        } else {
            tableConfig.value.columns.splice(1, 1);
        }
        getTableList();
    }

    const addParams = () => {
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = 0;
            tableConfig.value.tableData.unshift({
                id: '',
                reqType: '',
                paramName: '',
                paramType: '',
                remark: '',
                parentId: props.row.id
            });
            formData.value.id = '';
            formData.value.paramName = '';
            formData.value.paramType = '';
            formData.value.remark = '';
            formData.value.reqType = '';
            formData.value.parentId = props.row.id;
            if(activeName.value == 'Request') {
                formData.value.dataType = 0;
            }else {
                formData.value.dataType = 1;
            }
            isEdit.value = false;
        }
    };

    const editParams = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.paramName = rows.paramName;
        formData.value.paramType = rows.paramType;
        formData.value.remark = rows.remark;
        formData.value.reqType = rows.reqType;
        formData.value.parentId = rows.parentId;
        if(activeName.value == 'Request') {
            formData.value.dataType = 0;
        }else {
            formData.value.dataType = 1;
        }
        isEdit.value = true;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const saveData = () => {
        if (!paramsRef) return;
        paramsRef.value.validate(async (valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                let res = await saveParams(formData.value);
                loading.close();
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    editIndex.value = '';
                    isEmptyData.value = false;
                    getTableList();
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                }
            }
        });
    };

    const cancalData = () => {
        editIndex.value = '';
        formData.value.paramName = '';
        formData.value.paramType = '';
        formData.value.remark = '';
        formData.value.reqType = '';
        paramsRef.value.resetFields();
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    function handlerSelectData(row, data) {
        idsArr.value = row;
    }

    const delParams = (rows) => {
        ElMessageBox.confirm('您确定要删除参数吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        .then(async () => {
            let res = await deleteParams({ id: rows.id });           
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
                editIndex.value = '';
                getTableList();
            } else {
                ElMessage({ message: res.msg, type: 'error', offset: 65 });
            }
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '已取消删除',
                offset: 65
            });
        });
    };

    function handleTest() {
        dialogConfig.value.resetText = false;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '30%',
            title: '参数填写',
            showFooter: true,
            okText: '请求测试'
        });
    }

    async function requestTest(resolve, reject) {
        let headObj = ref([]);
        let paramsObj = ref([]);
        let formData = new FormData();
        tableConfig.value.tableData.forEach((item) => {
            if (item.reqType == 'Headers') {
                headObj.value.push({name: item.paramName, value: item.value});
            }
            if (item.reqType == 'Body') {
                formData.append(item.paramName, item.value);
            }
            if (item.reqType == 'Params') {
                paramsObj.value.push({name: item.paramName, value: item.value});
            }
        });
        let res = await apiTest({method: props.row.requestType, url: props.row.interfaceUrl, headers: headObj.value, 
            params: paramsObj.value, body: JSON.stringify(formData), contentType: 'application/json'});
        if (res.success) {
            dialogConfig.value.resetText = '生成响应参数';
        }
        editParamsRef.value.resData = res.data;
        reject();
    }

    async function saveAllResParams(resolve, reject) {
        let resData = JSON.parse(editParamsRef.value.resData);
        if (typeof resData.data != 'object') {
            ElMessage({ type: 'error', message: '响应格式不符合，无法生成响应参数', offset: 65 });
            reject();
        } else {
            dialogConfig.value.loading = true;
            let res = await saveResponseParams({interfaceId: props.row.id, jsonData: resData.data});
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65 });
            }
            dialogConfig.value.loading = false;
            return reject();
        }
    }
</script>

<style lang="scss">
    .paramsForm .el-form-item {
        margin-bottom: 0px !important;
    }

    .paramsForm .y9-filter .y9-filter-item {
        margin-bottom: 10px !important;
    }
    .el-link {
        margin-right: 15px;
    }
</style>

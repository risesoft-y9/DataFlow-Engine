<template>
    <el-form ref="interfaceRef" :model="formData" :rules="rules" class="interfaceForm">
        <y9Table :config="tableConfig" :filterConfig="filterConfig">
            <template #addBtn>
                <el-input v-model="search" clearable placeholder="输入接口名称或者地址" style="margin-left: 10px" />
                <el-select v-model="type" placeholder="请选择接口类型" style="margin-left: 10px">
                    <el-option key="" label="全部" value=""></el-option>
                    <el-option key="1" label="外置" value="1"></el-option>
                    <el-option key="0" label="内置" value="0"></el-option>
                </el-select>
                <el-button class="global-btn-main" style="margin-left: 10px" type="primary" @click="getTableList"
                    ><i class="ri-search-line"></i>搜索
                </el-button>
                <el-button class="global-btn-main" type="primary" @click="addInterface"
                    ><i class="ri-add-line"></i>新增
                </el-button>
                <el-button class="global-btn-main" type="primary" @click="apiTestPage"
                    ><i class="ri-links-line"></i>接口在线测试
                </el-button>
            </template>
            <template #interfaceName="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="interfaceName">
                    <el-input v-model="formData.interfaceName" clearable />
                </el-form-item>
                <span v-else>{{ row.interfaceName }}</span>
            </template>
            <template #requestType="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="requestType">
                    <el-select v-model="formData.requestType">
                        <el-option key="GET" label="GET" value="GET"></el-option>
                        <el-option key="POST" label="POST" value="POST"></el-option>
                    </el-select>
                </el-form-item>
                <span v-else>{{ row.requestType }}</span>
            </template>
            <template #interfaceUrl="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="interfaceUrl">
                    <el-input v-model="formData.interfaceUrl" clearable />
                </el-form-item>
                <span v-else>{{ row.interfaceUrl }}</span>
            </template>
            <template #dataType="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="dataType">
                    <el-select v-model="formData.dataType">
                        <el-option key="1" label="外置" value="1"></el-option>
                        <el-option key="0" label="内置" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <span v-else>{{ row.dataType == '1' ? '外置' : '内置' }}</span>
            </template>
            <template #opt_button="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-link type="primary" :underline="false" @click="saveData(interfaceRef)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-link>
                    <el-link type="primary" :underline="false" @click="cancalData(interfaceRef)"
                        ><i class="ri-close-line"></i>取消
                    </el-link>
                </div>
                <div v-else>
                    <el-link type="primary" :underline="false" @click="editInfo(row, index)"
                        ><i class="ri-edit-line"></i>修改
                    </el-link>
                    <el-link type="primary" :underline="false" @click="openParamsList(row)"
                        ><i class="ri-git-commit-line"></i>接口参数
                    </el-link>
                    <el-link type="primary" :underline="false" @click="handleTest(row)"
                        ><i class="ri-links-line"></i>请求测试
                    </el-link>
                    <el-link type="primary" :underline="false" @click="delInfo(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-link>
                </div>
            </template>
        </y9Table>
    </el-form>
    <y9Dialog v-model:config="dialogConfig">
        <ParamsList v-if="dialogConfig.type == 'paramsList'" ref="paramsListRef" :row="row" />
        <EditParams v-if="dialogConfig.type == 'editParams'" ref="editParamsRef" :params="paramsList" :row="row" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import axios from 'axios';
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import { ElLoading, ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
    import {
        searchPage,
        findParamsList,
        removeInterface,
        saveResponseParams,
        saveInterface,
        apiTest
    } from '@/api/interface';
    import ParamsList from '@/views/interface/paramsList.vue';
    import EditParams from '@/views/interface/editParams.vue';
    import { getStoragePageSize } from '@/utils';
    import router from '@/router';

    const interfaceRef = ref<FormInstance>();
    const rules = reactive<FormRules>({
        interfaceName: { required: true, message: '请输入接口名称', trigger: 'blur' },
        interfaceUrl: { required: true, message: '请输入接口地址', trigger: 'blur' }
    });
    const data = reactive({
        isEmptyData: false,
        editIndex: '',
        formData: { id: '', interfaceUrl: '', interfaceName: '', requestType: 'GET', dataType: '1' },
        isEdit: false,
        tableConfig: {
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '接口名称', key: 'interfaceName', width: '240', slot: 'interfaceName' },
                { title: '请求方式', key: 'requestType', width: '110', slot: 'requestType' },
                { title: '接口地址', key: 'interfaceUrl', slot: 'interfaceUrl', align: 'left' },
                { title: '接口类型', key: 'dataType', slot: 'dataType', width: '110' },
                { title: '添加时间', key: 'createTime', width: '160' },
                { title: '操作', width: '330', slot: 'opt_button' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: {
                currentPage: 1,
                pageSize: getStoragePageSize('engineConfig', 15),
                total: 0,
                pageSizeOpts:[10,15,30,60,120,240]
            }
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
                    return saveAllResParams(resolve, reject);
                });
            },
            visibleChange: (visible) => {}
        },
        row: '',
        search: '',
        type: '',
        paramsList: [],
        editParamsRef: ''
    });

    let {
        editIndex,
        isEmptyData,
        formData,
        isEdit,
        filterConfig,
        tableConfig,
        dialogConfig,
        row,
        search,
        type,
        paramsList,
        editParamsRef
    } = toRefs(data);

    async function getTableList() {
        // 接口参数
        let params = {
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            search: search.value,
            dataType: type.value
        };
        let res = await searchPage(params);
        if (res.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    onMounted(() => {
        getTableList();
    });

    const addInterface = () => {
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = 0;
            tableConfig.value.tableData.unshift({
                id: '',
                interfaceName: '',
                interfaceUrl: '',
                requestType: 'GET',
                dataType: '1',
            });
            formData.value.id = '';
            formData.value.interfaceName = '';
            formData.value.interfaceUrl = '';
            formData.value.requestType = 'GET';
            formData.value.dataType = '1';
            isEdit.value = false;
        }
    };

    const editInfo = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.interfaceName = rows.interfaceName;
        formData.value.interfaceUrl = rows.interfaceUrl;
        formData.value.requestType = rows.requestType;
        formData.value.dataType = rows.dataType + '';
        isEdit.value = true;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const openParamsList = (rows) => {
        row.value = rows;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '65%',
            title: '接口参数',
            showFooter: false,
            type: 'paramsList'
        });
    };

    async function apiTestPage() {
        //跳转路由并打开新窗口
		const routeUrl = router.resolve({
			path: '/api-test',
		});
		window.open(routeUrl.href);
    }

    async function handleTest(rows) {
        row.value = rows;
        let res = await findParamsList({parentId: rows.id, dataType: 0});
        paramsList.value = res.data;
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

    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveInterface(formData.value).then((res) => {
                    loading.close();
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        editIndex.value = '';
                        isEmptyData.value = false;
                        getTableList();
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
            }
        });
    };

    const cancalData = (refForm) => {
        editIndex.value = '';
        formData.value.interfaceName = '';
        formData.value.interfaceUrl = '';
        formData.value.requestType = 'GET';
        formData.value.dataType = '1';
        refForm.resetFields();
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delInfo = (rows) => {
        ElMessageBox.confirm('您确定要删除【' + rows.interfaceName + '】接口吗?', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
            type: 'warning'
        })
        .then(() => {
            removeInterface({ id: rows.id }).then((res) => {
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    editIndex.value = '';
                    getTableList();
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                }
            });
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '已取消删除',
                offset: 65
            });
        });
    };

    async function requestTest(resolve, reject) {
        let headObj = ref([]);
        let paramsObj = ref([]);
        let formData = new FormData();
        paramsList.value.forEach((item) => {
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
        let res = await apiTest({method: row.value.requestType, url: row.value.interfaceUrl, headers: headObj.value, 
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
            let res = await saveResponseParams({interfaceId: row.value.id, jsonData: resData.data});
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65 });
            }
            dialogConfig.value.loading = false;
            reject();
        }
    }
</script>

<style lang="scss">
    .interfaceForm .el-form-item {
        margin-bottom: 0px !important;
    }
    .el-link {
        margin-right: 15px;
    }
</style>

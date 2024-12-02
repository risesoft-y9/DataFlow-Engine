<template>
    <el-form ref="formRef" :model="formData" :rules="rules" class="pForm">
        <y9Table :config="modelTableConfig" :filterConfig="filterConfig">
            <template #addBtn>
                <el-input v-model="search" clearable placeholder="输入名称" style="margin-left: 10px" />
                <el-select v-model="pattern" placeholder="请选择状态" style="margin-left: 10px">
                    <el-option key="" label="全部" value=""></el-option>
                    <el-option key="0" label="关闭" value="0"></el-option>
                    <el-option key="1" label="开启" value="1"></el-option>
                </el-select>
                <el-button class="global-btn-main" style="margin-left: 10px" type="primary" @click="getTableList"
                    ><i class="ri-search-line"></i>搜索
                </el-button>
                <el-button class="global-btn-main" type="primary" @click="addInfo"
                    ><i class="ri-add-line"></i>新增
                </el-button>
            </template>
            <template #name="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="name">
                    <el-input v-model="formData.name" clearable />
                </el-form-item>
                <span v-else>{{ row.name }}</span>
            </template>
            <template #content="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="content">
                    <el-input v-model="formData.content" clearable />
                </el-form-item>
                <span v-else>{{ row.content }}</span>
            </template>
            <template #cron="{ row, column, index }">               
                <el-form-item v-if="editIndex === index" prop="cron" @click="selectTime">
                    <el-input readonly v-model="formData.cron"></el-input>
                    <el-popover
                        :visible="visible"
                        :width="850"
                        placement="top">
                        <div class="corn-popper" v-if="visible">
                            <veCorn :value="_cron" @change="handleChangeCron" @tabChange="handleTabChange"/>
                            <div>
                                <el-table :data="tableDataValue" border style="width: 100%">
                                    <el-table-column prop="seconds" label="秒" align="center" style="font-weight: bold"/>
                                    <el-table-column prop="minutes" label="分" align="center"/>
                                    <el-table-column prop="Hour" label="時" align="center"/>
                                    <el-table-column prop="day" label="日" align="center"/>
                                    <el-table-column prop="month" label="月" align="center"/>
                                    <el-table-column prop="weeks" label="周" align="center"/>
                                    <el-table-column prop="years" label="年" align="center"/>
                                </el-table>
                            </div>
                        </div>
                        <div style="text-align: center;margin-top: 15px">
                            <el-button type="primary" @click="visible=false"><span>取消</span></el-button>
                            <el-button type="primary" @click="saveCorn"><span>确定</span></el-button>
                        </div>
                    </el-popover>
                </el-form-item>
                <span v-else>{{ row.cron }}</span>
            </template>
            <template #pattern="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="pattern">
                    <el-select v-model="formData.pattern">
                        <el-option key="0" label="关闭" value="0"></el-option>
                        <el-option key="1" label="开启" value="1"></el-option>
                    </el-select>
                </el-form-item>
                <span v-else>{{ row.pattern == '0' ? '关闭' : '开启' }}</span>
            </template>
            <template #opt_button="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-link type="primary" :underline="false" @click="saveData(formRef)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-link>
                    <el-link type="primary" :underline="false" @click="cancalData(formRef)"
                        ><i class="ri-close-line"></i>取消
                    </el-link>
                </div>
                <div v-else>
                    <el-link type="primary" :underline="false" @click="jobLog(row)"
                        ><i class="ri-search-eye-line"></i>日志
                    </el-link>
                    <el-link type="primary" :underline="false" @click="handleJob(row)"
                        ><i class="ri-task-line"></i>执行一次
                    </el-link>
                    <el-link type="primary" :underline="false" @click="editInfo(row, index)"
                        ><i class="ri-edit-line"></i>修改
                    </el-link>
                    <el-link type="primary" :underline="false" @click="addModel(row)"
                        ><i class="ri-git-commit-line"></i>流程设计
                    </el-link>
                    <el-link type="primary" :underline="false" @click="delInfo(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-link>
                </div>
            </template>
        </y9Table>
    </el-form>
    <y9Dialog v-model:config="dialogConfig" class="bpmnDialog">
        <Y9BpmnModel
            ref="Y9BpmnModelRef"
            :rowId="rowId"
            @closeDialog="dialogConfig.show = false"
            @saveModelXml="saveModelXml"
        />
    </y9Dialog>
    <y9Dialog v-model:config="logDialogConfig">
        <logList :arrangeId="arrangeId"></logList>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import { ElLoading, ElMessage, ElMessageBox, ElNotification, FormInstance, FormRules, UploadProps } from 'element-plus';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings.ts';
    import axios from 'axios';
    import { deleteArrange, executeProcess, getModelList, saveArrange } from '@/api/processAdmin/processModel';
    import Y9BpmnModel from './bpmnModel.vue';
    import logList from './logList.vue';
    import veCorn from "../dispatch/comp/saveTask/comp/ve-cron/index.vue";
    import { getStoragePageSize } from '@/utils';

    const formRef = ref<FormInstance>();
    const rules = reactive<FormRules>({
        name: { required: true, message: '请输入名称', trigger: 'blur' }
    });

    const data = reactive({
        isEmptyData: false,
        formData: {id: '', name: '', content: '', pattern: '', cron: '* * * * * ? *'},
        isEdit: false,
        modelTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '名称', key: 'name', slot: 'name' },
                { title: '描述', key: 'content', slot: 'content' },
                { title: '状态', key: 'pattern', slot: 'pattern', width: '110' },
                { title: '定时表达式', key: 'cron', slot: 'cron', width: '180' },
                { title: '修改时间', key: 'updateTime', width: '180' },
                { title: '操作', width: '350', slot: 'opt_button' }
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
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        },
        editIndex: '',
        search: '',
        pattern: '',
        rowId: ''
    });

    let { formData, filterConfig, modelTableConfig, dialogConfig, editIndex, isEmptyData, isEdit, search, pattern, rowId } = toRefs(data);

    onMounted(() => {
        getTableList();
    });

    async function getTableList() {
        let params = {
            page: modelTableConfig.value.pageConfig.currentPage,
            size: modelTableConfig.value.pageConfig.pageSize,
            name: search.value,
            pattern: pattern.value
        };
        getModelList(params).then((res) => {
            if (res.success) {
                modelTableConfig.value.tableData = res.rows;
                modelTableConfig.value.pageConfig.total = res.total;
            }
        });
    }

    const addInfo = () => {
        for (let i = 0; i < modelTableConfig.value.tableData.length; i++) {
            if (modelTableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = 0;
            modelTableConfig.value.tableData.unshift({
                id: '',
                name: '',
                content: '',
                pattern: '0',
                cron: ''
            });
            formData.value.id = '';
            formData.value.name = '';
            formData.value.content = '';
            formData.value.pattern = '0';
            formData.value.cron = '';
            isEdit.value = false;
        }
    };

    const editInfo = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.name = rows.name;
        formData.value.content = rows.content;
        formData.value.pattern = rows.pattern + '';
        formData.value.cron = rows.cron;
        isEdit.value = true;
        for (let i = 0; i < modelTableConfig.value.tableData.length; i++) {
            if (modelTableConfig.value.tableData[i].id == '') {
                modelTableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const handleJob = (rows) => {
        ElMessageBox.confirm('您确定要执行【' + rows.name + '】任务吗?', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
            type: 'warning'
        })
        .then(() => {
            executeProcess({ id: rows.id }).then((res) => {
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                }
            });
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '已取消',
                offset: 65
            });
        });
    };

    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveArrange(formData.value).then((res) => {
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
        formData.value.name = '';
        formData.value.content = '';
        formData.value.pattern = '0';
        formData.value.cron = '';
        refForm.resetFields();
        for (let i = 0; i < modelTableConfig.value.tableData.length; i++) {
            if (modelTableConfig.value.tableData[i].id == '') {
                modelTableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delInfo = (rows) => {
        ElMessageBox.confirm('您确定要删除【' + rows.name + '】吗?', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
            type: 'warning'
        })
        .then(() => {
            deleteArrange({ id: rows.id }).then((res) => {
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

    function addModel(row) {
        rowId.value = row.id;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '100%',
            title: '流程设计',
            showFooter: false,
            fullscreen: true,
            showHeaderFullscreen: false
        });
    }

    let arrangeId = ref('');//任务编排ID

    function jobLog(row) {
        logDialogConfig.value.show = true;
        // 赋值当前id 用于传递给组件 用于接口请求
        arrangeId.value = row.id;
    }

    // 参数值弹框
    let logDialogConfig = ref({
        show: false,
        title: '日志列表',
        width: '62%',
        showFooter: false
    });

    function saveModelXml() {
        getTableList();
    }

    const visible = ref(false)
    const _cron = ref("* * * * * ? ")
    const handleChangeCron = (cron: string) => {
        _cron.value = cron
    }
    const tableDataValue = ref([
        {id: 1, seconds: '*', minutes: '*', Hour: '*', day: '*', month: '*', weeks: '?', years: ''},
    ])
    const handleTabChange = (params) => {
        for (const key in tableDataValue.value[0]) {
            if (key == params.type) {
                tableDataValue.value[0][key] = params.value
            }
        }
    }
    const selectTime = () => {
        visible.value = true
        if(formData.value.cron){
            let speed = formData.value.cron
            let speedSplit = speed.split(' ')
            tableDataValue.value[0].seconds = speedSplit[0]
            tableDataValue.value[0].minutes = speedSplit[1]
            tableDataValue.value[0].Hour = speedSplit[2]
            tableDataValue.value[0].day = speedSplit[3]
            tableDataValue.value[0].month = speedSplit[4]
            tableDataValue.value[0].weeks = speedSplit[5]
            tableDataValue.value[0].years = speedSplit[6]
            _cron.value = formData.value.cron
        }
    }
    const saveCorn = () => {
        let cron = _cron.value.split(' ')
        if (cron[3] === "?" && cron[5] === "?") {
            ElNotification({
                title: '错误',
                message: '日期与星期不可以同时为“不指定”',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return
        }
        if (cron[3] !== "?" && cron[5] !== "?") {
            ElNotification({
                title: '错误',
                message: '日期与星期必须有一个为“不指定”',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        formData.value.cron = _cron.value
        visible.value = false
    }
</script>

<style lang="scss">
    @import '@/theme/global.scss';

    .createModel .el-form-item--default .el-form-item__label {
        width: 100px;
    }

    .bpmnDialog .y9-dialog-content {
        padding: 0 !important;
    }

    .pForm .el-form-item {
        margin-bottom: 0px !important;
    }

    .el-link {
        margin-right: 15px;
    }

    .corn-popper {
        min-height: 450px;
        padding: 0 30px;
    }

    // .el-popover.el-popper {
    //     left: 30% !important;
    //     top: 120px !important;
    // }
</style>

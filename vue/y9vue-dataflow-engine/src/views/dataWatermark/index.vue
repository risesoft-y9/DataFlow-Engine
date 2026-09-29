<template>
    <el-form ref="dataRef" :model="formData" :rules="rules" class="dataForm">
        <y9Table 
            :config="tableConfig" 
            :filterConfig="filterConfig" 
            @on-curr-page-change="onCurrentChange"
            @on-page-size-change="onPageSizeChange"
        >
            <template #addBtn>
                <el-button class="global-btn-main" type="primary" @click="getTableList">
                    <i class="ri-search-line"></i>搜索
                </el-button>
                <el-button class="global-btn-main" type="primary" @click="addData">
                    <i class="ri-add-line"></i>新增
                </el-button>
            </template>
            <template #content="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="content">
                    <el-input v-model="formData.content" clearable />
                </el-form-item>
                <span v-else>{{ row.content }}</span>
            </template>
            <template #opt_button="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-link type="primary" :underline="false" @click="saveData(dataRef)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-link>
                    <el-link type="primary" :underline="false" @click="cancalData(dataRef)"
                        ><i class="ri-close-line"></i>取消
                    </el-link>
                </div>
                <div v-else>
                    <el-link type="primary" :underline="false" @click="editInfo(row, index)"
                        ><i class="ri-edit-line"></i>修改
                    </el-link>
                    <el-link type="primary" :underline="false" @click="delInfo(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-link>
                </div>
            </template>
        </y9Table>
    </el-form>
</template>
<script lang="ts" setup>
    import axios from 'axios';
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import { ElLoading, ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
    import { searchPage, saveWatermark, removeWatermark } from '@/api/dataWatermark';

    const dataRef = ref<FormInstance>();
    const rules = reactive<FormRules>({
        content: { required: true, message: '不能为空', trigger: 'blur' }
    });
    const data = reactive({
        isEmptyData: false,
        editIndex: '',
        formData: { id: '', content: '' },
        isEdit: false,
        tableConfig: {
            loading: false,
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '水印ID', key: 'id', width: '240' },
                { title: '水印信息', key: 'content', slot: 'content' },
                { title: '添加时间', key: 'createTime', width: '160' },
                { title: '操作', width: '330', slot: 'opt_button' }
            ],
            border: false,
            headerBackground: true,
            tableData: [{}],
            pageConfig: {
                currentPage: 1,
                pageSize: 15,
                total: 0,
                pageSizeOpts:[10,15,30,60,120,240]
            }
        }
    });

    let filterData = ref({});
    let filterConfig = ref({
        //过滤配置
        itemList: [
            {
                type: 'input',
                key: 'id',
                label: '水印ID',
                span: 5
            },
            {
                type: 'slot',
                span: 6,
                slotName: 'addBtn'
            }
        ],
        filtersValueCallBack: (filters) => {
            filterData.value = filters;
        }
    });

    let {
        editIndex,
        isEmptyData,
        formData,
        isEdit,
        tableConfig
    } = toRefs(data);

    async function getTableList() {
        tableConfig.value.loading = true;
        // 接口参数
        let params = {
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            ...filterData.value
        };
        let res = await searchPage(params);
        if (res.code == 0) {
            // 对返回的接口数据进行赋值与处理
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
        tableConfig.value.loading = false;
    }

    // 分页操作
    function onCurrentChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        getTableList();
    }

    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        getTableList();
    }

    onMounted(() => {
        getTableList();
    });

    const addData = () => {
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = 0;
            tableConfig.value.tableData.unshift({
                id: '',
                content: ''
            });
            formData.value.id = '';
            formData.value.content = '';
            isEdit.value = false;
        }
    };

    const editInfo = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.content = rows.content;
        isEdit.value = true;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveWatermark(formData.value).then((res) => {
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
        formData.value.content = '';
        refForm.resetFields();
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delInfo = (rows) => {
        ElMessageBox.confirm('您确定要删除【' + rows.id + '】吗?', '提示', {
            cancelButtonText: '取消',
            confirmButtonText: '确定',
            type: 'warning'
        })
        .then(() => {
            removeWatermark({ id: rows.id }).then((res) => {
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

</script>

<style lang="scss">
    .dataForm .el-form-item {
        margin-bottom: 0px !important;
    }

    .el-link {
        margin-right: 15px;
    }
</style>

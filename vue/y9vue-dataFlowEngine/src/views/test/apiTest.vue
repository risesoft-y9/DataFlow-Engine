<template>
<div class="home-contents">
    <!-- 标题 -->
    <div class="home-title">接口在线测试</div>
    <!-- 内容 -->
    <div class="home-content">
        <el-row>
            <div class="content content-article">
                <div class="article-content">
                    <div class="article">
                        <table border="0" cellpadding="0" cellspacing="1" lay-skin="line row" class="layui-table">
                            <tbody>
                                <tr style="height: 40px">
                                    <td colspan="6" align="left">{{ $t('HTTP 请求') }}</td>
                                </tr>
                                <tr>
                                    <td class="lefttd">{{ $t('请求方法') }}</td>
                                    <td class="rigthtd" style="width: 120px">
                                        <el-select v-model="apiForm.type">
                                            <el-option key="GET" label="GET" value="GET"></el-option>
                                            <el-option key="POST" label="POST" value="POST"></el-option>
                                        </el-select>
                                    </td>
                                    <td class="lefttd">{{ $t('请求地址') }}</td>
                                    <td class="rigthtd">
                                        <el-input v-model="apiForm.url" />
                                    </td>
                                    <td style="width: 80px">
                                        <el-button class="global-btn-main" type="primary" @click="requestTest">
                                            发送
                                        </el-button>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lefttd">{{ $t('Query参数') }}</td>
                                    <td class="rigthtd" colspan="5">
                                        <el-table :data="tableParam">  
                                            <el-table-column prop="name" label="参数名" width="180">
                                                <template #default="scope">  
                                                    <el-input v-model="scope.row.name" />
                                                </template>
                                            </el-table-column>  
                                            <el-table-column prop="value" label="参数值" width="350">  
                                                <template #default="scope">  
                                                    <el-input v-model="scope.row.value" />
                                                </template>  
                                            </el-table-column>  
                                            <el-table-column label="操作" width="180">  
                                                <template #default="{ $index }">
                                                    <el-button v-if="$index != 0" @click="deleteRow($index, 1)">
                                                        <i class="ri-delete-bin-line"></i>
                                                    </el-button>
                                                    <el-button v-else @click="addRow(1)"><i class="ri-add-line"></i></el-button>
                                                </template>  
                                            </el-table-column>  
                                        </el-table>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lefttd">{{ $t('Headers参数') }}</td>
                                    <td class="rigthtd" colspan="5">
                                        <el-table :data="tableData">  
                                            <el-table-column prop="name" label="参数名" width="180">
                                                <template #default="scope">  
                                                    <el-input v-model="scope.row.name" />
                                                </template>
                                            </el-table-column>  
                                            <el-table-column prop="value" label="参数值" width="350">  
                                                <template #default="scope">  
                                                    <el-input v-model="scope.row.value" />
                                                </template>  
                                            </el-table-column>  
                                            <el-table-column label="操作" width="180">  
                                                <template #default="{ $index }">
                                                    <el-button v-if="$index != 0" @click="deleteRow($index, 2)">
                                                        <i class="ri-delete-bin-line"></i>
                                                    </el-button>
                                                    <el-button v-else @click="addRow(2)"><i class="ri-add-line"></i></el-button>
                                                </template>  
                                            </el-table-column>  
                                        </el-table>                                      
                                    </td>
                                </tr>
                                <tr style="height: 40px">
                                    <td class="lefttd">{{ $t('Body参数') }}</td>
                                    <td class="rigthtd" colspan="5">
                                        <el-radio-group v-model="apiForm.contentType">
                                            <el-radio value="">none</el-radio>
                                            <el-radio value="application/json">application/json</el-radio>
                                            <el-radio value="application/x-www-form-urlencoded">application/x-www-form-urlencoded</el-radio>
                                            <el-radio value="multipart/form-data">multipart/form-data</el-radio>
                                        </el-radio-group>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="rigthtd" colspan="6">
                                        <el-input v-model="apiForm.body" placeholder="格式：{key1:value1,key2:value2,...}" type="textarea" :rows="5" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table border="0" cellpadding="0" cellspacing="1" lay-skin="line row" class="layui-table">
                            <tbody>
                                <tr style="height: 40px">
                                    <td colspan="6" align="left">{{ $t('HTTP 响应') }}</td>
                                </tr>
                                <tr>
                                    <td class="rigthtd" colspan="5">
                                        <el-input v-model="resData" type="textarea" :rows="8" readonly />
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </el-row>
    </div>
</div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, toRefs } from 'vue';
import axios from 'axios';
import { ElMessageBox } from 'element-plus';
import { apiTest } from '@/api/interface';

const data = reactive({
    apiForm: {
        type: 'GET',
        url: '',
        contentType: '',
        body: ''
    },
    resData: ''
});

let { apiForm, resData } = toRefs(data);

defineExpose({
    apiForm
});

// query参数
const tableParam = ref([
    {
        name: '',
        value: ''
    }
]);
// headers参数
const tableData = ref([
    {
        name: '',
        value: ''
    }
]);
 
function addRow(type) {
    if(type === 1) {
        tableParam.value.push({
            name: '',
            value: ''
        });
    }else {
        tableData.value.push({
            name: '',
            value: ''
        });
    }
}

function deleteRow(index, type) {
    if(type === 1) {
        tableParam.value.splice(index, 1);
    }else {
        tableData.value.splice(index, 1);
    }
}

async function requestTest() {
    let res = await apiTest({method: apiForm.value.type, url: apiForm.value.url, headers: tableData.value, 
        params: tableParam.value, body: apiForm.value.body, contentType: apiForm.value.contentType});
    resData.value = res.data;
    // let options = {
    //     method: apiForm.value.type,
    //     url: apiForm.value.url,
    //     headers: (() => {
    //         let headObj = {};
    //         tableData.value.forEach((item) => {
    //             headObj[item.name] = item.value;
    //         });
    //         return headObj;
    //     })()
    // };
    // options.data = (() => {
    //     let formData = new FormData();
    //     if (apiForm.value.body != '') {
    //         formData.append("", apiForm.value.body);
    //         options.headers['Content-Type'] = apiForm.value.contentType;
    //     }
    //     return formData;
    // })();
    // options.params = (() => {
    //     let paramsObj = {};
    //     tableParam.value.forEach((item) => {
    //         paramsObj[item.name] = item.value;
    //     });
    //     return paramsObj;
    // })();
    // axios(options)
    //     .then((res) => {
    //         resData.value = JSON.stringify(res.data);
    //         return;
    //     })
    //     .catch((err) => {
    //         ElMessageBox.alert(err.message);
    //         return;
    //     });
}

</script>

<style lang="scss" scoped>
@import '@/theme/global.scss';
.home-contents {
	background: linear-gradient(180deg,#FBFCFF 100%,#E8EFFB 0);
    min-width: 1230px;
    margin: 0 10%;
	.home-title {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 4vh;
		font-size: 20px;
		letter-spacing: 1px;
		margin-bottom: 10px;
		border: 1px solid #efefef;
		background: #fff;
		box-shadow: $boxShadow;
		box-sizing:border-box;
	}
	.home-content {
		height: calc(100vh - 2vh - 40px);
		width: 100%;
		padding: 0 20px 20px;
		box-sizing: border-box;
		.content {
            width: 100%;
			height: 100%;
			border: 1px solid #efefef;
			box-shadow: $boxShadow;
			border-radius: 4px;
			background-color: var(--el-bg-color);
		}
		.content-article {
			height: calc(100vh - 2vh - 40px);
			padding: 10px 10px 0 10px;
			box-sizing: border-box;
			.article-content {
				height: calc(100% - 20px);
				overflow: auto;
			}
		}
	}
	:deep(.el-row) {
		width: 100%;
		height: 100%;
	}
}
.article {
    height: 100%;
    :deep(.el-row) {
        width: auto !important;
        height: auto !important;
    }
}
.layui-table {
    width: 100%;
    border-collapse: collapse;
    border-spacing: 0;

    td {
        position: revert;
        padding: 5px 10px;
        border-width: 1px;
        border-style: solid;
        border-color: #e6e6e6;
        display: table-cell;
        vertical-align: inherit;
        color: var(--el-text-color-primary);
    }

    .lefttd {
        background: var(--el-fill-color-light);
        text-align: center;
        color: var(--el-text-color-regular);
        width: 14%;        
    }
}

</style>
<script setup lang="ts">
import { addTaskForm, globalData, radioChange, tableSetForm } from '@/views/taskConfig/comps/data';
import { getTaskDetails } from '@/api/taskConfig';
import { onMounted, reactive, ref } from 'vue';
import tooltips from '@/views/dispatch/comp/saveTask/comp/tooltips.vue';

let loading = ref(true);
onMounted(() => {
    getData();
    loading.value = false;
});
const state = reactive({
    taskInfo: [
        {
            title: '基础信息',
            list: [
                { label: '任务名称', key: 'name', span: 2, value: '' },
                { label: '业务分类', key: 'business', span: 1, value: '' },
                { label: '描述', key: 'description', span: 1, value: '' },
                { label: '数据源', key: 'sourceName', span: 1, value: '' },
                { label: '目的源', key: 'tagertName', span: 1, value: '' }
            ]
        },
        {
            title: '源头表信息',
            list: [
                { label: '源头表名称', key: 'sourceTable', span: 2, value: '' },
                { label: '源头表字段详情', key: 'sourceCloumn', span: 2, value: '', slot: 'sourceCloumn' },
                { label: '执行类', key: 'sourceClass', span: 2, value: '' },
                { label: 'where语句', key: 'whereSql', span: 1, value: '' },
                { label: 'fetchSize', key: 'fetchSize', span: 1, value: '' },
                { label: '切分字段', key: 'splitPk', span: 1, value: '' },
                { label: '切分模式', key: 'radio', span: 1, value: '' }, //todo
                { label: '切分数量', key: 'tableNumber', span: 1, value: '' },
                { label: '执行数基数', key: 'splitFactor', span: 1, value: '' },
                { label: '数据脱敏', key: 'maskFields', span: 1, value: '' },
                { label: '数据加密', key: 'encrypFields', span: 1, value: '' }
            ]
        },
        {
            title: '目的表信息',
            list: [
                { label: '目的表名称', key: 'tagertTable', span: 2, value: '' },
                { label: '目的表字段详情', key: 'targetCloumn', span: 2, value: '', slot: 'targetCloumn' },
                { label: '执行类', key: 'tagertClass', span: 2, value: '' },
                { label: '异字段同步', key: 'differentField', span: 2, value: '', slot: 'differentField' },
                { label: '数据转换', key: 'convertField', span: 2, value: '', slot: 'convertField' },
                { label: '输出类型', key: 'writerType', span: 1, value: '' },
                { label: '更新字段', key: 'updateField', span: 1, value: '' },
                { label: '日期格式转换', key: 'format', span: 1, value: '' }
            ]
        }
    ],
    taskInfo2: [
        {
            title: '基础信息',
            list: [
                { label: '任务名称', key: 'name', span: 2, value: '' },
                { label: '业务分类', key: 'business', span: 1, value: '' },
                { label: '描述', key: 'description', span: 1, value: '' },
                { label: '数据源', key: 'sourceName', span: 1, value: '' },
                { label: '目的源', key: 'tagertName', span: 1, value: '' }
            ]
        },
        {
            title: '源头表信息',
            list: [
                { label: '文件目录', key: 'sourceTable', span: 2, value: '' },
                { label: '执行类', key: 'sourceClass', span: 2, value: '' },
                { label: '文件名称', key: 'whereSql', span: 1, value: '' },
                { label: '编码方式', key: 'fetchSize', span: 1, value: '' },
                { label: '文件时间', key: 'splitPk', span: 1, value: '' }
            ]
        },
        {
            title: '目的表信息',
            list: [
                { label: '文件目录', key: 'tagertTable', span: 2, value: '' },
                { label: '执行类', key: 'tagertClass', span: 2, value: '' },
                { label: '缓存大小/kb', key: 'tableNumber', span: 1, value: '' }
            ]
        }
    ],
    taskSet: {
        title: '任务设置信息',
        list: [
            { title: '输入/输出线程池', key: 'executor', list: [] },
            { title: '数据闸口', key: 'exchange', list: [] },
            { title: '输入/输出通道', key: 'channel', list: [] },
            { title: '其它插件', key: 'plugs', list: [] }
        ]
    }
});

const getData = async () => {
    let res = await getTaskDetails({ id: globalData.row.id });
    if (res) {
        let data = res.data;
        data.taskCoreList.forEach((item) => {
            if (item.keyName == 'name') {
                item.label = item.dataType + '执行类';
            } else {
                item.label = item.keyName;
            }
        });
        const groupedData = groupDataByTypeName(data.taskCoreList);
        state.taskSet.list.forEach((item) => {
            for (const key in groupedData) {
                if (item.key == key) {
                    item.list = groupedData[key];
                }
            }
        });

        if (data.type != 'ftp') {
            data.writerType == 'insert' ? (data.writerType = '新增') : (data.writerType = '更新');
            if (data.precise) {
                state.taskInfo[1].list[6].value = '精准切分';
                state.taskInfo[1].list.splice(7, 1);
                state.taskInfo[1].list.splice(7, 1);
            } else {
                if (data.tableNumber != '' && data.tableNumber != null && data.tableNumber != undefined) {
                    state.taskInfo[1].list[6].value = '平均切分';
                    state.taskInfo[1].list.splice(8, 1);
                } else {
                    state.taskInfo[1].list[6].value = '执行数切分';
                    state.taskInfo[1].list.splice(7, 1);
                }
            }

            state.taskInfo.forEach((item) => {
                item.list.forEach((ret) => {
                    for (const key in data) {
                        if (key == ret.key) {
                            ret.value = data[key];
                        }
                    }
                });
            });
        } else {
            state.taskInfo2.forEach((item) => {
                item.list.forEach((ret) => {
                    for (const key in data) {
                        if (key == ret.key) {
                            ret.value = data[key];
                        }
                    }
                });
            });

            state.taskInfo = state.taskInfo2;
        }
    }
};

// 将数据按照typeName字段进行分组的函数
function groupDataByTypeName(data) {
    return data.reduce((acc, obj) => {
        const key = obj.typeName;
        if (!acc[key]) {
            acc[key] = [];
        }
        acc[key].push(obj);
        return acc;
    }, {});
}
</script>

<template>
    <div v-loading="loading">
        <div v-for="(item, index) in state.taskInfo" :key="index">
            <el-divider content-position="left" class="title">
                <div class="title">{{ item.title }}</div>
            </el-divider>
            <el-descriptions border :column="2">
                <el-descriptions-item label-align="center" :span="ret.span" v-for="ret in item.list">
                    <template #label>
                        <div>
                            <span>{{ ret.label }}</span>
                        </div>
                    </template>
                    <el-form-item>
                        <div v-if="ret.slot" class="slotName">
                            <div class="field" v-if="ret.key == 'sourceCloumn' || ret.key == 'targetCloumn'">
                                <div v-for="(res, index) in ret.value" class="type">
                                    <tooltips :lineClamp="1" :tipText="res"> {{ res }}</tooltips>
                                </div>
                            </div>
                            <div class="field" v-else-if="ret.key == 'convertField'">
                                <div v-for="(res, index) in ret.value" class="type">
                                    <tooltips
                                        :lineClamp="1"
                                        :tipText="`${res.fieldName}:${res.oldData}-${res.newData}`"
                                    >
                                        {{ res.fieldName }}:{{ res.oldData }}-{{ res.newData }}
                                    </tooltips>
                                </div>
                            </div>
                            <div class="field" v-else-if="ret.key == 'differentField'">
                                <div v-for="(res, index) in ret.value" class="type">
                                    <tooltips :lineClamp="1" :tipText="`${res.source}-${res.target}`">
                                        {{ res.source }}-{{ res.target }}
                                    </tooltips>
                                </div>
                            </div>
                        </div>
                        <div v-else>
                            <tooltips :lineClamp="1" :tipText="ret.value"> {{ ret.value }}</tooltips>
                        </div>
                    </el-form-item>
                </el-descriptions-item>
            </el-descriptions>
        </div>
        <div>
            <div style="margin-bottom: 33px">
                <el-divider content-position="left" class="title">
                    <div class="title"> {{ state.taskSet.title }}</div>
                </el-divider>
            </div>
            <div v-for="item in state.taskSet.list">
                <el-divider content-position="left" class="title" st>{{ item.title }}</el-divider>
                <el-descriptions border :column="2">
                    <el-descriptions-item label-align="center" :span="ret.span" v-for="ret in item.list">
                        <template #label>
                            <div>
                                <span>{{ ret.label }}</span>
                            </div>
                        </template>
                        <el-form-item>
                            <div v-if="ret.slot"> 自定义 </div>
                            <div v-else>
                                <tooltips :lineClamp="1" :tipText="ret.value"> {{ ret.value }}</tooltips>
                            </div>
                        </el-form-item>
                    </el-descriptions-item>
                </el-descriptions>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
//webkit内核浏览器 滚动条大小
::-webkit-scrollbar {
    width: 4px;
    height: 2px;
    background-color: transparent;
}

.title {
    font-weight: bold;
    font-size: 16px;
}

.slotName {
    width: 100%;
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
</style>
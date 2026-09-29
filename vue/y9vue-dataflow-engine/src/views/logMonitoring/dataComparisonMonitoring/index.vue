<template>
    <y9Table :config="y9TableConfig" uniqueIdent="dataComparsion" :filterConfig="filterConfig"></y9Table>
</template>

<script lang="ts" setup>
    import { ref, h } from 'vue';
    import { getStoragePageSize } from '@/utils/index';
    const y9TableConfig = ref({
        headerBackground: true,
        pageConfig: {
            background: false, //是否显示背景色
            layout: 'prev, pager, next,sizes', //布局
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: getStoragePageSize('dataComparsion', 5), //每页显示条目个数，支持 v-model 双向绑定
            total: 2, //总条目数
            pageSizeOpts: [5, 10, 20, 30, 40, 1000] //每页显示个数选择器的选项设置
        },
        columns: [
            {
                type: 'index',
                title: '序号',
                width: 60
            },
            {
                title: '姓名',
                key: 'name'
            },
            {
                title: '性别',
                key: 'sex'
            },
            {
                title: '所在部门',
                key: 'department'
            },
            {
                title: '操作',
                render: (row) => {
                    return h('span', '详情');
                }
            }
        ],
        tableData: [
            {
                id: '1',
                name: '小红',
                sex: '女',
                department: '运营部'
            },
            {
                id: '2',
                name: '小白',
                sex: '男',
                department: '技术部'
            }
        ]
    });

    let filterConfig = ref({
        itemList: [
            {
                type: 'input',
                key: 'name',
                span: 6
            }
        ],
        filtersValueCallBack: (filters) => {
            console.log('过滤值', filters);
        }
    });
</script>

<style lang="scss" scoped></style>

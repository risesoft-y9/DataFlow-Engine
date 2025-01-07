import { getTableList } from '@/api/libraryTable';
import { useSettingStore } from '@/store/modules/settingStore';
import { ref, reactive, computed } from 'vue';
import { currNode } from '../index';
import { getStoragePageSize } from '@/utils/index';
export const tableHeight = ref(useSettingStore().getWindowHeight - 60 - 80 - 188 - 40 - 10);
export const state = reactive({
    dialogConfig: {
        show: false
    },
    copyTabledialogConfig: {
        show: false
    },
    row: {},
    loading: false,
    copyBtn: false,
    // 过滤 数值
    currSearchValue: {},
    //  表格的过滤 条件
    filterConfig: {
        filtersValueCallBack: (filters) => {
            //过滤值回调
            state.currSearchValue = filters;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'name',
                label: '搜索',
                labelWith: '120px',
                props: {
                    placeholder: '数据表名称'
                },
                span: 8
            },
            {
                type: 'slot',
                slotName: 'bnts',
                span: 10
            }
        ],
        fangDouTime: 0, //防抖时间
        showBorder: true
    },
    // 个人权限 表格的 配置条件
    tableConfig: {
        height: tableHeight,
        columns: [
            {
                type: 'index',
                title: computed(() => '序号'),
                width: 100,
                fixed: 'left'
            },
            {
                title: computed(() => '数据表名称'),
                width: 220,
                key: 'name'
            },
            {
                title: computed(() => '表中文名称'),
                key: 'cname'
            },
            {
                title: computed(() => '数据源名称'),
                key: 'baseName',
                showOverflowTooltip: true
            },
            {
                title: computed(() => '数据源类型'),
                key: 'baseType'
            },
            {
                title: computed(() => '数据量'),
                key: 'dataNum'
            },
            {
                title: computed(() => '操作'),
                fixed: 'right',
                width: 180,
                key: 'operation',
                slot: 'operation'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: getStoragePageSize('dataSheet', 10), //每页显示条目个数，支持 v-model 双向绑定
            total: 0
        }
    }
});

export const getPage = async () => {
    let params = {
        page: state.tableConfig.pageConfig.currentPage,
        size: state.tableConfig.pageConfig.pageSize,
        baseId: currNode.value.id,
        ...state.currSearchValue
    };
    const res = await getTableList(params);
    if (res.code == 0) {
        state.tableConfig.tableData = res.rows;
        state.tableConfig.pageConfig.total = res.total;
    }
};

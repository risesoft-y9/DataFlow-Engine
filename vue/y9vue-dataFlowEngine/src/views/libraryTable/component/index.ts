import { reactive, ref } from 'vue';
import { getPage } from '@/views/libraryTable/component/DataSheet/data';
import {findByTypeList, getDataSourceByType, getDataSourceType, searchDataSource,} from "@/api/dataSource";
export const currNode = ref({});

export const treeApiObj = reactive({
    //tree接口对象
    topLevel: getDataSourceType,
    flag: 'libraryTableType',
    childLevel: {
        api: getDataSourceByType,
        paramsUseNodeField: {
            category: 'name'
        }
    },
    search: {
        api: searchDataSource,
        searchKeyName: 'baseName'
    },
});

//删除节点
export const onDeleteTree = (data) => {
    console.log('删除节点', data);
};

//点击节点时
export const onNodeClick = async (currTreeNode) => {
    if(currTreeNode.$level == 2) {
        currNode.value = currTreeNode;
        getPage();
    }
};

export const onRefreshTree = () => {
    currNode.value = {};
    getPage();
};

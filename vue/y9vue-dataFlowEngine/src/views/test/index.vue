<script setup lang="ts">
    import { inject, nextTick, onMounted, reactive, watch } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import Y9Table2Comp from './comps/Y9Table2Comp.vue';
    import BodyTypeComp from './comps/BodyTypeComp.vue';
    import JSONEditor from 'jsoneditor';
    import { randomString, debounce } from '@/utils/index';
    import ZanWuShiJu from '@/assets/images/dataflowEnginePro/暂无数据 (2).png';
    import { NumberLiteralType } from 'typescript';
    import { setActivePinia } from 'pinia';
    import { cloneDeep } from 'lodash';

    //
    const settingStore = useSettingStore();
    const apiTest_el_tabs = ref('apiTest_el_tabs_' + randomString(10));
    const contentHeight = ref(settingStore.getWindowHeight - 60 - 30 - 30);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    watch(
        () => settingStore.getWindowHeight,
        (windowHeight) => {
            contentHeight.value = windowHeight - 60 - 30 - 30;
        }
    );
    const headerHeight = ref(70);
    const sizeObjInfo = inject('sizeObjInfo');
    const pageHeaderFontSize = ref(sizeObjInfo.extraLargeFont);
    const currentId = ref('');
    const currentFolderNode = ref({ name: '' });
    const hightlightNode = ref(null);
    const showFolderPage = ref(false);

    const templateTreeDataItem = {
        name: '接口模版',
        id: '1',
        type: 'api',
        ApiForm: {
            name: '接口模版',
            method: 'GET',
            url: '',
            header: [
                {
                    isSelect: true,
                    Key: 'Access-Control-Request-Method',
                    Param: 'keep-alive'
                }
            ],
            query: [
                {
                    isSelect: false,
                    Key: '',
                    Param: ''
                }
            ],
            body: {
                type: 1,
                1: null,
                2: [
                    {
                        isSelect: false,
                        headerKey: '',
                        headerParam: ''
                    }
                ],
                3: null,
                4: null,
                5: null,
                6: null,
                7: null
            }
        }
    };
    let treeData = ref([
        {
            name: '文件夹-1',
            type: 'folder',
            id: '1',
            children: [
                {
                    name: '示例接口-1',
                    id: '1-1',
                    type: 'api',
                    ApiForm: {
                        name: '示例接口-1',
                        method: 'POST',
                        url: 'http://localhost:8091/api/restful/post/1',
                        header: [
                            {
                                isSelect: true,
                                Key: 'Access-Control-Request-Method',
                                Param: 'keep-alive'
                            },
                            {
                                isSelect: true,
                                Key: 'content-type',
                                Param: 'application/json'
                            }
                        ],
                        query: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        body: {
                            type: 1,
                            1: null,
                            2: [
                                {
                                    isSelect: false,
                                    headerKey: '',
                                    headerParam: ''
                                }
                            ],
                            3: null,
                            4: null,
                            5: null,
                            6: null,
                            7: null
                        }
                    }
                },
                {
                    name: '示例接口-2',
                    id: '1-2',
                    type: 'api',
                    ApiForm: {
                        name: '示例接口-2',
                        method: 'POST',
                        url: 'http://localhost:8091/api/restful/post/2',
                        header: [
                            {
                                isSelect: true,
                                Key: 'Access-Control-Request-Method',
                                Param: 'keep-alive'
                            },
                            {
                                isSelect: true,
                                Key: 'content-type',
                                Param: 'application/json'
                            }
                        ],
                        query: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        body: {
                            type: 1,
                            1: null,
                            2: [
                                {
                                    isSelect: false,
                                    headerKey: '',
                                    headerParam: ''
                                }
                            ],
                            3: null,
                            4: null,
                            5: null,
                            6: null,
                            7: null
                        }
                    }
                },
                {
                    name: '示例接口-3',
                    id: '1-3',
                    type: 'api',
                    ApiForm: {
                        name: '示例接口-3',
                        method: 'POST',
                        url: 'http://localhost:8091/api/restful/post/1',
                        header: [
                            {
                                isSelect: true,
                                Key: 'Access-Control-Request-Method',
                                Param: 'keep-alive'
                            },
                            {
                                isSelect: true,
                                Key: 'content-type',
                                Param: 'application/json'
                            }
                        ],
                        query: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        body: {
                            type: 1,
                            1: null,
                            2: [
                                {
                                    isSelect: false,
                                    headerKey: '',
                                    headerParam: ''
                                }
                            ],
                            3: null,
                            4: null,
                            5: null,
                            6: null,
                            7: null
                        }
                    }
                }
            ]
        }
    ]);
    const treeDataSearchKey = ref('');

    let ApiForm = reactive({});
    /**
     * body.type
     * 1、none 2、form-data 3、json 4、xml 5、html 6、javascript 7、binary
     */

    const bodyType = ref(1);
    const RequestActiveName = ref('Query');
    const ResultActiveName = ref('实时响应');
    const RequestTabPane = reactive({
        headerTitle: 'Header',
        queryTitle: 'Query',
        bodyTitle: 'Body'
    });
    const ResultTabPane = reactive({
        responsiveTitle: '实时响应',
        requestTitle: '请求头',
        resultTitle: '响应头',
        cookieTitle: 'Cookie'
    });

    //
    const headerItemList = reactive([]);
    const queryItemList = reactive([]);
    const bodyFormDataItemList = reactive([]);
    const resposeRequestItemList = reactive([]);
    const resposeResposeItemList = reactive([]);

    let cookieTableConfig = ref({
        headerBackground: true,
        pageConfig: false,
        columns: [
            {
                title: 'name',
                key: 'name'
            },
            {
                title: 'value',
                key: 'value'
            },
            {
                title: 'httpOnly',
                key: 'httpOnly'
            },
            {
                title: 'domain',
                key: 'domain'
            },
            {
                title: 'expires',
                key: 'expires'
            },
            {
                title: 'path',
                key: 'path'
            },
            {
                title: 'secure',
                key: 'secure'
            }
        ],
        tableData: [
            // {
            //     name: 'name',
            //     value: 'value',
            //     httpOnly: 'httpOnly',
            //     domain: 'domain',
            //     expires: 'expires',
            //     path: 'path',
            //     secure: 'secure'
            // }
        ]
    });

    function onDeleteItem() {
        console.log('onDeleteItem');
        ApiForm.header = cloneDeep(headerItemList);
        ApiForm.query = cloneDeep(queryItemList);
        ApiForm.body['2'] = cloneDeep(bodyFormDataItemList);
        ApiFormChange();
    }
    function onEditItem() {
        console.log('onEditItem');
        ApiForm.header = cloneDeep(headerItemList);
        ApiForm.query = cloneDeep(queryItemList);
        ApiForm.body['2'] = cloneDeep(bodyFormDataItemList);
        ApiFormChange();
    }

    function searchByNodeId(nodeId) {
        function theItorator(array, id) {
            return array.forEach((item) => {
                if (item.id == id) {
                    return item;
                }
                if (item.children && item.children.length > 0) {
                    let result = theItorator(item.children, id);
                    if (result !== undefined) return result;
                }
            });
        }
        return theItorator(treeData.value, nodeId);
    }

    function treeDataFilter() {
        if (treeDataSearchKey.value) {
            return treeData.value.filter((item: any) => {
                return item.name.indexOf(treeDataSearchKey.value) > -1;
            });
        } else {
            return treeData.value;
        }
    }

    // 点击新建按钮
    const newApiTest = () => {
        templateTreeDataItem.id = treeData.value.length + 1 + '';
        let clonetemplateData = cloneDeep(templateTreeDataItem);
        treeData.value.push(clonetemplateData);
        nextTick(() => {
            removeActiveNode();
            let array = Array.from(document.getElementsByClassName('y9-tree-item'));
            let length = array.length;
            if (length) {
                array[length - 1].classList.add('active-node');
                hightlightNode.value = array[length - 1];
                setApiFormData(templateTreeDataItem);
            }
        });
    };
    function removeActiveNode() {
        let array = Array.from(document.getElementsByClassName('y9-tree-item'));
        let length = array.length;
        array.forEach((item, index) => {
            item.classList.remove('active-node');
        });
    }
    function setApiFormData(itemData) {
        function clearArray(array) {
            while (array.length) {
                array.pop();
            }
        }
        function pushAll(array, data) {
            for (let i = 0; i < data.length; i++) {
                array.push(data[i]);
            }
        }
        Object.assign(ApiForm, itemData.ApiForm);
        clearArray(headerItemList);
        clearArray(queryItemList);
        clearArray(bodyFormDataItemList);
        pushAll(headerItemList, itemData.ApiForm.header);
        pushAll(queryItemList, itemData.ApiForm.query);
        pushAll(bodyFormDataItemList, itemData.ApiForm.body['2']);
        bodyType.value = Number(itemData.ApiForm.body['type']);
        if (bodyType.value > 1) {
            RequestActiveName.value = 'Body';
        } else {
            RequestActiveName.value = 'Query';
        }

        if (document.getElementsByClassName('active-node').length) {
            hightlightNode.value = document.getElementsByClassName('active-node')[0];
        }
    }
    // 点击树节点
    const onTreeNodeClick = (node) => {
        console.log('被点击了', node);
        currentId.value = node.id;
        if (node.type == 'folder') {
            currentFolderNode.value = searchByNodeId(currentId.value);
            console.log(currentFolderNode.value);
            showFolderPage.value = true;
        } else {
            showFolderPage.value = false;
            if (node.children && node.children.length) {
                setApiFormData(node.children[0]);
            } else {
                setApiFormData(templateTreeDataItem);
            }
        }

        // removeActiveNode();
        // setApiFormData(node);
        // nextTick(() => {
        //     hightlightNode.value = document.getElementsByClassName('active-node')[0];
        // });
    };

    // 更改文件夹名字
    function changeFolderName(value) {
        if (value && value.indexOf(' ') > -1) {
            value = value.trim();
        }
        currentFolderNode.value.name = value;
        console.log(currentFolderNode.value);
    }
    //点击删除icon
    const onTreeRemoveIcon = (node) => {
        console.log('删除icon被点击了', node);
    };
    // 点击添加文件夹icon
    const onTreeAddFolderIcon = (node) => {
        console.log('点击了添加文件夹', node);
    };
    // 点击添加API icon
    const onTreeAddApiIcon = (node) => {
        console.log('点击了添加API', node);
    };
    // 改变api配置数据
    function ApiFormChange() {
        if (ApiForm.name && ApiForm.name.indexOf(' ') > -1) {
            ApiForm.name = ApiForm.name.trim();
        }

        if (treeData.value.length) {
            treeData.value.map((item) => {
                if (item.id == currentId.value) {
                    item.name = ApiForm.name;
                    Object.assign(item.ApiForm, ApiForm);
                    console.log(item);
                }
            });
            nextTick(() => {
                hightlightNode.value.classList.add('active-node');
            });
        } else {
            ElMessageBox.alert('左侧列表没有接口数据，请先点击新增', '操作提示', {
                confirmButtonText: 'OK'
            });
        }
    }
    // 选择body类型
    function setBodyType(value) {
        ApiForm.body['type'] = Number(value);
        bodyType.value = Number(value);
        ApiFormChange();
    }

    //
    const jsonContainer = ref(null);
    function initJsonContainer() {
        jsonContainer.value = document.getElementById('json-container');
        const options = {
            selectionStyle: 'tree',
            mode: 'code',
            statusBar: true,
            mainMenuBar: false,
            onChangeText() {
                console.log('json changed');
            }
        };
        const editor = new JSONEditor(jsonContainer.value, options);

        // set json
        // const initialJson = {
        //     Array: [1, 2, 3],
        //     Boolean: true,
        //     Null: null,
        //     Number: 123,
        //     Object: { a: 'b', c: 'd' },
        //     String: 'Hello World'
        // };
        // editor.set(initialJson);
        editor.set();

        document.querySelector('#json-container .jsoneditor').style.height = '66vh';
        // console.log(editor);
        // get json
        // const updatedJson = editor.get();
    }
    function initTreeDataHightlightItem() {
        if (document.getElementsByClassName('y9-tree-item').length) {
            // document.getElementsByClassName('y9-tree-item')[0].classList.add('active-node');
            setApiFormData(treeData.value[0].children[0]);
        } else {
            setApiFormData(templateTreeDataItem);
        }
    }
    function init() {
        // initTreeDataHightlightItem();
        setApiFormData(templateTreeDataItem);
        initJsonContainer();
    }
    onMounted(() => {
        init();
    });
</script>
<template>
    <el-container>
        <el-header><i class="ri-link"></i>接口在线测试</el-header>
        <el-container>
            <el-aside width="31%">
                <y9Card title="我是标题">
                    <template #header>
                        <el-button>刷新</el-button>
                        <el-input
                            v-model="treeDataSearchKey"
                            @blur="treeDataFilter"
                            placeholder="请输入关键词/URL"
                            :prefix-icon="Search"
                        />
                        <el-button type="primary" @click="newApiTest">新建</el-button>
                    </template>
                    <y9Tree :data="treeDataFilter()" :expandOnClickNode="true" @node-click="onTreeNodeClick">
                        <template #actions="{ item }">
                            <i
                                class="ri-folder-add-line"
                                v-show="item.type == 'folder'"
                                @click="onTreeAddFolderIcon(item)"
                            ></i>
                            <i class="ri-add-large-line" @click="onTreeAddApiIcon(item)"></i>
                            <i class="ri-delete-bin-line" @click="onTreeRemoveIcon(item)"></i>
                        </template>
                    </y9Tree>
                </y9Card>
            </el-aside>
            <el-main>
                <div>
                    <div class="mainHeader">
                        <span>{{ ApiForm.name }}</span>
                        <span><i class="ri-map-pin-line"></i>{{ ApiForm.name }}</span>
                    </div>
                    <div class="mainContainer" v-show="showFolderPage">
                        <div class="folder">
                            <span>文件夹</span>
                            <span><el-input v-model="currentFolderNode.name" @input="changeFolderName" /></span>
                        </div>
                    </div>
                    <div class="mainContainer" v-show="!showFolderPage">
                        <el-form v-model="ApiForm">
                            <el-form-item label="接口名称">
                                <el-input v-model="ApiForm.name" @input="ApiFormChange"></el-input>
                            </el-form-item>
                            <el-form-item label="请求方法">
                                <el-select v-model="ApiForm.method" placeholder="GET" @change="ApiFormChange">
                                    <el-option label="GET" value="GET" />
                                    <el-option label="POST" value="POST" />
                                    <el-option label="DELETE" value="DELETE" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="请求地址">
                                <el-input v-model="ApiForm.url" @input="ApiFormChange"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-tabs class="apiTest_el_tabs" v-model="RequestActiveName" @tab-click="handleClick">
                                    <el-tab-pane name="Header">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ RequestTabPane.headerTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <Y9Table2Comp
                                            key="header"
                                            :itemList="headerItemList"
                                            @on-delete="onDeleteItem"
                                            @on-edit="onEditItem"
                                        ></Y9Table2Comp>
                                    </el-tab-pane>
                                    <el-tab-pane name="Query">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ RequestTabPane.queryTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <Y9Table2Comp
                                            key="query"
                                            :itemList="queryItemList"
                                            @on-delete="onDeleteItem"
                                            @on-edit="onEditItem"
                                        ></Y9Table2Comp>
                                    </el-tab-pane>
                                    <el-tab-pane name="Body">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ RequestTabPane.bodyTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div class="body-content">
                                            <BodyTypeComp
                                                key="bodyTypeComp"
                                                :type="bodyType"
                                                :itemList="bodyFormDataItemList"
                                                @on-delete="onDeleteItem"
                                                @on-edit="onEditItem"
                                                @on-radio-change="setBodyType"
                                            ></BodyTypeComp>
                                        </div>
                                    </el-tab-pane>
                                </el-tabs>
                            </el-form-item>
                            <el-form-item>
                                <el-divider>
                                    <el-button type="primary">发送</el-button>
                                    <el-button>保存</el-button>
                                </el-divider>
                                <div class="fixed-status">
                                    <span><i class="ri-global-line"></i>&nbsp; 状态：</span>
                                    <span>200</span>&nbsp;
                                    <span>时间：</span>
                                    <span>10:09:36 &nbsp; 42ms</span> &nbsp;
                                    <span>大小：</span>
                                    <span>0.04kb <i class="ri-arrow-down-circle-fill"></i></span>
                                </div>
                                <el-tabs
                                    class="apiTest_el_tabs respose-el-tabs"
                                    v-model="ResultActiveName"
                                    @tab-click="handleClick"
                                >
                                    <el-tab-pane name="实时响应">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ ResultTabPane.responsiveTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div id="json-container"></div>
                                    </el-tab-pane>
                                    <el-tab-pane name="请求头">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ ResultTabPane.requestTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div class="requestTitle-content">
                                            <div v-for="item in resposeRequestItemList" :key="item.key">
                                                <el-row>
                                                    <el-col :span="8"
                                                        ><span>{{ item.key }}</span></el-col
                                                    >
                                                    <el-col :span="16"
                                                        ><span>{{ item.value }}</span></el-col
                                                    >
                                                </el-row>
                                            </div>
                                        </div>
                                    </el-tab-pane>
                                    <el-tab-pane name="响应头">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ ResultTabPane.resultTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div class="resultTitle-content">
                                            <div v-for="item in resposeResposeItemList" :key="item.key">
                                                <el-row>
                                                    <el-col :span="8"
                                                        ><span>{{ item.key }}</span></el-col
                                                    >
                                                    <el-col :span="16"
                                                        ><span>{{ item.value }}</span></el-col
                                                    >
                                                </el-row>
                                            </div>
                                        </div>
                                    </el-tab-pane>
                                    <el-tab-pane name="Cookie">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ ResultTabPane.cookieTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div class="cookieTitle-content">
                                            <y9Table :config="cookieTableConfig"></y9Table>
                                        </div>
                                    </el-tab-pane>
                                </el-tabs>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>
<style lang="scss" scoped>
    .el-container {
        min-width: 1440px;
        & > .el-header {
            color: var(--el-color-primary);
            background-color: var(--el-bg-color);
            font-size: v-bind(pageHeaderFontSize);
            height: 60px;
            line-height: 60px;
            display: flex;
            align-items: center;
            padding-left: 30px;
            i {
                margin-right: 10px;
            }
            box-shadow: 0 2px 4px #0000000d;
        }
        & > .el-aside {
            padding: 30px;

            :deep(.y9-card) {
                height: v-bind('contentHeight+"px"');
                .y9-card-header {
                    height: v-bind('headerHeight+"px"');
                    line-height: v-bind('headerHeight+"px"');
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    button.el-button {
                        min-width: 75px;
                        min-height: 30px;
                        font-weight: 400;
                        &:first-child {
                            border: solid 1px var(--el-color-primary);
                            color: var(--el-color-primary);
                        }
                    }
                    .el-input {
                        min-width: 240px;
                        margin: 0 15px;
                        .el-input__wrapper {
                            border-radius: 20px;
                        }
                    }
                }
                .y9-card-content {
                    .y9-tree {
                        .y9-tree-item.active-node {
                            & > .y9-tree-item-content .y9-tree-item-content-div .action-icons i[class^='ri-'] {
                                color: var(--el-bg-color);
                            }
                        }
                    }
                }
            }
        }
        & > .el-main {
            padding: 30px;
            padding-left: 0;
            & > div {
                background-color: rgb(233, 233, 239);
                box-shadow: 0 2px 4px #0000000d;
                height: v-bind('contentHeight+"px"');
                border-radius: 5px;
                padding: 0 15px 15px 15px;
                .mainHeader {
                    height: v-bind('headerHeight+"px"');
                    line-height: v-bind('headerHeight+"px"');
                    display: flex;
                    justify-content: space-between;
                    & > span:first-child {
                        font-size: v-bind('fontSizeObj.largerFontSize');
                    }
                }
                .mainContainer {
                    height: v-bind('contentHeight-headerHeight-15+"px"');
                    overflow: scroll;
                    background-color: var(--el-bg-color);
                    padding: 15px;
                    border-radius: 5px;

                    .folder {
                        height: v-bind('contentHeight-headerHeight-15+"px"');
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                    .el-form {
                        .el-form-item {
                            .el-form-item__content {
                                .el-tabs {
                                    width: 100%;
                                    .requestTitle-content,
                                    .resultTitle-content {
                                        & > div {
                                            width: 100%;
                                            border: rgb(236, 238, 245) solid 1px;
                                            span {
                                                padding: 0 15px;
                                                overflow: scroll;
                                            }
                                            &:not(:first-child) {
                                                border-top: none;
                                            }
                                        }
                                    }
                                    .cookieTitle-content {
                                        :deep(.y9-table-div) {
                                            background-color: var(--el-bg-color);
                                            padding: 0;
                                            .el-table__empty-text {
                                                background-image: url('@/assets/images/dataflowEnginePro/暂无数据 (2).png');
                                                background-repeat: no-repeat;
                                                background-position: 50.5% 18px;
                                                line-height: 200px;
                                            }
                                        }
                                        .no-data-ui {
                                            position: relative;
                                            z-index: 1;
                                            width: 100%;
                                            min-height: 180px;
                                            display: flex;
                                            justify-content: center;
                                            align-items: center;
                                        }
                                    }
                                }
                                .respose-el-tabs {
                                    position: relative;
                                    top: -40px;
                                }
                            }
                            .apiTest_el_tabs {
                                .label-title {
                                    span:last-child {
                                        color: rgb(102, 182, 104);
                                    }
                                }
                            }
                            .fixed-status {
                                position: relative;
                                z-index: 1;
                                line-height: 40px;
                                height: 40px;
                                width: 400px;
                                text-align: right;
                                left: calc(100% - 400px);
                                & > span:nth-child(even) {
                                    color: rgb(102, 182, 104);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
</style>
<style>
    #json-container .jsoneditor {
        border: thin solid rgb(236, 238, 245);
        height: auto;
        min-height: 20vh;
        .ace-jsoneditor .ace_gutter {
            background: var(--el-bg-color);
            border-right: thin solid rgb(236, 238, 245);
            color: #c0c0c0;
        }
    }
    .apiTest_el_tabs div.el-tabs__nav-wrap::after {
        height: 1px;
    }
</style>

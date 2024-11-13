<script setup lang="ts">
    import { inject, nextTick, onMounted, reactive, watch } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import Y9Table2Comp from './comps/Y9Table2Comp.vue';
    import BodyTypeComp from './comps/BodyTypeComp.vue';
    import JSONEditor from 'jsoneditor';
    import ZanWuShiJu from '@/assets/images/dataflowEnginePro/暂无数据 (2).png';
    import { cloneDeep, forEach, forIn } from 'lodash';
    import { initData } from './apiUtils';
    import { randomString } from './apiUtils';

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
    const tokenKey = ref('');
    const tokenValue = ref('');
    const Authorization = ref(false);
    const count = ref(0);
    const headerHeight = ref(70);
    const sizeObjInfo = inject('sizeObjInfo');
    const pageHeaderFontSize = ref(sizeObjInfo.extraLargeFont);
    const currentId = ref('');
    const activeNode = ref(null);
    const folderName = ref('');
    const hightlightNode = ref(null);
    const showFolderPage = ref(true);
    const currentExpandedKeys = reactive([]);
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
                    Key: 'Connection',
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
                        Key: '',
                        Param: ''
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
    const templateTreeDataFolder = {
        name: '文件夹-1',
        type: 'folder',
        id: '1',
        children: []
    };
    // 初始化树组件数据
    let treeData = ref([]);
    treeData.value.push.call(treeData.value, ...initData);

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

    function searchByNodeId(node, operation = 'search') {
        function theItorator(array, id, operation) {
            for (let i = 0; i < array.length; i++) {
                let item = array[i];
                if (operation == 'search' && item.id == id) {
                    return item;
                }
                if (operation == 'searchElementNode') {
                    console.log(item);
                }
                if (operation == 'delete' && item.id == id) {
                    array.splice(i, 1);
                    return 'delete操作';
                }
                if (operation == 'addFolder' && item.id == id) {
                    templateTreeDataFolder.id = `${item.id}-${item.children.length + 1}`;
                    templateTreeDataFolder.children = [];
                    let clonetemplateData = cloneDeep(templateTreeDataFolder);
                    if (item.children.length) {
                        // 同级节点下，如果有其它的文件夹，插入到其它文件夹之后
                        for (let j = item.children.length - 1; j >= 0; j--) {
                            let child = item.children[j];
                            if (child.type == 'folder') {
                                item.children[j + 1] = clonetemplateData;
                                return 'addFolder操作';
                            } else {
                                item.children[j + 1] = child;
                            }
                            if (j == 0) {
                                item.children[0] = clonetemplateData;
                            }
                        }
                    } else {
                        item.children.push(clonetemplateData);
                        return 'addFolder操作';
                    }
                }

                if (operation == 'addApi' && item.id == id) {
                    if (node.type == 'folder') {
                        templateTreeDataItem.id = `${item.id}-${item.children.length + 1}`;
                        item.children.push(cloneDeep(templateTreeDataItem));
                    }
                    if (node.type == 'api') {
                        // 同级节点上的“+”事件，array[0]一定存在
                        let temp = array[0].id.split('-');
                        temp[temp.length - 1] = array.length + 1;
                        templateTreeDataItem.id = temp.join('-');
                        array.push(cloneDeep(templateTreeDataItem));
                    }
                    return 'addApi操作';
                }
                if (item.children && item.children.length > 0) {
                    let res = theItorator(item.children, id, operation);
                    if (res) {
                        return res;
                    } else {
                        continue;
                    }
                }
            }
        }

        let r = theItorator(treeData.value, node.id, operation);
        return r;
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

    /**
     * 新建按钮功能：根级节点新建文件夹或API
     */
    const createTreeDataRootNode = (value) => {
        let clonetemplateData = {};
        if (value == 'folder') {
            templateTreeDataFolder.id = treeData.value.length + 1 + '';
            templateTreeDataFolder.children = [cloneDeep(templateTreeDataItem)];
            templateTreeDataFolder.children[0].id = templateTreeDataFolder.id + '-1';
            clonetemplateData = cloneDeep(templateTreeDataFolder);
        } else {
            templateTreeDataItem.id = treeData.value.length + 1 + '';
            clonetemplateData = cloneDeep(templateTreeDataItem);
        }
        treeData.value.push(clonetemplateData);
        // nextTick(() => {
        //     removeActiveNode();
        //     let array = Array.from(document.getElementsByClassName('y9-tree-item'));
        //     let length = array.length;
        //     if (length) {
        //         array[length - 1].classList.add('active-node');
        //         hightlightNode.value = array[length - 1];
        //         setApiFormData(templateTreeDataItem);
        //     }
        // });
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
        // 设置表单数据
        Object.assign(ApiForm, itemData.ApiForm);
        clearArray(headerItemList);
        if (Authorization.value) {
            getToken();
        }
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
    }
    // 点击树节点
    const onTreeNodeClick = (node) => {
        // console.log('被点击了', node);
        currentId.value = node.id;
        if (node.type == 'folder') {
            currentExpandedKeys[0] = node.id;
            folderName.value = searchByNodeId(node).name;
            showFolderPage.value = true;
        } else {
            showFolderPage.value = false;
            setApiFormData(node);
            initResposeParams();
        }
    };

    // 更改文件夹名字
    function changeFolderName(name) {
        if (name && name.indexOf(' ') > -1) {
            name = name.trim();
        }
        folderName.value = name;
        let node = {
            id: currentId.value
        };
        searchByNodeId(node).name = folderName.value;
    }
    //点击删除icon
    const onTreeRemoveIcon = (node) => {
        // console.log('删除icon被点击了', node);
        if (treeData.value.length <= 1) {
            searchByNodeId(node, 'delete');
        } else {
            ElMessageBox.alert('至少保留一个根节点', '操作提示', {
                confirmButtonText: 'OK'
            });
        }
    };
    // 点击添加文件夹icon
    const onTreeAddFolderIcon = (node) => {
        // console.log('点击了添加文件夹', node);
        searchByNodeId(node, 'addFolder');
    };
    // 点击添加API icon
    const onTreeAddApiIcon = (node) => {
        console.log('点击了添加API', node);
        searchByNodeId(node, 'addApi');
    };
    // 改变api配置数据
    function ApiFormChange() {
        if (ApiForm.name && ApiForm.name.indexOf(' ') > -1) {
            ApiForm.name = ApiForm.name.trim();
        }

        if (treeData.value.length) {
            let item = searchByNodeId({ id: currentId.value });
            item.name = ApiForm.name;
            Object.assign(item.ApiForm, ApiForm);
            count.value++;
        } else {
            ElMessageBox.alert('左侧列表没有接口数据，请先点击新增', '操作提示', {
                confirmButtonText: 'OK'
            });
        }
    }

    // 当前节点高亮丢失问题
    function checkHightlightNodeClass() {}
    // 选择body类型
    function onRadioChange(value) {
        ApiForm.body['type'] = Number(value);
        bodyType.value = Number(value);
        ApiFormChange();
    }

    function onJsonEditorChange(text) {
        ApiForm.body[bodyType.value] = text;
        ApiFormChange();
    }

    function initResposeParams() {
        requstTime.value = 0;
        statusCode.value = 0;
        contentLength.value = '0';
        editor.value.set();
        while (resposeRequestItemList.length) {
            resposeRequestItemList.pop();
        }
        while (resposeResposeItemList.length) {
            resposeResposeItemList.pop();
        }
        while (cookieTableConfig.value.tableData.length) {
            cookieTableConfig.value.tableData.pop();
        }
    }
    const requstTime = ref(0);
    const statusCode = ref(0);
    const contentLength = ref('0');
    async function submit() {
        // console.log(ApiForm);
        // {
        //                     isSelect: true,
        //                     Key: 'Cache-Control',
        //                     Param: 'public'
        //                 },
        //                 {
        //                     isSelect: true,
        //                     Key: 'Accept',
        //                     Param: '*/*'
        //                 },
        //                 {
        //                     isSelect: true,
        //                     Key: 'Accept-Encoding',
        //                     Param: 'gzip, deflate, br'
        //                 },
        // 1、统一处理请求头的逻辑
        let req_headers = {};
        for (let i = 0; i < ApiForm.header.length; i++) {
            const item = ApiForm.header[i];
            req_headers[item.Key] = item.Param;
        }
        if (!req_headers['Cache-Control']) {
            req_headers['Cache-Control'] = 'public';
        }
        if (!req_headers['Accept']) {
            req_headers['Accept'] = '*/*';
        }
        if (!req_headers['Accept-Encoding']) {
            req_headers['Accept-Encoding'] = 'gzip, deflate, br';
        }
        if (!req_headers['Access-Control-Allow-Credentials']) {
            req_headers['Access-Control-Allow-Credentials'] = 'true';
        }

        // 请求标头
        while (resposeRequestItemList.length) {
            resposeRequestItemList.pop();
        }
        forEach(req_headers, (value, key) => {
            resposeRequestItemList.push({ key: key, value: value });
        });
        // 如果没有自定义请求头，添加默认请求头
        if (!req_headers['User-Agent']) {
            resposeRequestItemList.push({ key: 'User-Agent', value: window.navigator.userAgent });
        }
        if (!req_headers['Host']) {
            resposeRequestItemList.push({ key: 'Host', value: window.location['host'] });
        }
        if (!req_headers['Origin']) {
            resposeRequestItemList.push({ key: 'Origin', value: window.location['origin'] });
        }
        if (!req_headers['Access-Control-Allow-Credentials']) {
            resposeRequestItemList.push({ key: 'Access-Control-Allow-Credentials', value: 'true' });
        }

        // 2、统一处理请求参数的逻辑
        let data = {},
            dataStr = '?',
            hasData = false;
        function __forArray(array) {
            for (let i = 0; i < array.length; i++) {
                const item = array[i];
                const isSelect = item.isSelect;
                const Key = item.Key;
                const Param = item.Param;
                if (isSelect) {
                    hasData = true;
                    data[Key] = Param;
                    dataStr += `${Key}=${Param}&`;
                }
            }
        }
        console.log(ApiForm.body.type);
        switch (ApiForm.body.type) {
            case 1:
                // bydyType为none，获取query
                __forArray(ApiForm.query);
                break;
            case 2:
                // form-data
                __forArray(ApiForm.body[2]);
                break;
            case 3:
                // 数据可能不是标准的json格式
                try {
                    let arr = [];
                    forIn(JSON.parse(ApiForm.body[3]), (value, key) => {
                        arr.push({ isSelect: true, Key: key, Param: value });
                    });
                    __forArray(arr);
                } catch (error) {
                    console.log(error);
                }
                break;
            case 4:
            case 5:
                // xml & html
                // 如果是get方式，采用拼接参数，计算最大支持的长度，如果超过2048，则提示
                console.log(ApiForm.body[4]);
                // 如果是post方式，转obj对象，采用body发送
                break;
            case 6:
                // 上传文件

                break;
            default:
                break;
        }

        // 3、统一处理请求体参数的逻辑
        let EncodingFormatBody = null;
        switch (ApiForm.method.toUpperCase()) {
            case 'GET':
            case 'HEAD':
                // 如果有参数，GET只支持url拼接参数，不支持请求体body
                if (dataStr.length > 1) {
                    dataStr = dataStr.substring(0, dataStr.length - 1);
                    ApiForm.url = encodeURI(ApiForm.url.split('?')[0] + dataStr);
                }
                break;
            case 'POST':
            case 'PUT':
                // 如果有参数，发送body
                if (hasData) {
                    // 区分不同响应头的编码要求
                    if (req_headers['Content-Type'].toLowerCase().include('application/json')) {
                        EncodingFormatBody = JSON.stringify(data);
                    } else if (req_headers['Content-Type'].toLowerCase().include('application/x-www-form-urlencoded')) {
                        // dataStr在上面的代码中，是以?开头，以&结尾，都需要去掉
                        let bodyStr = dataStr.substring(0, dataStr.length - 1);
                        EncodingFormatBody = bodyStr.substring(1, bodyStr.length);
                    } else {
                        ApiForm.url = encodeURI(ApiForm.url.split('?')[0] + dataStr);
                    }
                }
                break;
            default:
                break;
        }

        // 4、发送请求
        let t = new Date().getTime();
        const response = await fetch(ApiForm.url, {
            method: ApiForm.method,
            headers: new Headers(req_headers),
            withCredentials: 'include',
            body: EncodingFormatBody
        })
            .then((response) => {
                console.log(response);
                // 响应标头
                const headers = response.headers;
                while (resposeResposeItemList.length) {
                    resposeResposeItemList.pop();
                }
                let obj = {};
                for (let [key, value] of headers.entries()) {
                    obj[key] = value;
                    resposeResposeItemList.push({ key: key, value: value });
                }
                let cl = obj['content-length'];
                if (cl < 1024) {
                    contentLength.value = cl + 'B';
                } else if (cl >= 1024 && cl < 1024 * 1000) {
                    contentLength.value = cl / 1024 + 'KB';
                } else {
                    console.log(cl);
                    contentLength.value = cl / 1024 / 1000 + 'MB';
                }

                // 获取cookie
                console.log(headers.getSetCookie());
                while (cookieTableConfig.value.tableData.length) {
                    cookieTableConfig.value.tableData.pop();
                }
                for (let [key, value] of headers.getSetCookie()) {
                    let obj = {};
                    // { name: key, value: value,  httpOnly: '', secure: '', path: '',domain: '',expires: '' }
                    // cookieTableConfig.value.tableData.push(obj);
                }
                statusCode.value = response.status;

                // 其他处理逻辑...
                return response.json();
            })
            .then((data) => {
                console.log(data);
                editor.value.set(data);
                requstTime.value = new Date().getTime() - t;
            })
            .catch((error) => {
                console.log(error);
                editor.value.set(error.toString());
            });
    }

    //
    const editor = ref(null);
    const jsonContainer = ref(null);
    function initJsonContainer() {
        jsonContainer.value = document.getElementById('json-container');
        const options = {
            selectionStyle: 'tree',
            mode: 'code',
            statusBar: true,
            mainMenuBar: false,
            onChangeText() {
                onJsonEditorChange(editor.value.getText());
            }
        };
        editor.value = new JSONEditor(jsonContainer.value, options);

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
        editor.value.set();

        document.querySelector('#json-container .jsoneditor').style.height = '66vh';
        // console.log(editor);
        // get json
        // const updatedJson = editor.get();
    }

    function getToken() {
        const json = JSON.parse(sessionStorage.getItem(tokenKey.value));
        tokenValue.value = json.access_token;
        let find = false;
        for (let i = 0; i < ApiForm.header.length; i++) {
            const item = ApiForm.header[i];
            if (item.Key == 'Authorization') {
                item.Param = `Bearer ${tokenValue.value}`;
                find = true;
            }
        }
        if (!find) {
            ApiForm.header.push({ isSelect: true, Key: 'Authorization', Param: `Bearer ${tokenValue.value}` });
        }
    }
    function autoAddToken() {
        tokenKey.value = import.meta.env.VUE_APP_SSO_SITETOKEN_KEY;

        if (sessionStorage.getItem(tokenKey.value)) {
            Authorization.value = true;
            getToken();
            window.addEventListener('storage', (e) => {
                // console.log(e);
                if (e.key == tokenKey.value) {
                    getToken();
                }
            });
        }
    }

    function init() {
        setApiFormData(templateTreeDataItem);
        initJsonContainer();
        autoAddToken();
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
                        <el-dropdown placement="bottom" @command="createTreeDataRootNode">
                            <el-button type="primary">新建</el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="folder">添加一级目录</el-dropdown-item>
                                    <el-dropdown-item command="api">添加一级API</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </template>
                    <y9Tree
                        :key="count"
                        :data="treeDataFilter()"
                        :expandOnClickNode="true"
                        :defaultExpandedKeys="currentExpandedKeys"
                        @node-click="onTreeNodeClick"
                    >
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
                            <span><el-input v-model="folderName" @input="changeFolderName" /></span>
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
                                    <el-option label="PUT" value="PUT" />
                                    <el-option label="HEAD" value="HEAD" />
                                    <el-option label="OPTIONS" value="OPTIONS" />
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
                                                <span>（{{ ApiForm.header.length }}）</span>
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
                                                <span>（{{ ApiForm.query.length }}）</span>
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
                                                <span></span>
                                            </div>
                                        </template>
                                        <div class="body-content">
                                            <BodyTypeComp
                                                :type="bodyType"
                                                :itemList="bodyFormDataItemList"
                                                :ApiForm="ApiForm"
                                                @on-delete="onDeleteItem"
                                                @on-edit="onEditItem"
                                                @on-radio-change="onRadioChange"
                                                @on-json-editor-change="onJsonEditorChange"
                                            ></BodyTypeComp>
                                        </div>
                                    </el-tab-pane>
                                </el-tabs>
                            </el-form-item>
                            <el-form-item>
                                <el-divider>
                                    <el-button type="primary" @click="submit">发送</el-button>
                                    <!-- <el-button>保存</el-button> -->
                                </el-divider>
                                <div class="fixed-status">
                                    <span><i class="ri-global-line"></i>&nbsp; 状态：</span>
                                    <span>{{ statusCode }}</span
                                    >&nbsp;
                                    <span>时间：</span>
                                    <span
                                        >{{
                                            `${new Date().getHours()}:${new Date().getMinutes()}:${new Date().getSeconds()}`
                                        }}
                                        &nbsp; {{ requstTime }}ms</span
                                    >
                                    &nbsp;
                                    <span>大小：</span>
                                    <span>{{ contentLength }} <i class="ri-arrow-down-circle-fill"></i></span>
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
                                                <span></span>
                                            </div>
                                        </template>
                                        <div id="json-container"></div>
                                    </el-tab-pane>
                                    <el-tab-pane name="请求头">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ ResultTabPane.requestTitle }}</span>
                                                <span>（{{ resposeRequestItemList.length }}）</span>
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
                                                <span>（{{ resposeResposeItemList.length }}）</span>
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
                                                <span>（{{ cookieTableConfig.tableData.length }}）</span>
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
                    .el-dropdown {
                        button.el-button {
                            color: var(--el-bg-color);
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
                                            .el-row {
                                                .el-col:first-child {
                                                    span {
                                                        padding: 0 15px;
                                                        overflow: scroll;
                                                    }
                                                }
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

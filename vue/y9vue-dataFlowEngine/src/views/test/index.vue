<script setup lang="ts">
    import { inject, onMounted, reactive, watch } from 'vue';
    import { Search } from '@element-plus/icons-vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import Y9Table2Comp from './comps/Y9Table2Comp.vue';
    import BodyTypeComp from './comps/BodyTypeComp.vue';
    import JSONEditor from 'jsoneditor';
    import { randomString } from '@/utils/index';
    import ZanWuShiJu from '@/assets/images/dataflowEnginePro/暂无数据 (2).png';

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
    const defaultAcitonIcons = { edit: true, add: false, remove: true, preview: false };
    let treeData = ref([
        {
            name: '北京有生博大有限公司',
            id: '1'
        },
        {
            name: '有生集团',
            id: '2'
        }
    ]);
    //点击编辑icon
    const onTreeEditIcon = (node) => {
        console.log('编辑icon被点击了', node);
    };
    //点击删除icon
    const onTreeRemoveIcon = (node) => {
        y9TreeDefaultActiveRef.value.remove(node);
        console.log('删除icon被点击了', node);
    };

    const ApiForm = reactive({
        name: '',
        method: 'GET',
        url: '',
        header: {},
        query: null,
        body: null
    });
    const bodyType = ref(1);
    const RequestActiveName = ref('Header');
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
    const headerItemList = reactive([
        {
            isSelect: true,
            headerKey: 'Access-Control-Request-Method',
            headerParam: 'keep-alive'
        }
    ]);
    const queryItemList = reactive([
        {
            isSelect: false,
            headerKey: '',
            headerParam: ''
        }
    ]);
    const bodyFormDataItemList = reactive([
        {
            isSelect: true,
            headerKey: 'Access-Control-Request-Method',
            headerParam: 'keep-alive'
        }
    ]);
    const resposeRequestItemList = reactive([
        {
            key: 'Access-Control-Request-Method',
            value: '-'
        },
        {
            key: 'Accept',
            value: '*/*'
        },
        {
            key: 'User-Agent',
            value: 'PostmanRuntime-ApipostRuntime/1.1.0'
        }
    ]);

    const resposeResposeItemList = reactive([
        {
            key: 'Access-Control-Request-Method',
            value: '-'
        },
        {
            key: 'Accept',
            value: '*/*'
        },
        {
            key: 'User-Agent',
            value: 'PostmanRuntime-ApipostRuntime/1.1.0'
        }
    ]);

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
        const initialJson = {
            Array: [1, 2, 3],
            Boolean: true,
            Null: null,
            Number: 123,
            Object: { a: 'b', c: 'd' },
            String: 'Hello World'
        };
        editor.set(initialJson);

        document.querySelector('#json-container .jsoneditor').style.height = '66vh';
        // console.log(editor);
        // get json
        // const updatedJson = editor.get();
    }
    onMounted(() => {
        initJsonContainer();
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
                        <el-input v-model="input1" placeholder="请输入关键词/URL" :prefix-icon="Search" />
                        <el-button type="primary">新建</el-button>
                    </template>
                    <y9Tree
                        :data="treeData"
                        :defaultAcitonIcons="defaultAcitonIcons"
                        @edit-icon="onTreeEditIcon"
                        @remove-icon="onTreeRemoveIcon"
                    ></y9Tree>
                </y9Card>
            </el-aside>
            <el-main>
                <div>
                    <div class="mainHeader">
                        <span>新建接口</span>
                        <span><i class="ri-map-pin-line"></i>新建接口</span>
                    </div>
                    <div class="mainContainer">
                        <el-form v-model="ApiForm">
                            <el-form-item label="接口名称">
                                <el-input v-model="ApiForm.name"></el-input>
                            </el-form-item>
                            <el-form-item label="请求方法">
                                <el-select v-model="ApiForm.method" placeholder="GET">
                                    <el-option label="GET" value="GET" />
                                    <el-option label="POST" value="POST" />
                                    <el-option label="DELETE" value="DELETE" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="请求地址">
                                <el-input v-model="ApiForm.url"></el-input>
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
                                        <Y9Table2Comp :itemList="headerItemList"></Y9Table2Comp>
                                    </el-tab-pane>
                                    <el-tab-pane name="Query">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ RequestTabPane.queryTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <Y9Table2Comp :itemList="queryItemList"></Y9Table2Comp>
                                    </el-tab-pane>
                                    <el-tab-pane name="Body">
                                        <template #label>
                                            <div class="label-title">
                                                <span>{{ RequestTabPane.bodyTitle }}</span>
                                                <span>（2）</span>
                                            </div>
                                        </template>
                                        <div class="body-content">
                                            <el-radio-group v-model="bodyType">
                                                <el-radio :value="1">none</el-radio>
                                                <el-radio :value="2">form-data</el-radio>
                                                <el-radio :value="3">json</el-radio>
                                                <el-radio :value="4">xml</el-radio>
                                                <el-radio :value="5">html</el-radio>
                                                <el-radio :value="6">javascript</el-radio>
                                                <el-radio :value="7">binary</el-radio>
                                            </el-radio-group>
                                            <BodyTypeComp
                                                :type="bodyType"
                                                :itemList="bodyFormDataItemList"
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
    }
    .apiTest_el_tabs div.el-tabs__nav-wrap::after {
        height: 1px;
    }
</style>

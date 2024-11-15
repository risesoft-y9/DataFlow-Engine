<script setup lang="ts">
    import JSONEditor from 'jsoneditor';
    import Y9Table2Comp from './Y9Table2Comp.vue';
    import { watch } from 'vue';
    import type { UploadInstance } from 'element-plus';

    const uploadRef = ref<UploadInstance>();

    const props = defineProps({
        key: {
            type: Number,
            default: 0
        },
        type: {
            /**
             * 初始化类型["none", "form-data", "json", "xml", "html", "binary"]
             */
            type: Number,
            default: 1
        },
        itemList: {
            /**
             * form-data 类型下的动态数据
             */
            type: Array,
            default() {
                return [];
            }
        },
        ApiForm: {
            /**
             * 整个左侧表单数据
             */
            type: Object,
            default() {
                return {};
            }
        }
    });
    const bodyType = ref(props.type);

    watch(bodyType, (value) => {
        emits('onRadioChange', value);
    });
    watch(
        () => props.type,
        (value) => {
            bodyType.value = Number(value);
            if (props.ApiForm.body[bodyType.value]) {
                editor.value.set(props.ApiForm.body[bodyType.value]);
            } else {
                editor.value.set();
            }
        }
    );
    watch(
        () => props.key,
        (value) => {
            console.log(value);
            if (props.ApiForm.body[bodyType.value]) {
                editor.value.set(props.ApiForm.body[bodyType.value]);
            } else {
                editor.value.set();
            }
        }
    );

    const emits = defineEmits(['onAdd', 'onDelete', 'onEdit', 'onRadioChange', 'onJsonEditorChange']);

    function onDeleteItem() {
        emits('onDelete');
    }
    function onEditItem() {
        emits('onEdit');
    }

    /**
     * jsoneditor 配置
     * Mode: 'code', 'form', 'text', 'tree', 'view', 'preview'
     * C.VALID_OPTIONS = [
     *      "ajv", "schema", "schemaRefs", "templates", "ace", "theme",
     *      "autocomplete", "onChange", "onChangeJSON", "onChangeText", "onExpand",
     *      "onEditable", "onError", "onEvent", "onModeChange", "onNodeName",
     *      "onValidate", "onCreateMenu", "onSelectionChange", "onTextSelectionChange",
     *      "onClassName", "onFocus", "onBlur", "colorPicker", "onColorPicker",
     *      "timestampTag", "timestampFormat", "escapeUnicode", "history", "search",
     *      "mode", "modes", "name", "indentation", "sortObjectKeys", "navigationBar",
     *      "statusBar", "mainMenuBar", "languages", "language", "enableSort", "enableTransform",
     *      "limitDragging", "maxVisibleChilds", "onValidationError", "modalAnchor", "popupAnchor",
     *      "createQuery", "executeQuery", "queryDescription", "allowSchemaSuggestions", "showErrorTable"
     * ],
     */
    const bodyTypeJsonContainerHeight = ref(196);
    const bodyTypeJsonContainer = ref(null);
    const editor = ref(null);
    const options = reactive({
        selectionStyle: 'tree',
        statusBar: false,
        mainMenuBar: false,
        showErrorTable: false,
        onBlur() {
            // console.log(editor.value.getText());
            switch (bodyType.value) {
                case 3:
                // emits('onJsonEditorChange', editor.value.get());
                // break;
                case 4:
                case 5:
                    emits('onJsonEditorChange', editor.value.getText());
                    break;
                default:
                    break;
            }
        }
    });

    function initBodyTypeJsonContainer(mode = 'code') {
        if (editor.value) {
            return null;
        }
        bodyTypeJsonContainer.value = document.getElementById('body-type-json-container');

        editor.value = new JSONEditor(bodyTypeJsonContainer.value, options);

        // 设置模式
        editor.value.setMode(mode);
        // 设置数据
        editor.value.set();

        document.querySelector('#body-type-json-container .jsoneditor').style.height =
            bodyTypeJsonContainerHeight.value + 'px';
        // console.log(editor.value);
        // get json
        // const updatedJson = editor.value.get();
    }
    function init(value) {
        switch (value) {
            case 3:
            case 4:
            case 5:
                initBodyTypeJsonContainer();
                break;
            default:
                break;
        }
    }
    onMounted(() => {
        // init(props.type);
        initBodyTypeJsonContainer();
    });
    function onUploadChange(uploadFile, uploadFiles) {
        console.log(uploadFile, uploadFiles);
    }
</script>
<template>
    <div>
        <el-radio-group v-model="bodyType">
            <el-radio :value="1">none</el-radio>
            <el-radio :value="2">form-data</el-radio>
            <el-radio :value="3">json</el-radio>
            <el-radio :value="4">xml</el-radio>
            <el-radio :value="5">html</el-radio>
            <!-- <el-radio :value="6">javascript</el-radio> -->
            <el-radio :value="7">binary</el-radio>
        </el-radio-group>
        <div v-show="type == 1" class="body-type-none"></div>
        <div v-show="type == 2" class="body-type-form-data">
            <Y9Table2Comp key="bodyTypeFormData" :itemList="itemList" @on-delete="onDeleteItem" @on-edit="onEditItem" />
        </div>
        <div v-show="type == 3 || type == 4 || type == 5 || type == 6" class="body-type-json">
            <div id="body-type-json-container"></div>
        </div>
        <div v-show="type == 7" class="body-type-binary">
            <div class="upload-area-button">
                <el-upload
                    ref="uploadRef"
                    class="upload-api-test"
                    drag
                    :action="ApiForm.url"
                    :method="ApiForm.method"
                    multiple
                    :auto-upload="false"
                    :on-change="onUploadChange"
                >
                    <div
                        ><el-icon class="el-icon--upload"><upload-filled /></el-icon
                    ></div>
                    <div
                        ><el-button><i class="ri-upload-2-line"></i> &nbsp; 上传文件</el-button></div
                    >
                </el-upload>
            </div>
        </div>
    </div>
</template>
<style lang="scss" scoped>
    .body-type-none {
        border: solid 1px rgb(236, 238, 245);
        border-radius: 3px;
        width: 100%;
        height: v-bind('bodyTypeJsonContainerHeight + "px"');
        background-image: url('@/assets/images/dataflowEnginePro/暂无数据 (2).png');
        background-repeat: no-repeat;
        background-position: 50.3% 40%;
        overflow: hidden;
        &::after {
            content: '暂无数据';
            color: rgb(236, 238, 245);
            display: flex;
            justify-content: center;
            align-items: center;
            line-height: 240px;
        }
    }
    .body-type-binary {
        border: solid 1px rgb(236, 238, 245);
        border-radius: 3px;
        width: 100%;
        min-height: v-bind('bodyTypeJsonContainerHeight + 4 + "px"');
        overflow: hidden;
        // .el-upload-list {
        //     top: v-bind('-(bodyTypeJsonContainerHeight + 4) + "px"') !important;
        // }
    }
</style>
<style>
    #body-type-json-container .jsoneditor {
        border: thin solid rgb(236, 238, 245);
        height: auto;
        min-height: 20vh;
        .ace-jsoneditor .ace_gutter {
            background: var(--el-bg-color);
            border-right: thin solid rgb(236, 238, 245);
            color: #c0c0c0;
        }
    }
</style>

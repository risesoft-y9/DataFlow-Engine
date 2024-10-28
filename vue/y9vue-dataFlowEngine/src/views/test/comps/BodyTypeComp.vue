<script setup lang="ts">
    import JSONEditor from 'jsoneditor';
    import Y9Table2Comp from './Y9Table2Comp.vue';
    import { watch } from 'vue';
    const props = defineProps({
        type: {
            type: Number,
            default: 1
        },
        itemList: {
            type: Array,
            default() {
                return [];
            }
        }
    });

    watch(
        () => props.type,
        (value) => {
            init(value);
        }
    );

    function init(value) {
        switch (value) {
            case 3:
            case 4:
            case 5:
            case 6:
                initBodyTypeJsonContainer();
                break;

            default:
                break;
        }
    }

    const bodyTypeJsonContainerHeight = ref(196);
    const bodyTypeJsonContainer = ref(null);
    const editor = ref(null);
    function initBodyTypeJsonContainer() {
        if (editor.value) {
            return null;
        }
        bodyTypeJsonContainer.value = document.getElementById('body-type-json-container');
        const options = {
            selectionStyle: 'tree',
            mode: 'code',
            statusBar: false,
            mainMenuBar: false,
            onChangeText() {
                console.log('json changed');
            }
        };
        editor.value = new JSONEditor(bodyTypeJsonContainer.value, options);

        // set json
        editor.value.set();

        document.querySelector('#body-type-json-container .jsoneditor').style.height =
            bodyTypeJsonContainerHeight.value + 'px';
        // console.log(editor.value);
        // get json
        // const updatedJson = editor.value.get();
    }
    onMounted(() => {
        init(props.type);
    });
</script>
<template>
    <div>
        <div v-show="type == 1" class="body-type-none"></div>
        <div v-show="type == 2" class="body-type-form-data">
            <Y9Table2Comp :itemList="itemList" />
        </div>
        <div v-show="type == 3 || type == 4 || type == 5 || type == 6" class="body-type-json">
            <div id="body-type-json-container"></div>
        </div>
        <div v-show="type == 7" class="body-type-binary">
            <div class="upload-file-item"></div>
            <div class="upload-area-button">
                <el-upload
                    class="upload-api-test"
                    drag
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    multiple
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
        height: v-bind('bodyTypeJsonContainerHeight + 4 + "px"');
        overflow: hidden;
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

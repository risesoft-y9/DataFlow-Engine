<template>
    <div class="form-index">
        <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
    </div>
</template>

<script lang="ts" setup>
    import { $keyNameAssign } from '@/utils/object';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const props = defineProps({
        isEditState: {
            //是否为编辑状态
            type: Boolean
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    //表单配置
    const y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px'
            // contentWidth: '200px'
        },
        model: {
            //表单属性
            id: '',
            name: '',
            createTime: '',
            tabIndex: ''
        },
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'id',
                label: computed(() => t('唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.id);
                    }
                }
            }
            // {
            //     type: 'text',
            //     type1: 'text', //自定义字段-编辑时显示的类型
            //     type2: 'text', //自定义字段-非编辑状态显示文本类型
            //     prop: 'updateTime',
            //     label: computed(() => t('更新时间')),
            //     props: {
            //         render: () => {
            //             //text类型渲染的内容
            //             return h('span', props.currInfo?.customId);
            //         }
            //     }
            // }
        ]
    });

    //改变y9Form显示类型
    const changeY9FormType = (isEdit) => {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
            };
        } else {
            y9FormConfig.value.rules = {};
        }
        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    };

    //监听是否为编辑状态
    watch(
        () => props.isEditState,
        (isEdit) => {
            if (isEdit) {
                //编辑状态
                $keyNameAssign(y9FormConfig.value.model, props.currInfo); //编辑状态给表单赋值
            }
            changeY9FormType(isEdit);
        },
        {
            immediate: true
        }
    );

    //表单实例
    const y9FormRef = ref();
    defineExpose({
        y9FormRef
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    .form-index {
        margin-top: 20px;
    }
</style>

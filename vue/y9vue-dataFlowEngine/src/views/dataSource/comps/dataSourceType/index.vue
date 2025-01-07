<template>
    <div class="action-btns" v-if="!isAdd">
        <el-button
            v-if="!isEditStatus"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            class="global-btn-main"
            type="primary"
            @click="onEdit"
        >
            <i class="ri-edit-line"></i>
            {{ $t('编辑') }}
        </el-button>
        <div v-else>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="onSave"
            >
                <i class="ri-save-line"></i>
                {{ $t('保存') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-second"
                @click="onShow"
            >
                <i class="ri-close-line"></i>
                {{ $t('取消') }}
            </el-button>
        </div>
    </div>
    <y9Form ref="y9FormRef" :config="formConfig">
        <template #paramsType>
            <el-radio-group v-model="formConfig.model.type" @change="handleChange" :required="true">
                <el-radio label="0">{{ $t('数据库') }}</el-radio>
                <el-radio label="1"> {{ $t('其他数据源') }} </el-radio>
            </el-radio-group>
        </template>
    </y9Form>
</template>

<script lang="ts" setup>
    import { ref, computed, inject, watch, h } from 'vue';
    import { pick, cloneDeep, forIn } from 'lodash';
    import { addDataSourceInfo } from '@/api/dataSource';
    import { base64ToUrl, base64ImgtoFile } from '@/utils/index';
    import { equalObjectKey } from '@/utils/object';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();

    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    // 数据库与驱动的对应关系
    // mysql、oracle、postgresql、kingbase、dm
    let dataDrive = {
        mysql: 'com.mysql.cj.jdbc.Driver',
        oracle: 'oracle.jdbc.OracleDriver',
        postgresql: 'org.postgresql.Driver',
        kingbase: 'com.kingbase8.Driver',
        dm: 'dm.jdbc.driver.DmDriver',
        sqlserver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
    };

    // 数据库
    let DataList = [
        { label: 'mysql', value: 'mysql' },
        { label: 'oracle', value: 'oracle' },
        { label: 'postgresql', value: 'postgresql' },
        { label: 'kingbase', value: 'kingbase' },
        { label: 'dm', value: 'dm' },
        { label: 'sqlserver', value: 'sqlserver' }
    ];

    // 其他数据库
    // ftp、elasticsearch
    let othersDataList = [
        { label: 'ftp', value: 'ftp' },
        { label: 'elasticsearch', value: 'elasticsearch' }
    ];

    const props = defineProps({
        isAdd: {
            type: Boolean
        },
        currNode: {
            type: Object,
            default: () => {
                return {};
            }
        },
        changeLoading: {
            type: Function
        },
        flxedTree: {
            type: Object
        }
    });

    //表单实例
    const y9FormRef = ref(null);

    //表单配置
    const formConfig = ref({
        //描述表单配置
        descriptionsFormConfig: {
            column: 1,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        //表单属性
        model: {
            id: '',
            type: '0',
            name: '',
            driver: '',
            iconFile: ''
        },
        //表单验证规则
        rules: {
            type: [{ required: true, message: computed(() => t('请选择类型')), trigger: 'change' }],
            name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'change' }]
        },
        //表单显示列表
        itemList: [
            {
                type: 'slot',
                type1: 'slot',
                type2: 'text',
                label: computed(() => t('类型')),
                required: true,
                prop: 'type',
                props: {
                    slotName: 'paramsType'
                }
            },
            {
                type: 'select',
                type1: 'select',
                type2: 'text',
                prop: 'name',
                label: computed(() => t('名称')),
                required: true,
                props: {
                    // mysql、oracle、postgresql、kingbase、dm
                    // ftp、elasticsearch
                    // dataDrive
                    options: DataList,
                    events: {
                        change: (value) => {
                            formConfig.value.model.name = value;
                            formConfig.value.model.driver = dataDrive[value];
                        }
                    }
                }
            },
            {
                type: 'input',
                type1: 'input',
                type2: 'text',
                prop: 'driver',
                label: computed(() => t('驱动')),
                required: false,
                props: {
                    disabled: true,
                    placeholder: '驱动将根据名称选择自动填充'
                }
            },
            {
                type: 'upload',
                type1: 'upload',
                type2: 'upload',
                prop: 'iconFile',
                label: computed(() => t('图标')),
                props: {
                    defaultCustomClass: 'custom-picture-card',
                    listType: 'picture-card',
                    limit: 1,
                    drag: false,
                    accept: 'image/jpeg,image/jpg,image/png',
                    onChange: (val) => {
                        const elFormRef = y9FormRef.value?.elFormRef;
                        if (elFormRef) {
                            //重新校验字段
                            elFormRef.validateField(['iconFile']);
                        }
                    },
                    onRemove: (File) => {
                        y9FormRef.value.model.iconFile = '';
                    },
                    fileList: []
                }
            }
        ]
    });

    /**
     * 切换表单状态
     * @param isEdit Boolean 是否为编辑状态
     * @param info Object 非编辑状态的字段信息
     */
    const changeFormStatus = (isEdit) => {
        //根据表单状态来切换对应的字段显示状态
        formConfig.value.itemList.forEach((item) => {
            if (isEdit) {
                item.type = item.type1;
                formConfig.value.model.type = '' + y9FormRef.value.model.type;
                handleChange('' + y9FormRef.value.model.type);
                if (item.prop === 'iconFile') {
                    if (props.currNode.imgData) {
                        try {
                            const base64 = 'data:image/jpg;base64,' + props.currNode.imgData;
                            const url = base64ToUrl(base64) || '';
                            item.props.fileList = [
                                {
                                    name: 'iconFile',
                                    url: url
                                }
                            ];
                            item.props.removeIcon = true;
                        } catch (e) {
                            console.error('e', e);
                        }
                    } else {
                        item.props.fileList = [];
                    }
                }

                item.props.render = null;
            } else {
                item.type = item.type2;

                switch (item.prop) {
                    case 'iconFile':
                        if (props.currNode.imgData) {
                            try {
                                const base64 = 'data:image/jpg;base64,' + props.currNode.imgData;
                                const url = base64ToUrl(base64);
                                item.props.fileList = [
                                    {
                                        name: 'iconFile',
                                        url: url
                                    }
                                ];
                                item.props.removeIcon = false;
                            } catch (e) {
                                console.error('e', e);
                            }
                        } else {
                            item.props.fileList = [];
                            item.type = 'slot';
                        }

                        break;
                    default:
                        item.props.render = () => {
                            let text = props.currNode[item.prop];

                            if (item.prop === 'type') {
                                text = props.currNode[item.prop] === 0 ? '数据库' : '其他数据源';
                            }

                            return h('span', text || '');
                        };
                        break;
                }
            }
        });
    };
    //是否为编辑状态
    const isEditStatus = ref(false);

    //保存按钮触发
    const onSave = async () => {
        console.log('y9FormRef', y9FormRef.value);

        let formData = {}; //记录表单数据
        //校验表单
        const validResult = await y9FormRef.value.elFormRef.validate(() => {});

        //校验通过，提取表单数据
        if (validResult) {
            if (
                props.currNode.imgData &&
                Object.prototype.toString.call(y9FormRef.value.model.iconFile) === '[object Array]'
            ) {
                try {
                    const base64 = 'data:image/jpg;base64,' + props.currNode.imgData;
                    const file = await base64ImgtoFile(base64);
                    y9FormRef.value.model.iconFile = file;
                } catch (e) {
                    console.error('e', e);
                }
            }

            Object.assign(formData, y9FormRef.value.model);
            props.changeLoading && props.changeLoading(true);
            if (formData.type == '1') {
                formData.driver = '';
            }
            console.log('提交给后端的数据：', formData);

            //请求接口
            let result = await addDataSourceInfo(formData);
            props.changeLoading && props.changeLoading(false);
            ElNotification({
                title: result.success ? t('保存成功') : t('保存失败'),
                message: result.msg,
                type: result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            onShow();
            // 刷新树
            props.flxedTree?.onRefreshTree();
        } else {
            ElMessage({
                type: 'error',
                message: t('校验不通过，请检查'),
                offset: 65
            });
        }

        // let formData = {}; //记录表单数据
        // let checkPassCount = 0; //记录表单校验通过数量
        // for (let i = 0; i < y9FormRef.value.length; i++) {
        //     const itemRef = y9FormRef.value[i];
        //     //逐个校验表单
        //     const validResult = await itemRef.elFormRef.validate(() => {});
        //     //校验通过，提取表单数据
        //     if (validResult) {
        //         Object.assign(formData, itemRef.model);
        //         checkPassCount++;
        //     }
        // }
        // //所有表单校验通过，提交数据
        // if (checkPassCount === formList.value.length) {
        //     props.changeLoading && props.changeLoading(true);
        //     //模拟接口请求，将数据提交给后端
        //     console.log('提交给后端的数据：', formData);
        //     //请求接口
        //     let result = await saveDataConnectInfo(formData);
        //     props.changeLoading && props.changeLoading(false);
        //     ElNotification({
        //         title: result.success ? t('保存成功') : t('保存失败'),
        //         message: result.msg,
        //         type: result.success ? 'success' : 'error',
        //         duration: 2000,
        //         offset: 80
        //     });
        //     onShow();
        //     // 刷新树
        //     props.flxedTree?.onRefreshTree();
        // } else {
        //     ElMessage({
        //         type: 'error',
        //         message: t('校验不通过，请检查'),
        //         offset: 65
        //     });
        // }
    };
    //编辑按钮触发
    const onEdit = () => {
        isEditStatus.value = true;
        //改变表单状态
        changeFormStatus(true);
    };
    //恢复展示状态
    const onShow = () => {
        isEditStatus.value = false;
        //改变表单状态
        changeFormStatus(false);
    };

    watch(
        () => props.currNode,
        (newNode) => {
            // console.log('newNode', newNode);
            if (!props.isAdd) {
                formConfig.value.model = pick(newNode, Object.keys(formConfig.value.model));
                console.log('formConfig.value.model==', formConfig.value.model);
                onShow();
            }
        },
        {
            immediate: true,
            deep: true
        }
    );

    // 添加数据源表单的单选框的逻辑处理
    let driverRowData = {};
    formConfig.value.itemList.map((item, index) => {
        if (item.prop == 'driver') {
            driverRowData = cloneDeep(formConfig.value.itemList[index]);
        }
    });

    function handleChange(value) {
        console.log(isEditStatus.value);
        driverRowData.type = 'input';
        let dataParams = pick(props.currNode, Object.keys(formConfig.value.model));

        if (value == '1') {
            // 选择其他数据源时
            // 新增的时候置空
            if (!isEditStatus.value) formConfig.value.model.name = '';
            // 编辑的时候换了新的数据源 新的数据源置空，旧的显示原有值
            let match = othersDataList?.filter((item) => item.value == dataParams.name);
            if (match.length) {
                formConfig.value.model.name = dataParams.name;
            } else {
                formConfig.value.model.name = '';
            }
            formConfig.value.itemList?.map((item, index) => {
                if (item.prop == 'driver') {
                    formConfig.value.itemList.splice(index, 1);
                }
                if (item.prop == 'name') {
                    item.props.options = othersDataList;
                }
            });
        } else {
            // 新增的时候 置空
            if (!isEditStatus.value) {
                formConfig.value.model.name = '';
                formConfig.value.model.driver = '';
            }
            // 编辑的时候换了新的数据源 新的数据源置空，旧的显示原有值
            let match = DataList?.filter((item) => item.value == dataParams.name);
            if (match.length) {
                formConfig.value.model.name = dataParams.name;
                formConfig.value.model.driver = dataParams.driver;
            } else {
                formConfig.value.model.name = '';
                formConfig.value.model.driver = '';
            }
            // '选择数据库时'
            // 查找数组中是否有prop为'driver'的对象
            const hasDriver = formConfig.value.itemList.some((item) => item.prop === 'driver');

            // 如果没有找到，那么在type为'slot'的元素后面添加新对象
            if (!hasDriver) {
                // 先找到 name 的索引，在这行的后面加上
                for (let index = 0; index < formConfig.value.itemList.length; index++) {
                    const item = formConfig.value.itemList[index];
                    if (item.prop == 'name') {
                        console.log('=======' + driverRowData);
                        formConfig.value.itemList.splice(index + 1, 0, driverRowData);
                        item.props.options = DataList;
                    }
                }
            }
        }
    }

    defineExpose({
        y9FormRef
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    .action-btns {
        margin-bottom: 20px;
    }
    .list {
        display: inline-flex;
        .item {
            width: 200px;
            height: 120px;
            margin-right: 20px;
            cursor: pointer;
            &:hover {
                border: solid 1px #eee;
                border-radius: 0px;
                box-sizing: border-box;
                -moz-box-sizing: border-box;
            }
            .icon {
                width: 100%;
                height: 100%;
            }
        }
    }
</style>

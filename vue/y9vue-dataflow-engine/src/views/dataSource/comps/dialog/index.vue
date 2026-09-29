<template>
    <y9Dialog v-model:config="dialogConfig">
        <component ref="compRef" :is="dialogConfig.flag" :isAdd="true" :flxedTree="flxedTree"></component>
    </y9Dialog>
</template>
<script lang="ts">
    import DataSourceType from '../dataSourceType/index.vue';
    import DataSource from '../dataSource/dataForm.vue';
    export default {
        components: {
            DataSourceType,
            DataSource
        }
    };
</script>
<script lang="ts" setup>
    import { ref } from 'vue';
    import { addDataSourceInfo, saveDataConnectInfo } from '@/api/dataSource';
    import { FormType } from '../../enums';

    const compRef = ref(null);

    const props = defineProps({
        currNode: {
            type: Object,
            default: () => {
                return {};
            }
        },
        flxedTree: {
            type: Object
        }
    });

    const dialogConfig = ref({
        show: false,
        width: '60%',
        title: '我是标题',
        resetText: '重置',
        onOk: (config) => {
            return new Promise(async (resolve, reject) => {
                //数据源类型表单
                if (config.value.flag === FormType.DATA_SOURCE_TYPE) {
                    //校验表单
                    const validResult = await compRef.value.y9FormRef.elFormRef.validate(() => {});
                    //校验不通过，不关闭弹窗
                    if (!validResult) {
                        reject();
                        return;
                    }
                    //校验通过提交数据
                    await addDataSourceInfo(compRef.value.y9FormRef.model)
                        .then((res) => {
                            if (res.code == 0) {
                                // 新增新节点
                            }
                            ElNotification({
                                title: res.success ? '成功' : '失败',
                                message: res.msg,
                                type: res.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                        })
                        .catch((err) => console.log(err));

                    // 刷新树
                    props.flxedTree?.onRefreshTree();

                    //关闭弹窗
                    resolve();
                }
                //数据源表单
                else if (config.value.flag === FormType.DATA_SOURCE) {
                    let formData = {
                        id: '',
                        type: props.currNode.type
                    }; //记录表单数据
                    let checkPassCount = 0; //记录表单校验通过数量

                    const formsRef = compRef.value.y9FormRef;
                    for (let i = 0; i < formsRef.length; i++) {
                        const itemRef = formsRef[i];
                        //逐个校验表单
                        const validResult = await itemRef.elFormRef.validate(() => {});
                        //校验通过，提取表单数据
                        if (validResult) {
                            Object.assign(formData, itemRef.model);
                            checkPassCount++;
                        }
                    }

                    //所有表单校验通过，提交数据
                    if (checkPassCount === compRef.value.formList.length) {
                        console.log('树的增加，提交给后端的数据：', formData);
                        let result = await saveDataConnectInfo(formData);
                        console.log('result', result);
                        // 刷新树
                        props.flxedTree?.onRefreshTree();
                        resolve();
                    } else {
                        reject();
                    }
                }
            });
        },
        onReset: (config) => {
            //数据源表单
            if (config.value.flag === FormType.DATA_SOURCE) {
                //重置表单
                compRef.value.y9FormRef.forEach((itemRef) => {
                    itemRef.elFormRef.resetFields();
                });

                //根据数据源类型设置对应的表单字段
                const { type } = config.value.params;
                if (type) compRef.value.changeType(type);
            }
        },
        visibleChange: (visible) => {
            if (visible && dialogConfig.value.flag === FormType.DATA_SOURCE) {
                const { type } = dialogConfig.value.params;
                nextTick(() => {
                    if (type) compRef.value.changeType(type);
                });
            }
        }
    });

    const assginDialogConfig = (config) => {
        Object.assign(dialogConfig.value, config);
    };

    defineExpose({
        assginDialogConfig: assginDialogConfig
    });
</script>

<style lang="scss" scoped></style>

<script setup lang="ts">
    import { randomString } from '@/utils/index';
    import { nextTick, watch } from 'vue';

    // 注入 字体对象
    const Y9Table2Body_Id = ref('Y9Table2Body_Id_' + randomString(6));
    const fontSizeObj: any = inject('sizeObjInfo');
    const Y9Table2BodyHeight = ref(164); // rowHeight=41px
    const isSelectAll = ref(false);
    const count = ref(0);
    const size = ref('large');

    const props = defineProps({
        itemList: {
            type: Array as PropType<any[]>,
            default: () => []
        }
    });

    const emits = defineEmits(['onAdd', 'onDelete', 'onEdit']);

    const selectAll = () => {
        isSelectAll.value = !isSelectAll.value;
        if (isSelectAll.value) {
            for (let i = 0; i < props.itemList.length; i++) {
                props.itemList[i]['isSelect'] = true;
            }
        } else {
            for (let i = 0; i < props.itemList.length; i++) {
                props.itemList[i]['isSelect'] = false;
            }
        }
        count.value += 1;
    };
    // watch(
    //     () => props.itemList,
    //     (val) => {
    //         console.log(val);
    //     },
    //     {
    //         immediate: true,
    //         deep: true
    //     }
    // );
    const addRowFunc = (e) => {
        props.itemList.push({
            Key: '',
            Param: ''
        });
        count.value += 1;
        nextTick(() => {
            if (document.getElementById(Y9Table2Body_Id.value).scrollHeight > Y9Table2BodyHeight.value) {
                document
                    .getElementById(Y9Table2Body_Id.value)
                    .scrollTo(0, document.getElementById(Y9Table2Body_Id.value).scrollHeight);
            }
        });
        emits('onAdd');
    };
    const deleteRowFunc = (index: number) => {
        const item = props.itemList.splice(index, 1);
        console.log('delete', item);
        emits('onDelete');
    };
    const selectItemChange = (index: number) => {
        props.itemList[index]['isSelect'] = !props.itemList[index]['isSelect'];
    };
    const isBlankCheck = (index) => {
        const i = index;
        if (props.itemList[i].Key) {
            if (props.itemList[i].Param) {
                props.itemList[i]['isSelect'] = true;
            } else {
                props.itemList[i]['isSelect'] = false;
            }
        } else {
            props.itemList[i]['isSelect'] = false;
        }
        emits('onEdit');
        count.value += 1;
    };
</script>
<template>
    <div class="y9-table-2">
        <div class="y9-table-2__header">
            <span><el-checkbox :size="size" :value="isSelectAll" @change="selectAll" /></span>
            <span>参数名</span>
            <span>参数值</span>
            <span>操作</span>
        </div>
        <div class="y9-table-2__body" :id="Y9Table2Body_Id">
            <div class="y9-table-2__item" v-for="(item, index) in itemList" :key="index">
                <span
                    ><el-checkbox
                        :size="size"
                        :key="count"
                        :checked="itemList[index]['isSelect']"
                        :value="itemList[index]['isSelect']"
                        @change="selectItemChange(index)"
                /></span>
                <span
                    ><el-input
                        v-model="itemList[index]['Key']"
                        placeholder="Please input"
                        :value="item.Key"
                        @input="isBlankCheck(index)"
                /></span>
                <span
                    ><el-input
                        v-model="itemList[index]['Param']"
                        placeholder="Please input"
                        :value="item.Param"
                        @input="isBlankCheck(index)"
                /></span>
                <span>
                    <i class="ri-add-box-line" v-if="index === 0" @click="addRowFunc"></i>
                    <i class="ri-delete-bin-line" v-else @click="deleteRowFunc(index)"></i>
                </span>
            </div>
        </div>
    </div>
</template>
<style lang="scss" scoped>
    @mixin row-style {
        &:nth-child(1) {
            min-width: 3%;
        }
        &:nth-child(2) {
            min-width: 30%;
        }
        &:nth-child(3) {
            min-width: 60%;
        }
        &:nth-child(4) {
            min-width: 7%;
        }
    }
    .y9-table-2 {
        width: 100%;
        .y9-table-2__header {
            width: 100%;
            height: 32px;
            display: flex;
            align-items: center;
            background-color: rgb(233, 233, 239);
            border-radius: 3px;
            padding: 0 15px;
            & > span {
                text-align: center;
                @include row-style;
            }
        }
        .y9-table-2__body {
            width: 100%;
            height: v-bind('Y9Table2BodyHeight +"px"');
            overflow: scroll;
            .y9-table-2__item {
                width: 100%;
                display: flex;
                align-items: center;
                padding: 0 15px;
                background-color: var(--el-bg-color);
                border-bottom: 1px solid var(--el-border-color);
                & > span {
                    text-align: center;
                    @include row-style;
                    .el-checkbox.el-checkbox--large {
                        min-width: 22px;
                    }
                    &:nth-child(2) {
                        margin-right: 15px;
                    }
                    &:nth-child(3) {
                        min-width: calc(60% - 15px);
                    }
                    &:nth-child(4) {
                        font-size: v-bind('fontSizeObj.largerFontSize');
                        color: var(--el-color-primary);
                    }
                }
                &:last-child {
                    border-bottom: none;
                }
            }
        }
    }
</style>

<script setup lang="ts">
    import { inject } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const contentHeight = ref(settingStore.getWindowHeight - 60 - 30 - 30 + 'px');
    const sizeObjInfo = inject('sizeObjInfo');
    const pageHeaderFontSize = ref(sizeObjInfo.extraLargeFont);
</script>
<template>
    <el-container>
        <el-header><i class="ri-link"></i>接口在线测试</el-header>
        <el-container>
            <el-aside width="30%">
                <y9Card title="我是标题"> 我是内容 </y9Card>
            </el-aside>
            <el-main>
                <div>
                    <div class="mainHeader">
                        <span>新建接口</span>
                        <span>新建接口</span>
                    </div>
                    <div class="mainContainer"> </div>
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>
<style lang="scss" scoped>
    .el-container {
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
        }
        & > .el-main {
            padding: 30px;
            padding-left: 0;
            & > div {
                background-color: rgb(233, 233, 239);
                box-shadow: 0 2px 4px #0000000d;
                height: v-bind(contentHeight);
                border-radius: 5px;
                .mainHeader {
                    height: 80px;
                    line-height: 80px;
                    display: flex;
                    justify-content: space-between;
                }
                .mainContainer {
                    height: auto;
                    background-color: var(--el-bg-color);
                }
            }
        }
    }
</style>

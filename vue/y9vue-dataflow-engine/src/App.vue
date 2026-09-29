<!--
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-02-24 14:59:28
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/App.vue
-->
<script lang="ts" setup>
    import router from '@/router';
    import { useI18n } from 'vue-i18n';
    import settings from '@/settings';
    import { onMounted, onUnmounted, ref, provide, watch, computed } from 'vue';
    // 引入字体调整的方法
    import { getConcreteSize } from '@/utils/index';
    // 引入水印插件
    import watermark from 'y9plugin-watermark/lib/index';
    import y9_storage from '@/utils/storage';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const { t } = useI18n();

    interface watermarkData {
        text?;
        deptName?;
        name?;
    }

    // 定义⽔印⽂字变量
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
    let dept = userInfo.dn?.split(',')[1]?.split('=')[1] || '';
    let watermarkValue = ref<watermarkData>({
        name: userInfo.name,
        text: computed(() => t('保守秘密，慎之又慎')),
        deptName: dept
    });
    //监听语⾔变化，传⼊对应的⽔印语句
    watch(
        () => useSettingStore().getWebLanguage,
        (newLang) => {
            setTimeout(() => {
                watermarkValue.value.name = t(userInfo.name);
                watermarkValue.value.deptName = t(dept);
                watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
            });
        }
    );
    //监听⼤⼩变化，传⼊对应⽔印⽂字⼤⼩
    watch(
        () => useSettingStore().getFontSize,
        (newLang) => {
            setTimeout(() => {
                watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
            });
        }
    );

    onMounted(() => {
        // 执⾏⽔印⽅法
        setTimeout(() => {
            if (userInfo && userInfo.name) {
                watermarkValue.value.name = t(userInfo.name);
                watermarkValue.value.deptName = t(dept);
                watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
            } else {
                if (!document.location.pathname.includes('/login')) {
                    watermarkValue.value.name = '背景水印文案';
                    watermarkValue.value.deptName = '部门';
                    watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
                }
            }
        });
        if (import.meta.env.VUE_APP_APPFEATURES == '1') {
            checkLogin();
        }
    });

    // 单机登录模式，检测登录状态
    function checkLogin() {
        let token = y9_storage.getObjectItem(settings.siteTokenKey);
        // 没有登录，记录当前访问地址，并导航至单机登录页面
        if (!token) {
            sessionStorage.setItem('lastUrl', window.location.href);
            router.push({
                path: '/login'
            });
        }
    }

    onUnmounted(() => {
        watermark('');
    });

    // 主题切换
    const theme = computed(() => settingStore.getThemeName);
    const toggleColor = (theme) => {
        if (document.getElementById('head')) {
            let themeDom = document.getElementById('head');
            let pathArray = themeDom.href.split('/');
            pathArray[pathArray.length - 1] = theme + '.css';
            let newPath = pathArray.join('/');
            themeDom.href = newPath;
        }
    };
    toggleColor(theme.value);

    /***
     *  字体大中小
     * 定义变量
     */

    let sizeObjInfo = ref({
        baseFontSize: getConcreteSize(settingStore.getFontSize, 14) + 'px',
        mediumFontSize: getConcreteSize(settingStore.getFontSize, 16) + 'px',
        largeFontSize: getConcreteSize(settingStore.getFontSize, 18) + 'px',
        largerFontSize: getConcreteSize(settingStore.getFontSize, 19) + 'px',
        extraLargeFont: getConcreteSize(settingStore.getFontSize, 20) + 'px',
        extraLargerFont: getConcreteSize(settingStore.getFontSize, 24) + 'px',
        moreLargeFont: getConcreteSize(settingStore.getFontSize, 26) + 'px',
        moreLargerFont: getConcreteSize(settingStore.getFontSize, 32) + 'px',
        biggerFontSize: getConcreteSize(settingStore.getFontSize, 40) + 'px',
        maximumFontSize: getConcreteSize(settingStore.getFontSize, 48) + 'px',
        buttonSize: settingStore.getFontSize,
        lineHeight: settingStore.getLineHeight
    });
    // 监听 转换font-size值
    watch(
        () => settingStore.getFontSize,
        (newVal) => {
            sizeObjInfo.value.baseFontSize = getConcreteSize(newVal, 14) + 'px';
            sizeObjInfo.value.mediumFontSize = getConcreteSize(newVal, 16) + 'px';
            sizeObjInfo.value.largeFontSize = getConcreteSize(newVal, 18) + 'px';
            sizeObjInfo.value.largerFontSize = getConcreteSize(newVal, 19) + 'px';
            sizeObjInfo.value.extraLargeFont = getConcreteSize(newVal, 20) + 'px';
            sizeObjInfo.value.extraLargerFont = getConcreteSize(newVal, 24) + 'px';
            sizeObjInfo.value.moreLargeFont = getConcreteSize(newVal, 26) + 'px';
            sizeObjInfo.value.moreLargerFont = getConcreteSize(newVal, 32) + 'px';
            sizeObjInfo.value.biggerFontSize = getConcreteSize(newVal, 40) + 'px';
            sizeObjInfo.value.maximumFontSize = getConcreteSize(newVal, 48) + 'px';
            sizeObjInfo.value.buttonSize = newVal;
            sizeObjInfo.value.lineHeight = settingStore.getLineHeight;
        }
    );

    // provide提供
    provide('sizeObjInfo', sizeObjInfo.value);
</script>

<template>
    <router-view></router-view>
</template>
<style lang="scss">
    body {
        font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, SimSun, sans-serif;
    }
</style>

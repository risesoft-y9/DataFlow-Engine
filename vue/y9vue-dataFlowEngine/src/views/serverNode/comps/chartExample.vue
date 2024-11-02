<template>
    <el-card class="box-card left-two" style="background-color: #fff">
        <!-- echarts图 -->
        <div :id="`home-left-line-${props.serverId}`" style="width: 601px; height: 412px"></div>
    </el-card>
</template>

<script lang="ts" setup>
    import echarts from '@/utils/echarts'; // echarts图表插件
    import { ref, inject, computed, onMounted, watch, onBeforeUnmount } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const { t } = useI18n();

    // echarts 对象 定义
    // 左边下角圆形
    let leftBottomLine = ref(null);

    let loading = ref(false);

    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 收缩左侧
    const menuCollapsed = computed<Boolean>(() => settingStore.getMenuCollapsed);

    const props = defineProps({
        serverId: {
            type: Number,
            default: 1
        }
    });

    onMounted(() => {
        initChart();
        // 所有图案响应容器的大小变化
        window.onresize = function () {
            chartInit();
        };
    });

    let themeType = {
        'theme-green': '#4e9876',
        'theme-blue': '#1e5896',
        'theme-default': '#586cb1'
    };

    // echarts 的颜色随主题颜色的变化而变化
    let echartsColor = ref(themeType[settingStore.getThemeName]);
    watch(
        () => settingStore.getThemeName,
        (newVal) => {
            echartsColor.value = themeType[newVal];
            initChart();
        }
    );

    // cpu使用率
    let cpuUsers = ref(props.serverId * 20);
    // watch(
    //     () => props.serverId,
    //     (newId) => {
    //         console.log(5555);

    //         if (newId) {
    //             console.log(2222);

    //             cpuUsers.value = newId * 20;
    //             initChart();
    //         }
    //     }
    // );

    let base = +new Date(2022, 3, 4);
    let oneHour = 20 * 1000;
    let data = [[base, Math.random() * 30]];
    for (let i = 1; i < 2000; i++) {
        let now = new Date((base += oneHour));
        data.push([+now, Math.round((Math.random() - 0.49) * 2 + data[i - 1][1])]);
    }
    console.log(data, 'data');

    // 初始化echarts 图
    function initChart() {
        const homeLeftBottomLine = {
            color: [echartsColor.value],
            tooltip: {
                trigger: 'axis',
                position: function (pt) {
                    return [pt[0], '10%'];
                }
            },
            xAxis: {
                type: 'time',
                boundaryGap: false
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: function (value, index) {
                        return value + 'GB';
                    }
                },
                min: 0
            },
            series: [
                {
                    name: 'Fake Data',
                    type: 'line',
                    smooth: true,
                    symbol: 'none',
                    areaStyle: {},
                    data: data
                }
            ]
        };

        leftBottomLine.value = echarts.init(document.getElementById(`home-left-line-${props.serverId}`));
        leftBottomLine.value.setOption(homeLeftBottomLine);
    }

    function chartInit() {
        loading.value = true;
        setTimeout(() => {
            leftBottomLine.value.resize();
        }, 300);
        setTimeout(() => {
            initChart();
            loading.value = false;
        }, 300);
    }

    watch(
        () => menuCollapsed.value,
        () => {
            chartInit();
        }
    );

    onBeforeUnmount(() => {
        if (!leftBottomLine.value) {
            return;
        }
        // 对资源进行释放
        leftBottomLine.value.dispose();
        leftBottomLine.value = null;
    });
</script>

<style lang="scss" scoped>
    .left-two {
        :deep(.el-card__body) {
            padding: 0px;
            height: 100%;
            display: flex;
            justify-content: center;
        }
        height: 100%;
    }
    .box-card {
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
        // margin-bottom: 1.5rem;
        border-radius: 0.25rem;
        position: relative;
        display: flex;
        flex-direction: column;
        background-color: var(--el-color-primary);
        min-width: 0;
        word-wrap: break-word;
        background-clip: border-box;
    }
</style>

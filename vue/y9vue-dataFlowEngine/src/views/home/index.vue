<script lang="ts" setup>
    import echarts from '@/utils/echarts'; // echarts图表插件
    import { inject, onBeforeUnmount, onMounted, ref, computed, watch, nextTick } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const { t } = useI18n();

    import {
        selectClick,
        getHomeDataInfo,
        homeData,
        ChartManager,
        setAllEchartsLoadingStates
    } from '@/views/home/comp/dispatchingChart/data';
    // col变量
    const spanValue = ref(12);
    let loading = ref(false);

    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 收缩左侧
    const menuCollapsed = computed<Boolean>(() => settingStore.getMenuCollapsed);

    onMounted(() => {
        nextTick(() => {
            initData();
            // 所有图案响应容器的大小变化
            window.onresize = function () {
                chartInit();
            };
        });
    });
    const initData = async () => {
        loading.value = true;
        setAllEchartsLoadingStates(true);
        await getHomeDataInfo(homeData.defaultSelectValue);
        initChart();
        loading.value = false;
        setAllEchartsLoadingStates(false);
    };

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

    // 初始化echarts 图
    function initChart() {
        ChartManager.refreshAllChartColor(echartsColor.value); //刷新颜色
        //所有echart图表 - 响应容器大小的变化;
        window.onresize = function () {
            ChartManager.resizeAllCharts();
        };
        if (settingStore.device === 'mobile') {
            spanValue.value = 24;
        } else {
            spanValue.value = 12;
        }
    }

    function chartInit() {
        loading.value = true;
        setAllEchartsLoadingStates(true);

        setTimeout(() => {
            ChartManager.resizeAllCharts();
        }, 300);
        setTimeout(() => {
            initChart();
            loading.value = false;
            setAllEchartsLoadingStates(true);
        }, 300);
    }

    watch(
        () => menuCollapsed.value,
        () => {
            chartInit();
        }
    );

    onBeforeUnmount(() => {
        ChartManager.disposeAllChartInstance();
    });
</script>

<template>
    <div style="height: 100%; overflow-y: auto; overflow-x: hidden; scrollbar-width: none">
        <el-row :gutter="20" style="height: 100%">
            <el-col :span="spanValue">
                <div class="left">
                    <el-card class="box-card">
                        <div class="center">
                            <img src="@/assets/images/app-icon.png" class="image" />
                            <h1>{{ $t('数据流引擎') }}</h1>
                            <div class="remark">
                                <span>{{ $t('规则配置管理') }}</span>
                                <span>{{ $t('任务配置管理') }}</span>
                                <span>{{ $t('任务调度管理') }}</span>
                                <span>{{ $t('日志监控') }}</span>
                                <span>{{ $t('安全管理') }}</span>
                            </div>
                        </div>
                    </el-card>
                    <el-card class="box-card left-two" style="background-color: #fff">
                        <div class="card-header">
                            <span>{{ $t('当前运行任务情况') }}</span>
                            <el-dropdown>
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                >
                                    {{ homeData.selectName }}
                                </el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item
                                            v-for="item in homeData.allEnvironments"
                                            :key="item.name"
                                            @click="selectClick(item)"
                                        >
                                            {{ item.description }}
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                        <div class="two-content" v-loading="homeData.currentTaskInfoLoadingStates">
                            <div class="row">
                                <div class="center">
                                    <p>{{ $t('全部任务') }}</p>
                                    <span>{{ homeData.currentTaskInfo.allTask }}</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('今日已执行(成功/失败)') }}</p>
                                    <span>{{ homeData.currentTaskInfo.doneTask }}</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('正在执行') }}</p>
                                    <span>{{ homeData.currentTaskInfo.doingTask }}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="center">
                                    <p>{{ $t('总执行成功') }}</p>
                                    <span>{{ homeData.currentTaskInfo.successTask }}</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('总执行失败') }}</p>
                                    <span>{{ homeData.currentTaskInfo.errorTask }}</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('等待执行') }}</p>
                                    <span>{{ homeData.currentTaskInfo.waitTask }}</span>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </div>
            </el-col>
            <el-col :span="spanValue" style="padding-left: 0px">
                <div class="right">
                    <div class="right-top">
                        <el-col :span="spanValue">
                            <el-card class="box-card">
                                <div class="card-header">
                                    <span>{{ $t('每日调度次数') }}</span>
                                    <el-dropdown>
                                        <el-button
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                        >
                                            {{ homeData.selectName }}
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-item
                                                    v-for="item in homeData.allEnvironments"
                                                    :key="item.name"
                                                    @click="selectClick(item)"
                                                >
                                                    {{ item.description }}
                                                </el-dropdown-item>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                                <div class="metric-content" v-loading="homeData.dailySchedulingInfoLoadingStates">
                                    <!-- echarts图 -->
                                    <div id="dailySchedulingFrequencyInfo" style="width: 100%;height: 180px;"></div>
                                </div>
                            </el-card>
                        </el-col>
                        <el-col :span="spanValue">
                            <el-card class="box-card">
                                <div class="card-header">
                                    <span>{{ $t('调度任务比例图') }}</span>
                                    <el-dropdown>
                                        <el-button
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                        >
                                            {{ homeData.selectName }}
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-item
                                                    v-for="item in homeData.allEnvironments"
                                                    :key="item.name"
                                                    @click="selectClick(item)"
                                                >
                                                    {{ item.description }}
                                                </el-dropdown-item>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                                <div
                                    style="display: flex; justify-content: space-around"
                                    v-loading="homeData.taskInfoLoadingStates"
                                >
                                    <!-- echarts图 -->
                                    <div id="taskStateInfo" style="width: 100%;height: 180px"></div>
                                </div>
                            </el-card>
                        </el-col>
                    </div>
                    <div class="right-bottom">
                        <el-card class="box-card">
                            <div class="card-header">
                                <span>{{ $t('调度情况') }}</span>
                                <el-dropdown>
                                    <el-button
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                    >
                                        {{ homeData.selectName }}
                                    </el-button>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item
                                                v-for="item in homeData.allEnvironments"
                                                :key="item.name"
                                                @click="selectClick(item)"
                                            >
                                                {{ item.description }}
                                            </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                            <div class="card-content" v-loading="homeData.schedulingInfoLoadingStates">
                                <div id="schedulingInfo" style="width: 100%;height: 480px;"></div>
                            </div>
                        </el-card>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
    .left {
        display: flex;
        flex-direction: column;
        height: 100%;
        .left-two {
            :deep(.el-card__body) {
                padding: 0px;
                height: 100%;
            }
            .two-content {
                // height: calc(100% - 50px - 1.1rem);
                display: flex;
                flex-direction: column;
                justify-content: center;
                .row {
                    padding: 6rem 5rem;
                    display: flex;
                    justify-content: space-between;
                    .center {
                        text-align: center;
                        p {
                            margin-top: 0;
                            margin-bottom: 2rem;
                            font-size: v-bind('fontSizeObj.mediumFontSize');
                        }
                        span {
                            font-size: v-bind('fontSizeObj.moreLargeFont') !important;
                        }
                    }
                }
            }
        }
        .box-card {
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
            margin-bottom: 1.5rem;
            border-radius: 0.25rem;
            position: relative;
            display: flex;
            flex-direction: column;
            background-color: var(--el-color-primary);
            min-width: 0;
            word-wrap: break-word;
            background-clip: border-box;
            .center {
                text-align: center;
                .image {
                    background: #fff;
                    border: 2px solid #fff;
                    width: 70px;
                    height: 70px;
                    margin-top: 1rem !important;
                    border-radius: 50%;
                }
                h1 {
                    font-weight: 200;
                    font-size: v-bind('fontSizeObj.biggerFontSize');
                    color: #fff;
                    margin-bottom: 3rem !important;
                    margin-top: 1.5rem !important;
                    font-family: Nunito, Montserrat, system-ui, BlinkMacSystemFont, -apple-system, sans-serif;
                }
                .remark {
                    text-align: center;
                    margin-bottom: 2.5rem;
                }
                .remark span {
                    padding: 0 4%;
                    font-size: v-bind('fontSizeObj.baseFontSize');
                    font-weight: 600;
                    letter-spacing: 0.1rem;
                    text-decoration: none;
                    text-transform: uppercase;
                    color: #fff;
                    background-color: transparent;
                }
            }
            .header {
                padding: 1.1rem 1.1rem 0;
            }
            h4 {
                font-size: v-bind('fontSizeObj.extraLargeFont');
                margin-bottom: 1rem;
                font-weight: 400;
            }
        }

        .box-card:nth-child(2) {
            margin-bottom: v-bind("settingStore.device === 'mobile'? '1.5rem' : '0px'");
            // height: calc(100% - 338px);
        }
    }
    .card-header {
        display: flex;
        padding: 1.1rem 1.1rem 0;
        justify-content: space-between;
        font-size: v-bind('fontSizeObj.mediumFontSize');
        :deep(.el-button) {
            font-size: v-bind('fontSizeObj.baseFontSize');
            padding: 0.54rem 0.9rem !important;
        }
        span {
            margin-right: 10px;
        }
    }
    .right {
        display: flex;
        flex-direction: column;
        height: 100%;
        .box-card {
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
            border-radius: 0.25rem;
        }
        :deep(.el-card__body) {
            padding: 0px;
            height: 100%;
        }
        .right-top {
            display: flex;
            flex-direction: v-bind("settingStore.device === 'mobile'? 'column' : 'row'");
            .box-card {
                margin-bottom: 1.5rem;
            }
        }
        .right-bottom {
            height: calc(100% - 207px);
            .box-card {
                margin: 0 3px 0 10px;
                margin-bottom: 1.5rem;
            }
            .card-content {
                display: flex;
                justify-content: v-bind("settingStore.device === 'mobile'? 'center' : 'space-evenly'");
                flex-wrap: wrap;
                .content-left {
                    text-align: left;
                    padding: 1rem;
                    h1 {
                        font-size: v-bind('fontSizeObj.maximumFontSize');
                        margin-top: 1.5rem;
                        margin-bottom: 0;
                        color: #2c2c2c;
                        font-weight: 400;
                    }
                    h5 {
                        margin-top: 18px;
                        font-size: v-bind('fontSizeObj.baseFontSize');
                        color: #2c2c2c;
                        font-weight: 500;
                        span:nth-child(1) {
                            color: #21b978;
                        }
                        span:nth-child(2) {
                            margin: 0px 8px;
                        }
                    }
                }
            }
        }
        .metric-content {
            height: 180px;
        }
        #right-top-one {
            :deep(div) {
                canvas {
                    // width: 100% !important;
                    left: -47px !important;
                    top: 5px !important;
                    height: 84px !important;
                }
            }
        }
        #right-top-two {
            :deep(div) {
                canvas {
                    left: 42px !important;
                }
            }
        }
    }
    .footer {
        text-align: center;
        padding: 1.5rem 0 1rem;
        span {
            color: #2c2c2c;
        }
    }
</style>

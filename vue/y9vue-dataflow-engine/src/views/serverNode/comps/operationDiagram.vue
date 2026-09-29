<template>
    <el-card class="box-card left-two" style="background-color: #fff">
        <div class="card-header">
            <span>{{ $t('当前运行任务情况') }}</span>
            <span class="network"
                >{{ $t('网络情况')
                }}<el-progress
                    style="margin-left: 5px"
                    type="circle"
                    :stroke-width="18"
                    :percentage="100"
                    :color="echartsColor"
                    :show-text="false"
            /></span>
        </div>
        <div class="two-content">
            <div class="row" v-loading="loading">
                <div class="left">
                    <div>
                        <h1>{{ cpuUsers }}%</h1>
                        <small>{{ $t('内存占用') }}</small>
                    </div>
                    <div class="ip-address">
                        <p>192.168.0.202</p>
                        <span>服务器地址</span>
                    </div>
                </div>
                <div>
                    <!-- echarts图 -->
                    <div
                        :id="`home-left-circle-${props.serverId}`"
                        style="width: 360px; height: 276px; background-color: #fff"
                    ></div>
                    <!-- <div v-else :id="`home-left-line-${props.serverId}`" style="width: 300px; height: 276px"></div> -->
                </div>
                <div class="select-picture">
                    <span @click="handleClick('pie')" :class="{ 'span-active': echartsType == 'pie' }">饼图</span>
                    <span @click="handleClick('line')" :class="{ 'span-active': echartsType == 'line' }">折线图</span>
                </div>
            </div>
            <div class="footer">
                <div class="center">
                    <p>{{ $t('正在执行') }}</p>
                    <span>{{ cpuUsers }}</span>
                </div>
                <div class="center">
                    <p>{{ $t('全部任务') }}</p>
                    <span>100</span>
                </div>
                <div class="center">
                    <p>{{ $t('今日已执行') }}</p>
                    <span>1d</span>
                </div>
            </div>
        </div>
    </el-card>
</template>

<script lang="ts" setup>
    import echarts from '@/utils/echarts'; // echarts图表插件
    import { ref, inject, computed, onMounted, watch, onBeforeUnmount } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const { t } = useI18n();

    // echarts图显示的变量
    const echartsType = ref('pie');
    // echarts 对象 定义
    // 左边下角圆形
    let leftBottom = ref(null);

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

    // 初始化echarts 图
    function initChart() {
        // 左边下角的圆形
        const gaugeData = [
            {
                value: cpuUsers.value,
                detail: {
                    valueAnimation: true,
                    offsetCenter: ['0%', '0%']
                }
            }
        ];
        let homeLeftBottom;
        if (echartsType.value == 'pie') {
            homeLeftBottom = {
                color: [echartsColor.value],
                series: [
                    {
                        type: 'gauge',
                        startAngle: 90,
                        endAngle: -270,
                        pointer: {
                            show: false
                        },
                        progress: {
                            show: true,
                            overlap: false,
                            roundCap: true,
                            clip: false,
                            itemStyle: {
                                borderWidth: 1,
                                borderColor: '#464646'
                            }
                        },
                        axisLine: {
                            lineStyle: {
                                width: 40
                            }
                        },
                        splitLine: {
                            show: false,
                            distance: 0,
                            length: 10
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            show: false,
                            distance: 50
                        },
                        data: gaugeData,
                        title: {
                            fontSize: fontSizeObj.baseFontSize
                        },
                        detail: {
                            width: 50,
                            height: 14,
                            fontSize: 16,
                            formatter: 'cpu使用率{value}%'
                        }
                    }
                ],
                xAxis: {
                    show: false
                }
            };
        } else {
            homeLeftBottom = {
                color: [echartsColor.value],
                tooltip: {
                    trigger: 'axis',
                    position: function (pt) {
                        return [pt[0], '10%'];
                    }
                },
                xAxis: {
                    show: true,
                    type: 'time',
                    boundaryGap: false,
                    axisLabel: { interval: 0, rotate: 30 }
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        formatter: function (value, index) {
                            return value + 'G';
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
        }
        leftBottom.value = echarts.init(document.getElementById(`home-left-circle-${props.serverId}`));
        leftBottom.value.setOption(homeLeftBottom);
    }

    function chartInit() {
        loading.value = true;
        setTimeout(() => {
            leftBottom.value.resize();
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

    function handleClick(type) {
        if (type == 'pie') {
            echartsType.value = 'pie';
        } else {
            echartsType.value = 'line';
        }
        initChart();
    }

    onBeforeUnmount(() => {
        if (!leftBottom.value) {
            return;
        }
        // 对资源进行释放
        leftBottom.value.dispose();
        leftBottom.value = null;
    });
</script>

<style lang="scss" scoped>
    .left-two {
        :deep(.el-card__body) {
            padding: 0px;
            height: 100%;
        }
        .two-content {
            height: calc(100% - 50px - 1.1rem);
            display: flex;
            flex-direction: column;
            justify-content: center;
            .row {
                display: flex;
                flex-wrap: wrap;
                margin-right: -14px;
                margin-left: -14px;
                justify-content: space-around;
                position: relative;
                .left {
                    display: flex;
                    flex: 0 0 16.6666666667%;
                    max-width: 16.6666666667%;
                    text-align: center;
                    flex-wrap: wrap;
                    flex-direction: column;
                    justify-content: center;
                    h1 {
                        font-size: v-bind('fontSizeObj.maximumFontSize');
                        margin: 0;
                        font-weight: 400;
                        font-family: Nunito, Montserrat, system-ui, BlinkMacSystemFont, -apple-system, sans-serif;
                        color: #2c2c2c;
                    }
                    small {
                        font-size: v-bind('fontSizeObj.mediumFontSize');
                        font-weight: 400;
                    }
                    .ip-address {
                        font-size: 18px;
                        margin-top: 30px;
                    }
                }
                .select-picture {
                    display: flex;
                    // flex-direction: column;
                    position: absolute;
                    // top: 10%;
                    // left: 86%;
                    align-items: end;
                    > span {
                        margin-left: 6px;
                    }
                    > span:hover {
                        cursor: pointer;
                        // text-decoration: underline;
                        color: var(--el-color-primary);
                    }
                    .span-active {
                        color: var(--el-color-primary);
                        // text-decoration: underline;
                    }
                }
            }
            .footer {
                padding: 1rem 5rem;
                display: flex;
                justify-content: space-between;
                .center {
                    text-align: center;
                    p {
                        margin-top: 0;
                        margin-bottom: 1rem;
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
        // margin-bottom: 1.5rem;
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
                padding: 0 25px;
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
        .network {
            display: flex;
            align-items: center;
            :deep(.el-progress-circle) {
                height: 23px !important;
                width: 23px !important;
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

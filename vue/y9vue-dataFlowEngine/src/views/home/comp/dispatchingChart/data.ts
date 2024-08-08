import { getEnvironmentAll, showJobView } from '@/api/dispatch';
import {
    getCurrentTaskInfo,
    getDailySchedulingFrequencyInfo,
    getHomeData,
    getJobLogInfo,
    getSchedulingInfo,
    getTaskStateInfo
} from '@/api/homeData';

import { inject, nextTick, ref } from 'vue';
import * as echarts from 'echarts';
import { useSettingStore } from '@/store/modules/settingStore';
const settingStore = useSettingStore();
const fontSizeObj: any = inject('sizeObjInfo');

let themeType = {
    'theme-green': '#4e9876',
    'theme-blue': '#1e5896',
    'theme-default': '#586cb1'
};

let echartsColor = ref(themeType[settingStore.getThemeName]);

let nameArr = ref([]);
let dataArr = ref([]);

//管理所有的echarts实例
export const ChartManager = (function () {
    let chartInstances = {};

    return {
        getChartInstance: function (chartId) {
            if (!chartInstances[chartId]) {
                // 如果图表实例不存在，则初始化并存储
                chartInstances[chartId] = echarts.init(document.getElementById(chartId));
            }
            // 返回图表实例
            return chartInstances[chartId];
        },
        disposeChartInstance: function (chartId) {
            if (chartInstances[chartId]) {
                // 销毁图表实例
                chartInstances[chartId].dispose();
                delete chartInstances[chartId];
            }
        },
        forEachChartInstance: function (callback) {
            //获取所有，为销毁、刷新、等
            Object.values(chartInstances).forEach(callback);
        },
        resizeAllCharts: function () {
            this.forEachChartInstance((chart) => {
                chart.resize();
            });
        },
        //销毁
        disposeAllChartInstance: function () {
            this.forEachChartInstance((chart) => {
                chart.dispose();
            });
            chartInstances = {};
        },
        //刷新颜色
        refreshAllChartColor: function (color) {
            let colors = [color, '#dceae4'];
            this.forEachChartInstance((chartObj) => {
                chartObj.setOption({
                    color: colors
                });
            });
        }
    };
})();

//echarts 加载状态
export let echartsLoadingStates = ref({
    taskStateInfo: true, //状态
    currentTaskInfo: true, //当前运行
    dailySchedulingFrequencyInfo: true, //每日调度
    schedulingInfo: true, //调度情况
    logInfo: true //日志信息
});
//设置加载状态
export function setAllEchartsLoadingStates(value: boolean) {
    for (const key in echartsLoadingStates.value) {
        echartsLoadingStates.value[key] = value;
    }
}

//后端首页数据结构
export let homeData = ref({
    //时间数组
    timeRanges: [
        {
            name: '',
            startTime: '',
            endTime: ''
        }
    ],
    //当前任务情况
    currentTaskInfo: {
        executing: 0, // 正在执行
        allTasks: 0, // 全部任务
        executedToday: 0, // 今日已经执行
        executTaskRatio: 0 //正在执行的任务数占今日总执行的任务数据比例
    },
    //每日任务执行情况
    dailySchedulingFrequencyInfo: {
        dateList: [],
        frequencyList: []
    },
    //任务类型
    schedulingInfo: {
        typeList: [], //类型列表
        dateList: [], //x轴日期
        taskScheduLingInfo: [] //y任务情况
    },
    //环境列表
    allEnvironments: [],
    //任务状态
    taskStateInfo: [],
    //日志
    jobLogInfo: {
        successCount: 0,
        failureCount: 0,
        logGroupInfo: [
            {
                executeStartTime: '',
                success: 0,
                failure: 0
            }
        ]
    }
});

//每日调度次数 今日信息
export let todayScheduling = ref();
export let dailySchedulingFrequencyQueryInfoSelectName = ref('最近一周');
export let dailySchedulingFrequencyQueryAllSelect = ref();

export let taskStateInfoQueryInfoSelectName = ref('最近一个月');
export let taskStateInfoQueryAllSelect = ref();

export let currentTaskInfoSelectName = ref('最近一个月');
export let currentTaskInfoQueryAllSelect = ref();

export let log_totalSuccess = ref(0);
export let log_totalFailure = ref(0);
export let logInfoSelectName = ref('最近一周');
export let logInfoQueryAllSelect = ref();

export let showEnvironmentAll = ref(false);
export let selectName = ref('Public');
export let environmentAll = ref([]);
export let showChat = ref(false);

export const selectClick = (item) => {
    nameArr.value = [];
    dataArr.value = [];
    selectName.value = item.name;
    getData();
};

//查询调度情况
export const selectClick2SchedulingInfo = async (name) => {
    echartsLoadingStates.value.schedulingInfo = true;

    selectName.value = name;
    await querySchedulingInfo(name);
    echartsLoadingStates.value.schedulingInfo = false;
};
//查询今日执行
export const selectClick2DailySchedulingFrequencyInfo = async (item) => {
    echartsLoadingStates.value.dailySchedulingFrequencyInfo = true;

    dailySchedulingFrequencyQueryInfoSelectName.value = item.name;
    await queryDailySchedulingFrequencyInfo(item);
    echartsLoadingStates.value.dailySchedulingFrequencyInfo = false;
};
//查询正常状态
export const selectClick2taskStateInfo = async (item) => {
    echartsLoadingStates.value.taskStateInfo = true;
    taskStateInfoQueryInfoSelectName.value = item.name;
    await queryTaskStateInfo(item);
    echartsLoadingStates.value.taskStateInfo = false;
};
//查询每日情况
export const selectClick2CurrentTaskInfo = async (item) => {
    echartsLoadingStates.value.currentTaskInfo = true;
    currentTaskInfoSelectName.value = item.name;
    await queryCurrentTaskInfo(item);
    echartsLoadingStates.value.currentTaskInfo = false;
};
//查询日志
export const selectClick2LogInfo = async (item) => {
    echartsLoadingStates.value.logInfo = true;
    logInfoSelectName.value = item.name;
    await queryLogInfo(item);
    echartsLoadingStates.value.logInfo = false;
};

//获取环境列表
export const getEnvironment = async () => {
    let res = await getEnvironmentAll();
    if (res) {
        environmentAll.value = res.data;
        selectName.value = res.data[1].name;
        showEnvironmentAll.value = true;
        // console.log(environmentAll,'environmentAll',showEnvironmentAll.value)
    }
};

export const getData = async () => {
    let params = {
        serviceId: 'DATA-TRANSFER',
        environment: selectName.value
    };
    let res = await showJobView(params);
    if (res.success) {
        res.data.forEach((item) => {
            nameArr.value.push(item.name);
            dataArr.value.push(item.count);
        });
        if (res.data?.length <= 9) {
            option.value.dataZoom[0].show = false;
            option.value.dataZoom[1].show = false;
        } else {
            option.value.dataZoom[0].show = true;
            option.value.dataZoom[1].show = true;
        }
        initChat();
    } else {
        nameArr.value = [];
        dataArr.value = [];
        initChat();
    }
};
export const initChat = () => {
    setTimeout(() => {
        option.value.xAxis.data = nameArr.value;
        option.value.series[0].data = dataArr.value;
        let chartDom = document.getElementById('main');
        let myChart = echarts.init(chartDom);
        myChart.setOption(option.value);
        myChart.resize();
    }, 50);
};

//*************************获取首页数据******************************** */ */
export const getHomeDataInfo = async () => {
    let params = {
        //暂时不需要
    };
    let res = await getHomeData(params);
    if (res.success) {
        homeData.value = res.data;
        let queryTimes = homeData.value.timeRanges;
        //*****************处理数据start*************** */
        homeData.value.schedulingInfo.taskScheduLingInfo.forEach((task: any) => {
            task.type = 'line';
        });
        showEnvironmentAll.value = true;
        environmentAll.value = homeData.value.allEnvironments;

        //dailySchedulingFrequencyInfo 获取今日执行数

        dailySchedulingFrequencyQueryAllSelect.value = queryTimes;

        taskStateInfoQueryAllSelect.value = queryTimes;

        currentTaskInfoQueryAllSelect.value = queryTimes;

        logInfoQueryAllSelect.value = queryTimes;

        //*****************处理数据end*************** */
        //调度情况
        initSchedulingInfo();
        //每日调度
        initDailySchedulingFrequencyInfo();
        //正常任务状态比例
        initTaskStateInfo();
        //当前任务情况
        initCurrentTaskInfo();
        //日志
        initlogInfo();
    } else {
        // initlogInfo();
    }
};
//获取今日执行数;
function getTodayScheduling(dateList, frequencyList) {
    let result = '';
    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth() + 1; // 月份从0开始，需要加1
    let day = today.getDate();
    let todayFormat = year + '-' + (month < 10 ? '0' + month : month) + '-' + (day < 10 ? '0' + day : day);
    let index = dateList.indexOf(todayFormat);
    result = todayFormat + ':';
    if (index !== -1) {
        result += frequencyList[index];
    } else {
        result += '0';
    }
    return result;
}
//初始化调度情况折线图
export const initSchedulingInfo = () => {
    let myChart = ChartManager.getChartInstance('schedulingInfo');
    myChart.clear(); // 清除之前的配置
    if (
        homeData.value.schedulingInfo.typeList == null ||
        homeData.value.schedulingInfo.dateList == null ||
        homeData.value.schedulingInfo.typeList.length == 0 ||
        homeData.value.schedulingInfo.dateList.length == 0
    ) {
        myChart.setOption({
            title: {
                text: '暂无数据',
                left: 'center',
                top: 'middle'
            }
        });
    } else {
        // setTimeout(() => {
        schedulingOpinion.value.legend.data = homeData.value.schedulingInfo.typeList;
        schedulingOpinion.value.xAxis.data = homeData.value.schedulingInfo.dateList;

        if (homeData.value.schedulingInfo.taskScheduLingInfo.length <= 10) {
            schedulingOpinion.value.dataZoom[0].show = false;
            schedulingOpinion.value.dataZoom[1].show = false;
        } else {
            schedulingOpinion.value.dataZoom[0].show = true;
            schedulingOpinion.value.dataZoom[1].show = true;
        }
        schedulingOpinion.value.series = homeData.value.schedulingInfo.taskScheduLingInfo;
        // let schedulingInfoDom = document.getElementById('schedulingInfo');
        // let myChart = echarts.init(schedulingInfoDom);
        // let myChart = ChartManager.getChartInstance('schedulingInfo');
        myChart.setOption(schedulingOpinion.value);
        myChart.resize();
        // }, 0);
    }
};

const option = ref({
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow',
            z: 1,
            shadowStyle: {
                color: '#586cb1',
                opacity: 0.4
            }
        },
        className: 'echarts-tooltip'
        // formatter: (params) => {
        //     const param = params;
        //     return `
        // 			<div class="tooltip-name">${param[0].name}</div>
        // 			<div style="display: flex;align-items: baseline">
        // 				<div class="tooltip-name" >数据量</div>
        // 			<div class="tooltip-value">${param[0].value}</div>
        // 			</div>
        // 					<div style="display: flex;align-items: baseline">
        // 				<div class="tooltip-name" >访问量</div>
        // 			<div class="tooltip-value">${param[1].value}</div>
        // 			</div></div>
        // 		`;
        // }
    },
    grid: {
        left: '6%',
        right: '13%',
        bottom: '60px',
        containLabel: true
    },
    toolbox: {
        feature: {
            dataView: { show: false, readOnly: false },
            magicType: { show: false, type: ['line', 'bar'] },
            restore: { show: false },
            saveAsImage: { show: false }
        }
    },
    legend: {
        show: true,
        top: 17
    },
    xAxis: {
        type: 'category',
        data: nameArr.value,
        boundaryGap: false,
        axisPointer: {
            type: 'shadow'
        },
        axisTick: {
            show: false
        },
        axisLine: {
            show: false
        },
        offset: 20,
        axisLabel: {
            interval: 0,
            rotate: -45, // 设置为-45表示逆时针倾斜45度
            fontSize: 12,
            color: '#586cb1',
            fontWeight: 400
        },
        lineStyle: {
            // width: 10
        }
    },
    yAxis: {
        type: 'value',
        name: '调度数',
        nameTextStyle: {
            padding: [0, -20, 8, 0],
            color: '#8B8BA7',
            fontSize: 14
        },
        // min: 0,
        // max: 250,
        interval: 10,
        axisLabel: {
            formatter: '{value}',
            fontSize: 14,
            color: '#595966'
        },
        splitLine: {
            lineStyle: {
                type: [6, 5],
                dashOffset: 5,
                color: '#C5C5D3'
            }
        }
    },
    series: [
        {
            name: '调度数',
            data: dataArr.value,
            type: 'line',
            areaStyle: {},
            smooth: true,
            showSymbol: false,
            symbolSize: 16,
            symbol: 'circle',
            tooltip: {
                valueFormatter: function (value) {
                    return value;
                }
            },
            // yAxisIndex: 1,
            itemStyle: {
                color: '#586cb1',
                borderWidth: 4,
                borderColor: '#fff'
            },
            emphasis: {
                scale: false
            },
            lineStyle: {
                width: 3,
                shadowColor: '#586cb1',
                shadowBlur: 40,
                shadowOffsetY: 16
            }
        }
    ],
    dataZoom: [
        {
            type: 'slider', // 单独滚动条
            left: 'center',
            show: true,
            realtime: true,
            startValue: 0,
            endValue: 9,
            xAxisIndex: [0],
            showDetail: false,
            height: 20
        },
        {
            type: 'inside', // 内置于坐标系中
            start: 0,
            end: 50,
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: false, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
            xAxisIndex: [0],
            zoomLock: true
        }
    ]
    // dataZoom: [
    //     {
    //         show: true,
    //         type: 'slider', // 单独滚动条
    //         filterMode: 'none', // 不过滤数据 - 保证 y 轴数据范围不变
    //         bottom: 5,
    //         height: 5,
    //         //         // 设置滚动条显示位置
    //         left: "center",
    //         backgroundColor: 'transparent',
    //         // 选中范围的填充颜色
    //         fillerColor: 'transparent',
    //         borderWidth: 0,
    //         borderColor: 'transparent',
    //         dataBackground: {
    //             lineStyle: {
    //                 color: 'transparent'
    //             },
    //             areaStyle: {
    //                 color: 'transparent'
    //             }
    //         },
    //         selectedDataBackground: {
    //             lineStyle: {
    //                 color: 'transparent'
    //             },
    //             areaStyle: {
    //                 color: 'transparent'
    //             }
    //         },
    //
    //         startValue: 0,
    //         endValue: 9,
    //         xAxisIndex: [0],
    //         showDetail: false,
    //         zoomLock: true,
    //         handleSize: '0%',
    //         moveOnMouseMove: false, // 鼠标移动能触发数据窗口平移
    //         zoomOnMouseWheel: false, // 鼠标移动能触发数据窗口缩放
    //         // 移动手柄尺寸高度
    //         // 测试发现手柄颜色和边框颜色会出现 偏差，所有设置手柄高度为0, 添加边框高度。由边框撑起高度
    //         moveHandleSize: 0, // 设置拖动手柄高度为0，只由边框负责高度展示
    //         // 不展示拖动手柄图标
    //         moveHandleIcon: 'none',
    //
    //         moveHandleStyle: {
    //             borderColor: '#5d6177',
    //             borderWidth: 10, // 设置边框高度
    //             borderType: 'solid',
    //             borderCap: 'round',
    //             // 保证拖动手柄右边框结尾有圆角
    //             borderJoin: 'round'
    //         },
    //         // 拖动高亮时设置
    //         emphasis: {
    //             moveHandleStyle: {
    //                 borderColor: '#5d6177',
    //                 borderWidth: 10,
    //                 borderType: 'solid',
    //                 borderCap: 'round'
    //             }
    //         }
    //     },
    //     // 添加鼠标滚轮控制左右滑动
    //     {
    //         type: 'inside',// 内置于坐标系中
    //         start: 0,
    //         end: 50,
    //         zoomOnMouseWheel: false,  // 关闭滚轮缩放
    //         moveOnMouseWheel: false, // 开启滚轮平移
    //         moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
    //         xAxisIndex: [0],
    //         zoomLock: true
    //     },
    // ]

    // dataZoom: [
    //     {
    //         // 设置滚动条的隐藏与显示
    //         show: true,
    //         brushSelect:true,
    //         // 设置滚动条类型
    //         type: "slider",
    //         // start:0,
    //         // end:40,
    //         // 设置背景颜色
    //         // backgroundColor: '#fff',
    //         // // 设置选中范围的填充颜色
    //         // fillerColor: "#586cb1",
    //         // // 设置边框颜色
    //         // // borderColor: "transparent",
    //         // borderColor: 'rgba(205,205,205,1)',
    //         backgroundColor: 'transparent',
    //         // 选中范围的填充颜色
    //         fillerColor: 'transparent',
    //         borderWidth: 0,
    //         borderColor: 'transparent',
    //         dataBackground: {
    //             lineStyle: {
    //                 color: 'transparent'
    //             },
    //             areaStyle: {
    //                 color: 'transparent'
    //             }
    //         },
    //         selectedDataBackground: {
    //             lineStyle: {
    //                 color: 'transparent'
    //             },
    //             areaStyle: {
    //                 color: 'transparent'
    //             }
    //         },
    //         // 是否显示detail，即拖拽时候显示详细数值信息
    //         showDetail: false,
    //         // 数据窗口范围的起始数值
    //         startValue: 0,
    //         // 数据窗口范围的结束数值（一页显示多少条数据）
    //         endValue: 9,
    //         // empty：当前数据窗口外的数据，被设置为空。
    //         // 即不会影响其他轴的数据范围
    //         filterMode: "empty",
    //         // 设置滚动条宽度，相对于盒子宽度
    //         width: "80%",
    //         // 设置滚动条高度
    //         height: 0,
    //         // 设置滚动条显示位置
    //         left: "center",
    //         // 是否锁定选择区域（或叫做数据窗口）的大小
    //         zoomLock: true,
    //         // 控制手柄的尺寸
    //         // dataZoom-slider组件离容器下侧的距离
    //         bottom: 13,
    //         moveHandleSize:0,
    //         handleSize: '0%',
    //         moveHandleIcon: 'none',
    //         moveHandleStyle: {
    //             borderColor:'#5d6177',
    //             borderWidth: 10, // 设置边框高度
    //             borderType: 'solid',
    //             borderCap: 'round',
    //             // 保证拖动手柄右边框结尾有圆角
    //             borderJoin: 'round'
    //         },
    //         // 移动手柄尺寸高度
    //         // 测试发现手柄颜色和边框颜色会出现 偏差，所有设置手柄高度为0, 添加边框高度。由边框撑起高度
    //         // moveHandleSize: 0, // 设置拖动手柄高度为0，只由边框负责高度展示
    //         moveOnMouseMove: false, // 鼠标移动能触发数据窗口平移
    //         zoomOnMouseWheel: false, // 鼠标移动能触发数据窗口缩放
    //         brushStyle:{
    //             brushStyle:0
    //         }
    //     },
    //     {
    //         type: 'inside',// 内置于坐标系中
    //         start: 0,
    //         end: 50,
    //         zoomOnMouseWheel: false,  // 关闭滚轮缩放
    //         moveOnMouseWheel: false, // 开启滚轮平移
    //         moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
    //         xAxisIndex: [0],
    //         zoomLock:true
    //     },
    //
    //
    //     // {
    //     //     type: 'slider',//有单独的滑动条，用户在滑动条上进行缩放或漫游。inside是直接可以是在内部拖动显示
    //     //     show: false,//是否显示 组件。如果设置为 false，不会显示，但是数据过滤的功能还存在。
    //     //     start: 0,//数据窗口范围的起始百分比0-100
    //     //     end: 50,//数据窗口范围的结束百分比0-100
    //     //     xAxisIndex: [0],// 此处表示控制第一个xAxis，设置 dataZoom-slider 组件控制的 x轴 可是已数组[0,2]表示控制第一，三个；xAxisIndex: 2 ，表示控制第二个。yAxisIndex属性同理
    //     //     bottom:-10 //距离底部的距离
    //     // },
    // ]
});

//调度情况echart
const schedulingOpinion = ref({
    // title: {
    //     text: 'Stacked Line'
    // },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow',
            z: 1,
            shadowStyle: {
                color: '#586cb1',
                opacity: 0.4
            }
        }
    },
    color: [echartsColor.value, '#dceae4'],

    legend: {
        show: true,
        top: 17,
        data: homeData.value.schedulingInfo.typeList
    },
    grid: {
        left: '6%',
        right: '13%',
        bottom: '60px',
        containLabel: true
    },
    toolbox: {
        feature: {
            dataView: { show: false, readOnly: false },
            magicType: { show: false, type: ['line', 'bar'] },
            restore: { show: false },
            saveAsImage: { show: false }
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: homeData.value.schedulingInfo.dateList,
        axisPointer: {
            type: 'line'
        },
        axisTick: {
            show: false
        },
        axisLine: {
            show: false
        },
        offset: 20,
        axisLabel: {
            interval: 0,
            rotate: -45, // 设置为-45表示逆时针倾斜45度
            fontSize: 12,
            color: '#586cb1',
            fontWeight: 400
        },
        lineStyle: {
            // width: 10
        }
    },
    yAxis: {
        type: 'value',
        name: '调度数'
    },

    series: homeData.value.schedulingInfo.taskScheduLingInfo,
    dataZoom: [
        {
            type: 'slider', // 单独滚动条
            left: 'center',
            show: true,
            realtime: true,
            startValue: 0,
            endValue: 9,
            xAxisIndex: [0],
            showDetail: false,
            height: 20
        },
        {
            type: 'inside', // 内置于坐标系中
            start: 0,
            end: 50,
            zoomOnMouseWheel: false, // 关闭滚轮缩放
            moveOnMouseWheel: false, // 开启滚轮平移
            moveOnMouseMove: true, // 鼠标移动能触发数据窗口平移
            xAxisIndex: [0],
            zoomLock: true
        }
    ]
});

//初始化每日调度次数
function initDailySchedulingFrequencyInfo() {
    let myChart = ChartManager.getChartInstance('dailySchedulingFrequencyInfo');
    myChart.clear(); // 清除之前的配置

    let dateList = homeData.value.dailySchedulingFrequencyInfo.dateList;
    let frequencyList = homeData.value.dailySchedulingFrequencyInfo.frequencyList;

    if (
        dateList == null ||
        frequencyList == null ||
        dateList.length == 0 ||
        frequencyList.length == 0 ||
        dateList.length != frequencyList.length
    ) {
        myChart.setOption({
            title: {
                text: '暂无数据',
                left: 'center',
                top: 'middle'
            }
        });
    } else {
        todayScheduling.value = getTodayScheduling(dateList, frequencyList);
        dailySchedulingFrequencyOpinion.value.xAxis.data = homeData.value.dailySchedulingFrequencyInfo.dateList;
        dailySchedulingFrequencyOpinion.value.series[0].data =
            homeData.value.dailySchedulingFrequencyInfo.frequencyList;
        let dailySchedulingFrequencyInfoDom = document.getElementById('dailySchedulingFrequencyInfo');
        // let myChart = echarts.init(dailySchedulingFrequencyInfoDom);
        myChart.setOption(dailySchedulingFrequencyOpinion.value);
        myChart.resize();
    }
}
//每日调度echarts;
export const dailySchedulingFrequencyOpinion = ref({
    color: [echartsColor.value, '#dceae4'],

    xAxis: {
        show: true,
        type: 'category',
        boundaryGap: false,
        data: homeData.value.dailySchedulingFrequencyInfo.dateList,
        axisLabel: {
            interval: 'auto', // 自动调整间隔显示
            rotate: -45, // 倾斜显示日期
            formatter: function (value) {
                // 可以根据需要动态调整日期格式
                return value.substring(5);
            }
        },
        dataZoom: [
            // 添加 dataZoom 组件
            {
                type: 'slider', // 使用滑动条
                start: 0, // 初始化时显示的数据比例起点
                end: 5, // 初始化时显示的数据比例终点
                filterMode: 'filter' // 设置拖动时是否实时触发 option 的更新
            }
        ]
    },
    yAxis: {
        show: false,
        type: 'value'
    },
    tooltip: {
        trigger: 'item',
        formatter: (params) => {
            todayScheduling.value = params.name + ':' + params.value;
            return '';
        }
    },
    series: [
        {
            data: homeData.value.dailySchedulingFrequencyInfo.frequencyList,
            type: 'line',
            areaStyle: {},
            smooth: true,
            label: {
                show: true, // 显示 label
                position: 'top' // label 位置设置为顶部
            }
        }
    ]
});

//初始化任务比例
function initTaskStateInfo() {
    let myChart = ChartManager.getChartInstance('taskStateInfo');
    myChart.clear(); // 清除之前的配置

    let dataList = homeData.value.taskStateInfo;

    if (dataList == null || dataList.length == 0) {
        myChart.setOption({
            title: {
                text: '暂无数据',
                left: 'center',
                top: 'middle'
            }
        });
    } else {
        taskStateInfoOpinion.value.series[0].data = dataList;
        myChart.setOption(taskStateInfoOpinion.value);
        myChart.resize();
    }
}

//任务比例echarts
export const taskStateInfoOpinion = ref({
    tooltip: {
        trigger: 'item'
    },
    legend: {
        top: '30%',
        left: '-8%',
        orient: 'vertical',
        itemGap: 20
    },
    color: [echartsColor.value, '#dceae4'],
    series: [
        {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
                show: true,
                formatter: '{b}: {c}',
                position: 'outside', // 将标签放置在扇形区域外部
                emphasis: {
                    show: true, // 鼠标悬停时显示标签
                    textStyle: {
                        fontSize: '16',
                        fontWeight: 'bold'
                    }
                }
            },
            labelLine: {
                show: true,
                length: 10, // 标签线长度
                length2: 15 // 标签线的另一端长度
            },
            data: homeData.value.taskStateInfo
        }
    ]
});

//初始化当前任务比例
function initCurrentTaskInfo() {
    let myChart = ChartManager.getChartInstance('currentTaskInfo');
    myChart.clear(); // 清除之前的配置
    let executingTasks = homeData.value.currentTaskInfo.executing;
    let totalTasks = homeData.value.currentTaskInfo.executedToday;
    let executTaskRatio = 0;

    if (totalTasks != 0) {
        Math.round((executingTasks / totalTasks) * 100);
    }
    homeData.value.currentTaskInfo.executTaskRatio = executTaskRatio;

    // option.series[0].detail.formatter = '{value}%';
    currentTaskInfoOpinion.value.series[0].data[0].value = homeData.value.currentTaskInfo.executTaskRatio;
    myChart.setOption(currentTaskInfoOpinion.value);
    myChart.resize();
}

//当前任务charts
export const currentTaskInfoOpinion = ref({
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
            data: [
                {
                    value: 60,
                    detail: {
                        valueAnimation: true,
                        offsetCenter: ['0%', '0%']
                    }
                }
            ],
            // title: {
            //     fontSize: fontSizeObj.baseFontSize
            // },
            detail: {
                width: 50,
                height: 14,
                fontSize: 24,
                formatter: '{value}%'
            }
        }
    ]
});
//日志
function initlogInfo() {
    let myChart = ChartManager.getChartInstance('logInfo');
    myChart.clear(); // 清除之前的配置

    let seriesData = homeData.value.jobLogInfo.logGroupInfo
        .map((item) => [
            { value: item.success, name: `${item.executeStartTime.substring(5)}：完成：` },
            { value: item.failure, name: `${item.executeStartTime.substring(5)}：失败：` }
        ])
        .flat();
    logInfoOpinion.value.legend.data = seriesData;
    logInfoOpinion.value.series[0].data = seriesData.flat();
    log_totalSuccess.value = homeData.value.jobLogInfo.successCount;
    log_totalFailure.value = homeData.value.jobLogInfo.failureCount;
    // 计算总体的成功与失败占比
    // const totalSuccess = seriesData.reduce((acc, cur) => acc + cur.value, 0);
    // const totalFailure = totalSuccess - seriesData.reduce((acc, cur) => acc + cur.value, 0);
    // const overallSuccess = totalSuccess / (totalSuccess + totalFailure);
    // const overallFailure = totalFailure / (totalSuccess + totalFailure);
    myChart.setOption(logInfoOpinion.value);
    myChart.resize();
}

//日志echarts
export const logInfoOpinion = ref({
    color: [echartsColor.value],
    legend: {
        orient: 'vertical',
        right: 10,
        top: 20,
        data: [],
        selected: {},
        selector: true,
        itemWidth: 8, // 设置图例选项的宽度
        itemHeight: 8 // 设置图例选项的高度
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            data: [],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            },
            label: {
                show: true,
                position: 'outside',
                formatter: function (params) {
                    return `{b|${params.name}\n${params.value}}`;
                },
                rich: {
                    b: {
                        fontSize: 14,
                        fontWeight: 'bold',
                        lineHeight: 16,
                        padding: [0, 0, 0, 10]
                    },
                    a: {
                        fontSize: 12,
                        lineHeight: 14
                    },
                    d: {
                        fontSize: 12,
                        lineHeight: 14,
                        align: 'right',
                        padding: [0, 10, 0, 0]
                    }
                }
            },
            labelLine: {
                show: true,
                length: 10,
                length2: 15
            }
        }
    ],
    tooltip: {
        show: true,
        formatter: function (params) {
            return `${params.name}\n${params.value}\n 占比： ${params.percent}%`;
        },
        backgroundColor: '#fff',
        textStyle: {
            color: '#000'
        },
        extraCssText: 'box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);'
    }
});

//获取调度情况
const querySchedulingInfo = async (item) => {
    let params = {
        startTime: null,
        endTime: null,
        environment: item
    };

    let res = await getSchedulingInfo(params);
    if (res.success) {
        homeData.value.schedulingInfo = res.data;
        homeData.value.schedulingInfo.taskScheduLingInfo.forEach((task: any) => {
            task.type = 'line';
        });
        initSchedulingInfo();
    } else {
    }
};

//查询每日情况
const queryDailySchedulingFrequencyInfo = async (item) => {
    let params = {
        startTime: item.startTime,
        endTime: item.endTime
    };
    let res = await getDailySchedulingFrequencyInfo(params);
    if (res.success) {
        homeData.value.dailySchedulingFrequencyInfo = res.data;
        initDailySchedulingFrequencyInfo();
    } else {
    }
};

//查询任务状态
const queryTaskStateInfo = async (item) => {
    let params = {
        startTime: item.startTime,
        endTime: item.endTime
    };
    let res = await getTaskStateInfo(params);
    if (res.success) {
        homeData.value.taskStateInfo = res.data;
    } else {
        homeData.value.taskStateInfo = [];
    }
    initTaskStateInfo();
};
//查询每日
const queryCurrentTaskInfo = async (item) => {
    let params = {
        startTime: item.startTime,
        endTime: item.endTime
    };
    let res = await getCurrentTaskInfo(params);
    if (res.success) {
        homeData.value.currentTaskInfo = res.data;
    } else {
        homeData.value.currentTaskInfo = {
            executing: 0, // 正在执行
            allTasks: 0, // 全部任务
            executedToday: 0, // 今日已经执行
            executTaskRatio: 0
        };
    }
    initCurrentTaskInfo();
};

//查询日志
const queryLogInfo = async (item) => {
    let params = {
        startTime: item.startTime,
        endTime: item.endTime
    };
    let res = await getJobLogInfo(params);
    if (res.success) {
        homeData.value.jobLogInfo = res.data;
    } else {
        homeData.value.jobLogInfo = {
            totalSuccess: 0,
            totalFailure: 0,
            logGroupInfo: [
                {
                    executeStartTime: '',
                    success: 0,
                    failure: 0
                }
            ]
        };
    }
    initlogInfo();
};

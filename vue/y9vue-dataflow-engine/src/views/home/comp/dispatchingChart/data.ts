import {
    getDailySchedulingFrequencyInfo,
    getHomeData,
    getSchedulingInfo,
    getTaskStateInfo
} from '@/api/homeData';

import { reactive, ref } from 'vue';
import * as echarts from 'echarts';
import { useSettingStore } from '@/store/modules/settingStore';
const settingStore = useSettingStore();

let themeType = {
    'theme-green': '#4e9876',
    'theme-blue': '#1e5896',
    'theme-default': '#586cb1'
};

let echartsColor = ref(themeType[settingStore.getThemeName]);

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

//设置加载状态
export function setAllEchartsLoadingStates(value: boolean) {
    homeData.currentTaskInfoLoadingStates = value;
    homeData.dailySchedulingInfoLoadingStates = value;
    homeData.taskInfoLoadingStates = value;
    homeData.schedulingInfoLoadingStates = value;
}

//后端首页数据结构
export const homeData = reactive({
    //当前任务情况
    currentTaskInfo: {
        allTask: 0, // 总任务数
        doneTask: 0, // 今日已执行数
        doingTask: 0, // 正在执行数据
        successTask: 0, // 执行成功数
        errorTask: 0, // 执行失败数
        waitTask: 0 // 等待执行数
    },
    currentTaskInfoLoadingStates: true,
    //每日调度次数
    dailySchedulingFrequencyInfo: {
        dateList: [],
        frequencyList: []
    },
    dailySchedulingInfoLoadingStates: true,
    //调度情况
    schedulingInfo: {
        dateList: [], //x轴日期
        taskScheduLingInfo: [] //y任务情况
    },
    schedulingInfoLoadingStates: true,
    //环境列表
    allEnvironments: [],
    defaultSelectValue: 'Public',
    selectName: '默认环境',
    //任务状态比例图
    taskStateInfo: [],
    taskInfoLoadingStates: true
});

export const selectClick = (item) => {
    homeData.selectName = item.description;
    selectClickTaskInfo(item);
    selectDailySchedulingFrequencyInfo(item);
    selectClicktaskStateInfo2(item);
    selectClickSchedulingInfo(item);
};

//查询调度情况
export const selectClickSchedulingInfo = async (item) => {
    homeData.schedulingInfoLoadingStates = true;
    await querySchedulingInfo(item.name);
    homeData.schedulingInfoLoadingStates = false;
};

//查询每日调度次数
export const selectDailySchedulingFrequencyInfo = async (item) => {
    homeData.dailySchedulingInfoLoadingStates = true;
    await queryDailySchedulingFrequencyInfo(item.name);
    homeData.dailySchedulingInfoLoadingStates = false;
};

//查询正常状态
export const selectClicktaskStateInfo2 = async (item) => {
    homeData.taskInfoLoadingStates = true;
    await queryTaskStateInfo(item.name);
    homeData.taskInfoLoadingStates = false;
};

//查询当前运行情况
export const selectClickTaskInfo = async (item) => {
    homeData.currentTaskInfoLoadingStates = true;
    getHomeDataInfo(item.name);
    homeData.currentTaskInfoLoadingStates = false;
};

export const getHomeDataInfo = async (type) => {
    let params = {
        type: type
    };
    let res = await getHomeData(params);
    if (res.success) {
        const data = res.data;
        homeData.allEnvironments = data.allEnvironments;
        homeData.currentTaskInfo.allTask = data.allTask;
        homeData.currentTaskInfo.doingTask = data.doingTask;
        homeData.currentTaskInfo.doneTask = data.doneTask;
        homeData.currentTaskInfo.successTask = data.successTask;
        homeData.currentTaskInfo.errorTask = data.errorTask;
        homeData.currentTaskInfo.waitTask = data.waitTask;

        //调度情况
        querySchedulingInfo(type);
        //每日调度
        queryDailySchedulingFrequencyInfo(type);
        //正常任务状态比例
        queryTaskStateInfo(type);
    }
};

//初始化调度情况折线图
export const initSchedulingInfo = () => {
    let myChart = ChartManager.getChartInstance('schedulingInfo');
    myChart.clear(); // 清除之前的配置
    if (
        homeData.schedulingInfo.dateList == null ||
        homeData.schedulingInfo.dateList.length == 0
    ) {
        myChart.setOption({
            title: {
                text: '暂无数据',
                left: 'center',
                top: 'middle'
            }
        });
    } else {
        schedulingOpinion.value.xAxis.data = homeData.schedulingInfo.dateList;
        if (homeData.schedulingInfo.taskScheduLingInfo.length <= 10) {
            schedulingOpinion.value.dataZoom[0].show = false;
            schedulingOpinion.value.dataZoom[1].show = false;
        } else {
            schedulingOpinion.value.dataZoom[0].show = true;
            schedulingOpinion.value.dataZoom[1].show = true;
        }
        schedulingOpinion.value.series = homeData.schedulingInfo.taskScheduLingInfo;
        myChart.setOption(schedulingOpinion.value);
        myChart.resize();
    }
};

//调度情况echart
const schedulingOpinion = ref({
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
        data: ['成功', '失败']
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
        data: homeData.schedulingInfo.dateList,
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
    series: homeData.schedulingInfo.taskScheduLingInfo,
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

    let dateList = homeData.dailySchedulingFrequencyInfo.dateList;
    let frequencyList = homeData.dailySchedulingFrequencyInfo.frequencyList;

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
        dailySchedulingFrequencyOpinion.value.xAxis.data = homeData.dailySchedulingFrequencyInfo.dateList;
        dailySchedulingFrequencyOpinion.value.series[0].data = homeData.dailySchedulingFrequencyInfo.frequencyList;
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
        data: homeData.dailySchedulingFrequencyInfo.dateList,
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
        show: false,
    },
    series: [
        {
            data: homeData.dailySchedulingFrequencyInfo.frequencyList,
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

    let dataList = homeData.taskStateInfo;

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
        left: '0%',
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
            data: homeData.taskStateInfo
        }
    ]
});

//获取调度情况
const querySchedulingInfo = async (type) => {
    let params = {
        type: type
    };
    let res = await getSchedulingInfo(params);
    if (res.success) {
        homeData.schedulingInfo = res.data;
        homeData.schedulingInfo.taskScheduLingInfo.forEach((task: any) => {
            task.type = 'line';
        });
        initSchedulingInfo();
    }
};

//查询调度次数
const queryDailySchedulingFrequencyInfo = async (type) => {
    let params = {
        type: type
    };
    let res = await getDailySchedulingFrequencyInfo(params);
    if (res.success) {
        homeData.dailySchedulingFrequencyInfo = res.data;
        initDailySchedulingFrequencyInfo();
    }
};

//查询任务状态
const queryTaskStateInfo = async (type) => {
    let params = {
        type: type
    };
    let res = await getTaskStateInfo(params);
    if (res.success) {
        homeData.taskStateInfo = res.data;
    } else {
        homeData.taskStateInfo = [];
    }
    initTaskStateInfo();
};

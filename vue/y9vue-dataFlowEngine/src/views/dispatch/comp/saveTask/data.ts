import {reactive} from "vue";

export const globalDataTask = reactive({
    tableData: {
        args: [],//任务集合id
        serviceId: 'DATA-TRANSFER',
        type: 'job',
        source: 'awaitExecutorJobs',
        name: null,//任务名
        jobSource: null,//任务来源
        jobType:null,//任务项的业务类型id
        description: null,//描述
        environment: null,//环境id
        errorCount: 0,//失败重复次数
        sourceTimeOut: 600,//调度超时时间
        timeOut: 3600,//任务整个超时时间
        dispatchArgs: '',//分片广播的时候使用此处不使用 传空即可
        dispatchMethod: '均衡',//调度方式 固定均衡 吧
        blockingStrategy: '串行',//阻塞策略
        dispatchType:'cron',//速度类型 cron /固定速度 其中下拉选择时 cron 为固定速度最后传值为cron
        speed:'* * * * * ?  ',//速度根据dispatchType 判断为cron 还是固定的秒
        status:0,//是否启动
    },
    initTableData: {
        args: [],//任务集合id
        serviceId: 'DATA-TRANSFER',
        type: 'job',
        source: 'awaitExecutorJobs',
        name: null,//任务名
        jobSource: null,//任务来源
        jobType:null,//任务项的业务类型id
        description: null,//描述
        environment: null,//环境id
        errorCount: 0,//失败重复次数
        sourceTimeOut: 600,//调度超时时间
        timeOut: 3600,//任务整个超时时间
        dispatchArgs: '',//分片广播的时候使用此处不使用 传空即可
        dispatchMethod: '均衡',//调度方式 固定均衡 吧
        blockingStrategy: '串行',//阻塞策略
        dispatchType:'cron',//速度类型 cron /固定速度 其中下拉选择时 cron 为固定速度最后传值为cron
        speed:'* * * * * ?  ',//速度根据dispatchType 判断为cron 还是固定的秒
        status:0,//是否启动
    },
    type:'add',
    blockingOptions: [
        {label: '并行', value: '并行',id:1},
        {label: '串行', value: '串行',id:2},
        {label: '丢弃后续调度', value: '丢弃后续调度',id:3},
    ],
    dispatchTypeOptions: [
        {label: 'cron', value: 'cron',id:1},
        {label: '固定速度', value: '固定速度',id:2},
    ],
    environmentAll:[],//环境选择
})
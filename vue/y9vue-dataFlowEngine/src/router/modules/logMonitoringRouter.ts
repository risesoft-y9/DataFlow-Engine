const logMonitoringRouter = {
    path: '/logMonitoring',
    component: () => import('@/layouts/index.vue'),
    redirect: '/taskRealtimeLog',
    name: 'logMonitoringIndex',
    meta: {
        title: '日志监控',
        icon: 'ri-article-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        // {
        //     path: '/taskRealtimeLog',
        //     component: () => import('@/views/logMonitoring/taskRealtimeLog/index.vue'),
        //     name: 'taskRealtimeLog',
        //     meta: {
        //         title: '任务实时日志',
        //         icon: 'ri-donut-chart-line' //remix 图标 优先级最高
        //         // elIcon: "House"//element-plus 图标 优先级第二
        //     }
        // },
        {
            path: '/dispatching',
            component: () => import('@/views/dispatch/dispatchingLog/index.vue'),
            name: 'dispatching',
            meta: {
                title: '调度日志',
                icon: 'ri-server-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        },
        {
            path: '/failLog',
            component: () => import('@/views/dispatch/failLog/index.vue'),
            name: 'failLog',
            meta: {
                title: '失败日志',
                icon: 'ri-server-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        },
        {
            path: '/waitLog',
            component: () => import('@/views/dispatch/waitLog/index.vue'),
            name: 'waitLog',
            meta: {
                title: '等待日志',
                icon: 'ri-server-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        },
        {
            path: '/groupingLog',
            component: () => import('@/views/dispatch/groupingLog/index.vue'),
            name: 'groupingLog',
            meta: {
                title: '分组失败日志',
                icon: 'ri-server-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
        // {
        //     path: '/dataComparisonMonitoring',
        //     component: () => import('@/views/logMonitoring/dataComparisonMonitoring/index.vue'),
        //     name: 'dataComparisonMonitoring',
        //     meta: {
        //         title: '数据对比监测',
        //         icon: 'ri-line-chart-line' //remix 图标 优先级最高
        //         // elIcon: "House"//element-plus 图标 优先级第二
        //     }
        // },
        // {
        //     path: '/serverNodeMonitoring',
        //     component: () => import('@/views/logMonitoring/serverNodeMonitoring/index.vue'),
        //     name: 'serverNodeMonitoring',
        //     meta: {
        //         title: '服务节点监测',
        //         icon: 'ri-server-line' //remix 图标 优先级最高
        //         // elIcon: "House"//element-plus 图标 优先级第二
        //     }
        // }
    ]
};

export default logMonitoringRouter;

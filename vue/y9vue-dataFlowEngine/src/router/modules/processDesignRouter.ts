const processDesignRouter = {
    path: '/processDesign',
    component: () => import('@/layouts/index.vue'),
    redirect: '/processDesign',
    name: 'processDesignRouterIndex',
    meta: {
        title: '调度流程设计',
        icon: 'ri-shapes-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        {
            path: '/processDesign',
            component: () => import('@/views/processDesign/index.vue'),
            name: 'processDesign',
            meta: {
                title: '调度流程设计',
                icon: 'ri-shapes-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
    ]
};

export default processDesignRouter;

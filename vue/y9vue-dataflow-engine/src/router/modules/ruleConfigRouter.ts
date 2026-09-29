const ruleConfigRouter = {
    path: '/engineConfig',
    component: () => import('@/layouts/index.vue'),
    redirect: '/engineConfig',
    name: 'ruleConfigIndex',
    meta: {
        title: '插件库',
        icon: 'ri-git-close-pull-request-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        {
            path: '/engineConfig',
            component: () => import('@/views/ruleConfig/engineConfig/index.vue'),
            name: 'engineConfig',
            meta: {
                title: '插件库',
                icon: 'ri-playstation-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
    ]
};

export default ruleConfigRouter;

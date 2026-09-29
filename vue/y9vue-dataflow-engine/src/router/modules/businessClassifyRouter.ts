const businessClassifyRouter = {
    path: '/businessClassify',
    component: () => import('@/layouts/index.vue'),
    redirect: '/businessClassify',
    name: 'businessClassifyIndex',
    meta: {
        title: '业务分类',
        icon: 'ri-apps-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        {
            path: '/businessClassify',
            component: () => import('@/views/businessClassify/index.vue'),
            name: 'businessClassify',
            meta: {
                title: '业务分类',
                icon: 'ri-apps-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
        // {
        //     path: '/businessClassify-table',
        //     component: () => import('@/views/businessClassify/tableIndex/index.vue'),
        //     name: 'businessClassifyTable',
        //     meta: {
        //         title: '业务分类-表格',
        //         icon: 'ri-apps-line' //remix 图标 优先级最高
        //         // elIcon: "House"//element-plus 图标 优先级第二
        //     }
        // },
        // {
        //     path: '/businessClassify-tree',
        //     component: () => import('@/views/businessClassify/treeIndex/index.vue'),
        //     name: 'businessClassifyTree',
        //     meta: {
        //         title: '业务分类-树',
        //         icon: 'ri-apps-line' //remix 图标 优先级最高
        //         // elIcon: "House"//element-plus 图标 优先级第二
        //     }
        // }
    ]
};

export default businessClassifyRouter;

const safetyRouter = {
    path: '/safety/whitelistManagement',
    component: () => import('@/layouts/index.vue'),
    redirect: '/safety/whitelistManagement',
    name: 'safetyIndex',
    meta: {
        title: '白名单管理',
        icon: 'ri-shield-keyhole-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        {
            path: '/safety/whitelistManagement',
            component: () => import('@/views/safety/whitelistManagement/index.vue'),
            name: 'whitelistManagement',
            meta: {
                title: '白名单管理',
                icon: 'ri-list-view' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
        // {
        // 	path: '/safety',
        // 	component: () => import('@/views/safety/index.vue'),
        // 	name: 'safety',
        // 	meta: {
        // 		title: '安全管理',
        // 		icon: 'ri-shield-keyhole-line' //remix 图标 优先级最高
        // 		// elIcon: "House"//element-plus 图标 优先级第二
        // 	}
        // }
    ]
};

export default safetyRouter;

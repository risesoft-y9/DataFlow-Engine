const systemManagementRouter = {
    path: '/systemManagement',
    component: () => import('@/layouts/index.vue'),
    redirect: '/userInfo',
    name: 'systemManagementIndex',
    meta: {
        title: '系统管理',
        icon: 'ri-dashboard-line' //remix 图标 优先级最高
        // elIcon: "House"//element-plus 图标 优先级第二
    },
    children: [
        {
            path: '/userInfo',
            component: () => import('@/views/systemManagement/userInfo/index.vue'),
            name: 'userInfo',
            meta: {
                title: '用户信息',
                icon: 'ri-user-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        },
        {
            path: '/roleManager',
            component: () => import('@/views/systemManagement/roleManagement/index.vue'),
            name: 'roleManager',
            meta: {
                title: '角色管理',
                icon: 'ri-user-3-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        },
        {
            path: '/authManagement',
            component: () => import('@/views/systemManagement/authManagement/index.vue'),
            name: 'authManagement',
            meta: {
                title: '授权管理',
                icon: 'ri-admin-line' //remix 图标 优先级最高
                // elIcon: "House"//element-plus 图标 优先级第二
            }
        }
    ]
};

export default systemManagementRouter;

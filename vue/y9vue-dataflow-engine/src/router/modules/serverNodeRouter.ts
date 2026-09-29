const serverNodeRouter = {
	path: '/serverNode',
	component: () => import('@/layouts/index.vue'),
	redirect: '/serverNode',
	name: 'serverNodeIndex',
	meta: {
		title: '节点管理',
		icon: 'ri-node-tree' //remix 图标 优先级最高
		// elIcon: "House"//element-plus 图标 优先级第二
	},
	children: [
		{
			path: '/serverNode',
			component: () => import('@/views/serverNode/index.vue'),
			name: 'serverNode',
			meta: {
				title: '节点管理',
				icon: 'ri-node-tree' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default serverNodeRouter;

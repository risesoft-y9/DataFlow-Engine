const interfaceRouter = {
	path: '/interface',
	component: () => import('@/layouts/index.vue'),
	redirect: '/interface',
	name: 'interfaceIndex',
	meta: {
		title: '接口管理',
		icon: 'ri-git-pull-request-line',
	},
	children: [
		{
			path: '/interface',
			component: () => import('@/views/interface/index.vue'),
			name: 'interface',
			meta: {
				title: '接口管理',
				icon: 'ri-git-pull-request-line' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default interfaceRouter;

const dispatchRouter = {
	path: '/dispatch',
	component: () => import('@/layouts/index.vue'),
	redirect: '/dispatch',
	name: 'dispatchIndex',
	meta: {
		title: '任务调度管理',
		icon: 'ri-sound-module-line' //remix 图标 优先级最高
		// elIcon: "House"//element-plus 图标 优先级第二
	},
	children: [
		{
			path: '/dispatch',
			component: () => import('@/views/dispatch/index.vue'),
			name: 'dispatch',
			meta: {
				title: '任务调度管理',
				icon: 'ri-sound-module-line' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		},
	]
};

export default dispatchRouter;

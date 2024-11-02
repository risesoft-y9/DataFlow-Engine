const taskConfigRouter = {
	path: '/taskConfig',
	component: () => import('@/layouts/index.vue'),
	redirect: '/taskConfig',
	name: 'taskConfigIndex',
	meta: {
		title: '任务配置管理',
		icon: 'ri-equalizer-line' //remix 图标 优先级最高
		// elIcon: "House"//element-plus 图标 优先级第二
	},
	children: [
		{
			path: '/taskConfig',
			component: () => import('@/views/taskConfig/index.vue'),
			name: 'taskConfig',
			meta: {
				title: '任务配置管理',
				icon: 'ri-equalizer-line' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default taskConfigRouter;

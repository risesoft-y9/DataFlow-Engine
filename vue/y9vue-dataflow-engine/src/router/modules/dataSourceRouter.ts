const dataSourceRouter = {
	path: '/dataSource',
	component: () => import('@/layouts/index.vue'),
	redirect: '/dataSource',
	name: 'dataSourceIndex',
	meta: {
		title: '数据源',
		icon: 'ri-database-line' //remix 图标 优先级最高
		// elIcon: "House"//element-plus 图标 优先级第二
	},
	children: [
		{
			path: '/dataSource',
			component: () => import('@/views/dataSource/index.vue'),
			name: 'dataSource',
			meta: {
				title: '数据源',
				icon: 'ri-database-line' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default dataSourceRouter;

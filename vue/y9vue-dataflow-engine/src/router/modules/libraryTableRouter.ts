const libraryTableRouter = {
	path: '/libraryTable',
	component: () => import('@/layouts/index.vue'),
	redirect: '/libraryTable',
	name: 'libraryTableIndex',
	meta: {
		title: '库表管理',
		icon: 'ri-table-view' //remix 图标 优先级最高
		// elIcon: "House"//element-plus 图标 优先级第二
	},
	children: [
		{
			path: '/libraryTable',
			component: () => import('@/views/libraryTable/index.vue'),
			name: 'libraryTable',
			meta: {
				title: '库表管理',
				icon: 'ri-table-view' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default libraryTableRouter;

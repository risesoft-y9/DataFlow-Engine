const dataWatermarkRouter = {
	path: '/dataWatermark',
	component: () => import('@/layouts/index.vue'),
	redirect: '/dataWatermark',
	name: 'dataWatermarkIndex',
	meta: {
		title: '数据水印管理',
		icon: 'ri-bookmark-line',
	},
	children: [
		{
			path: '/dataWatermark',
			component: () => import('@/views/dataWatermark/index.vue'),
			name: 'dataWatermark',
			meta: {
				title: '数据水印管理',
				icon: 'ri-bookmark-line' //remix 图标 优先级最高
				// elIcon: "House"//element-plus 图标 优先级第二
			}
		}
	]
};

export default dataWatermarkRouter;

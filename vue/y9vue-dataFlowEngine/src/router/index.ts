/*
 * @Author: your name
 * @Date: 2021-05-14 09:26:23
 * @LastEditTime: 2022-08-02 11:13:19
 * @LastEditors: error: git config user.name && git config user.email & please set dead value or install git
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-info\src\router\index.js
 */

import { routerBeforeEach } from '@/router/checkRouter';
import NProgress from 'nprogress';
import { createRouter, createWebHistory } from 'vue-router';
import homeRouter from './modules/homeRouter';
import dataSourceRouter from './modules/dataSourceRouter';
import libraryTableRouter from './modules/libraryTableRouter';
import businessClassifyRouter from './modules/businessClassifyRouter';
import ruleConfigRouter from './modules/ruleConfigRouter';
import taskConfigRouter from './modules/taskConfigRouter';
import dispatchRouter from './modules/dispatchRouter';
import processDesignRouter from './modules/processDesignRouter';
import logMonitoringRouter from './modules/logMonitoringRouter';
import serverNodeRouter from './modules/serverNodeRouter';
import safetyRouter from './modules/safetyRouter';
import systemManagementRouter from './modules/systemManagementRouter';
import interfaceRouter from './modules/interfaceRouter';
import y9_storage from '@/utils/storage';
//constantRoutes为不需要动态判断权限的路由，如登录、404、500等
export const constantRoutes: Array<any> = [
    {
        path: '/',
        name: 'index',
        hidden: true,
        redirect: import.meta.env.VUE_APP_APPFEATURES == '1' ? '/login' : '/home'
    },
    {
        path: '/login',
        name: 'login',
        hidden: true,
        meta: {
            title: '数据流引擎-登录页面'
        },
        component: () => import('@/views/login/index.vue')
    },
    {
        path: '/401',
        hidden: true,
        meta: {
            title: 'Not Permission'
        },
        component: () => import('@/views/401/index.vue')
    },
    {
        path: '/404',
        hidden: true,
        meta: {
            title: 'Not Found'
        },
        component: () => import('@/views/404/index.vue')
    },
    {
        path: '/api-test',
        hidden: true,
        meta: {
            title: '接口在线测试'
        },
        component: () => import('@/views/test/index.vue')
    }
];

// 根据条件显示系统管理菜单
const systemRouter = import.meta.env.VUE_APP_APPFEATURES == '1' ? systemManagementRouter : {};
const businessRouter = import.meta.env.VUE_APP_APPFEATURES == '1' ? businessClassifyRouter : {};
//asyncRoutes需求动态判断权限并动态添加的页面  这里的路由模块顺序也是菜单显示的顺序（位置：src->router->modules）
export const asyncRoutes = [
    homeRouter,
    dataSourceRouter,
    libraryTableRouter,
    businessRouter,
    interfaceRouter,
    ruleConfigRouter,
    taskConfigRouter,
    dispatchRouter,
    processDesignRouter,
    logMonitoringRouter,
    serverNodeRouter,
    safetyRouter,
    systemRouter
];
// 引入其他模块路由

//创建路由模式，采用history模式没有“#”
const router = createRouter({
    history: createWebHistory(import.meta.env.VUE_APP_PUBLIC_PATH),
    routes: constantRoutes
});

//在用户点击前，进入routerBeforeEach去判断用户是否有权限
//全部判断逻辑请查看checkRouter.js
router.beforeEach(routerBeforeEach);
router.afterEach(() => {
    NProgress.done();
});
export default router;

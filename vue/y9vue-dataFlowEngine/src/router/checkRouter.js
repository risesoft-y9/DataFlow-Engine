/*
 * @Author: your name
 * @Date: 2021-12-22 14:38:02
 * @LastEditTime: 2022-02-08 17:25:26
 * @LastEditors: Please set LastEditors
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz-team-frontend-9.5.x/y9vue-home/src/router/checkRouter.js
 */
import '@/assets/css/nprogress.css'; // progress bar style
import router from '@/router';
import { checkRole } from '@/router/checkRole';
import { constantRoutes } from '@/router/index';
import { useSettingStore } from '@/store/modules/settingStore';
import y9_storage from '@/utils/storage';
import NProgress from 'nprogress'; // progress bar
import { $y9_SSO } from '../main';
import { getLoginInfo } from './getInitData';
// import { isStarred } from '@/api/gitee';

NProgress.configure({
    showSpinner: false,
    easing: 'ease',
    speed: 1000
});

// 路由白名单过滤
function routerWriteList(array, path) {
    let find = false;
    for (let index = 0; index < array.length; index++) {
        const item = array[index];
        if (item.path === path) {
            return true;
        }
        if (item.children) {
            find = routerWriteList(item.children, path);
        }
    }
    if (find) {
        return true;
    } else {
        return false;
    }
}

// 路由白名单
export async function checkWriteList(to, from) {
    // 白名单
    let isWriteList = routerWriteList(constantRoutes, to.path);
    if (isWriteList) {
        if (to.path === '/' || to.path === '/login') {
            // 登陆过 导航直接跳过login页面进入系统
            const isRight = await $y9_SSO.checkToken();
            if (isRight) {
                window.location = import.meta.env.VUE_APP_HOST_INDEX;
            }
        }
        return true;
    } else {
        return false;
    }
}

let userRole = ['user'];
async function check() {
    let isTokenValid, isRoleValid;

    // access_token 是否过期
    isTokenValid = await $y9_SSO.checkToken();
    // console.log(`isTokenValid=${isTokenValid}`);
    if (!isTokenValid) {
        return false;
    }

    const y9UserInfo = JSON.parse(sessionStorage.getItem('ssoUserInfo'));

    if (y9UserInfo.managerLevel === 1) {
        userRole = ['systemAdmin'];
    } else if (y9UserInfo.managerLevel === 2) {
        userRole = ['securityAdmin'];
    } else if (y9UserInfo.managerLevel === 3) {
        userRole = ['auditAdmin'];
    }

    isRoleValid = (await checkRole(userRole)) ? true : false;
    // 根据角色权限获取路由
    // let isLoadRouter = sessionStorage.getItem('isLoadRouter');
    // 是否加载过数据
    // if (import.meta.env.VUE_APP_APPFEATURES === '1' && isLoadRouter !== '1') {
    //     // 获取应用初始化数据，可选
    //     let initInfo = await getLoginInfo();
    //     y9_storage.setObjectItem('initInfo', initInfo.data);
    // } else {
    //     isRoleValid = true;
    // }
    // 每个工程都请求这个接口，当错误时，不再请求
    if (sessionStorage.getItem('getLoginInfo') != 'true') {
        // let initInfo = await getLoginInfo();
        // y9_storage.setObjectItem('initInfo', initInfo.data);
    }

    if (!isRoleValid) {
        return false;
    }
    // token在有效期且角色已获取路由
    if (isTokenValid && isRoleValid) {
        return true;
    } else {
        if (!isTokenValid || !isRoleValid) {
            await $y9_SSO.ssoLogout({
                logoutUrl: import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/'
            });
        }
        return false;
    }
}

async function y9ToRoute(from, to) {
    // console.log(await router.getRoutes(),router,from,to, router.hasRoute(to.name));
    if (!to.name) {
        let array = await router.getRoutes();
        // console.log(to, array)
        array.forEach((item) => {
            if (item.path === to.path && item.name) {
                // console.log("=====", router.hasRoute(item.name));
                router.push({
                    name: item.name
                });
            }
        });
    } else {
        return true;
    }
}

// 所有路由上带的参数塞到一个对象里
const parseQueryString = (string) => {
    if (string == '') {
        return false;
    }
    var segments = string.split('&').map((s) => s.split('='));
    var queryString = {};
    segments.forEach((s) => (queryString[s[0]] = s[1]));
    return queryString;
};
// 存储任意路由的参数
const cacheQuery = (query) => {
    // 维护同一个参数，任意组件内可取出使用对应的参数
    let session_query = y9_storage.getObjectItem('query');
    if (session_query) {
        for (const key in query) {
            session_query[key] = query[key];
        }
        y9_storage.setObjectItem('query', session_query);
    } else {
        y9_storage.setObjectItem('query', query);
    }
};

let flag = 0;
export const routerBeforeEach = async (to, from) => {
    if (!y9_storage.getStringItem('initCheck')) {
        y9_storage.setStringItem('initCheck', 'false');
    }
    flag++;
    const settingStore = useSettingStore();
    // 进度条
    if (settingStore.getProgress) {
        NProgress.start();
    }

    // 检查路由白名单
    if (to.fullPath.indexOf('?platform=gitee&code=') == -1) {
        let isWriteRoute = await checkWriteList(to, from);
        if (isWriteRoute) {
            return true;
        }
    }

    let session_query = y9_storage.getObjectItem('query');
    if(session_query['search'] != null) {
        session_query['search'] = '';
        y9_storage.setObjectItem('query', session_query);
    }

    // 路由是否带参数
    let query = parseQueryString(window.location.search.substring(1));
    if (query) {
        cacheQuery(query);
    }

    // 不使用数据流引擎单点登陆，直接进入页面级组件
    if (import.meta.env.VUE_APP_APPFEATURES == '1') {
        // 特殊判断，gitee上是否点赞
        // if (query.platform && query.code && query.platform === 'gitee') {
        //     let result = await isStarred(query.code);
        //     if (!result.data) {
        //         router.push({ path: '/unstarred' });
        //         return;
        //     } else {
        //         y9_storage.setStringItem('initCheck', 'true');
        //     }
        // }
        // if (y9_storage.getStringItem('initCheck') === 'false' && to.path !== '/unstarred') {
        //     const giteeAuthUrl =
        //         'https://gitee.com/oauth/authorize?client_id=e05fe4423a35a22d85978c0c28c6acceda1c7636201f83a5e86545503041ae5a&redirect_uri=https%3A%2F%2Ftest.youshengyun.com%2Fy9vue-dataFlowEngine%2F%3Fplatform%3Dgitee&response_type=code';
        //     window.location = giteeAuthUrl;
        //     return false;
        // }
        // if(y9_storage.getStringItem('initCheck') === 'true' && to.fullPath.indexOf("?platform=gitee&code=") > -1) {
        //     window.location = import.meta.env.VUE_APP_HOST_INDEX + 'home';
        //     return;
        // }

        // 默认返回所有路由
        await checkRole();
        return await y9ToRoute(from, to);
    }
    // 正常使用数据流引擎单点登录方式
    else {
        let CHECK = await check();
        if (CHECK) {
            // if(flag > 1) {
            //     // 特殊判断，gitee上是否点赞
            //     if (query.platform && query.code && query.platform === 'gitee') {
            //         let result = await isStarred(query.code);
            //         if (!result.data) {
            //             router.push({ path: '/unstarred' });
            //             return;
            //         } else {
            //             y9_storage.setStringItem('initCheck', 'true');
            //         }
            //     }
            //     if (y9_storage.getStringItem('initCheck') === 'false' && to.path !== '/unstarred') {
            //         const giteeAuthUrl =
            //             'https://gitee.com/oauth/authorize?client_id=e05fe4423a35a22d85978c0c28c6acceda1c7636201f83a5e86545503041ae5a&redirect_uri=https%3A%2F%2Ftest.youshengyun.com%2Fy9vue-dataFlowEngine%2F%3Fplatform%3Dgitee&response_type=code';
            //         window.location = giteeAuthUrl;
            //         return false;
            //     }
            // }
            return await y9ToRoute(from, to);
        } else {
            await $y9_SSO.checkLogin();
        }
        return false;
    }
};

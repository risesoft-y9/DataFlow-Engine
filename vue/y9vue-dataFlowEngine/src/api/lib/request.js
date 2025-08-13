import settings from '@/settings';
import y9_storage from '@/utils/storage';
import axios from 'axios'; // 考虑CDN
import { ElMessage } from 'element-plus';
import i18n from '@/language/index';
const { t } = i18n.global;
import { isExternal } from '@/utils/validate.ts';
import { $y9_SSO } from '@/main';
// import { useI18n } from "vue-i18n"
// const { t } = useI18n();

// 创建一个axios实例
function y9Request(baseUrl = '') {
    let requestList = new Set();

    if (baseUrl) {
        baseUrl = import.meta.env.VUE_APP_HOST + baseUrl;
    }

    const service = axios.create({
        baseURL: baseUrl ? baseUrl : import.meta.env.VUE_APP_CONTEXT,
        withCredentials: true,
        timeout: 0
    });
    // 请求拦截器
    service.interceptors.request.use(
        (config) => {
            // config.cancelToken = new axios.CancelToken(e => {
            //     // 阻止重复请求
            //     requestList.has(config.url) ? e(`${location.host}${config.url}---重复请求被中断`) : requestList.add(config.url)
            // })
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            // 自定义
            if (config.cType) {
                config.headers['userLoginName'] = config.data.userLoginName;
            }
            // console.log(config);
            if (config.JSON) {
                config.headers['Content-Type'] = 'application/json';
            }
            const access_token = y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            if (import.meta.env.VUE_APP_APPFEATURES == '0' && access_token) {
                config.headers['Authorization'] = 'Bearer ' + access_token;
            }

            if (import.meta.env.VUE_APP_APPFEATURES == '1' && access_token) {
                config.headers['x-token'] = access_token;
            }

            return config;
        },
        (error) => {
            // 处理请求错误
            return Promise.reject(error);
        }
    );

    // 响应拦截器
    service.interceptors.response.use(
        (response) => {
            // 相同请求不得在600毫秒内重复发送，反之继续执行
            setTimeout(() => {
                requestList.delete(response.config.url);
            }, 600);
            let res;
            if (response.data) {
                res = response.data;
            } else {
                res = response;
            }
            const { code } = res;
            if (code !== 0) {
                // 获取替换后的字符串
                const reqUrl = response.config.url.split('?')[0].replace(response.config.baseURL, '');
                const noVerifyBool = settings.ajaxResponseNoVerifyUrl.includes(reqUrl);
                switch (code) {
                    case 100: // 未登陆
                    case 101: // 令牌已失效
                    case 102: // 校验令牌出问题了
                        if (!noVerifyBool) {
                            ElMessageBox({
                                title: t('提示'),
                                showClose: false,
                                closeOnClickModal: false,
                                closeOnPressEscape: false,
                                message: t('当前用户登入信息已失效，请重新登入再操作'),
                                beforeClose: (action, instance, done) => {
                                    if (isExternal(settings.serverLoginUrl)) {
                                        window.location.href = settings.serverLoginUrl;
                                    } else {
                                        sessionStorage.clear();
                                        window.location.reload();
                                    }
                                }
                            });
                        }
                        break;
                    case 40300:
                        window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/401';
                        break;
                    case 40400:
                        window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/404';
                        break;
                    case 50000:
                        return res;
                    case 500:
                    case 401:
                    //case 403:
                        if (!noVerifyBool) {
                            ElMessage({
                                message: res.msg,
                                type: 'error',
                                duration: 1500,
                            });
                        }
                        break;
                    default:
                        if (!noVerifyBool) {
                            console.error(res.msg);
                        }
                        break;
                }

                if (code === 101000) {
                    // 返回错误 走 catch
                    return Promise.resolve(res);
                } else {
                    // 返回错误 走 catch
                    return res;
                }
            } else {
                return res;
            }
        },
        (error) => {
            // 异常情况
            if (axios.isCancel(error)) {
                // log
                // 请求取消
                console.warn(error);
                // console.table([error.message.split('---')[0]], 'cancel')
            } else {
                ElMessage({
                    message: error.message,
                    type: 'error',
                    duration: 5 * 1000
                });
                // 请求失败
                requestList.delete(error.config.url);
                if (error.message.includes('401')) {
                    ElMessageBox({
                        title: t('提示'),
                        showClose: false,
                        closeOnClickModal: false,
                        closeOnPressEscape: false,
                        message: t('当前用户登入信息已失效，请重新登入再操作'),
                        beforeClose: (action, instance, done) => {
                            if (isExternal(settings.serverLoginUrl)) {
                                window.location.href = settings.serverLoginUrl;
                            } else {
                                const params = {
                                    to: {
                                        path: window.location.pathname
                                    },
                                    logoutUrl:
                                        import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/',
                                    __y9delete__: () => {
                                        // 删除前执行的函数
                                        console.log('删除前执行的函数');
                                    }
                                };
                                $y9_SSO.ssoLogout(params);
                                // window.location.reload();
                            }
                        }
                    });
                }
            }

            return Promise.reject(error);
        }
    );

    return service;
}

export default y9Request;

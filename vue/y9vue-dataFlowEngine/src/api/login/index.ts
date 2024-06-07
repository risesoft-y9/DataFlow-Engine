import Request from '@/api/lib/request';
import qs from 'qs';
const loginRequest = Request('risedata/services/rest/');

/**
 * 获取token
 * @param params
 * @returns
 */
export const login = async (params) => {
    const data = qs.stringify(params);
    return await loginRequest({
        url: 'login/getToken',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 获取当前用户信息
 * @param params
 * @returns
 */
export const getUserLoginInfo = async () => {
    return await loginRequest({
        url: 'login/getUser',
        method: 'get',
        cType: false
    });
};

/**
 * 删除token
 * @param params
 * @returns
 */
export const deleteToken = async () => {
    return await loginRequest({
        url: 'login/removeToken',
        method: 'post',
        cType: false
    });
};

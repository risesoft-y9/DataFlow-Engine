import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 查询用户
 * @param params
 * @returns
 */
export const searchUsersList = async (params) => {
    return await platformRequest({
        url: 'role/link/searchUsers',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 授权
 * @param params
 * @returns
 */
export const userEmpower = async (params) => {
    return await platformRequest({
        url: 'role/link/bind',
        method: 'POST',
        cType: false,
        JSON: true,
        data: params
    });
};

/**
 * 解除授权
 * @param params
 * @returns
 */
export const revokeUserEmpower = async (params) => {
    return await platformRequest({
        url: 'role/link/unBind',
        method: 'POST',
        cType: false,
        JSON: true,
        data: params
    });
};

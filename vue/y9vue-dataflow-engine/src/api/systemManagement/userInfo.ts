import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 查询用户
 * @param params
 * @returns
 */
export const getUserList = async (params) => {
    return await platformRequest({
        url: 'user/searchForPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 创建新用户
 * @param params
 * @returns
 */
export const addNewUser = async (params) => {
    // 遍历对象并填充到formData中
    return await platformRequest({
        url: 'user/createUser',
        method: 'POST',
        cType: false,
        JSON: true,
        data: params
    });
};

/**
 * 修改用户信息
 * @param params
 * @returns
 */
export const updateUserInfo = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'user/updateUserInfo',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除用户
 * @param params
 * @returns
 */
export const deleteUserAccount = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'user/deleteUser',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 修改密码
 * @param params
 * @returns
 */
export const updateAccountPassword = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'user/updatePassword',
        method: 'POST',
        cType: false,
        data
    });
};

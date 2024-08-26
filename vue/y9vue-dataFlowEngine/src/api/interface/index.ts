import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 分页获取接口列表
 * @param params
 * @returns
 */
export const searchPage = async (params) => {
    return await platformRequest({
        url: 'interface/searchPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取接口参数信息
 * @param params
 * @returns
 */
export const findParamsList = async (params) => {
    return await platformRequest({
        url: 'interface/findParamsList',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 保存接口返回参数信息
 * @param params
 * @returns
 */
export const saveResponseParams = async (data) => {
    return await platformRequest({
        url: 'interface/saveResponseParams',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 保存接口信息
 * @param params
 * @returns
 */
export const saveInterface = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'interface/saveData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除接口信息
 * @param params
 * @returns
 */
export const removeInterface = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'interface/deleteData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除接口参数
 * @param params
 * @returns
 */
export const deleteParams = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'interface/deleteParams',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 保存接口参数信息
 * @param params 
 */
export const saveParams = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: "interface/saveParams",
        method: 'post',
        data
    });
};

/**
 * 接口测试
 * @param params 
 */
export const apiTest = async (data) => {
    return await platformRequest({
        url: "interface/apiTest",
        method: 'post',
        JSON: true,
        data
    });
};

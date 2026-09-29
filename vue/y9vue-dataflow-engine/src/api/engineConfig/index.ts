import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取配置映射数据
 * @param params
 * @returns
 */
export const getTableList = async (params) => {
    return await platformRequest({
        url: 'mapping/getMappingPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 保存配置映射信息
 * @param params
 * @returns
 */
export const saveConfigureMapping = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'mapping/saveData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除配置映射信息
 * @param params
 * @returns
 */
export const deleteConfigureMapping = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'mapping/deleteData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 获取配置映射参数数据
 * @param params
 * @returns
 */
export const getMappingParams = async (params) => {
    return await platformRequest({
        url: 'mapping/getArgsPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 保存配置映射参数信息
 * @param params
 * @returns
 */
export const saveMappingParams = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'mapping/saveArgsData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除配置映射参数信息
 * @param params
 * @returns
 */
export const deleteMappingParams = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'mapping/deleteArgs',
        method: 'POST',
        cType: false,
        data
    });
};

import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 一级接口 数据源类型
 * @param params
 * @returns
 */
export const getDataSourceType = async () => {
    return await platformRequest({
        url: 'source/findCategoryAll',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 二级接口 根据数据源类型查找对应数据列表
 * @param params
 * @returns
 */
export const getDataSourceByType = async (params) => {
    return await platformRequest({
        url: 'source/findByBaseType',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 搜索数据源
 * @param params
 * @returns
 */
export const searchDataSource = async (params) => {
    return await platformRequest({
        url: 'source/searchSource',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 获取数据源详情
 * @param params
 * @returns
 */
export const getDataSourceById = async (params) => {
    return await platformRequest({
        url: 'source/getDataSource',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 新增数据源分类信息
 * @param params
 * @returns
 */
export const addDataSourceInfo = async (params) => {
    let formData = new FormData();
    for (let ele in params) {
        formData.append(ele, params[ele]);
    }
    return await platformRequest({
        url: 'source/saveDataCategory',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 保存数据源连接信息
 * @param params
 * @returns
 */
export const saveDataConnectInfo = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/saveSource',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除数据源信息
 * @param params
 * @returns
 */
export const deleteDataSource = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/deleteSource',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除数据源分类信息
 * @param params
 * @returns
 */
export const deleteDataSourceType = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/deleteCategory',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 根据类型获取关系型和其他数据源列表
 * @param params
 * @returns
 */
export const findByTypeList = async (params) => {
    return await platformRequest({
        url: 'source/findByType',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 检测数据源状态
 * @param params
 * @returns
 */
export const checkDataStatus = async (params) => {
    return await platformRequest({
        url: 'source/checkStatus',
        method: 'GET',
        cType: false,
        params: params
    });
};

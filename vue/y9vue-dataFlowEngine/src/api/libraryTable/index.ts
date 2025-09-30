import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取数据库的表
 * @param params
 * @returns
 */
export const getTableList = async (params) => {
    return await platformRequest({
        url: 'source/getTableAll',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取表字段信息
 * @param params
 * @returns
 */
export const getTableField = async (params) => {
    return await platformRequest({
        url: 'source/getFieldList',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取接口返回字段信息
 * @param params
 * @returns
 */
export const getApiField = async (params) => {
    return await platformRequest({
        url: 'interface/getApiField',
        method: 'GET',
        cType: false,
        params
    });
};

/****
 * 保存表字段信息
 * @param params
 * @returns
 */
export const saveTableField = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/saveField',
        method: 'POST',
        cType: false,
        data
    });
};

/****
 * 删除表字段信息
 * @param params
 * @returns
 */
export const deleteTableField = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/deleteField',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 获取数据库需要提取的表
 * @param params
 * @returns
 */
export const getExtractTable = async (params) => {
    return await platformRequest({
        url: 'source/getNotExtractList',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 提取表信息
 * @param params
 * @returns
 */
export const ExtractTableInfo = async (params) => {
    return await platformRequest({
        url: 'source/extractTable',
        method: 'POST',
        cType: false,
        params
    });
};

/**
 * 复制表信息
 * @param params
 * @returns
 */
export const copyTableInfo = async (params) => {
    return await platformRequest({
        url: 'source/copyTable',
        method: 'POST',
        cType: false,
        params
    });
};

/**
 * 删除表信息
 * @param params
 * @returns
 */
export const deleteTableInfo = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/deleteTable',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 生成表
 */
export const buildTable = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/buildTable',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 更新表结构
 * @param params
 */
export const updateTable = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/updateTable',
        method: 'POST',
        cType: false,
        data
    });
};


/**
 * 保存表信息
 * @param params
 * @returns
 */
export const saveTable = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'source/saveTable',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 获取数据库可选字段类型列表
 * @param params
 * @returns
 */
export const getFieldTypes = async (params) => {
    return await platformRequest({
        url: 'source/getFieldTypes',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取表关联关系
 * @param params
 * @returns
 */
export const getTableJob = async (params) => {
    return await platformRequest({
        url: 'source/getTableJob',
        method: 'GET',
        cType: false,
        params
    });
};

import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 获取任务列表并设置定时
 * @param params
 * @returns
 */
export const getDataFindAll = async (params) => {
    return await platformRequest({
        url: 'task/findAll',
        method: 'GET',
        cType: false,
        params
    });
};
/**
 * 查询任务接口
 * @param params
 * @returns
 */
export const getDataSearch = async (params) => {
    return await platformRequest({
        url: 'job/data/search',
        method: 'GET',
        cType: false,
        params
    });
};
/**
 * 获取用户的环境
 * @param params
 * @returns
 */
export const getEnvironment = async () => {
    return await platformRequest({
        url: 'system/Environment/getEnvironment',
        method: 'GET',
        cType: false
    });
};
/**
 * 获取所有环境
 * @param params
 * @returns
 */
export const getEnvironmentAll = async () => {
    return await platformRequest({
        url: 'system/Environment/getAll',
        method: 'GET',
        cType: false
    });
};

/**
 * 根据ID删除任务
 * @param params
 * @returns
 */
export const dataDelete = async (params) => {
    // const data = qs.stringify(params);
    return await platformRequest({
        url: 'job/data/delete',
        method: 'POST',
        JSON: true,
        params
    });
};
/**
 * 设置任务状态
 * @param params
 * @returns
 */
export const setStatus = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'job/data/setStatus',
        method: 'POST',
        cType: false,
        data
    });
};
/**
 * 保存任务
 * @param params
 * @returns
 */
export const saveJob = async (data) => {
    // const data = qs.stringify(params);
    return await platformRequest({
        url: 'job/data/save',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 调度一次
 * @param params
 * @returns
 */
export const sendJob = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'job/data/sendJob',
        method: 'POST',
        cType: false,
        data
    });
};
/**
 * 下次执行时间
 * @param params
 * @returns
 */
export const nextExecutorTime = async (params) => {
    return await platformRequest({
        url: 'job/data/nextExecutorTime',
        method: 'POST',
        cType: false,
        params
    });
};

/**
 * 获取某个环境下的任务总数
 * @param params
 * @returns
 */
export const getCount = async (params) => {
    return await platformRequest({
        url: 'job/data/getCount',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 删除任务正在等待的任务
 * @param params
 * @returns
 */
export const killAwaitJob = async (params) => {
    return await platformRequest({
        url: 'job/data/killAwaitJob',
        method: 'POST',
        cType: false,
        JSON: true,
        params
    });
};

/**
 * 生成任务忙碌图
 * @param params
 * @returns
 */
export const showJobView = async (params) => {
    return await platformRequest({
        url: 'job/data/showJobView',
        method: 'GET',
        cType: false,
        params
    });
};

//获取调度日志
export const getLogSearch = async (params) => {
    return await platformRequest({
        url: 'job/log/search',
        method: 'GET',
        cType: false,
        params
    });
};

//获取日志结果数据
export const getConsole = async (params) => {
    return await platformRequest({
        url: 'job/log/getConsole',
        method: 'GET',
        cType: false,
        params
    });
};

//获取可调度的服务
export const getServiceByName = async (params) => {
    return await platformRequest({
        url: 'system/service/getServiceByName',
        method: 'GET',
        cType: false,
        params
    });
};
//获取任务失败分组调度日志
export const getLogSearchByGroup = async (params) => {
    return await platformRequest({
        url: 'job/log/searchByGroup',
        method: 'GET',
        cType: false,
        params
    });
};

//获取所有的服务
export const getServicesAll = async (params) => {
    return await platformRequest({
        url: 'system/service/getServicesAll',
        method: 'GET',
        cType: false,
        params
    });
};
//获取分组日志分析报告
export const searchByGroupReport = async (params) => {
    return await platformRequest({
        url: 'job/log/searchByGroupReport',
        method: 'GET',
        cType: false,
        params
    });
};

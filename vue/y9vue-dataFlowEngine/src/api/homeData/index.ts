import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 获取首页数据
 * @param params  //暂时没有
 * @returns
 */
export const getHomeData = async (params) => {
    return await platformRequest({
        url: 'home/getHomeData',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取调度情况
 * @param params
 * @returns
 */
export const getSchedulingInfo = async (data) => {
    return await platformRequest({
        url: 'home/getSchedulingInfo',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 获取每日
 * @param data
 * @returns
 */
export const getDailySchedulingFrequencyInfo = async (data) => {
    return await platformRequest({
        url: 'home/getDailySchedulingFrequencyInfo',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 获取状态
 * @param data
 * @returns
 */
export const getTaskStateInfo = async (data) => {
    return await platformRequest({
        url: 'home/getTaskStateInfo',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 获取当前任务
 */
export const getCurrentTaskInfo = async (data) => {
    return await platformRequest({
        url: 'home/getCurrentTaskInfo',
        method: 'POST',
        JSON: true,
        data
    });
};
/**
 *
 * @param data 获取日志分类信息
 * @returns
 */
export const getJobLogInfo = async (data) => {
    return await platformRequest({
        url: 'home/getJobLogInfo',
        method: 'POST',
        JSON: true,
        data
    });
};

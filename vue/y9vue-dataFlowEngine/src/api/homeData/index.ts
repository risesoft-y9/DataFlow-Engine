import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 获取首页数据
 * @param params
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
export const getSchedulingInfo = async (params) => {
    return await platformRequest({
        url: 'home/getSchedulingInfo',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取每日调度次数
 * @param params
 * @returns
 */
export const getDailySchedulingFrequencyInfo = async (params) => {
    return await platformRequest({
        url: 'home/getDailyInfo',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 获取任务状态比例图
 * @param params
 * @returns
 */
export const getTaskStateInfo = async (params) => {
    return await platformRequest({
        url: 'home/getTaskStateInfo',
        method: 'GET',
        cType: false,
        params
    });
};

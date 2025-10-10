import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 分页获取列表
 * @param params
 * @returns
 */
export const searchPage = async (params) => {
    return await platformRequest({
        url: 'watermark/searchPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 保存信息
 * @param params
 * @returns
 */
export const saveWatermark = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'watermark/saveData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 删除信息
 * @param params
 * @returns
 */
export const removeWatermark = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'watermark/deleteData',
        method: 'POST',
        cType: false,
        data
    });
};

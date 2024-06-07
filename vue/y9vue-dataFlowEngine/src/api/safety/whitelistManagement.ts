import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 分页查询白名单
 * @param params
 * @returns
 */
export const searchWhiteList = async (params) => {
    return await platformRequest({
        url: 'system/networkWhiteList/searchForPage',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 根据id删除
 * @param params
 * @returns
 */
export const deleteWhiteById = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'system/networkWhiteList/delById',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 保存修改白名单
 * @param params
 * @returns
 */
export const saveOrUpdateWhite = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'system/networkWhiteList/save',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 根据id获取白名单信息
 * @param params
 * @returns
 */
export const getWhiteById = async (params) => {
    return await platformRequest({
        url: 'system/networkWhiteList/findById',
        method: 'GET',
        cType: false,
        params
    });
};

import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取业务分类列表
 * @param params
 * @returns
 */
export const getTableList = async (params?) => {
    let data = params ? params : { page: 1, size: 20 };
    return await platformRequest({
        url: 'business/getAll',
        method: 'GET',
        cType: false,
        params: data
    });
};

/**
 * 保存业务分类信息
 * @param params
 * @returns
 */
export const saveBusinessType = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'business/saveData',
        method: 'POST',
        cType: false,
        data
    });
};

/**
 * 根据id获取业务分类信息
 * @param params
 * @returns
 */
export const getBusinessTypeById = async (params) => {
    return await platformRequest({
        url: 'business/getDataById',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 删除业务分类数据
 * @param params
 * @returns
 */
export const deleteBusinessType = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'business/deleteData',
        method: 'POST',
        cType: false,
        data
    });
};

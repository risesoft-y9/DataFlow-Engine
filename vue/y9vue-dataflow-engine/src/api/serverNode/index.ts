import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取所有服务
 * @returns
 */
export const getServicesList = async (params) => {
    return await platformRequest({
        url: 'system/service/getServicesAll',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 修改服务器状态
 * @param id status
 * @returns
 */
export const modifyServiceStatus = async (id, status) => {
    return await platformRequest({
        url: `system/service/updateStatus/${id}/${status}`,
        method: 'GET',
        cType: false
    });
};

/**
 * 重启服务器
 * @param id
 * @returns
 */
export const restartService = async (id) => {
    return await platformRequest({
        url: `system/service/reStart/${id}`,
        method: 'POST',
        cType: false
    });
};

/**
 * 删除服务
 * @param id
 * @returns
 */
export const deleteService = async (params) => {
    return await platformRequest({
        url: 'system/service/remove',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 检查实例
 * @param id
 * @returns
 */
export const checkServiceExample = async (params) => {
    return await platformRequest({
        url: 'system/service/check',
        method: 'POST',
        cType: false,
        params
    });
};

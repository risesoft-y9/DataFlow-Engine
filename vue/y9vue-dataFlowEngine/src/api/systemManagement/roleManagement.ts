import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 分页查询
 * @param params
 * @returns
 */
export const getRolesList = async (params) => {
    return await platformRequest({
        url: 'role/search',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 保存和修改角色
 * @param params
 * @returns
 */
export const saveOrUpdateRole = async (params) => {
    return await platformRequest({
        url: 'role/saveRole',
        method: 'POST',
        cType: false,
        JSON: true,
        data: params
    });
};

/**
 * 根据id删除角色
 * @param params
 * @returns
 */
export const deleteRoleById = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'role/deleteRole',
        method: 'POST',
        cType: false,
        data
    });
};

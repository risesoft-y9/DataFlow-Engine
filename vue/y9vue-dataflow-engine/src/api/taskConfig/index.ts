import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 全量获取业务分类
 * @param params
 * @returns
 */
export const getFindAll = async () => {
    return await platformRequest({
        url: 'business/findAll',
        method: 'GET',
        cType: false,
    });
};

/**
 * 根据id获取表列表和执行类
 * @param params
 * @returns
 */
export const getDataTableList = async (params) => {
    return await platformRequest({
        url: 'source/getTableList',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 分页获取任务配置列表
 * @param params
 */
export const getFindPage = async (params) => {
    return await platformRequest({
        url: 'task/findPage',
        method: 'GET',
        cType: false,
        params
    });
};


/**
 * 获取页面执行器  类别名称，输入/输出线程池：executor，数据闸口：exchange，
 * 输入/输出通道：channel，其它插件：plugs
 * @param params
 */
export const getFindByTypeName = async (params) => {
    return await platformRequest({
        url: 'mapping/findByTypeName',
        method: 'GET',
        cType: false,
        params
    });
};


/**
 * mappingId 根据id获取列表
 * @param params
 */
export const getFindByMappingId = async (params) => {
    return await platformRequest({
        url: 'mapping/findByMappingId',
        method: 'GET',
        cType: false,
        params
    });
};
/**
 * 新增任务
 * @param data
 * @returns
 */
export const saveTaskTable = async (data) => {
    return await platformRequest({
        url: 'task/save',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 根据id任务修改使用
 * @param params
 */
export const getDataById = async (params) => {
    return await platformRequest({
        url: 'task/getDataById',
        method: 'GET',
        cType: false,
        params
    });
};

export const getSingleTaskById = async (params) => {
    return await platformRequest({
        url: 'task/getSingleTaskById',
        method: 'GET',
        cType: false,
        params
    });
};


/**
 * 删除任务
 * @param data
 * @returns
 */
export const deleteDataId = async (params) => {
    return await platformRequest({
        url: 'task/deleteData',
        method: 'POST',
        cType: false,
        params
    });
};


/**
 * 获取任务详情
 * @param data
 * @returns
 */
export const getTaskDetails = async (params) => {
    return await platformRequest({
        url: 'task/getTaskDetails',
        method: 'GET',
        cType: false,
        params
    });
};

/**
 * 新增单任务
 * @param params
 * @returns
 */
export const saveSingleTask = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'task/saveSingleTask',
        method: 'POST',
        cType: false,
        data
    });
};

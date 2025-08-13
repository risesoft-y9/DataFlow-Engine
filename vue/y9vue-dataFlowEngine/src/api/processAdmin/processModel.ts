import Request from "@/api/lib/request";
import qs from "qs";

var processAdminRequest = new Request();

//获取任务编排列表
export const getModelList = async (params) => {
  return await processAdminRequest({
    url: "arrange/searchPage",
    method: 'get',
    params
  });
}

//保存
export const saveArrange = async (params) => {
  const data = qs.stringify(params);
  return await processAdminRequest({
    url: 'arrange/saveData',
    method: 'POST',
    cType: false,
    data
  });
};

//删除
export const deleteArrange = async (params) => {
  const data = qs.stringify(params);
  return await processAdminRequest({
    url: "arrange/deleteData",
    method: 'post',
    cType: false,
    data
  });
}

// 执行流程任务
export const executeProcess = async (params) => {
  const data = qs.stringify(params);
  return await processAdminRequest({
    url: "arrange/executeProcess",
    method: 'post',
    cType: false,
    data
  });
}

/**
 * 获取流程设计XML
 */
export const getModelXml = async (modelId) => {
  const params = {
    id: modelId
  };
  return await processAdminRequest({
    url: "arrange/getXmlById",
    method: 'get',
    params: params
  });
}

// 获取日志
export const getLogList = async (params) => {
  return await processAdminRequest({
    url: "arrange/getLogList",
    method: 'get',
    params: params
  });
}

// 保存流程
export const saveXmlData = async (params) => {
  const data = qs.stringify(params);
  return await processAdminRequest({
    url: 'arrange/saveXml',
    method: 'POST',
    cType: false,
    data
  });
};
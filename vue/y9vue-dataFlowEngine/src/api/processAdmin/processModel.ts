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

//创建流程设计
export const createModel = async (name, key, description) => {
  const params = {
    name: name,
    key: key,
    description: description
  };
  return await processAdminRequest({
    url: "/vue/processModel/create",
    method: 'post',
    params: params
  });
}

//部署流程设计
export const deployModel = async (modelId) => {
  const params = {
    modelId: modelId
  };
  return await processAdminRequest({
    url: "/vue/processModel/deployModel",
    method: 'post',
    params: params
  });
}

export const importModel = async (params) => {
  var data = new FormData();
  data.append("file", params.file);
  return await processAdminRequest({
    url: "/vue/processModel/import",
    method: 'POST',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    cType: false,
    data: data
  });
};
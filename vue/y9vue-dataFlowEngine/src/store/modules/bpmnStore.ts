import { remove } from 'lodash';
import { defineStore } from 'pinia';
import { createNonNullChain, isJSDocNullableType } from 'typescript';
import { isReactive, reactive, toRaw } from 'vue';
import { getDataSearch, getEnvironmentAll } from '@/api/dispatch';
import { getServicesList } from '@/api/serverNode/index';
import { getFindAll } from '@/api/taskConfig';
import { setTreeData } from '@/utils/object';

export const useBpmnStore = defineStore('useBpmnStore', {
    state: () => {
        return {
            currentBpmnModeler: null,
            taskApiParams: {
                environment: 'Public', // 必须 - 任务的环境
                pageNo: 1, // 必须 - 页码
                pageSize: 15, // 必须 - 每页显示数量
                dispatchType: '', // 可选 - 速度的类型 cron\固定速度
                name: '', // 可选 - 任务名字
                jobType: '' // 可选 - 业务分类
            },
            executNode: [], // 执行节点
            taskResult: [], // 所有任务结果
            environmentResult: [], // 所有环境
            jobTypeResult: [], // 所有业务分类
            taskResultTotal: 0 // 所有任务总数
        };
    },
    getters: {
        getCurrentBpmnModeler() {
            return this.currentBpmnModeler;
        },
        getTaskApiParams() {
            return this.taskApiParams;
        },
        getTaskResult() {
            return this.taskResult;
        },
        getEnvironmentResult() {
            return this.environmentResult;
        },
        getJobTypeResult() {
            return this.jobTypeResult;
        },
        getExecutNode() {
            return this.executNode;
        }
    },
    actions: {
        setCurrentBpmnModeler(bpmnModeler: any) {
            this.currentBpmnModeler = bpmnModeler;
        },
        setTaskApiParams(params: Object = {}) {
            this.taskApiParams = { ...this.taskApiParams, ...params };
        },
        setSearchApiParams(params: any) {
            this.taskApiParams = params;
        },
        async setInitData() {
            try {
                await new Promise.all([
                    this.setExecutNode(),
                    this.setEnvironmentResult(),
                    this.setJobTypeResult(),
                    this.setSearchResult()
                ]);
            } catch (error) {
                return error;
            }
        },
        async setExecutNode() {
            try {
                let res = await getServicesList();
                this.executNode = res.data;
            } catch (error) {
                return error;
            }
        },
        async setEnvironmentResult() {
            try {
                let res = await getEnvironmentAll();
                this.environmentResult = res.data;
                this.taskResultTotal = res.data.total;
            } catch (error) {
                return error;
            }
        },
        async setJobTypeResult() {
            try {
                let res = await getFindAll();
                // 空选项
                let temp_1 = setTreeData([
                    {
                        createTime: '',
                        id: '',
                        name: '空选项',
                        parentId: '0',
                        updateTime: ''
                    }
                ]);
                let temp_2 = setTreeData(res.data);
                this.jobTypeResult = [...temp_1, ...temp_2];
            } catch (error) {
                return error;
            }
        },
        async setSearchResult() {
            // 获取所有任务
            let all = false,
                currentPage = 1;
            while (this.taskResult.length) {
                this.taskResult.pop();
            }
            while (!all) {
                try {
                    let res = await getDataSearch(this.taskApiParams);
                    // taskId 字符串类型，否则 el-select 会有回显的小 bug
                    res.data.content.map((item) => {
                        item.taskId = '' + item.id;
                        item.taskName = item.name;
                    });
                    this.taskResult = [...this.taskResult, ...res.data.content];
                    if (currentPage >= res.data.totalpages) {
                        all = true;
                    } else {
                        currentPage++;
                        this.taskApiParams.pageNo = currentPage;
                    }
                } catch (error) {
                    return error;
                }
            }
        }
    }
});

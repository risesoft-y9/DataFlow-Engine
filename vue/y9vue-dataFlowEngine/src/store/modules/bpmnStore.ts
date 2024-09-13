import { remove } from 'lodash';
import { defineStore } from 'pinia';
import { createNonNullChain, isJSDocNullableType } from 'typescript';
import { isReactive, reactive, toRaw } from 'vue';

export const useBpmnStore = defineStore('useBpmnStore', {
    state: () => {
        return {
            currentBpmnModeler: null
        };
    },
    getters: {
        getCurrentBpmnModeler() {
            return this.currentBpmnModeler;
        }
    },
    actions: {
        setCurrentBpmnModeler(bpmnModeler: any) {
            this.currentBpmnModeler = bpmnModeler;
        }
    }
});

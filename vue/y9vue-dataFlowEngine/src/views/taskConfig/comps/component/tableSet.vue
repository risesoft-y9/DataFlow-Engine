<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from "vue";

const emits = defineEmits(['next', 'top', 'goBack', 'goBackSet','setTask','close'])
//去除警告
const other=()=>{
  emits('next');
  emits('top');
  emits('goBack');
  emits('goBackSet');
  emits('setTask');
  emits('close');
}
import { globalData, goalTableForm, goalTableRef, tableSetForm, tableSetRef} from '../data'
import {getTableField} from "@/api/libraryTable";
import {getDataTableList} from "@/api/taskConfig";

const setInitData = () => {
  tableSetRef.value.resetFields()
  if(globalData.type=='add'){
    for (let key in tableSetForm.tableData) {
      if (key == 'sourceCloumn') {
        tableSetForm.tableData[key] = [];
      } else if (key == 'sourceName') {
        tableSetForm.tableData[key] = tableSetForm.tableData[key]
      } else if (key == 'bulkSync') {
        tableSetForm.tableData[key] = false
      } else {
        tableSetForm.tableData[key] = null;
      }
    }
  }else{

  }

}
const next = async () => {
  let validate = await tableSetRef.value.validate()
  if (validate) {
    emits('next');
  }
}
const top = () => {
  emits('top');
}
const state = reactive({})
onMounted(() => {
  setInitData()
})

const getTableFieldList = async () => {
  let params = {tableId: tableSetForm.tableData.sourceTable,};
  let res = await getTableField(params);
  if (res) {
    tableSetForm.tableFieldList = res.data
    tableSetForm.tableData.sourceCloumn = []//清空字段详情勾选
    res.data.forEach((item) => {
      item.label=`${item.name}:${item.cname}(${item.fieldType})`
      //设置勾选
      tableSetForm.tableData.sourceCloumn.push(item.id)
    })
  }
}
const filteredTableFieldList = computed(() => {
  return tableSetForm.tableFieldList.filter(item => {
    return tableSetForm.tableData.sourceCloumn.includes(item.id);
  });
})
const checkBoxChange = () => {
  let exist = tableSetForm.tableData.sourceCloumn.includes(tableSetForm.tableData.splitPk);
  if (!exist) {
    tableSetForm.tableData.splitPk = null
  }
  const fieldSet = new Set(tableSetForm.tableData.sourceCloumn);
  goalTableForm?.tableData?.differentField?.forEach((item, index) => {
    if (!fieldSet.has(item.source)) {
      item.source = null;
      item.rules2 = [];
      goalTableRef.value.clearValidate(`differentField[${index}].target`)
      goalTableRef.value.clearValidate(`differentField[${index}].source`)
    }
  });
}
//表筛选
const tableChange = () => {
  getTableFieldList()
}
const rules = {
  sourceTable: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  sourceName: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  tableNumber: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ],
  splitFactor: [
    {required: true, message: '请输入', trigger: ['blur', 'change']}
  ]
}
const radioChange = (e) => {
  if (e == 1) {
    tableSetForm.tableData.splitFactor = null
    tableSetForm.tableData.tableNumber = null
  } else if (e == 2) {
    tableSetForm.tableData.splitFactor = null
  } else {
    tableSetForm.tableData.tableNumber = null
  }
}
let ruleFormRef = ref(tableSetRef)
</script>
<template>
  <el-form
      ref="ruleFormRef"
      :model="tableSetForm.tableData"
      :rules="rules">

    <el-descriptions border :column="1">
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>表名称</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="sourceTable">
          <el-select
              @change="tableChange"
              v-model="tableSetForm.tableData.sourceTable"
              class="m-2"
              placeholder="请选择表"
              size="small"
          >
            <el-option
                v-for="item in tableSetForm.tableOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="字段详情">
        <template #label>
          <div>
            <span>字段详情</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item prop="sourceCloumn">
          <el-checkbox-group v-model="tableSetForm.tableData.sourceCloumn" class="field">
            <div v-for="item in tableSetForm.tableFieldList" class="type">
              <el-checkbox @change="checkBoxChange" v-model="item.id" :label="item.id">
                {{ item.name }}:{{ item.cname }}({{ item.fieldType }})
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="执行类">
        <template #label>
          <div>
            <span>执行类</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <el-form-item prop="sourceName">
          <el-select
              v-model="tableSetForm.tableData.sourceName"
              class="m-2"
              value-key="id"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in tableSetForm.classListOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="where语句">
        <template #label>
          <div>
            <span>where语句</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item prop="whereSql">
          <el-input placeholder="请输入" show-word-limit type="textarea"
                    v-model="tableSetForm.tableData.whereSql"></el-input>
        </el-form-item>
      </el-descriptions-item>


      <el-descriptions-item label-align="center" label="fetchSize">
        <template #label>
          <div>
            <span>fetchSize</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item>
              <el-input-number class="number-input" placeholder="请填写数字,不填取默认值" :controls="false"
                               v-model="tableSetForm.tableData.fetchSize"/>
            </el-form-item>
          </div>
          <div class="tips">jdbc查询时ResultSet的每次读取记录数</div>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" label="切分字段">
        <template #label>
          <div>
            <span>切分字段</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item prop="splitPk">
          <el-select
              clearable
              v-model="tableSetForm.tableData.splitPk"
              class="m-2"
              value-key="id"
              placeholder="请选择"
              size="small"
          >
            <el-option
                v-for="item in filteredTableFieldList"
                :key="item.id"
                :label="`${item.name}:${item.cname}(${item.fieldType})`"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-descriptions-item>
      <!--      执行数基数  切分数量-->
      <el-descriptions-item label-align="center">
        <template #label>
          <div>
            <span>切分模式</span>
            <!--            <span class="y9-required-icon">*</span>-->
          </div>
        </template>
        <el-form-item>
          <el-radio-group v-model="tableSetForm.tableData.radios" @change="radioChange">
            <el-radio :label="1">精准切分</el-radio>
            <el-radio :label="2">平均切分</el-radio>
            <el-radio :label="3">执行数切分</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" v-if="tableSetForm.tableData.radios==2">
        <template #label>
          <div>
            <span>切分数量</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item prop="tableNumber">
              <el-input-number class="number-input" placeholder="请输入" :controls="false"
                              v-model="tableSetForm.tableData.tableNumber"/>
            </el-form-item>
          </div>
          <div class="tips">根据表数据量按数量平均切分：表数据量/切分数量</div>
        </div>
      </el-descriptions-item>
      <el-descriptions-item label-align="center" v-if="tableSetForm.tableData.radios==3">
        <template #label>
          <div>
            <span>执行数基数</span>
            <span class="y9-required-icon">*</span>
          </div>
        </template>
        <div class="fetch">
          <div class="fetch-w">
            <el-form-item prop="splitFactor">
              <el-input-number class="number-input" placeholder="请输入" :controls="false"
                              v-model="tableSetForm.tableData.splitFactor"/>
            </el-form-item>
          </div>
          <div class="tips">根据线程池线程设置大小切分：表数据量/(线程数*执行数基数)</div>
        </div>
      </el-descriptions-item>
    </el-descriptions>
  </el-form>
  <div class="demo-collapse">
    <el-collapse accordion>
      <el-collapse-item>
        <template #title>
          <div class="advanced">
            <div class="title">高级设置</div>
          </div>
        </template>
        <div>
          <el-descriptions border :column="1">
            <el-descriptions-item label-align="center" label="数据脱敏">
              <template #label>
                <div>
                  <span>数据脱敏</span>
                </div>
              </template>
              <div class="desensitization">
                <div class="type">
                  <el-form-item prop="maskFields">
                    <el-input placeholder="请输入脱敏字段" v-model="tableSetForm.tableData.maskFields"></el-input>
                  </el-form-item>
                </div>
                <div class="tips">多个脱敏字段之间用英文逗号区分</div>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="数据脱敏">
              <template #label>
                <div>
                  <span>数据加密</span>
                </div>
              </template>
              <div class="desensitization">
                <div class="type">
                  <el-form-item prop="jiami">
                    <el-input placeholder="请输入加密字段" v-model="tableSetForm.tableData.encrypFields"></el-input>
                  </el-form-item>
                </div>
                <div class="tips">多个加密字段之间用英文逗号区分</div>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label-align="center" label="增量同步">
              <template #label>
                <div>
                  <span>增量同步</span>
                </div>
              </template>
              <el-form-item>
                <el-switch v-model="tableSetForm.tableData.bulkSync" inline-prompt
                           active-text="ON"
                           inactive-text="OFF"/>
              </el-form-item>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>

  <div class="btn">
    <el-button @click="top" class="global-btn-second">上一步</el-button>
    <el-button type="primary" @click="next" class="global-btn-main">下一步</el-button>
  </div>
</template>

<style scoped lang="scss">
//webkit内核浏览器 滚动条大小
::-webkit-scrollbar {
  width: 4px;
  height: 2px;
  background-color: transparent;
}

.field {
  display: flex;
  width: 100%;
  flex-wrap: wrap;
  max-height: 120px;
  overflow-y: scroll;
  overflow-x: hidden;

  .type {
    width: 50%;
  }
}

.fetch {
  display: flex;
  align-items: center;

  .fetch-w {
    width: 20%;
  }

  .tips {
    margin-left: 20px;
    margin-top: 5px;
    color: #999999;
  }
}

.desensitization {
  display: flex;
  align-items: center;

  .type {
    width: 50%;
  }

  .tips {
    margin-left: 20px;
    margin-top: 5px;
    color: #999999;
  }
}

.btn {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.demo-collapse {
  margin-top: 10px;

  .advanced {
    width: 100%;
    display: flex;
    background: #eee;

    .title {
      margin-left: 40px;
    }
  }

  :deep(.el-icon) {
    position: absolute;
    margin-left: 20px;
  }
}

.number-input {
  width: 100%;
}

.number-inputs {
  width: 20%;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left !important;
  margin-left: -3px;
}
</style>

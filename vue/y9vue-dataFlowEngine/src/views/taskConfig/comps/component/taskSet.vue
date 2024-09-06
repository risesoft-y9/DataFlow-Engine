<script setup lang="ts">
import {addTaskForm, globalData, goalTableForm, tableSetForm, taskSetForm} from "@/views/taskConfig/comps/data";
import {onMounted} from "vue";
import executor from './comp/executor.vue'
import exchange from './comp/exchange.vue'
import channel from './comp/channel.vue'
import plugs from './comp/plugs.vue'
import {saveTaskTable} from "@/api/taskConfig";

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

const next = () => {
  emits('next');
}
const save = async () => {
  taskSetForm.tableData.showSave=false

  let all = {...tableSetForm.tableData, ...goalTableForm.tableData, ...addTaskForm.tableData,}
  if (all.radios == 1) {
    all['precise'] = true
  }
  all.sourceCloumn = all.sourceCloumn?.toString()
  all.targetCloumn = all.targetCloumn?.toString()
  all.updateField = all.updateField?.toString()
  let data = {
    name: '',//任务名称
    description: '',//描述
    businessId: '',//业务id
    maskFields: '',//脱敏字段
    encrypFields: "",//加密字段
    format: '',//日期格式
    taskConfigModel: {
      sourceId: "",//源头表id
      sourceType: '',//类型
      sourceTable: '',//数据表id
      sourceCloumn: '',//字段列
      sourceName: '',//执行类
      fetchSize: "",//fetchSize
      whereSql: '',//where语句
      splitPk: "",//切分字段
      precise:false,//是否精准切分
      tableNumber: '',//平均切分数
      splitFactor: '',//执行数切分数

      targetId: "",//目标源id
      targetType: '',//类型
      targetTable: '',//目标数据表id

      updateField:'',// 输出类型为更新时的选项

      targetCloumn: '',//目标字段详情
      targeName: '',//目标执行类
      writerType: '',//输出类型
    },//配置信息
    differentField: [],//异字段
    convertField: [],//数据转换
    taskCoreList: [],
    dateField:[],//日期格式
  }
  for (const allKey in all) {
    for (const dataKey in data) {
      if (allKey == dataKey) {
        data[dataKey] = all[allKey]
      }
    }
    for (const configKey in data.taskConfigModel) {
      if (allKey == configKey) {
        data.taskConfigModel[configKey] = all[allKey]
      }
    }
  }
  let convertField = []
  let differentField = []
  let dateField=[]
  let taskCoreList = []//设置属性 任务设置
  data?.convertField?.forEach((item) => {
    if(item.fieldName&&item.oldData&&item.newData){
      let obj = {
        fieldName: item.fieldName,
        oldData: item.oldData,
        newData: item.newData
      }
      convertField.push(obj)
    }

  })
  data?.differentField?.forEach((item) => {
    if(item.source&&item.target){
      let obj = {
        source: item.source,
        target: item.target,
      }
      differentField.push(obj)
    }
  })
  data?.dateField?.forEach((item) => {
    if(item.fieldName&&item.format){
      let obj = {
        fieldName: item.fieldName,
        format: item.format,
      }
      dateField.push(obj)
    }
  })
  data.dateField = dateField
  data.convertField = convertField
  data.differentField = differentField
  setTaskCoreList(taskCoreList)
  data.taskCoreList = taskCoreList
  if(globalData.type=='edit'){
    data.id=globalData.allData.id
    data.taskConfigModel.id=globalData.allData.taskConfigModel.id
    data.taskConfigModel.taskId=globalData.allData.taskConfigModel.taskId
    data?.taskCoreList?.forEach((item)=>{
      globalData.allData?.taskCoreList.forEach((res)=>{
        if(item.argsId==res.argsId){
          // item.id=res.id
          item.taskId=res.taskId
        }
      })
    })
  }

  let res = await saveTaskTable(data)
  if (res) {
    if (res.success == true) {
      ElNotification({
        title: ('成功'),
        message: ('保存成功'),
        type: 'success',
        duration: 2000,
        offset: 80
      });
      globalData.saveForm=res.data
      addTaskForm.treeData.forEach((item)=>{
        if(item.id==res?.data?.businessId){
          globalData.saveForm['businessName']=item.name
        }
      })
      emits('close',1);//修改或者新增查询列表
      emits('next');
    }else{
      taskSetForm.tableData.showSave=true
      ElNotification({
        title: ('失败'),
        message: ('保存失败'),
        type: 'error',
        duration: 2000,
        offset: 80
      });
    }

  }
}

const top = () => {
  emits('top');
}
let showBtn=ref(false)
onMounted(() => {
  setInitData()
  setTimeout(()=>{
    showBtn.value=true
  },800)
})

const setInitData = () => {
  if (globalData.type == 'add') {
    for (let key in taskSetForm.tableData) {
      if (key == 'activeName') {
        taskSetForm.tableData[key] = '1';
      } else if (key == 'showTimedTasks') {
        taskSetForm.tableData[key] = false;
      } else if (key == 'showSave') {
        taskSetForm.tableData[key] = true;
      }
    }
  } else {
  }
}
const setTaskCoreList = (taskCoreList) => {
  let executorList=addSequence(taskSetForm.executorList)
  let exchangeList=addSequence(taskSetForm.exchangeList)
  let channelList=addSequence(taskSetForm.channelList)
  let plugsList=addSequence(taskSetForm.plugsList)
  setData(executorList, taskCoreList, 'executor')
  setData(exchangeList, taskCoreList, 'exchange')
  setData(channelList, taskCoreList, 'channel')
  setData(plugsList, taskCoreList, 'plugs')
}

//判断是否存在重复的插件
const addSequence = (data) => {
  // 用一个对象来存储每个title出现的次数
  let titleCount  = {};
  data.forEach((obj, index) => {
    if (!titleCount[obj.title]) {
      titleCount[obj.title] = 1;
      obj.sequence = 1;
      if(obj.children){
        obj.children.forEach((res)=>{
          res.sequence=obj.sequence
        })
      }
    } else {
      obj.sequence = ++titleCount[obj.title];
      if(obj.children){
        obj.children.forEach((res)=>{
          res.sequence=obj.sequence
        })
      }
    }
  });
  return data;

}
const setData = (form, taskCoreList, typeName) => {
  form.forEach((list) => {
    let findClass = list?.classList?.find((res) => {
      return res.className == list.className
    })
    if (findClass) { //查找执行类
      console.log(findClass,'findClass')
      let obj = {
        argsId:findClass.id,
        keyName: 'name',//执行类参数名称name
        value: findClass.className,//右侧的value
        dataType: findClass.funcType,//接口-根据类别获取页面信息里返回的funcType值
        typeName,//例：输入/输出线程池-executor，数据闸口-exchange，输入/输出通道-channel，其它插件-plugs
        sequence:list.sequence,
      }
      taskCoreList.push(obj)
      list?.children?.forEach((child) => {
        let obj = {
          argsId:child.id,
          keyName: child.name,//执行类参数名称name
          value: child.defaultValue,//右侧的value
          dataType: findClass.funcType,//接口-根据类别获取页面信息里返回的funcType值
          typeName,//例：输入/输出线程池-executor，数据闸口-exchange，输入/输出通道-channel，其它插件-plugs
          sequence:list.sequence,
        }
        taskCoreList.push(obj)
      })
    }
  })
}
</script>

<template>
  <el-tabs type="border-card" cllass="pane" v-model="taskSetForm.tableData.activeName">
    <el-tab-pane label="输入/输出线程池" name="1">
      <div class="pane">
        <executor></executor>
      </div>
    </el-tab-pane>
    <el-tab-pane label="数据闸口" name="2">
      <div class="pane">
        <exchange></exchange>
      </div>
    </el-tab-pane>
    <el-tab-pane label="输入、输出通道" name="3">
      <div class="pane">
        <channel></channel>
      </div>
    </el-tab-pane>
    <el-tab-pane label="其它插件" name="4">
      <div class="pane">
        <plugs></plugs>
      </div>
    </el-tab-pane>
  </el-tabs>
  <div class="btn" v-if="showBtn">
    <el-button @click="top" v-if="!taskSetForm.tableData.showTimedTasks" class="global-btn-second">上一步</el-button>
    <el-button type="primary" v-if="taskSetForm.tableData.showSave" @click="save" class="global-btn-main">保存
    </el-button>
    <el-button type="primary" v-else @click="next" class="global-btn-main">下一步</el-button>
    <!--    <el-button type="primary" @click="close" class="global-btn-main">关闭</el-button>-->
  </div>
</template>

<style scoped lang="scss">
.pane {
  min-height: 300px;
}

.btn {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
:deep(.el-input-number .el-input__inner) {
  text-align: center !important;
}
</style>

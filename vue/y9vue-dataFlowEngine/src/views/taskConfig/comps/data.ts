import {reactive, ref} from "vue";
import {getDataTableList} from "@/api/taskConfig";
import {getTableField, getApiField} from "@/api/libraryTable";

/**
 * 公共类数据
 */
export const globalData=reactive({
    type:'add',//新增还是修改
    row:{},//整行数据
    allData:{},
    saveForm:{},//保存成功后的数据
})
export const addTaskLoading=ref(false)
/**
 * 新建任务数据
 */
export let addTaskFormRef = ref()
export const addTaskForm = reactive({
    tableData: {
        name: null,//任务名称
        businessId:null,//业务分类id
        description: null,//任务描述
        sourceId: null,//源头id
        sourceType: null,//类型
        targetId: null,//目标id
        targetType: null,//类型
        userId:'',//可选用户id
    },
    treeData:[],//业务分类列表
})

/**
 * 源头表数据
 */
export let tableSetRef = ref()
export const tableSetForm = reactive({
    tableData: {
        sourceTable: null,//数据表id
        sourceCloumn: [],//字段详情
        sourceName: null,//执行类
        whereSql: null,//where语句
        fetchSize: null,
        splitPk: null,//切分字段
        radios: null,//切分模式
        maskFields: null,//数据脱敏字段
        encrypFields: null,//数据加密字段
        tableNumber:null,//平均切分数量
        splitFactor:null,//执行数切分基数
    },
    tableOptions: [],//表名称列表
    tableFieldList: [],//字段详情列表
    classListOptions: [],//执行类
    splitFieldsList:[],//切分字段下拉
})

/**
 * 目的表数据
 */
export let goalTableRef = ref()
export const goalTableForm = reactive({
    tableData: {
        targetCloumn:[],//字段详情
        targetTable: [],//目的数据表id
        targeName: null,//目标执行类
        writerType: 'insert',//输出类型
        differentField: [],//异字段同步
        convertField:[],//数据转换
        dateField:[],//日期转换
        updateField:'',//如果是更新需选择字段
    },
    tableOptions: [],//表名称列表
    tableFieldList: [],//字段详情列表
    classListOptions: [],//执行类
})

export const taskSetForm = reactive({
    tableData: {
        activeName: '1',
        showSave: true,//显示保存
    },
    executorList:[],//输入输出线程池数据
    exchangeList:[],//数据闸口数据
    channelList:[],//输入输出数据
    plugsList:[],//其它插件数据
})

/**
 * 获取表格和执行类
 */
export const getTableList = async (type) => {
    let sourceId
    type == 'input' ? sourceId = addTaskForm.tableData.sourceId : sourceId = addTaskForm.tableData.targetId
    let params = {
        sourceId,
        type
    }
    let res = await getDataTableList(params)
    if (res) {
        if (type == 'input') {
            updateForm(tableSetForm, res.data,'input');
        } else {
            updateForm(goalTableForm, res.data,'output');
        }
    }
}

//修改时获取表格列表
export const getTableListAll = async (sourceId,targetId) => {
    let res = await getDataTableList({type:'input',sourceId})
    let ret = await getDataTableList({type:'output',sourceId:targetId})
    if(res){
        updateForm(tableSetForm,res.data,'input')
    }
    if(ret){
        updateForm(goalTableForm,ret.data,'output')
    }
}
const updateForm = (form, data,type) => {
    form.tableOptions = data.tableList;
    form.classListOptions = data.classList;
    if(type=='input'){
        form.tableData.sourceName = data?.classList[0]?.id;
    }else{
        form.tableData.targeName = data?.classList[0]?.id;
    }
}

//获取字段详情
export const getTableFieldListAll = async (sourceTable,targetTable) => {
    let res;
    debugger
    if(addTaskForm.tableData.sourceType == 'api') {
        res = await getApiField({apiId: sourceTable});
    }else {
        res = await getTableField({tableId: sourceTable});
    }
    if (res) {
        tableSetForm.tableFieldList = res.data
    }
    let ret;
    if(addTaskForm.tableData.targetType == 'api') {
        ret = await getApiField({apiId: targetTable});
    }else {
        ret = await getTableField({tableId: targetTable});
    }
    if (ret) {
        goalTableForm.tableFieldList = ret.data
    }
}

/**
 * 选择数据源
 * @param type
 * @param baseType 
 */
export const radioChange = async (type, baseType) => {
    addTaskLoading.value = true
    tableSetRef?.value?.resetFields()
    goalTableRef?.value?.resetFields()
    if (type == 'input') {
        addTaskForm.tableData.sourceType = baseType;
        await clearFormData(tableSetForm, type);
    } else {
        addTaskForm.tableData.targetType = baseType;
        await clearFormData(goalTableForm, type);
    }
    await getTableList(type) //获取表格和执行类
    addTaskLoading.value = false
}

const clearFormData = (form,type) => {
    form.tableOptions = [];
    form.tableData.name = null; //清空列表
    form.classListOptions = [];

    form.tableFieldList = [];
    if(type=='input'){
        form.tableData.sourceCloumn = []; //清空源头字段详情
        form.tableData.sourceName = null;
        form.tableData.sourceTable = null;
        form.tableData.splitPk = null; //清除切分字段
        form.tableData.whereSql = null;
        // form.tableData?.differentField?.forEach((item)=>{
        //     item.source=null
        //     item.rules1 = [{required: true, message: '请选择', trigger: ['blur', 'change']}]
        // })
        // console.log('清空了',form.tableData.sourceName,form.tableData.sourceTable)
    }else{
        form.tableData.targetCloumn = []; //清空目标字段详情
        form.tableData.targeName = null;
        form.tableData.targetTable = null;
        // form.tableData?.differentField?.forEach((item)=>{
        //     item.target=null
        //     item.rules2 = [{required: true, message: '请选择', trigger: ['blur', 'change']}]
        // })
        // form.tableData?.convertField?.forEach((item)=>{
        //     console.log(item,333)
        //     item.fieldName=null
        //     item.rules1 = [{required: true, message: '请选择', trigger: ['blur', 'change']}]
        // })
    }

}


<script setup lang="ts">
    import { reactive, toRefs, onMounted, ref, inject, nextTick } from 'vue';
    import {
      deleteTableField,
      getFieldTypes,
      getTableField,
      saveTable,
      saveTableAndField,
      saveTableField
    } from '@/api/libraryTable';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { getPage } from '@/views/libraryTable/component/DataSheet/data';
    import { useI18n } from 'vue-i18n';
    import { findByTypeList } from '@/api/dataSource';
    import { v4 as uuidv4 } from 'uuid';

    const { t } = useI18n();
    const tableHeight = ref(280);
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        type: {
            type: String
        },
        row: {
            type: Object,
            default: {}
        }
    });
    const emits = defineEmits(['close']);
    const windowHeightChange = (tableHeight, height) => {
        state.tableConfig.height = 0;
        state.tableConfig.height = 280;
    };
    let notChinese = (rule, value, callback) => {
        if (value) {
            if (/[\u4E00-\u9FA5]/g.test(value)) {
                callback(new Error('不能输入汉字'));
            } else {
                callback();
            }
        }
        callback();
    };
    let showTable = ref(false);
    const getFieldTypesOptions=async ()=>{
      let res=await getFieldTypes({sourceId:props.row.baseId||props.row.id})
      if(res){
        state.fieldTypeOptions=res.data
      }
    }

    // 初始化列表 请求
    onMounted(() => {
      getFieldTypesOptions()
        formData.value.rules = {
            name: [
                { required: true, message: '请输入数据表名称', trigger: ['blur', 'change'] },
                { validator: notChinese, trigger: 'blur' }
            ],
            cname: [{ required: true, message: '请输入表中文名称', trigger: ['blur', 'change'] }]
        };

        formData.value.itemList.forEach((item) => {
            if (item.label == '数据表名称' || item.label == '表中文名称') {
                item.type = item.type1;
            }
        });
        if (props.type == 'edit') {
            getPageList();
            showTable.value = true;
            formData.value.model.name = props.row?.name;
            formData.value.model.cname = props.row?.cname;
        }
    });

    const change = (e) => {
        console.log(e, '修改');
    };
    const getPageList = async () => {
        let params = {
            tableId: props.row.id,
            name: state.input
        };
        const res = await getTableField(params);
        if (res) {
            let row = res.data;
            row.forEach((item) => {
                item.edit = false;
            });
            form.value.tableData = row;
        }
    };
    let y9VxeTableRef = ref();
    const state = reactive({
        loading: false,
        input: '', //搜索
        tableData: [],
        form: {
            tableId: null
        },
        // 过滤 数值
        currSearchValue: {},
        options: [
            {
                value: 'YES',
                label: 'YES'
            },
            {
                value: 'NO',
                label: 'NO'
            }
        ],
        keyOptions: [
            {
                value: 'Y',
                label: 'Y'
            },
            {
                value: 'N',
                label: 'N'
            }
        ],
      fieldTypeOptions:[],//字段类型

        //  表格的过滤 条件
        filterConfig: {
            filtersValueCallBack: (filters) => {
                //过滤值回调
                state.currSearchValue = filters;
            },
            fangDouTime: 0, //防抖时间
            itemList: [
                {
                    type: 'slot',
                    slotName: 'bnts',
                    span: 12
                },
                {
                    type: 'slot',
                    slotName: 'add',
                    span: 12
                }
            ],
            showBorder: true
        },
        // 个人权限 表格的 配置条件
        tableConfig: {
            // scrollY: {
            //   //竖向滚动配置
            //   enabled: false, //开启虚拟滚动
            //   gt: 0, //虚拟显示条数
            // },
            height: 280,
            maxHeight: 280,
            openAutoComputedHeight: false,
            pageConfig: false, //无分页
            rowConfig: {
                height: 70
            }
        }
    });
    const tableData: any = ref([]);
    const form = ref({
        tableData: [],
        allTableData: [], //所有数据
        rules: {
            name: [{ required: true, message: '', trigger: ['blur', 'change'] }],
            cname: [{ required: true, message: '', trigger: ['blur', 'change'] }],
            fieldType: [{ required: true, message: '', trigger: ['blur', 'change'] }],
            //fieldLength: [{ required: true, message: '', trigger: ['blur', 'change'] }],
            fieldNull: [{ required: true, message: '', trigger: ['blur', 'change'] }],
            fieldPk: [{ required: true, message: '', trigger: ['blur', 'change'] }]
        }
    });
    let y9FormRef = ref();
    let formRef = ref();
    const formData = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 2, //两列模式
            labelAlign: 'center',
            labelWidth: '200px'
            // contentWidth: '200px'
        },
        model: {
            //表单对象
            name: '',
            sex: '',
            zhiwu: ''
        },
        rules: {}, //表单校验规则
        itemList: [

            {
                type: 'text',
                type1: 'input',
                type2: 'text',
                prop: 'baseName',
                label: '数据源名称',
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.row?.baseName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input',
                type2: 'text',
                prop: 'baseType',
                label: '数据源类型',
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.row?.baseType);
                    }
                }
            },
          {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'name',
            label: '数据表名称',
            props: {
              events: {
                blur: (e) => {
                  if (props.type == 'edit') {
                    // editSaveTable();
                  }
                }, //当选择器的输入框失去焦点时触发。
                change:(e)=>{
                  if(props.row.baseId){
                    editSaveTable();
                  }

                  // console.log('修改了')
                }
              },
              render: () => {
                return h('span', props.row?.name);
                //text类型渲染的内容
              }
            }
          },
          {
            type: 'text',
            type1: 'input',
            type2: 'text',
            prop: 'cname',
            label: '表中文名称',
            props: {
              events: {
                blur: (e) => {
                  if (props.type == 'edit') {
                    // editSaveTable();
                  }
                },
                change:(e)=>{
                  if(props.row.baseId){
                    editSaveTable();
                  }
                  // console.log('修改了')
                }
              },
              render: () => {
                return h('span', props.row?.cname);
                //text类型渲染的内容
              }
            }
          },
        ]
    });

    let inputRef = ref();
    const handle = async (row, type) => {
        if (type == 1) {
            const valid = await formRef.value.validate();
            if (!valid) {
                ElNotification({
                    title: '失败',
                    message: '请输入完整的字段信息',
                    type: 'error',
                    duration: 2000,
                    offset: 80
                });
                return;
            }
            let params = {
                name: row.name,
                cname: row.cname,
                tableId: row.tableId,
                fieldType: row.fieldType,
                fieldLength: row.fieldLength,
                fieldNull: row.fieldNull,
                fieldPk: row.fieldPk,
                typeNum:row.typeNum
            };
            if (row.id) {
             params.id = row.id
          }
            let res = await saveTableField(params);
            if (res) {
                if (res.success == true) {
                    let integrity = true;
                    for (const re in params) {
                        if (re != 'id' && re != 'fieldLength') {
                            if (!params[re]) {
                                ElNotification({
                                    title: '失败',
                                    message: '请输入完整的字段信息',
                                    type: 'error',
                                    duration: 2000,
                                    offset: 80
                                });
                                row.edit = true;
                                integrity = false;
                                break;
                            }
                        }
                    }
                    if (integrity) {
                        row.edit = false;
                        ElNotification({
                            title: '成功',
                            message: '保存成功',
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                    }
                }
            }
        } else if (type == 2) {
            row.edit = true;
            nextTick(() => {
                inputRef.value?.focus();
            });
        } else if (type == 4) {
            //新增删除
            form.value.tableData.forEach((item, i) => {
                if (item.id == row.id) {
                    form.value.tableData.splice(i, 1);
                }
            });
        } else {
            //修改删除
            if (row.type == '新增') {
                form.value.tableData.forEach((item, i) => {
                  console.log(item,row)
                    if (item.ids == row.ids) {
                        form.value.tableData.splice(i, 1);
                    }
                });
            } else {
                ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 字段?`, t('提示'), {
                    confirmButtonText: t('确定'),
                    cancelButtonText: t('取消'),
                    type: 'info'
                })
                    .then(async () => {
                        let res = await deleteTableField({ id: row.id });
                        if (res.success == true) {
                            form.value.tableData.forEach((item, i) => {
                                if (item.id == row.id) {
                                    form.value.tableData.splice(i, 1);
                                }
                            });
                            ElNotification({
                                title: res?.success ? t('删除成功') : t('删除失败'),
                                message: res?.msg,
                                type: res?.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                        }
                    })
                    .catch(() => {
                        ElMessage({
                            type: 'info',
                            message: t('已取消删除'),
                            offset: 65
                        });
                    });
            }
        }
    };
    //加字段
    let table = ref();
    const addRow = () => {
        let id = uuidv4();
        let row = {
            edit: true,
            fieldNull: '',
            name: '',
            cname: '',
            ids:id,
            fieldType: '',
            fieldLength: null,
            fieldPk: '',
            tableId: props.row.id,
            type: '新增'
        };

        form.value.tableData.push(row);
        handle(row, 2);
        nextTick(() => {
            inputRef.value?.focus();
        });
        nextTick(() => {
            setTimeout(() => {
                table.value.scrollTo(0, 30000);
            }, 10)
        });
    };
    const close = () => {
        emits('close');
    };
    const editSaveTable = async () => {
        const valid = y9FormRef?.value.elFormRef;
        if (!valid) return;
        valid.validate(async (valid, fields) => {
            if (valid) {
                let params = {
                    name: y9FormRef?.value.model.name,
                    cname: y9FormRef?.value.model.cname,
                    baseId: props.row.baseId,
                    id: props.row.id
                };
                let res = await saveTable(params);
                if (res) {
                    if (res.success == true) {
                        ElNotification({
                            title: t('成功'),
                            message: t('修改成功'),
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        getPage();
                    } else {
                        ElNotification({
                            title: t('失败'),
                            message: res.msg,
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                    }
                }
            }
        });
    };
    const saveY9Table = async () => {
        const valid = y9FormRef?.value.elFormRef;
        if (!valid) return;
        valid.validate(async (valid, fields) => {
            if (valid) {
                let params = {
                    name: y9FormRef?.value.model.name,
                    cname: y9FormRef?.value.model.cname,
                    baseId: props.row.id //新增时没有表id tableId
                };
                let res = await saveTable(params);
                if (res) {
                    if (res.success == true) {
                        ElNotification({
                            title: t('成功'),
                            message: t('添加成功'),
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        formData.value.itemList.forEach((item) => {
                            if (item.label == '数据表名称' || item.label == '表中文名称') {
                                item.type = item.type2;
                            }
                        });
                        props.row.name = res.data.name;
                        props.row.cname = res.data.cname;
                        props.row.tableId = res.data.id;
                        state.form.tableId = res.data.id;
                        showTable.value = true;
                    } else {
                        ElNotification({
                            title: t('失败'),
                            message: res.msg,
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                    }
                }
            }
        });
    };
    const saveTableList = async () => {
        const valid = await formRef.value.validate();
        if (valid) {
            if (form.value.tableData.length > 0) {
                let arr = [];
                form.value.tableData.forEach((item) => {
                    let obj = {
                        name: item.name,
                        cname: item.cname,
                        tableId: props.row.tableId,
                        fieldType: item.fieldType,
                        fieldLength: item.fieldLength,
                        fieldNull: item.fieldNull,
                        fieldPk: item.fieldPk,
                        typeNum:item.typeNum
                    };
                    arr.push(obj);
                });
                let res = await saveTableAndField(arr);
                if (res) {
                    if (res.success == true) {
                        ElNotification({
                            title: t('成功'),
                            message: t('保存成功'),
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        emits('close');
                    } else {
                        ElNotification({
                            title: t('失败'),
                            message: res.msg,
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                    }
                }
            } else {
                ElNotification({
                    title: t('成功'),
                    message: t('保存成功'),
                    type: 'success',
                    duration: 2000,
                    offset: 80
                });
                emits('close');
            }
            getPage();
        }
    };
    //字段过滤
    const handleClickQuery = () => {
        getPageList();
    };
    const optionChange=(e)=>{
      let typeNum=e.fieldType
      const foundType = state.fieldTypeOptions.find(type => type.typeName === e.fieldType);
      if (foundType) {
        e.typeNum = foundType.typeNum;
      }
    }
</script>

<template>
    <y9Form ref="y9FormRef" :config="formData"></y9Form>
    <div
        slot="footer"
        class="dialog-footer"
        style="text-align: center; margin-top: 25px"
        v-if="props.type == 'add' && !showTable"
    >
        <el-button type="primary" @click="saveY9Table"><i class="ri-save-line"></i><span>保存</span></el-button>
        <el-button type="primary" @click="close"><i class="ri-close-line"></i><span>关闭</span></el-button>
    </div>
    <div class="table-top"></div>
    <div v-if="showTable">
        <el-divider>表字段</el-divider>
        <div class="search">
            <div class="search-type" v-if="type == 'edit'">
                <div class="label">搜索</div>
                <div>
                    <el-input
                        @change="change"
                        placeholder="字段名称"
                        autofocus
                        class="input"
                        v-model="state.input"
                    ></el-input>
                </div>
                <div style="margin-left: 10px">
                    <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
                        ><i class="ri-search-2-line"></i>{{ $t('搜索') }}
                    </el-button>
                </div>
            </div>
            <div v-else></div>
            <div>
                <div>
                    <el-button
                        @click="addRow"
                        type="primary"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                    >
                        <span> + 字段</span>
                    </el-button>
                </div>
            </div>
        </div>
        <el-form :model="form" ref="formRef" :rules="form.rules">
            <el-table ref="table" maxHeight="330" border :data="form.tableData">
                <el-table-column type="index" label="序号" width="60">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div style="text-align: center"> {{ scope.$index + 1 }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="字段名称">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div v-if="scope.row.edit" class="text-center">
                            <el-form-item :prop="'tableData.' + scope.$index + '.name'" :rules="form.rules.name">
                                <el-input   placeholder="请输入" v-model="scope.row.name"></el-input>
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">{{ scope.row.name }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="cname" label="字段中文名">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div v-if="scope.row.edit" class="text-center">
                            <el-form-item :prop="'tableData.' + scope.$index + '.cname'" :rules="form.rules.cname">
                                <el-input  placeholder="请输入"  v-model="scope.row.cname"></el-input>
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">{{ scope.row.cname }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="fieldType" label="字段类型" width="150">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div v-if="scope.row.edit" class="text-center">
                            <el-form-item
                                :prop="'tableData.' + scope.$index + '.fieldType'"
                                :rules="form.rules.fieldType"
                            >
                              <el-select
                                  placeholder="请选择"
                                  :size="fontSizeObj.buttonSize"
                                  v-model="scope.row.fieldType"
                                  class="m-2"
                                  size="large"
                                  @change="optionChange(scope.row)"
                              >
                                <el-option
                                    v-for="(item,index) in state.fieldTypeOptions"
                                    :key="item.typeName"
                                    :label="item.typeName"
                                    :value="item.typeName"

                                />
                              </el-select>
<!--                                <el-input v-model="scope.row.fieldType"></el-input>-->
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">
                            {{ scope.row.fieldType }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="fieldLength" label="字段长度" width="100">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope" class="text-center">
                        <div v-if="scope.row.edit">
                            <el-form-item
                                :prop="'tableData.' + scope.$index + '.fieldLength'"
                                :rules="form.rules.fieldLength"
                            >
                                <el-input-number
                                    placeholder="请输入"
                                    style="width: 80px"
                                    :controls="false"
                                    v-model="scope.row.fieldLength"
                                    :min="0"
                                    :max="100000"
                                    :size="fontSizeObj.buttonSize"
                                />
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">{{ scope.row.fieldLength }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="fieldNull" label="能否为空" width="120">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div v-if="scope.row.edit" class="text-center">
                            <el-form-item
                                :prop="'tableData.' + scope.$index + '.fieldNull'"
                                :rules="form.rules.fieldNull"
                            >
                                <el-select
                                    :size="fontSizeObj.buttonSize"
                                    v-model="scope.row.fieldNull"
                                    class="m-2"
                                    size="large"
                                >
                                    <el-option
                                        v-for="item in state.options"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">
                            {{ scope.row.fieldNull }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="fieldPk" label="是否为主键" width="120">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope" class="text-center">
                        <div v-if="scope.row.edit">
                            <el-form-item :prop="'tableData.' + scope.$index + '.fieldPk'" :rules="form.rules.fieldPk">
                                <el-select
                                    :size="fontSizeObj.buttonSize"
                                    v-model="scope.row.fieldPk"
                                    class="m-2"
                                    size="large"
                                >
                                    <el-option
                                        v-for="item in state.keyOptions"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    />
                                </el-select>
                            </el-form-item>
                        </div>
                        <div v-else class="text-center">{{ scope.row.fieldPk }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="70">
                    <template #header="{ column }">
                        <div style="text-align: center">{{ column.label }}</div>
                    </template>
                    <template v-slot="scope">
                        <div class="operation" v-if="props.type == 'edit'">
                            <div class="fields">
                                <i class="ri-save-line" v-if="scope.row.edit" @click="handle(scope.row, 1)"></i>
                                <i class="ri-edit-line" v-else @click="handle(scope.row, 2)"></i>
                                <i class="ri-edit-line" v-else @click="handle(scope.row, 2)"></i>
                            </div>
                            <div class="delete" @click="handle(scope.row, 3)">
                                <i class="ri-delete-bin-line"></i>
                            </div>
                        </div>
                        <div v-else style="cursor: pointer; text-align: center">
                            <i class="ri-delete-bin-line" @click="handle(scope.row, 4)"></i>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-form>
        <div
            slot="footer"
            class="dialog-footer"
            style="text-align: center; margin-top: 25px"
            v-if="props.type == 'add' && showTable"
        >
            <el-button type="primary" @click="saveTableList"><i class="ri-save-line"></i><span>保存</span></el-button>
            <el-button type="primary" @click="close"><i class="ri-close-line"></i><span>关闭</span></el-button>
        </div>
    </div>
</template>

<style scoped lang="scss">
    @import '@/theme/global.scss';
    .operation {
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;

        .fields {
            display: flex;
            align-items: center;
            margin-right: 10px;

            i {
                color: var(--el-color-primary);
                margin-right: 2px;
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }

        .delete {
            display: flex;
            align-items: center;

            i {
                color: var(--el-color-primary);
                margin-right: 2px;
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }

    .table-top {
        height: 3px;
    }

    .search {
        display: flex;
        margin-bottom: 6px;
        justify-content: space-between;
        align-items: center;

        .search-type {
            display: flex;
            align-items: center;
        }

        .label {
            margin-right: 10px;
            color: #606266;
            font-size: var(--f36f60c5-fontSizeObj\.baseFontSize\?fontSizeObj\.baseFontSize\:\'inherit\');
            display: flex;
            align-items: cente;
        }

        //.input {
        //  border-radius: 30px;
        //  box-shadow: 0 2px 4px 0 rgb(0 0 0 / 5%);
        //  border: 1px solid var(--el-color-primary-light-7);
        //  font-size: v-bind("fontSizeObj.baseFontSize?fontSizeObj.baseFontSize:'inherit'");
        //}

        .el-input {
            color: var(--el-text-color-regular);
            font-size: v-bind("fontSizeObj.baseFontSize?fontSizeObj.baseFontSize:'inherit'");
        }
    }

    :deep(.vxe-table--body-wrapper .body--wrapper) {
        max-height: 200px;
    }

    .text-center {
        text-align: center;
    }
</style>

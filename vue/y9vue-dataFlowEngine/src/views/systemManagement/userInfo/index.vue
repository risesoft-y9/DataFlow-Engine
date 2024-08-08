<template>
  <y9Table
      :config="tableConfig"
      :filterConfig="filterConfig"
      uniqueIdent="userInfo"
      @on-curr-page-change="onCurrentChange"
      @on-page-size-change="onPageSizeChange"
  >
    <template #queryFun>
      <el-button type="primary" @click="handleClickQuery" class="global-btn-main"
      ><i class="ri-search-2-line"></i>{{ $t('搜索') }}</el-button
      >
      <div class="right-btn">
        <el-button type="primary" class="global-btn-main" @click="handleAddUser"
        ><i class="ri-add-line"></i>{{ $t('新增') }}</el-button
        >
      </div>
    </template>
  </y9Table>
  <!-- 表单 -->
  <y9Dialog v-model:config="dialogConfig">
    <y9Form
        v-if="dialogConfig.type == 'updatePassword'"
        :config="formPasswordConfig"
        ref="y9PasswordFormRef"
    ></y9Form>
    <y9Form v-else :config="formConfig" ref="y9TreeFormRef"></y9Form>
  </y9Dialog>
  <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { getStoragePageSize } from '@/utils/index';
import {
  getUserList,
  addNewUser,
  deleteUserAccount,
  updateAccountPassword,
  updateUserInfo
} from '@/api/systemManagement/userInfo';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 搜索的filter条件变量
let filterData = ref({});

let loading = ref(false);

// 表格列表配置
let tableConfig = ref({
  loading: false,
  border: false,
  headerBackground: true,
  columns: [
    {
      type: 'index',
      title: computed(() => t('序号')),
      showOverflowTooltip: false
    },
    {
      title: computed(() => t('账号')),
      key: 'account',
      showOverflowTooltip: false
    },
    {
      title: computed(() => t('用户名')),
      key: 'userName',
      showOverflowTooltip: false
    },
    {
      title: computed(() => t('操作')),
      fixed: 'right',
      render: (row) => {
        return [
          h(
              'span',
              {
                onclick: async () => {
                  formPasswordConfig.value.model = { id: row.id, password: '' };
                  Object.assign(dialogConfig.value, {
                    show: true,
                    title: computed(() => t('修改密码')),
                    type: 'updatePassword'
                  });
                }
              },
              t('修改密码')
          ),
          h(
              'span',
              {
                onclick: async () => {
                  formConfig.value.model = {
                    userName: row.userName,
                    account: row.account,
                    id: row.id,
                    password: ''
                  };
                  formConfig.value.itemList = editUserForm;
                  Object.assign(dialogConfig.value, {
                    show: true,
                    title: computed(() => t('编辑用户信息')),
                    type: 'editUserInfo'
                  });
                },
                style: {
                  margin: '0 10px'
                }
              },
              t('编辑')
          ),
          h(
              'span',
              {
                onclick: () => {
                  ElMessageBox.confirm(`${t('是否确定删除该节点')} ?`, t('提示'), {
                    confirmButtonText: t('确定'),
                    cancelButtonText: t('取消'),
                    type: 'info'
                  })
                      .then(async () => {
                        loading.value = true;
                        let result = await deleteUserAccount({ id: row.id });
                        if (result.code == 0) {
                          initTableData();
                        }
                        loading.value = false;
                        ElNotification({
                          title: result.success ? t('删除成功') : t('删除失败'),
                          message: result.msg,
                          type: result.success ? 'success' : 'error',
                          duration: 2000,
                          offset: 80
                        });
                      })
                      .catch(() => {
                        ElMessage({
                          type: 'info',
                          message: t('已取消删除'),
                          offset: 65
                        });
                      });
                }
              },
              t('删除')
          )
        ];
      }
    }
  ],
  tableData: [],
  pageConfig: {
    currentPage: 1,
    pageSize: getStoragePageSize('userInfo', 10),
    total: 0
  }
});

// 表格条件筛选配置
let filterConfig = ref({
  itemList: [
    {
      type: 'input',
      key: 'account',
      span: 6,
      label: computed(() => t('账号'))
    },
    {
      type: 'input',
      value: '',
      key: 'userName',
      label: computed(() => t('用户名')),
      span: 6
    },
    {
      type: 'slot',
      slotName: 'queryFun',
      span: 12
    }
  ],
  filtersValueCallBack: (filters) => {
    console.log('过滤值', filters);
    filterData.value = filters;
  }
});

// 表单配置
const y9TreeFormRef = ref(null);
const y9PasswordFormRef = ref(null);
const dialogConfig = ref({
  show: false,
  title: '',
  type: '',
  width: '42%',
  onOk: (newConfig) => {
    return new Promise(async (resolve, reject) => {
      if (newConfig.value.type == 'addUser' || newConfig.value.type == 'editUserInfo') {
        let valid = await y9TreeFormRef.value?.elFormRef?.validate((valid) => valid); //获取表单验证结果
        if (!valid) {
          reject();
          return;
        }
        let result;

        if (newConfig.value.type == 'addUser') {
          // 接口参数
          let params = {
            userName: y9TreeFormRef.value?.model.userName,
            account: y9TreeFormRef.value?.model.account,
            password: btoa(y9TreeFormRef.value?.model.password)
          };

          console.log(params, 'params');

          // 进行接口操作;
          try {
            result = await addNewUser(params);
          } catch (err) {
            resolve(err);
          }
        } else {
          // 接口参数
          let params = {
            userName: y9TreeFormRef.value?.model.userName,
            account: y9TreeFormRef.value?.model.account,
            id: y9TreeFormRef.value?.model.id
          };

          // console.log(params, 'params');

          // 进行接口操作;
          try {
            result = await updateUserInfo(params);
          } catch (err) {
            resolve(err);
          }
        }
        if (result?.code == 0) {
          // 请求接口
          initTableData();
        }
        ElNotification({
          title: result.success ? t('成功') : t('失败'),
          message: result.msg,
          type: result.success ? 'success' : 'error',
          duration: 2000,
          offset: 80
        });
        resolve();
      } else if (newConfig.value.type === 'updatePassword') {
        let valid = await y9PasswordFormRef.value?.elFormRef?.validate((valid) => valid); //获取表单验证结果
        if (!valid) {
          reject();
          return;
        }
        // 接口参数
        let params = {
          id: y9PasswordFormRef.value?.model.id,
          password: btoa(y9PasswordFormRef.value?.model.password)
        };

        // console.log(params, 'params');
        // 进行接口操作;
        try {
          let result = await updateAccountPassword(params);
          if (result.code == 0) {
            // 请求接口
            initTableData();
          }
          ElNotification({
            title: result.success ? t('修改密码成功') : t('修改密码失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
          });
          resolve();
        } catch (err) {
          reject(err);
        }
      }
    });
  }
});

let createUserForm = [
  //表单显示列表
  {
    type: 'input',
    prop: 'userName',
    label: computed(() => t('用户名')),
    required: true
  },
  {
    type: 'input',
    prop: 'account',
    label: computed(() => t('账号')),
    required: true
  },
  {
    type: 'input',
    prop: 'password',
    label: computed(() => t('密码')),
    required: true,
    props: {
      type: 'password',
      showPassword: true
    }
  }
];

let editUserForm = [
  //表单显示列表
  {
    type: 'input',
    prop: 'userName',
    label: computed(() => t('用户名')),
    required: true
  },
  {
    type: 'input',
    prop: 'account',
    label: computed(() => t('账号')),
    required: true
  }
];

const formConfig = ref({
  descriptionsFormConfig: {
    //描述表单配置
    column: 1, //1列模式
    labelAlign: 'center',
    labelWidth: '150px'
  },
  model: {
    //表单属性
    userName: '',
    account: '',
    password: '',
    id: ''
  },
  rules: {
    //表单验证规则
    userName: { required: true, message: computed(() => t('请输入用户名')), trigger: 'blur' },
    account: { required: true, message: computed(() => t('请输入账号')), trigger: 'blur' },
    password: { required: true, message: computed(() => t('请输入密码')), trigger: 'blur' }
  },
  itemList: createUserForm
});

const formPasswordConfig = ref({
  descriptionsFormConfig: {
    //描述表单配置
    column: 1, //1列模式
    labelAlign: 'center',
    labelWidth: '150px'
  },
  model: {
    //表单属性
    password: '',
    id: ''
  },
  rules: {
    //表单验证规则
    password: { required: true, message: computed(() => t('请输入密码')), trigger: 'blur' }
  },
  itemList: [
    {
      type: 'input',
      prop: 'password',
      label: computed(() => t('新密码')),
      required: true,
      props: {
        type: 'password',
        showPassword: true
      }
    }
  ]
});

onMounted(() => {
  initTableData();
});

// init 数据
async function initTableData() {
  tableConfig.value.loading = true;
  // 接口参数
  let params = {
    pageNo: tableConfig.value.pageConfig.currentPage,
    pageSize: tableConfig.value.pageConfig.pageSize,
    ...filterData.value
  };

  // 请求接口
  let result = await getUserList(params);

  if (result.code == 0) {
    // 对返回的接口数据进行赋值与处理
    tableConfig.value.tableData = result.data.content;
    tableConfig.value.pageConfig.total = result.data.total;
  }

  tableConfig.value.loading = false;
}

// 搜索
function handleClickQuery() {
  tableConfig.value.pageConfig.currentPage = 1;
  initTableData();
}

// 增加
function handleAddUser() {
  formConfig.value.model = { userName: '', account: '', password: '', id: '' };
  formConfig.value.itemList = createUserForm;
  Object.assign(dialogConfig.value, {
    show: true,
    title: computed(() => t('创建新用户')),
    type: 'addUser'
  });
}

// 分页操作
function onCurrentChange(currPage) {
  tableConfig.value.pageConfig.currentPage = currPage;
  initTableData();
}

function onPageSizeChange(pageSize) {
  tableConfig.value.pageConfig.pageSize = pageSize;
  initTableData();
}
</script>

<style lang="scss" scoped>
@import '@/theme/global.scss';
.right-btn {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>
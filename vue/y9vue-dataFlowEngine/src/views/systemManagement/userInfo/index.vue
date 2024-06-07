<template>
  <el-dialog
      v-model="state.newPasswordDialogVisible"
      title="修改密码"
      width="600"
  >
    <el-form ref="newRuleFormRef" :model="state.newPassword" :rules="state.newRules" label-width="auto">
      <el-form-item label="新密码" prop="password">
        <el-input v-model="state.newPassword.password"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.newPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addNewPassword">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
      v-model="state.dialogVisible"
      :title="state.type=='add'?'新增用户':'修改用户'"
      width="600"
  >
    <el-form ref="ruleFormRef" :model="state.form" :rules="state.rules" label-width="auto">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="state.form.userName"/>
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input v-model="state.form.account"/>
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="state.type=='add'">
        <el-input v-model="state.form.password"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addUser">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>

  <div class="main">
    <div class="search">
      <div class="left-btn">
        <div class="search-btn">
          <div class="label">账号</div>
          <div>
            <el-input v-model="state.search.account" style="width: 240px" placeholder="请输入账号"/>
          </div>
        </div>
        <div class="search-btn ml25">
          <div class="label">用户名</div>
          <div>
            <el-input v-model="state.search.userName" style="width: 240px" placeholder="请输入用户名"/>
          </div>
        </div>
        <div class="search-btn ml25">
          <el-button type="primary" :icon="Search">搜索</el-button>
        </div>
      </div>
      <div>
        <el-button type="primary" :icon="Plus" @click="addForm">新增</el-button>
      </div>
    </div>
    <div class="table">
      <el-table :data="table.tableData">
        <el-table-column type="index" width="180" align="center" label="序号"/>
        <el-table-column prop="account" align="center" label="账号"/>
        <el-table-column prop="userName" align="center" label="用户名"/>
        <el-table-column prop="address" align="center" label="操作">
          <template #default="scope">
            <div class="operation">
              <div class="ml10" @click="handle(1,scope.row)">修改密码</div>
              <div class="ml10" @click="handle(2,scope.row)">编辑</div>
              <div class="ml10" @click="handle(3,scope.row)">删除</div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {Plus, Search} from "@element-plus/icons";
import {computed, onMounted, reactive} from "vue";
import {getData} from "@/views/home/comp/dispatchingChart/data";
import {getUserList} from "@/api/systemManagement/userInfo";

let ruleFormRef = ref() //表单ref
let newRuleFormRef = ref() //新密码ref

/**
 * 表单数据
 */

const state = reactive({
  search: {
    userName: '',
    account: '',
    pageNo: 1,
    pageSize: 10,
    total: 0
  },
  type: 'add',//add新增 edit修改
  dialogVisible: false,
  newPasswordDialogVisible: false,//新密码弹框 也可以合并到一起 新手建议分开写
  form: {
    userName: '',
    account: '',
    password: '',
  },
  newPassword: {
    password: '',
  },
  newRules: {
    password: {required: true, message: '请输入密码', trigger: ['blur', 'change']}
  },
  rules: {
    //表单验证规则
    userName: {required: true, message: '请输入用户名', trigger: ['blur', 'change']},
    account: {required: true, message: '请输入账号', trigger: ['blur', 'change']},
    password: {required: true, message: '请输入密码', trigger: ['blur', 'change']}
  },
})
/**
 * 表格数据
 */
const table = reactive({
  tableData: []
})

onMounted(() => {
  getTable()
})

/**
 * 获取表格数据
 */
const getTable = async () => {
  let params = {
    pageNo: state.search.pageNo,
    pageSize: state.search.pageSize,
    userName: state.search.userName,
    account: state.search.account,
  };
  // 请求接口
  try {
    let res = await getUserList(params);
    if (res) {
      if (res.code == 0) {
        // 对返回的接口数据进行赋值与处理
        table.tableData = res.data.content;
        state.search.total = res.data.total;
      } else {
        ElMessage.error(res.msg)
      }
    }
  } catch (e) {
    ElMessage.error('错误')
  }
}

/**
 * 点击新增用户
 */
const addForm = () => {
  state.type = 'add'
  state.dialogVisible = true
  ruleFormRef.value?.resetFields() //格式化表单数据
}

/**
 * 新增用户确认按钮
 */
const addUser = () => {
  if (!ruleFormRef.value) return
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      if (state.type == 'add') { //新增用户
        //todo 验证通过
        //todo 调接口
        ElMessage.success('添加成功')
        //todo state.dialogVisible=false //关闭窗口
        //todo 加载表格 getTable()
      } else { //修改用户
        //todo 验证通过
        //todo 调接口
        ElMessage.success('修改成功')
        //todo state.dialogVisible=false //关闭窗口
        //todo 加载表格 getTable()
      }
      console.log('submit!')
    } else {
      console.log('error submit!')
    }
  })
}

/**
 * 增删改 1修改密码 2编辑  3删除 row行的数据
 */
const handle = (type, row) => {
  if (type == 1) {
    state.newPasswordDialogVisible = true
  } else if (type == 2) {
    //赋值
    state.form.userName = row.userName
    state.form.account = row.account
    state.type = 'edit'
    state.dialogVisible = true
  } else if (type == 3) {//删除
    ElMessageBox.confirm(
        `确定删除用户${row.userName}`,
        '删除用户',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(() => {
          //todo 调删除的接口
          //然后加载数据
          ElMessage({
            type: 'success',
            message: '删除成功',
          })
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '已取消',
          })
        })
  }
}

/**
 * 新增新密码
 */
const addNewPassword = () => {
  if (!newRuleFormRef.value) return
  newRuleFormRef.value.validate((valid) => {
    if (valid) {
      //todo 验证通过
      //todo 调接口
      ElMessage.success('修改成功')
      //todo state.newPasswordDialogVisible=false //关闭窗口
      //todo 加载表格 getTable()

      console.log('submit!')
    } else {
      console.log('error submit!')
    }
  })
}
</script>
<style lang="scss" scoped>
.main {
  .search {
    display: flex;
    justify-content: space-between;

    .left-btn {
      display: flex;

      .search-btn {
        display: flex;
        align-items: center;

        .label {
          margin-right: 10px;
        }
      }
    }
  }

  .table {
    margin-top: 20px;

    .operation {
      display: flex;
      justify-content: center;
      align-items: center;

      .ml10 {
        cursor: pointer;
        margin-left: 15px;
        color: #586cb1;
      }
    }
  }
}

.ml25 {
  margin-left: 25px;
}
</style>
<script setup lang="ts">
import {getFindByMappingId, getFindByTypeName} from "@/api/taskConfig";
import {globalData, goalTableForm, tableSetForm, taskSetForm} from "@/views/taskConfig/comps/data";
import {ref} from "vue";
import {uuid} from '@/utils/index';

onMounted(() => {
  taskSetForm.plugsList = []
  getData()
})

let showTable = ref(false)

const state = reactive({
  source: [],//选中项
  allDataList: [],//其他所有插件
})
const getData = async () => {
  let res = await getFindByTypeName({typeName: 'plugs'}) //插件列表
  if (res) {
    res.data?.forEach(async (item, i) => {
      let id = uuid()
      item.plugId = id
      if (globalData.type == 'add') {
        if (item.title === '脏数据处理') {
          item.className = item.classList[0].className;
          setChildren(item, item.classList[0].id);
          taskSetForm.plugsList.unshift(item)
        }
      }
    });
    state.allDataList = res.data
    if (globalData.type == 'add') {
      nextTick(() => {
        setTimeout(() => {
          showForm.value = true
        }, 1000)
      })
    } else {
      handleForm();
    }
  }
}
let loading = ref()
const dataListChange = () => {
  loading.value = true
  // showForm.value=false
  // if (taskSetForm.plugsList.length - 1 > state.source.length) { //需要删除
  //   taskSetForm.plugsList.forEach((item, i) => {
  //     if (item.title != '脏数据处理') {
  //       let isId = state.source.some(ret => item.classList[0].id === ret);
  //       if (!isId) {//不存在
  //         taskSetForm.plugsList.splice(i, 1)
  //       }
  //     }
  //   })
  // } else {//需要添加
  //   state.source.forEach((id) => {
  //     let isId = taskSetForm.plugsList.some(item => item.classList[0].id === id);
  //     if (!isId) {
  //       addList(id)
  //     }
  //   })
  // }
  state.source.forEach((item) => {
    editAddList(item)
  })
  nextTick(() => {
    setTimeout(() => {
      loading.value = false
    }, 1000)
  })
}

const extractUniqueObjects = (objArray) => {
  // 用一个 Set 来存储已经遍历过的 argsId
  let uniqueArgsIds = new Set();

  // 用来存储不重复 argsId 的对象
  let uniqueObjects = [];

  // 遍历对象数组
  for (let obj of objArray) {
    // 如果 argsId 没有出现过
    if (!uniqueArgsIds.has(obj.argsId)) {
      // 添加到 Set 中
      uniqueArgsIds.add(obj.argsId);
      // 添加到结果数组中
      uniqueObjects.push(obj);
    }
  }

  return uniqueObjects;
}


const editAddList = (item) => {
  // state.allDataList.forEach((item) => {
  //   if (item.classList[0].id == id) {
  //     let arr = item
  //     arr.className = arr.classList[0].className
  //     setChildren(arr, arr.classList[0].id)
  //     taskSetForm.plugsList.push(arr)
  //     // if (!arr?.children || !arr.className) {
  //     //   arr.className = arr.classList[0].className
  //     //   setChildren(arr, arr.classList[0].id)
  //     //   taskSetForm.plugsList.push(arr)
  //     // }else{
  //     //   taskSetForm.plugsList.push(arr)
  //     // }
  //   }
  // })
}
//添加列
const addList = (id) => {
  state.allDataList.forEach((item) => {
    if (item.classList[0].id == id) {
      let arr = item
      arr.className = arr.classList[0].className
      setChildren(arr, arr.classList[0].id)
      taskSetForm.plugsList.push(arr)
      // if (!arr?.children || !arr.className) {
      //   arr.className = arr.classList[0].className
      //   setChildren(arr, arr.classList[0].id)
      //   taskSetForm.plugsList.push(arr)
      // }else{
      //   taskSetForm.plugsList.push(arr)
      // }
    }
  })
}
const handleForm = () => {
  let arr = []
  globalData.allData?.taskCoreList?.forEach((list) => {
    if (list.typeName == "plugs") {
      arr.push(list)
    }
  })
  arr.forEach((ret) => {  //筛选需要渲染的个数
    state.allDataList?.forEach((item) => {
      item?.classList?.forEach((res) => {
        if (res.id == ret.argsId) {
          let items = JSON.parse(JSON.stringify(item))
          items.className = ret.value
          items.sequence = ret.sequence
          taskSetForm.plugsList.push(items)
        }
      })
    })
  })
  taskSetForm.plugsList.forEach((item) => {
    let id = uuid()
    item.plugId = id
    item.classList.forEach((res) => {
      if (res.className == item.className) {
        setChildren(item, res.id)
      }
    })
  })
  showForm.value = true
}
let rules = ref({
  className: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  value1: [],
  value2: [],
})
const selectChange = async (item) => {
  let findId = item.classList.find((res) => {
    return res.className == item.className
  })
  item.id = findId.id
  let mappingId = item.id
  setChildren(item, mappingId)
}
let showForm = ref(false)
const setChildren = async (item, mappingId) => { //根据执行类查询列表
  let res = await getFindByMappingId({mappingId})
  if (res) {
    item.children = res.data
    res.data.forEach((value) => {
      if (value.dataType == 2) {
        value.name = value.upName + '.' + value.name
      }
      if (globalData.type == 'add') {
        value.defaultValue = (value.defaultValue)
      } else {
        globalData.allData?.taskCoreList?.forEach((list) => {
          if (list.argsId == value.id && list.sequence == item.sequence) {
            value.defaultValue = (list.value)
          }
        })

      }
    })
  }
  item?.classList.forEach((ret) => {
    if (ret.id == mappingId) {
      item.description = ret.description
    }
  })
  return true
}
const dialogConfig = ref({
  show: false
});
const addForm = () => {
  Object.assign(dialogConfig.value, {
    show: true,
    title: '添加插件',
    width: '50%',
    okText: false,
    cancelText: false
  });
}
// const disabledAdd = computed((item) => {
//   return true
// })
const addFormList = async (item) => {
  loading.value = true
  let id = uuid()
  item.plugId = id
  let arr = JSON.parse(JSON.stringify(item))
  // let arr = item
  arr.className = arr.classList[0].className
  let type = await setChildren(arr, arr.classList[0].id)
  if (type) {
    taskSetForm.plugsList.push(arr)
    nextTick(() => {
      setTimeout(() => {
        loading.value = false
      }, 1200)
    })
  }
}
const disabledAdd = (item) => {
  let type = false
  if (item.classList[0].onlyOne == 1) { //唯一
    taskSetForm.plugsList.forEach((res) => {
      if (res.title == item.title) {
        type = true
      }
    })
  }
  return type
}
const disabledClass = (item) => {
  let type = false
  if (item.classList[0].onlyOne == 1) { //唯一
    taskSetForm.plugsList.forEach((res) => {
      if (res.title == item.title) {
        type = true
      }
    })
  }
  if (type) {
    return 'global-btn-main'
  }
}

const deleteForm = (item) => {
  taskSetForm.plugsList.forEach((ret, i) => {
    if (item.plugId == ret.plugId) {
      taskSetForm.plugsList.splice(i, 1)
    }
  })
}

</script>

<template>
  <div class="j-White">
    <y9Dialog v-model:config="dialogConfig">
      <div v-loading="loading">
        <div v-for="(item,index) in state.allDataList" :key="index" class="plugs-main">
          <y9Card :showHeader="false">
            <div class="card">
              <div>
                <div class="title">
                  <div class="name">插件名称：</div>
                  <div class="content">{{ item.title }}</div>
                </div>
                <div class="title">
                  <div class="content">
                    <div v-for="ret in item.classList" class="classList">
                      <div class="des">{{ ret?.description }} {{ ret?.className }} (执行类)</div>
                    </div>
                  </div>
                </div>

              </div>
              <div class="add-btn">
                <el-button type="primary" :disabled="disabledAdd(item)"
                           @click="addFormList(item)" :class="disabledClass(item)">添加
                </el-button>
              </div>
            </div>

          </y9Card>

        </div>
      </div>

    </y9Dialog>
  </div>
  <!--  <div class="w100">-->
  <!--    <el-form-item label="插件选择" style="width: 100%">-->
  <!--      <el-select-->
  <!--          style="width: 100%!important;"-->
  <!--          v-model="state.source"-->
  <!--          value-key="id"-->
  <!--          clearable-->
  <!--          multiple-->
  <!--          placeholder="请选择添加的插件"-->
  <!--          size="small"-->
  <!--          @change="dataListChange"-->
  <!--      >-->
  <!--        <el-option-->
  <!--            v-for="item in state.allDataList"-->
  <!--            :key="item.classList.id"-->
  <!--            :label="`${item.title}`"-->
  <!--            :value="item.classList[0].id"-->
  <!--        />-->
  <!--      </el-select>-->
  <!--    </el-form-item>-->
  <!--  </div>-->
  <div v-if="!showForm" style="width: 100%;height: 200px"></div>

  <el-form
      ref="ruleFormRef"
      :model="taskSetForm.tableData"
      :rules="rules"
      v-if="showForm">
    <div>
      <div v-for="items in taskSetForm.plugsList" style="position: relative;margin-bottom:30px">
        <el-divider content-position="left" class="title" v-if="items.title">{{ items.title }}</el-divider>
        <el-icon class="deleteIcon" :size="25" @click="deleteForm(items)">
          <CircleClose/>
        </el-icon>
        <el-descriptions border :column="1">
          <el-descriptions-item label-align="center" label="执行类">
            <template #label>
              <div>
                <span>执行类</span>
                <!--                             <span class="y9-required-icon">*</span>-->
              </div>
            </template>
            <div class="selectList">
              <div class="label-width">
                <el-form-item>
                  <el-select
                      v-model="items.className"
                      class="m-2"
                      value-key="id"
                      placeholder="请选择"
                      size="small"
                      @change="selectChange(items)"
                  >
                    <el-option
                        v-for="item in items.classList"
                        :key="item.id"
                        :label="item.className"
                        :value="item.className"
                    />
                  </el-select>
                </el-form-item>
              </div>
              <div class="tipsLabel">{{ items.description }}</div>

            </div>

          </el-descriptions-item>
          <div v-for="ret in items.children" :key="ret.id">
            <el-descriptions-item label-align="center" :label="ret.name">
              <div v-if="ret.type=='integer'">
                <div class="fetch">
                  <div class="fetch-w">
                    <el-form-item>
                      <el-input-number class="number-input" placeholder="请输入" :controls="false"
                                       v-model="(ret.defaultValue)"/>
                    </el-form-item>
                  </div>
                  <div class="tips">{{ ret.description }}</div>
                </div>
              </div>
              <div v-else-if="ret.type=='string'">
                <div class="fetch">
                  <div class="fetch-w">
                    <el-form-item>
                      <el-input placeholder="请输入" v-model="ret.defaultValue"></el-input>
                    </el-form-item>
                  </div>
                  <div class="tips">{{ ret.description }}</div>
                </div>
              </div>
              <div v-else-if="ret.type=='boolean'">
                <div class="fetch">
                  <div class="fetch-w">
                    <el-form-item>
                      <el-radio-group v-model="ret.defaultValue">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </div>
                  <div class="tips">{{ ret.description }}</div>

                </div>
              </div>
            </el-descriptions-item>
          </div>


        </el-descriptions>

      </div>
    </div>
  </el-form>
  <div class="bottom-add" :class="taskSetForm.plugsList?.length==0?'mgTop':''">
    <div class="icon" @click="addForm">
      <el-icon :size="20">
        <Plus/>
      </el-icon>
    </div>
  </div>

</template>

<style scoped lang="scss">
:deep(.el-divider__text ) {
  font-size: 18px;
}

.is-left {
  margin-top: 7px;
}

:deep(.el-divider__text.is-left) {
  margin-top: 7px;
}

.fetch {
  display: flex;
  align-items: center;

  .fetch-w {
    width: 35%;
  }

  .tips {
    margin-left: 20px;
    margin-top: 5px;
    color: #999999;
  }
}

.number-input {
  width: 100%;
}

.selectList {
  display: flex;
  align-items: center;

  .label-width {
    width: 50%;
  }

  .tipsLabel {
    margin-top: 5px;
    margin-left: 20px;
    color: #999999;

  }
}

.w100 {
  margin-left: 30px;

  :deep(.el-form-item__label) {
    font-size: 16px;
    font-weight: 700;
  }
}

:deep(.el-input__inner) {
  text-align: center;
}

.plugs-main {
  margin-bottom: 20px;

  .card {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      display: flex;
      width: 100%;

      .name {
        display: flex;
        min-width: 80px;
        font-size: 15px;
        font-weight: bold;
      }

      .content {
        width: 100%;

        .classList {
          width: 100%;

          .des {
            width: 100%;
            padding-right: 5px;
          }
        }
      }

    }

    .add-btn {
      margin-left: 10px;

      .global-btn-main {
        background-color: var(--el-button-disabled-bg-color) !important;
      }
    }
  }

}

.bottom-add {
  display: flex;
  justify-content: center;
  margin-top: 20px;

  .icon {
    padding: 6px 50px;
    border: 1px solid #d0d0d0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    cursor: pointer;
  }
}

.deleteIcon {
  position: absolute;
  right: -2px;
  top: 1px;
  cursor: pointer;
  color: var(--el-color-primary);
}

.mgTop {
  padding-top: 120px;
}
</style>
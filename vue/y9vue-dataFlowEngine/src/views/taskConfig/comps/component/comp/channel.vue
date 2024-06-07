<script setup lang="ts">
import {getFindByMappingId, getFindByTypeName} from "@/api/taskConfig";
import {globalData, goalTableForm, tableSetForm, taskSetForm} from "@/views/taskConfig/comps/data";
import {ref} from "vue";


onMounted(() => {
  getData()
})
const getData = async () => {
  let res = await getFindByTypeName({typeName: 'channel'})
  if (res) {
    taskSetForm.channelList = res.data
    res.data.forEach(async (item, i) => { //执行类
      if (globalData.type == 'add') {
        item.className = item.classList[0].className
        setChildren(item, item.classList[0].id)
      } else {
        handleForm(item)
      }
    })
    // console.log(state.list,6661)
  }
}
const handleForm = (item) => {
  globalData.allData?.taskCoreList?.forEach((list) => {
    item?.classList?.forEach((res) => {
      if (res.id == list.argsId) {
        item.className = list.value
        setChildren(item, res.id)
      }
    })
  })
}
let rules = ref({
  className: [
    {required: true, message: '请选择', trigger: ['blur', 'change']}
  ],
  value1: [],
  value2: [],
})
const selectChange = async (item) => {
  let findId=item.classList.find((res)=>{
    return res.className==item.className
  })
  item.id=findId.id
  let mappingId = item.id
  setChildren(item, mappingId)
}
let showForm = ref(false)
const setChildren = async (item, mappingId) => {
  let res = await getFindByMappingId({mappingId})
  if (res) {
    item.children = res.data
    res.data.forEach((value) => {
      if(value.dataType==2){
        value.name=value.upName+'.'+value.name
      }
      if (globalData.type == 'add') {
        value.defaultValue = Number(value.defaultValue)
      } else {
        globalData.allData?.taskCoreList?.forEach((list) => {
          if (list.argsId == value.id) {
            value.defaultValue = Number(list.value)
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
  nextTick(()=>{
    setTimeout(()=>{
      showForm.value = true
    },1000)
  })
}
</script>

<template>
  <el-form
      ref="ruleFormRef"
      :model="taskSetForm.tableData"
      :rules="rules"
      v-if="showForm">

    <div >
      <div v-for="items in taskSetForm.channelList">
        <el-divider content-position="left" class="title" v-if="items.title">{{ items.title }}</el-divider>

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
</template>

<style scoped lang="scss">
:deep(.el-divider__text ) {
  font-size: 18px;
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
</style>
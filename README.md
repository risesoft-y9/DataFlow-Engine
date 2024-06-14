<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-dataflow/stargazers'><img src='https://gitee.com/risesoft-y9/y9-dataflow/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.6-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
</p>

## 简介

数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。

## 源码目录

```
common -- 公共模块
 ├── risenet-y9boot-beta-api
 ├── risenet-y9boot-beta-client
 ├── risenet-y9boot-data-jdbc
 ├── risenet-y9boot-data-jdbc-commons
transfer -- 处理模块
 ├── risenet-y9boot-data-transfer-base
 ├── risenet-y9boot-data-transfer-core
 ├── risenet-y9boot-data-transfer-data
 ├── risenet-y9boot-data-transfer-elastic
 ├── risenet-y9boot-data-transfer-rdbms
 ├── risenet-y9boot-data-transfer-ftp
 ├── risenet-y9boot-data-transfer-stream
rpc 
 ├── risenet-y9boot-rpc-commons
 ├── risenet-y9boot-rpc-consumer
 ├── risenet-y9boot-rpc-provide
support -- 业务支撑模块
 ├── risenet-y9boot-support-risedata-scheduler
vue -- 前端工程
 ├── y9vue-dataFlowEngine -- 前端
webapp -- 后端工程
 ├── risenet-y9boot-webapp-risedata-executor -- 执行端
 ├── risenet-y9boot-webapp-risedata-manager -- 管理端
```

## 逻辑架构图

<div><img src="https://vue.youshengyun.com/files/dataflow/img/ljjgt.png"><div/>

## 功能架构图

<div><img src="https://vue.youshengyun.com/files/dataflow/img/gnjgt.png"><div/>

## 部署架构图

<div><img src="https://vue.youshengyun.com/files/dataflow/img/bsjgt.png"><div/>

## 后端技术选型

| 序号 | 依赖            | 版本    | 官网                                                         |
| ---- | --------------- | ------- | ------------------------------------------------------------ |
| 1    | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a> |
| 2    | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a> |
| 3    | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a> |
| 4    | SpringCloud     | 3.1.5   | <a href="https://spring.io/projects/spring-cloud" target="_blank">官网</a> |
| 5    | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a> |
| 6    | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a> |
| 7    | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a> |
| 8    | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a> |
| 9    | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a> |
| 10   | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |

## 前端技术选型

| 序号 | 依赖         | 版本    | 官网                                                         |
| ---- | ------------ | ------- | ------------------------------------------------------------ |
| 1    | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>     |
| 2    | vite2        | 2.9.13  | <a href="https://vitejs.cn/" target="_blank">官网</a>        |
| 3    | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a> |
| 4    | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a> |
| 5    | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a> |
| 6    | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a> |
| 7    | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8    | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a> |
| 9    | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>      |
| 10   | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>    |
| 11   | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>       |
| 12   | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a> |
| 13   | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a> |
| 14   | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>       |

## 中间件选型

| 序号 | 工具             | 版本 | 官网                                                         |
| ---- | ---------------- | ---- | ------------------------------------------------------------ |
| 1    | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>      |
| 2    | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a> |
| 3    | Kafka            | 2.6+ | <a href="https://kafka.apache.org/" target="_blank">官网</a> |
| 4    | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据源支持与兼容

| 序号 | 源库           | 目标库 | 源库版本                                                         |
| ----- | ----------- | ------------------------- | ----------- |
| 1    | Mysql | Mysql、Oracle、PostgresQL、人大金仓、达梦       | 5.7.19以上 |
| 2    | Oracle | Mysql、Oracle、PostgresQL、人大金仓、达梦             | 11g-19c |
| 3    | PostgresQL | Mysql、Oracle、PostgresQL、人大金仓、达梦       | 9.5.25以上 |
| 4    | 人大金仓 | Mysql、Oracle、PostgresQL、人大金仓、达梦 | KingbaseES V8 |
| 5    | 达梦 | Mysql、Oracle、PostgresQL、人大金仓、达梦 | DM8 |
| 6    | ElasticSearch | ElasticSearch | 6.x以上 |
| 7    | FTP | FTP |  |

## 信创

| 序号 | 类型     | 对象                       |
| ------- | -------- | -------------------------- |
| 1        | 浏览器   | 奇安信、火狐、谷歌、360等  |
| 2        | 插件     | 金山、永中、数科、福昕等   |
| 3        | 中间件   | 东方通、金蝶、宝兰德等     |
| 4        | 数据库   | 人大金仓、达梦、高斯等     |
| 5        | 操作系统 | 统信、麒麟、中科方德等     |
| 6        | 芯片     | ARM体系、MIPS体系、X86体系 |

## 引擎高级特性

| 序号 | 功能名称     | 功能解释                       |
| ------- | ----------- | -------------------------- |
| 1       | 输入   | 指的是源头表数据（数据生产者）  |
| 2       | 输出   | 指的是要存入的目的表（数据消费者）  |
| 3       | 输入/输出线程池   | 线程池处理器  |
| 4       | 输入/输出通道  | 数据处理推送通道  |
| 5       | 数据闸口   | 将接收到的数据通过批处理、直流推送或者限流推送到输出通道  |
| 6       | 脏数据     | 数据同步过程中报错的数据   |
| 7       | 异字段同步   | 将源表的字段数据插入到目的表里不同的字段里     |
| 8       | 数据转换   | 将源表的某个字段数据转换成其它数据插入到目的表里     |
| 9       | 切分 | 将要抽取的数据做分片处理，提高抽取的效率     |
| 10      | 数据脱敏     | 将敏感的数据脱敏处理后插入到目的表里 |
| 11      | 数据加密     | 将数据按加密规则加密处理后插入到目的表里 |
| 12      | 增量同步     | 做增量数据同步操作 |
| 13      | 提取表     | 将数据库的表结构信息提取出来做任务配置使用 |
| 14      | 生成表     | 将在页面上添加的表在数据库里生成 |

## 在线体验

演示地址：<a href="https://test.youshengyun.com/y9vue-dataFlowEngine/" target="_blank">https://test.youshengyun.com/y9vue-dataFlowEngine/</a>

> 演示账号：
>
> 账号：admin  密码：admin

## 文档专区

| 序号 | 名称                                                                                              |
|:---|-------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/dataflow/安装部署文档.pdf" target="_blank">安装部署文档</a>           |
| 2  | <a href="https://vue.youshengyun.com/files/dataflow/操作使用文档.pdf" target="_blank">操作使用文档</a>           |

## 系统截图

#### 界面截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/1.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/2.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/3.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/4.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/5.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/6.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/7.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/8.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/9.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/10.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/11.png"></td>
        <td><img src="https://vue.youshengyun.com/files/dataflow/img/12.png"></td>
    </tr>
</table>

## 同构开源项目

| 序号 | 项目名称          | 项目介绍           | 地址                                                         |
| ----- | ----------- | ----------------------------------------- | ----------- |
| 1    | 数字底座 | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。      | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云地址</a> |
| 2    | 工作流引擎 | 工作流引擎对内提供单位/机关流程管理规则和内部业务流程的数字化落地实践；对外提供自动化地第三方业务驱动、接口接入和算法单元驱动能力；工作流引擎在提供底层驱动引擎的同时对全局透明监控、安全防御和国产化特色功能进行充分考虑，是内部流程管理和业务算法驱动的不二之选。        | <a href="https://gitee.com/risesoft-y9/y9-flowable" target="_blank">码云地址</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源数据流引擎咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理-二维码.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


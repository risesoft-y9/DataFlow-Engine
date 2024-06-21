<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java分布式国产纯净数据集成引擎</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-dataflow/stargazers'><img src='https://gitee.com/risesoft-y9/y9-dataflow/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.6-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
    <img src="https://img.shields.io/badge/total%20lines-129.2k-blue.svg">
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
transfer -- 数据处理模块
 ├── risenet-y9boot-data-transfer-base
 ├── risenet-y9boot-data-transfer-core
 ├── risenet-y9boot-data-transfer-data
 ├── risenet-y9boot-data-transfer-elastic
 ├── risenet-y9boot-data-transfer-rdbms
 ├── risenet-y9boot-data-transfer-ftp
 ├── risenet-y9boot-data-transfer-stream
rpc -- 远程过程调用模块
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

## 引擎高级特点

| 序&nbsp;号       | 特点名称        | 特点描述                       |
| ------- | ----------- | -------------------------- |
| 1       | 异构适配   | 支持多种结构化、半结构化、非结构化数据库或系统的插件兼容模式  |
| 2       | 切分模式   | 支持按照多种规则对接入的数据进行切片处理，便于提高采集效率  |
| 3       | 全量增量   | 支持全量数据同步，支持根据一定规则进行增量数据同步  |
| 4       | 脏数据处理  | 针对报错数据，支持以多种处理模式进行自动化处理和执行  |
| 5       | 数据筛选   | 支持用户利用灵活的查询语句在同步过程中对数据进行筛选  |
| 6       | 数据转换   | 支持将源头表某字段数据转换成其他数据类型插入至目标表  |
| 7       | 数据脱敏   | 支持将某字段敏感数据按照一定规则脱敏后再进行处理和执行     |
| 8       | 数据加密   | 支持将批量数据按照选择的加密规则进行加密后再进行处理和执行     |
| 9       | 异字段转换 | 针对源头和目标的字段名称不同（含义一致），支持配置转换后插入     |
| 10      | 多线程    | 支持在线程池中配置多线程以增加执行端的效率 |
| 11      | 接口适配     | 支持源头和目标以约定的接口形式和数据结构进行接入和推出 |
| 12      | 断点续传     | 针对批处理过程中进行批次标记，从而实现断点续传，减少错误成本 |

## 引擎高级功能

| 序号 | 功能名称     | 功能描述                       |
| ------- | ----------- | -------------------------- |
| 1       | 输入流   | 在执行端中用于接入源头数据（生产方数据）的模块  |
| 2       | 输出流   | 在执行端中用于推出目标数据（消费方数据）的模块  |
| 3       | 输入线程池   | 在执行端中配置接入数据后的线程池  |
| 4       | 输出线程池  | 在执行端中配置推出数据前的线程池  |
| 5       | 输入通道    |	  在执行端中用于处理接入的源头数据的通道  |
| 6       | 输出通道    | 在执行端中用于处理推出的目标数据的通道 |
| 7       | 数据闸口     | 在执行端中利用批处理、直流推送和限流推送等方式的中间缓冲层     |
| 8       | 插件库      |	集中存储和配置各类执行端需要部署插件的统一仓库     |
| 9       | 任务配置   |	在管理端中配置某任务执行时所需的详细配置项     |
| 10      | 任务调度    |	 在管理端中对已经部署的任务和执行端进行调度 |
| 11      | 日志监控     | 在管理端中负责各个执行端的成功和失败日志的汇总查看 |
| 12      | 白名单管理  | 针对IP地址的白名单安全管控（执行端、源头和目标） |

## 在线体验

演示地址：<a href="https://test.youshengyun.com/y9vue-dataFlowEngine/" target="_blank">https://test.youshengyun.com/y9vue-dataFlowEngine/</a>

> 演示账号：
>
> 账号：guest  密码：guest

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
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/dataflow/img/qjfewm.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


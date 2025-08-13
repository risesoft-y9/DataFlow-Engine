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

数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。[系统在线体验----->>>>>](#在线体验)

## 源码目录

```
common -- 公共模块
 ├── risenet-y9boot-beta-api
 ├── risenet-y9boot-beta-client
 ├── risenet-y9boot-data-jdbc
 ├── risenet-y9boot-data-jdbc-commons
 ├── risenet-y9boot-data-common-tools
transfer -- 数据处理模块
 ├── risenet-y9boot-data-transfer-base
 ├── risenet-y9boot-data-transfer-core
 ├── risenet-y9boot-data-transfer-data
 ├── risenet-y9boot-data-transfer-elastic
 ├── risenet-y9boot-data-transfer-rdbms
 ├── risenet-y9boot-data-transfer-ftp
 ├── risenet-y9boot-data-transfer-stream
 ├── risenet-y9boot-data-transfer-api
 ├── risenet-y9boot-data-transfer-assembler
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
<div><img src="https://vue.youshengyun.com/files/dataflow/img/ljjgt2.png"><div/>

1. 数据流引擎分为管理端和执行端，管理端具备可视化界面面向用户操作，执行端无界面无状态

2. 管理端主要负责对于执行端任务的配置和监控

3. 执行端接收任务，数据从输入流至数据闸口，最终通过输出流推出

4. 插件库是数据流引擎的重要核心，每个环节中使用哪些插件的灵活组合可以应对多种定制化复杂业务

## 功能架构图

<div><img src="https://vue.youshengyun.com/files/dataflow/img/gnjgt.png"><div/>

1. 管理端的功能主要为任务配置和、任务调度和插件库配置

2. 执行端的每一个环节中均有不等的插件对数据任务进行处理

3. 数据流引擎可以依赖数字底座进行使用，也可以单独进行使用

## 部署架构图

<div><img src="https://vue.youshengyun.com/files/dataflow/img/bsjgt.png"><div/>

1. 管理端可以平行部署，执行端可以根据业务分类和业务量进行大规模部署，从而提高数据流转效率

2. 正式环境为保证安全，建议将数字底座与数据流引擎进行结合，用系统管理员账号进行管理端操作

3. 数据流引擎支持容器化方式部署

4. 数据流引擎单体在信创环境中，4核8GB的虚拟机可以轻松管理5000任务（需合理匹配多个执行端）

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

| 序&nbsp;号 | 源库           | 目标库 | 源库版本                                                         |
| ----- | ----------- | ------------------------- | ----------- |
| 1    | Mysql | Mysql、Oracle、PostgresQL、人大金仓、达梦       | 5.7.19以上 |
| 2    | Oracle | Mysql、Oracle、PostgresQL、人大金仓、达梦             | 11g-19c |
| 3    | PostgresQL | Mysql、Oracle、PostgresQL、人大金仓、达梦       | 9.5.25以上 |
| 4    | 人大金仓 | Mysql、Oracle、PostgresQL、人大金仓、达梦 | KingbaseES V8 |
| 5    | 达梦 | Mysql、Oracle、PostgresQL、人大金仓、达梦 | DM8 |
| 6    | ElasticSearch | ElasticSearch | 6.x以上 |
| 7    | FTP | FTP |  |

## 信创兼容适配

| 序号 | 类型     | 对象                       |
| ------- | -------- | -------------------------- |
| 1        | 浏览器   | 奇安信、火狐、谷歌、360等  |
| 2        | 插件     | 金山、永中、数科、福昕等   |
| 3        | 中间件   | 东方通、金蝶、宝兰德等     |
| 4        | 数据库   | 人大金仓、达梦、高斯等     |
| 5        | 操作系统 | 统信、麒麟、中科方德等     |
| 6        | 芯片     | ARM体系、MIPS体系、X86体系 |

## 引擎高级特点

| 序&nbsp;号       | 特&nbsp;点&nbsp;&nbsp;名&nbsp;称        | 特点描述                       |
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
| 12      | 任务终止     | 面对超时任务、卡死任务，支持在任务过程中人工中止，配合回滚补偿可使目标库恢复初始状态 |
| 13      | 回滚补偿     | 支持撰写插件利用数据库的回滚操作嵌入对目标库进行初始状态的恢复；支持将配置的已执行的日志记录在补充日志中，在异常后可在具体点位进行数据回滚恢复 |

## 引擎高级功能

| 序&nbsp;号 | 功&nbsp;能&nbsp;&nbsp;名&nbsp;称     | 功能描述                       |
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
| 13      | 模版导入  | 在管理端支持以Excel模板、SQL文件的方式定向进行数据采集和导入功能 |
| 14      | 数据源  | 支持对于多种主流和信创数据源进行配置和链接 |
| 15      | 库表管理  | 支持基于数据源的库表映射管理 |
| 16      | 业务分类  | 支持先对任务的业务种类进行分类和归集 |
| 17      | 接口管理  | 支持对于接口的定制、配置、测试、调用、管理等功能 |

## 在线体验

演示地址：<a href="https://demo.youshengyun.com/y9vue-dataFlowEngine/" target="_blank">https://demo.youshengyun.com/y9vue-dataFlowEngine/</a>

> 演示账号：
>
> 账号：guest  密码：Risesoft@2024

## 文档专区

| 序号 | 名称                                                                                              |
|:---|-------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>                   |
| 2  | <a href="https://vue.youshengyun.com/files/数字底座接口文档.pdf" target="_blank">数字底座接口文档</a>                   |
| 3  | <a href="https://vue.youshengyun.com/files/dataflow/安装部署文档.pdf" target="_blank">数据流引擎安装部署文档</a>           |
| 4  | <a href="https://vue.youshengyun.com/files/dataflow/操作使用文档.pdf" target="_blank">数据流引擎操作使用文档</a>           |
| 5  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>           |
| 6  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>                   |
| 7  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>                   |
| 8  | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>                 |
| 9  | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>                   |
| 10 | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>             |
| 11 | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>              |
| 12 | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                         |
| 13 | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>                       |
| 14 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                         |
| 15 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                         |
| 16 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                           |
| 17 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>                     |
| 18 | <a href="https://vue.youshengyun.com/files/Oracle数据库适配文档.pdf" target="_blank">Oracle数据库适配文档</a>         |
| 19 | <a href="https://vue.youshengyun.com/files/Dameng数据库适配文档.pdf" target="_blank">Dameng数据库适配文档</a>         |
| 20 | <a href="https://vue.youshengyun.com/files/PostgreSQL数据库适配文档.pdf" target="_blank">PostgreSQL数据库适配文档</a> |
| 21 | <a href="https://vue.youshengyun.com/files/Kingbase数据库适配文档.pdf" target="_blank">Kingbase数据库适配文档</a>     |
| 22 | <a href="https://vue.youshengyun.com/files/Mariadb数据库适配文档.pdf" target="_blank">Mariadb数据库适配文档</a>       |
| 23 | <a href="https://vue.youshengyun.com/files/OceanBase数据库适配文档.pdf" target="_blank">OceanBase数据库适配文档</a>   |

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

## 依赖开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;&nbsp;名&nbsp;称          | 项目介绍           | 地&nbsp;址                                                                                                                                                          |
| ----- | ----------- | ----------------------------------------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1    | 数字底座 | 数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用      | <a href="https://gitee.com/risesoft-y9/y9-core" target="_blank">码云</a> <a href="https://github.com/risesoft-y9/Digital-Infrastructure" target="_blank">GitHub</a> |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

### 中国城市发展研究会

官网：<a href="https://www.china-cfh.com/" target="_blank">https://www.china-cfh.com/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源产品咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/dataflow/img/qjfewm.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>


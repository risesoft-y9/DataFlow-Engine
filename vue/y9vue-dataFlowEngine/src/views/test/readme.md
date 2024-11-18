# 工程接口开发工具

## 工具介绍

    该工具为vue3项目开发提供接口文档在线测试功能，方便前端和后端人员快速沟通、协作。
    1、Authorization不需要手动添加，适配Y9前端工程，如果缓存中有，自动获取token并监听每一次的更新
    2、特定现场要求，比如内网，不可使用外网的接口工具

    注意：
    1、适配了主流的6种请求方式，分别是get、post、put、delete、patch、head，
    2、但主要适配
    content-type:application/json的get、post请求，
    content-type:application/x-www-form-urlencoded的get、post请求
    content-type:multipart/form-data的post请求以及上传文件的请求
    3、参数类型有query、body、formData、json、xml、html、binary
    4、未完善cookie，比较繁琐（同源和跨域以及浏览器问题），且不推荐使用

## 插件要求

1、sass@1.58.0（必须）
2、element-plus@2.7.7（必须y9plugin-component组件里的版本匹配）
5、vue@3.4.31

## 用法

1、拷贝该文件夹到任意vue3项目的src下
    示例：
    src/views/test
2、在main.js中引入jsoneditor的样式文件
    示例：
    import 'jsoneditor/dist/jsoneditor.min.css';
3、在router/index.js中添加一个静态路由（免单点登陆）
    示例：
    {
        path: '/api-test',
        hidden: true,
        meta: {
            title: '接口在线测试'
        },
        component: () => import('@/views/test/index.vue')
    }
4、在配置文件中添加一个配置
    示例：
    # 接口工具的数据来源（1-local，2-API）
    VUE_APP_API_UTILS_TREEDATA = '1'
5、如果有接口，需自行开发，开发接口的文件位置： apiUtils.js，就在test文件夹下

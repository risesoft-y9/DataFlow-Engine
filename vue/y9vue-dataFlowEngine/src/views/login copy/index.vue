<script lang="ts" setup>
    import router from '@/router';
    import md5 from 'md5';
    import { onMounted, ref } from 'vue';
    import { login, getUserLoginInfo } from '@/api/login';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    const name = ref('');
    const pwd = ref('');

    onMounted(() => {
        getCheckboxValue();
        checkLogin();
    });

    // 检查是否已经登录
    function checkLogin() {
        let token = y9_storage.getObjectItem(settings.siteTokenKey);
        if (token) {
            navTo();
        }
    }

    function navTo() {
        if (sessionStorage.getItem('lastUrl')) {
            let lastUrl = sessionStorage.getItem('lastUrl');
            if (lastUrl.endsWith('/')) {
                sessionStorage.setItem('lastUrl', lastUrl + 'home');
                window.location = lastUrl;
            } else {
                window.location = lastUrl;
            }
        } else {
            router.push({
                path: '/home'
            });
        }
    }
    // 实时监测checkout的值
    function getCheckboxValue() {
        // 获取复选框DOM元素
        var checkbox = document.getElementById('checkbox1');

        // 添加事件监听器来监控复选框的改变
        checkbox?.addEventListener('change', function () {
            // 检查复选框是否被选中，并进行操作
            if (checkbox?.checked) {
                console.log('复选框已被选中。');
                // 这里可以编写当复选框被选中时应该执行的逻辑
            } else {
                console.log('复选框没有被选中。');
                // 这里可以编写当复选框没被选中时应该执行的逻辑
            }
        });
    }

    // 登录
    async function loginFunc() {
        // 登录接口
        let res = await login({ account: name.value, password: btoa(pwd.value) });
        if (res.code == 0) {
            // 获取token值，进行token的存储
            y9_storage.setObjectItem(settings.siteTokenKey, {
                access_token: res.data
            });
            // 获取用户信息
            let result = await getUserLoginInfo();
            if (result.code == 0) {
                // 存储用户信息
                y9_storage.setObjectItem('ssoUserInfo', {
                    name: result.data.account,
                    id: result.data.id
                });

                navTo();
            }
        }
        ElMessage({
            message: res.success ? '正在登录中...' : res.msg,
            type: res.success ? 'success' : 'error',
            duration: 5000
        });
    }
</script>

<template>
    <div class="templateLogin">
        <div class="logo">
            <img src="@/views/login/static/img/logo.svg" />
            <!-- <img src="@/assets/images/youshengyunLogo.png" /> -->
        </div>
        <div class="login">
            <div class="layui-row">
                <div class="layui-col-xs6 login_form" id="tunnel">
                    <div class="login_title">
                        <span>用户登录</span>
                    </div>
                    <div class="login_fields">
                        <form id="fm1">
                            <div class="login_fields__user">
                                <div class="icon">
                                    <img alt="" src="@/views/login/static/img/user.svg" />
                                </div>
                                <span class="icon-1">|</span>
                                <input
                                    autocomplete="off"
                                    id="username"
                                    placeholder="请输入账号"
                                    tabindex="1"
                                    type="text"
                                    v-model="name"
                                />
                                <div class="validation">
                                    <img alt="" src="@/views/login/static/img/tick.png" />
                                </div>
                            </div>
                            <div class="login_fields__password">
                                <div class="icon">
                                    <img alt="" src="@/views/login/static/img/pwd.svg" />
                                </div>
                                <span class="icon-1">|</span>
                                <input
                                    autocomplete="off"
                                    htmlescape="true"
                                    id="password"
                                    placeholder="请输入密码"
                                    tabindex="2"
                                    type="password"
                                    v-model="pwd"
                                    @keyup.enter="loginFunc"
                                />

                                <div class="validation"><img alt="" src="@/views/login/static/img/tick.png" /></div>
                            </div>
                            <div class="login_fields_remember_password">
                                <input class="chk left" id="checkbox1" type="checkbox" />
                                <label class="left" for="checkbox1"></label>
                                <span class="left">记住密码</span>
                            </div>
                            <!-- 表单隐藏数据块结束 -->
                        </form>
                        <div class="login_fields__submit">
                            <input type="button" value="登录" @click="loginFunc" />
                        </div>
                    </div>
                    <div></div>

                    <div class="success"></div>
                    <div class="toolTips" style="display: none">
                        <ul id="toolTipsUl"></ul>
                    </div>
                </div>
                <div class="layui-col-xs6 noticeInfo">
                    <img src="@/views/login/static/img/wxts.svg" />
                    <!--            <div class="title">温馨提示</div>-->
                    <!--            <div class="info">-->
                    <!--                <span>1、本系统为非涉密系统，请勿上传涉密文件，请勿将标密文件、电报掩盖密级扫描上传；要求按密件上报的报告请走线下纸质渠道上报。</span>-->
                    <!--                <span>2、不要在公共场合保存登录信息</span>-->
                    <!--                <span>3、尽量避免多人使用同一账号</span>-->
                    <!--                <span>4、为保证您的账户安全，退出时请注销，并定期修改密码</span>-->
                    <!--            </div>-->
                </div>
            </div>
        </div>
        <!--<div class="bg_top_right"></div>-->
    </div>
</template>

<style lang="scss" scoped>
    .templateLogin {
        background: #ac1e02 url('@/views/login/static/img/loginbg.png') no-repeat fixed center;
        background-size: cover;
        filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#EA5C54 ',
        endColorstr='#bb6dec', GradientType=1);
        width: 100vw;
        height: 100vh;
    }

    .chk {
        margin-right: 10px;
    }

    .templateLogin .logo {
        position: absolute;
        top: 25px;
        left: 59px;
    }

    .templateLogin .logo img {
        /*width: 560px;*/
        width: 80%;
        /*height: 103px;*/
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 15px;
    }

    .templateLogin ::-webkit-input-placeholder {
        color: #4e546d;
    }

    .templateLogin .login {
        width: 880px;
        height: 470px;
        position: absolute;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        border-radius: 16px;
        -webkit-transition-timing-function: cubic-bezier(0.68, -0.25, 0.265, 0.85);
        -webkit-transition-property: -webkit-transform, opacity, box-shadow, top, left;
        transition-property: transform, opacity, box-shadow, top, left;
        -webkit-transition-duration: 0.5s;
        transition-duration: 0.5s;
        -webkit-transform-origin: 50% 50%;
        -ms-transform-origin: 50% 50%;
        transform-origin: 50% 50%;
        -webkit-transform: rotateX(0deg);
        transform: rotateX(0deg);
        background-color: #ffffff;
        margin: auto 10% auto auto;
    }

    .templateLogin .layui-row {
        height: 100% !important;
    }

    .templateLogin .login_form {
        border-radius: 10px 0px 0px 10px;
        background: #fff;
        height: 100%;
        width: 50%;
        float: left;
        box-shadow: 30px 30px 0px 0px rgba(0, 0, 0, 0.2);
    }

    .templateLogin .login_form .login_title {
        padding-top: 60px;
        padding-right: 53px;
        padding-left: 52px;
    }

    .templateLogin .login_title span {
        text-align: center;
        color: #000;
        font-size: 24px;
        font-style: normal;
        font-weight: 600;
        line-height: normal;
    }

    .templateLogin .login_fields {
        /*height: 61px;*/
        margin: 0;
        padding-right: 53px;
        padding-left: 52px;
    }

    .templateLogin .login_fields__user,
    .templateLogin .login_fields__password {
        position: relative;
        border-radius: 2px;
        border: 1px solid #dabcb6;
        background: #fffbfa;
        margin-top: 30px;
        padding: 14px 20px 16px;
        z-index: 1;
    }

    .templateLogin .login_fields__user:hover,
    .templateLogin .login_fields__password:hover,
    .templateLogin .login_fields__user:focus,
    .templateLogin .login_fields__password:focus {
        border-radius: 2px;
        border: 1px solid #c92606;
        background: #fffbfa;
        color: #333;
    }

    .templateLogin .login_fields_remember_password {
        color: #50302a;
        padding: 10px 0;
    }

    .templateLogin .login_fields_remember_password .left {
        color: #50302a !important;
    }

    .templateLogin .login_fields .icon {
        display: inline-block;
        vertical-align: middle;
    }

    .templateLogin .login_fields .icon img {
        width: 31px;
        height: 31px;
        vertical-align: middle;
        /*margin-top: 3px;*/
    }

    .templateLogin .login_fields .icon-1 {
        color: #d4d4d4;
        height: 20px;
        margin-left: 16px;
        display: inline-block;
    }

    .templateLogin .login_fields input[type='text'],
    .templateLogin .login_fields input[type='password'] {
        display: inline-block;
        width: 195px;
        height: 31px;
        vertical-align: middle;
        border: none;
        outline: none;
        box-shadow: none;
        font-size: 16px;
        padding-left: 10px;
        border-radius: 2px;
        background: #fffbfa;
        color: #333 !important;
        font-size: 16px;
        font-style: normal;
        font-weight: 400;
        line-height: normal;
    }

    .templateLogin .login_fields__submit {
        position: relative;
        width: 100%;
        border-radius: 3px;
        background: #9e2e17;
        box-shadow: 0px 4px 30px 0px rgba(172, 30, 2, 0.2);
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
        margin-top: 12px;
        -webkit-transition-property: background, color;
        transition-property: background, color;
        -webkit-transition-duration: 0.2s;
        transition-duration: 0.2s;
    }

    .templateLogin .login_fields__submit .forgot {
        float: right;
        font-size: 10px;
        margin-top: 11px;
        text-decoration: underline;
    }

    .templateLogin .login_fields__submit .forgot a {
        color: #606479;
    }

    .templateLogin .login_fields__submit input {
        display: block;
        margin: auto;
        border-radius: 8px;
        background: transparent;
        border: none;
        text-transform: uppercase;
        padding: 18px 86px;
        color: #fff;
        font-size: 18px;
        font-style: normal;
        font-weight: 400;
        line-height: normal;
    }

    .templateLogin .login_fields__submit:hover {
        box-shadow: none;
        outline: none;
        border-radius: 3px;
        background: #c92606;
        box-shadow: 0px 4px 30px 0px rgba(172, 30, 2, 0.2);
    }

    .templateLogin .login_fields__submit:hover {
        color: white;
        cursor: pointer;
        -webkit-transition-property: background, color;
        transition-property: background, color;
        -webkit-transition-duration: 0.2s;
        transition-duration: 0.2s;
        border-radius: 3px;
        background: #c92606;
        box-shadow: 0px 4px 30px 0px rgba(172, 30, 2, 0.2);
    }

    .templateLogin .noticeInfo {
        width: 50%;
        fill: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(2px);
        height: 100%;
        background-color: #fdf3f3;
        border-radius: 0px 10px 10px 0px;
        float: right;
        background: linear-gradient(91deg, #ffe9dc 4.02%, rgba(255, 249, 245, 0) 50.6%);
        backdrop-filter: blur(2px);
        box-shadow: 30px 30px 0px 0px rgba(0, 0, 0, 0.2);
    }

    .templateLogin .noticeInfo .title {
        color: #9e2e17 !important;
        font-size: 20px;
        font-style: normal;
        font-weight: 500;
        line-height: normal;
        margin: 60px 46px 0 46px;
    }

    .templateLogin .noticeInfo .info {
        margin: 40px 46px 0 52px;
    }

    .templateLogin .noticeInfo .info span {
        color: #50302a;
        font-family: Source Han Sans CN;
        font-size: 14px;
        font-style: normal;
        font-weight: 400;
        line-height: 22px;
        display: block;
        margin-bottom: 10px;
    }

    .templateLogin .noticeInfo img {
        display: flex;
        justify-content: center;
        bottom: 0;
        position: absolute;
        padding-left: 40px;
        padding-right: 40px;
    }

    .templateLogin p {
        color: #dfdfdf;
        font-size: 10px;
        text-align: left;
    }

    .templateLogin .login .validation {
        position: absolute;
        z-index: 1;
        right: 10px;
        top: 14px;
        opacity: 0;
    }

    .templateLogin .login .disclaimer {
        position: absolute;
        margin: auto;
        bottom: 25px;
        width: 150px;
        text-align: center;
        left: 98.3px;
    }

    .templateLogin .qvcode {
        height: 208px;
        width: 208px;
        margin: auto;
        position: relative;
        top: 10px;
        display: none;
    }

    .templateLogin .qvcode img {
        width: 100%;
    }

    .templateLogin .disclaimer p {
        text-align: center;
        font-size: 13px;
        cursor: pointer;
    }

    .templateLogin .disclaimer p:hover {
        color: rgb(255, 238, 0);
    }

    .templateLogin .disclaimer a {
        color: white;
    }

    .templateLogin .disclaimer a:hover {
        color: rgb(255, 238, 0);
    }

    .templateLogin .toolTips {
        width: 342px;
        position: relative;
        left: 65px;
        overflow: hidden;
        z-index: 10;
        background: linear-gradient(45deg, #000, #dfdfdf);
        border-radius: 5px;
        top: -182px;
    }

    .templateLogin .toolTips ul {
        width: 342px;
        max-height: 200px;
        overflow-y: scroll;
        overflow-x: hidden;
        overflow: -moz-scrollbars-none;
        -ms-overflow-style: none;
        /* border: 1px solid #f40; */
    }

    .templateLogin .toolTips ul::-webkit-scrollbar {
        width: 0 !important;
        height: 0 !important;
    }

    .templateLogin .toolTips ul li {
        line-height: 30px;
        padding: 0 10px;
        cursor: pointer;
    }

    .templateLogin .toolTips ul li:hover {
        color: #61bfff !important;
    }

    .templateLogin .toolTips ul li.active {
        color: #61bfff !important;
    }

    /* Color Schemes */
    .love {
        position: absolute;
        right: 20px;
        bottom: 0px;
        font-size: 11px;
        font-weight: normal;
    }

    .love p {
        color: white;
        font-weight: normal;
        font-family: 'Open Sans', sans-serif;
    }

    .love a {
        color: white;
        font-weight: 700;
        text-decoration: none;
    }

    .love img {
        position: relative;
        top: 3px;
        margin: 0px 4px;
        width: 10px;
    }

    .brand {
        position: absolute;
        left: 20px;
        bottom: 14px;
    }

    .brand img {
        width: 30px;
    }

    @media only screen and (min-device-width: 391px) and (max-device-width: 912px) {
    }

    @media only screen and (min-device-width: 390px) and (max-device-width: 390px) {
        .templateLogin .login {
            top: -15px !important;
        }
    }

    @media only screen and (min-device-width: 279px) and (max-device-width: 389px) {
        .templateLogin .login {
            top: -52px !important;
        }
    }
</style>

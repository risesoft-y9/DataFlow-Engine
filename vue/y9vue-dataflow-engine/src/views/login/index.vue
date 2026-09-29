<script lang="ts" setup>
    import router from '@/router';
    import md5 from 'md5';
    import { onMounted, ref } from 'vue';
    import { login, getUserLoginInfo } from '@/api/login';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import userSharedLine from '@/views/login/static/images/user-shared-line (1).png';
    import lockLine from '@/views/login/static/images/lock-line.png';
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
            } else if (lastUrl.endsWith('/login')) {
                sessionStorage.setItem('lastUrl', lastUrl.replace('login', 'home'));
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
            }else{
                sessionStorage.clear();
            }
        }
        // ElMessage({
        //     message: res.success ? '正在登录中...' : res.msg,
        //     type: res.success ? 'success' : 'error',
        //     duration: 5000
        // });
    }
</script>

<template>
    <div class="login-page">
        <!-- <div class="left">
            <div class="company-logo"></div>
            <div class="ui">
                <div class="data-line-img"></div>
                <div class="system-name"></div>
            </div>
        </div> -->
        <div class="right login-wrapper-bg">
            <div class="wrapper">
                <div class="login-form-title">用户登录</div>
                <div class="login-form-user">
                    <div class="icon">
                        <img :src="userSharedLine" alt="" />
                        <span></span>
                    </div>
                    <input tabindex="1" placeholder="请输入账号" autocomplete="off" v-model="name" />
                </div>
                <div class="login-form-pwd">
                    <div class="icon">
                        <img :src="lockLine" alt="" />
                        <span></span>
                    </div>
                    <input
                        tabindex="2"
                        type="password"
                        htmlescape="true"
                        autocomplete="off"
                        placeholder="请输入密码"
                        v-model="pwd"
                        @keyup.enter="loginFunc"
                    />
                </div>
                <div class="login-form-btn" @click="loginFunc">登录</div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
    .login-page {
        min-width: 100vw;
        height: 100vh;
        background-image: url('./static/images/bg.png');
        background-size: cover;
        display: flex;
        align-items: center;

        .left {
            width: 55vw;
            height: 100vh;

            .company-logo {
                width: 10vw;
                height: 5vh;
                background-image: url('./static/images/有生LOGO白色.png');
                background-size: contain;
                background-repeat: no-repeat;
                margin-top: 3vh;
                margin-left: 2vw;
            }
            .ui {
                .data-line-img {
                    width: 576px;
                    height: 443px;
                    background-image: url('./static/images/数据流水线-更新.png');
                    background-size: contain;
                    background-repeat: no-repeat;
                    margin-left: 12vw;
                    margin-top: 8vh;
                }
                .system-name {
                    width: 471px;
                    height: 142px;
                    background-image: url('./static/images/数据流引擎系统文字logo-更新.png');
                    background-size: contain;
                    background-repeat: no-repeat;
                    margin-left: 15vw;
                    margin-top: 8vh;
                }
            }
        }
        .right {
            width: 45vw;
            height: 100vh;
            margin-left: 35%;

            &.login-wrapper-bg {
                width: 601px;
                height: 575px;
                background-image: url('./static/images/bjb.png');
                background-size: contain;
                background-repeat: no-repeat;
            }

            .wrapper {
                width: 547px;
                height: 521px;
                border-radius: 5%;
                position: relative;
                background-color: #fff;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;

                .login-form-title {
                    font-size: 30px;
                    color: rgb(102, 102, 102);
                }
                .login-form-user {
                    margin-top: 50px;
                }
                .login-form-pwd {
                    margin-top: 50px;
                }
                .login-form-btn {
                    width: 300px;
                    line-height: 55px;
                    background-color: rgb(66, 109, 228);
                    border-radius: 28px;
                    margin-top: 50px;
                    text-align: center;
                    color: #fff;
                }
                .icon {
                    position: absolute;
                    margin-top: 15px;
                    margin-left: 15px;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    width: 40px;
                    span {
                        display: block;
                        width: 2px;
                        height: 15px;
                        background-color: rgb(195, 195, 195);
                    }
                    img {
                        width: 25px;
                        height: 25px;
                    }
                }
                input {
                    width: 300px;
                    height: 50px;
                    border: 1px solid rgb(227, 227, 227);
                    border-radius: 6px;
                    padding-left: 70px;
                    &:focus {
                        outline: none;
                    }
                    &::placeholder {
                        color: rgb(227, 227, 227);
                    }
                }
                .tips-text {
                    margin-top: 20px;
                    color: red;
                }
            }
        }
    }
</style>

<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <span class="title">心理咨询与诊断平台 - 咨询师端</span>
      </div>
      <div class="header-right">
        <span class="welcome">欢迎，{{ counselor.realName }}</span>
        <el-button type="text" @click="logout" style="color: #fff">退出</el-button>
      </div>
    </el-header>

    <el-container>
      <el-aside class="layout-aside" width="220px">
        <el-menu
          :default-active="activeMenu"
          class="side-menu"
          background-color="#1e2a3a"
          text-color="#bfcbd9"
          active-text-color="#ffffff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <i class="el-icon-s-home"></i>
            <span slot="title">工作台</span>
          </el-menu-item>
          <el-menu-item index="appointment">
            <i class="el-icon-date"></i>
            <span slot="title">预约列表</span>
          </el-menu-item>
          <el-menu-item index="timeConfig">
            <i class="el-icon-setting"></i>
            <span slot="title">时间设置</span>
          </el-menu-item>
          <el-menu-item index="account">
            <i class="el-icon-money"></i>
            <span slot="title">资金账户</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'CounselorLayout',
  data() {
    return {
      activeMenu: 'home',
      counselor: {}
    }
  },
  created() {
    const counselorStr = localStorage.getItem('counselor')
    if (counselorStr) {
      this.counselor = JSON.parse(counselorStr)
    } else {
      this.$router.push('/counselor/login')
    }
  },
  methods: {
    handleMenuSelect(index) {
      this.activeMenu = index
      const routes = {
        home: '/counselor/home',
        appointment: '/counselor/appointment',
        timeConfig: '/counselor/time-config',
        account: '/counselor/account'
      }
      if (routes[index]) {
        this.$router.push(routes[index])
      }
    },
    logout() {
      localStorage.removeItem('counselor')
      this.$router.push('/counselor/login')
      this.$message.success('已退出')
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}
.layout-header {
  background-color: #1e2a3a;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}
.header-left .title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.welcome {
  color: #fff;
}
.layout-aside {
  background-color: #1e2a3a;
  height: calc(100vh - 60px);
  overflow-y: auto;
}
.side-menu {
  border-right: none;
  height: 100%;
}
.side-menu .el-menu-item {
  height: 52px;
  line-height: 52px;
}
.side-menu .el-menu-item.is-active {
  background-color: #20bfa0 !important;
  color: #fff !important;
}
.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
<template>
  <el-container class="page-container">
    <!-- 顶部导航栏 -->
    <el-header class="app-header">
      <div class="header-left">
        <span class="platform-title">心理咨询与诊断平台</span>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="36" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
            <span class="user-name">用户：{{ username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人资料</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧侧边栏 -->
      <el-aside class="app-aside" width="200px">
        <el-menu
          :default-active="activeMenu"
          class="side-menu"
          background-color="#1e2a3a"
          text-color="#bfcbd9"
          active-text-color="#ffffff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="appointment">
            <i class="el-icon-date"></i>
            <span slot="title">我要预约</span>
          </el-menu-item>
          <el-menu-item index="myAppointment">
            <i class="el-icon-list"></i>
            <span slot="title">我的预约</span>
          </el-menu-item>
          <el-menu-item index="report">
            <i class="el-icon-document"></i>
            <span slot="title">我的报告</span>
          </el-menu-item>
          <el-menu-item index="evaluate">
            <i class="el-icon-edit"></i>
            <span slot="title">在线评估</span>
          </el-menu-item>
          <el-menu-item index="account">
            <i class="el-icon-money"></i>
            <span slot="title">我的账户</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧主内容区 -->
      <el-main class="app-main">
        <div class="page-header">
          <h2>咨询师列表</h2>
          <p>选择您信任的咨询师，预约线下咨询</p>
        </div>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="item in counselorList" :key="item.id">
            <el-card class="counselor-card" shadow="hover">
              <div class="counselor-avatar">
                <el-avatar :size="80" :src="item.avatar || defaultAvatar"></el-avatar>
              </div>
              <div class="counselor-name">{{ item.realName }}</div>
              <div class="counselor-title">{{ item.title }}</div>
              <div class="counselor-desc">{{ item.description }}</div>
              <div class="counselor-price">¥ {{ item.fee }} / 次</div>
              <el-button type="primary" @click="bookCounselor(item)">立即预约</el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorList',
  data() {
    return {
      activeMenu: 'appointment',
      username: '',
      counselorList: [],
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  created() {
    this.getUserInfo()
    this.fetchCounselors()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.username = JSON.parse(userStr).username
      } else {
        this.$router.push('/login')
      }
    },
    async fetchCounselors() {
      try {
        const res = await axios.get('/appointment/counselors')
        if (res.data.code === 200) {
          this.counselorList = res.data.data
        }
      } catch (error) {
        console.error('获取咨询师失败:', error)
        this.$message.error('加载咨询师列表失败')
      }
    },
    bookCounselor(item) {
      this.$router.push({
        path: '/appointment/form',
        query: {
          counselorId: item.id,
          counselorName: item.realName,
          fee: item.fee
        }
      })
    },
    handleMenuSelect(index) {
      const routes = {
        appointment: '/appointment',
        myAppointment: '/appointment/list',
        evaluate: '/evaluate',
        report: '/report',
        account: '/account'
      }
      if (routes[index]) {
        this.$router.push(routes[index])
      }
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$message.info('个人资料功能开发中...')
      } else if (command === 'logout') {
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.app-header {
  background-color: #1e2a3a;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.header-left .platform-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
}

.header-right .user-info:hover {
  background-color: #2a3a4a;
}

.user-avatar {
  margin-right: 8px;
  background-color: #409eff;
}

.user-name {
  color: #ffffff;
  font-size: 14px;
}

.app-aside {
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
  color: #ffffff !important;
}

.side-menu .el-menu-item:not(.is-active):hover {
  background-color: #2a3a4a !important;
  color: #ffffff !important;
}

.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #909399;
}

.counselor-card {
  margin-bottom: 20px;
  text-align: center;
  border-radius: 12px;
  transition: transform 0.3s;
}

.counselor-card:hover {
  transform: translateY(-4px);
}

.counselor-avatar {
  margin: 20px 0;
}

.counselor-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.counselor-title {
  font-size: 14px;
  color: #409eff;
  margin-bottom: 12px;
}

.counselor-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  padding: 0 10px;
  line-height: 1.5;
}

.counselor-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 16px;
}
</style>

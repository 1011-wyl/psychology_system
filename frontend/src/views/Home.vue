<template>
  <el-container class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="home-header">
      <div class="header-left">
        <span class="platform-title">心理咨询与诊断平台</span>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="36" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
            <span class="user-name">用户：{{ user.username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人资料</el-dropdown-item>
            <el-dropdown-item command="account">我的账户</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
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
      <el-main class="home-main">
        <!-- 欢迎卡片 -->
        <el-row :gutter="20" class="welcome-row">
          <el-col :span="24">
            <el-card class="welcome-card" shadow="never">
              <div class="welcome-content">
                <div class="welcome-text">
                  <h2>欢迎回来，{{ user.username }}！</h2>
                  <p>今天是 {{ currentDate }}，祝您心情愉快！</p>
                </div>
                <div class="balance-info">
                  <span class="balance-label">账户余额</span>
                  <span class="balance-value">¥ {{ user.balance }}</span>
                  <el-button type="success" size="small" plain @click="goToAccount">充值</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 快捷入口 -->
        <el-row :gutter="20" class="quick-entry">
          <el-col :xs="12" :sm="12" :md="6" v-for="item in quickEntries" :key="item.key">
            <el-card class="entry-card" shadow="hover" @click.native="goToPage(item.key)">
              <div class="entry-content">
                <i :class="item.icon" class="entry-icon"></i>
                <span class="entry-title">{{ item.title }}</span>
                <span class="entry-desc">{{ item.desc }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 最新报告预览 -->
        <el-row :gutter="20" class="recent-reports">
          <el-col :span="24">
            <el-card class="reports-card" shadow="never">
              <div slot="header" class="card-header">
                <span>最新评估报告</span>
                <el-link type="primary" @click="goToReport">查看全部</el-link>
              </div>
              <el-table :data="recentReports" stripe style="width: 100%">
                <el-table-column prop="createTime" label="评估日期" width="180"></el-table-column>
                <el-table-column prop="fieldName" label="评估领域"></el-table-column>
                <el-table-column prop="conclusion" label="评测结果"></el-table-column>
                <el-table-column label="操作" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="viewReport(scope.row.id)">查看报告</el-button>
                  </template>
                </el-table-column>
              </el-table>
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
  name: 'UserHome',
  data() {
    return {
      user: {},
      activeMenu: '',
      currentDate: '',
      quickEntries: [
        { key: 'appointment', title: '我要预约', desc: '预约咨询师', icon: 'el-icon-date' },
        { key: 'evaluate', title: '在线评估', desc: '心理健康测评', icon: 'el-icon-edit' },
        { key: 'report', title: '我的报告', desc: '查看评估报告', icon: 'el-icon-document' },
        { key: 'account', title: '我的账户', desc: '余额查询充值', icon: 'el-icon-money' }
      ],
      recentReports: []
    }
  },
  created() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      this.user = JSON.parse(userStr)
      const path = this.$route.path
      if (path === '/appointment') this.activeMenu = 'appointment'
      else if (path === '/report') this.activeMenu = 'report'
      else if (path === '/evaluate') this.activeMenu = 'evaluate'
      else if (path === '/account') this.activeMenu = 'account'
      else this.activeMenu = ''
    } else {
      this.$router.push('/login')
    }
    this.currentDate = this.getCurrentDate()
    this.fetchRecentReports()
  },
  methods: {
    getCurrentDate() {
      const date = new Date()
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const weekday = weekdays[date.getDay()]
      return `${year}年${month}月${day}日 ${weekday}`
    },
    async fetchRecentReports() {
      try {
        const response = await axios.get('/evaluate/reports')
        if (response.data.code === 200) {
          this.recentReports = response.data.data.slice(0, 3)
        }
      } catch (error) {
        console.error('获取报告失败:', error)
      }
    },
    handleMenuSelect(index) {
      this.activeMenu = index
      this.goToPage(index)
    },
    goToPage(page) {
      if (page === 'appointment') {
        this.$router.push('/appointment')
      } else if (page === 'report') {
        this.$router.push('/report')
      } else if (page === 'evaluate') {
        this.$router.push('/evaluate')
      } else if (page === 'account') {
        this.$router.push('/account')
      }
    },
    goToAccount() {
      this.$router.push('/account')
    },
    goToReport() {
      this.$router.push('/report')
    },
    viewReport(id) {
      this.$router.push(`/report/detail/${id}`)
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$message.info('个人资料功能开发中...')
      } else if (command === 'account') {
        this.goToAccount()
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
.home-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

/* 顶部导航栏 */
.home-header {
  background-color: #1e2a3a;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 60px;
}

.header-left .platform-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 1px;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
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
  margin-right: 4px;
}

/* 左侧侧边栏 */
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
  font-size: 14px;
  transition: all 0.3s;
}

.side-menu .el-menu-item.is-active {
  background-color: #20bfa0 !important;
  color: #ffffff !important;
}

.side-menu .el-menu-item:not(.is-active):hover {
  background-color: #2a3a4a !important;
  color: #ffffff !important;
}

.side-menu .el-menu-item i {
  margin-right: 12px;
  font-size: 18px;
}

/* 右侧主内容区 */
.home-main {
  background-color: #f0f2f5;
  padding: 20px;
}

/* 欢迎卡片 */
.welcome-row {
  margin-bottom: 20px;
}

.welcome-card {
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.85;
}

.balance-info {
  text-align: right;
}

.balance-label {
  font-size: 14px;
  opacity: 0.85;
  margin-right: 12px;
}

.balance-value {
  font-size: 28px;
  font-weight: bold;
  margin-right: 16px;
}

/* 快捷入口 */
.quick-entry {
  margin-bottom: 20px;
}

.entry-card {
  margin-bottom: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.entry-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.entry-content {
  text-align: center;
  padding: 16px;
}

.entry-icon {
  font-size: 40px;
  color: #409eff;
  display: block;
  margin-bottom: 12px;
}

.entry-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.entry-desc {
  font-size: 12px;
  color: #909399;
}

/* 最新报告卡片 */
.reports-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 滚动条 */
.app-aside::-webkit-scrollbar {
  width: 6px;
}

.app-aside::-webkit-scrollbar-track {
  background: #1e2a3a;
}

.app-aside::-webkit-scrollbar-thumb {
  background: #2a3a4a;
  border-radius: 3px;
}
</style>

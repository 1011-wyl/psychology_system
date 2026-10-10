<template>
  <el-container class="evaluate-container">
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
        <div class="evaluate-container">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-card class="header-card" shadow="never">
                <h2>在线心理评估</h2>
                <p>请选择您想要评估的领域，系统将根据您的答题情况生成专业的心理评估报告</p>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="field-grid">
            <el-col :xs="24" :sm="12" :md="8" v-for="field in fieldList" :key="field.id">
              <el-card class="field-card" shadow="hover" @click.native="startEvaluate(field.id, field.fieldName)">
                <div class="field-icon">
                  <i :class="getFieldIcon(field.fieldName)"></i>
                </div>
                <div class="field-name">{{ field.fieldName }}</div>
                <div class="field-desc">{{ field.description || '心理健康评估' }}</div>
                <el-button type="primary" size="small" plain>开始评估</el-button>
              </el-card>
            </el-col>
          </el-row>

          <!-- 历史评估记录 -->
          <el-row :gutter="20" class="history-section">
            <el-col :span="24">
              <el-card class="history-card" shadow="never">
                <div slot="header" class="card-header">
                  <span>历史评估记录</span>
                  <el-link type="primary" @click="goToReport">查看全部报告</el-link>
                </div>
                <el-table :data="historyReports" stripe style="width: 100%">
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
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'UserEvaluate',
  data() {
    return {
      activeMenu: 'evaluate',
      username: '',
      fieldList: [],
      historyReports: []
    }
  },
  created() {
    this.getUserInfo()
    this.fetchFields()
    this.fetchHistoryReports()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.username = JSON.parse(userStr).username
      }
    },
    async fetchFields() {
      try {
        const response = await axios.get('/evaluate/fields')
        if (response.data.code === 200) {
          this.fieldList = response.data.data
        }
      } catch (error) {
        console.error('获取评估领域失败:', error)
        this.$message.error('加载评估领域失败')
      }
    },
    async fetchHistoryReports() {
      try {
        const response = await axios.get('/evaluate/reports')
        if (response.data.code === 200) {
          this.historyReports = response.data.data
        }
      } catch (error) {
        console.error('获取历史报告失败:', error)
      }
    },
    getFieldIcon(fieldName) {
      const iconMap = {
        '焦虑评估': 'el-icon-warning',
        '抑郁评估': 'el-icon-s-platform',
        '压力评估': 'el-icon-s-data'
      }
      return iconMap[fieldName] || 'el-icon-document'
    },
    startEvaluate(fieldId, fieldName) {
      this.$router.push({
        path: '/evaluate/question',
        query: { fieldId, fieldName }
      })
    },
    viewReport(id) {
      this.$router.push(`/report/detail/${id}`)
    },
    handleMenuSelect(index) {
      const routes = {
        appointment: '/appointment',
        report: '/report',
        evaluate: '/evaluate',
        account: '/account'
      }
      if (routes[index]) {
        this.$router.push(routes[index])
      }
    },
    goToReport() {
      this.$router.push('/report')
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
.evaluate-container {
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

.header-card {
  margin-bottom: 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-card h2 {
  margin: 0 0 10px;
}

.header-card p {
  margin: 0;
  opacity: 0.9;
}

.field-grid {
  margin-bottom: 30px;
}

.field-card {
  margin-bottom: 20px;
  text-align: center;
  cursor: pointer;
  padding: 20px;
  transition: all 0.3s ease;
}

.field-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.field-icon i {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 15px;
}

.field-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.field-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 15px;
}

.history-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

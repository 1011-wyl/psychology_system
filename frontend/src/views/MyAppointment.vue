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

      <el-main class="app-main">
        <div class="page-header">
          <h2>我的预约</h2>
          <p>查看您所有的预约记录</p>
        </div>

        <el-table :data="appointmentList" stripe>
          <el-table-column prop="appointmentDate" label="预约日期" width="120"></el-table-column>
          <el-table-column prop="appointmentTime" label="时间段" width="120"></el-table-column>
          <el-table-column label="咨询师" width="150">
            <template slot-scope="scope">
              {{ getCounselorName(scope.row.counselorId) }}
            </template>
          </el-table-column>
          <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip></el-table-column>
          <el-table-column prop="fee" label="费用" width="100">
            <template slot-scope="scope">
              ¥ {{ scope.row.fee }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="appointmentList.length === 0" description="暂无预约记录"></el-empty>

        <el-dialog title="预约详情" :visible.sync="detailVisible" width="520px">
          <el-descriptions v-if="selectedAppointment" :column="1" border>
            <el-descriptions-item label="预约日期">
              {{ selectedAppointment.appointmentDate }}
            </el-descriptions-item>
            <el-descriptions-item label="时间段">
              {{ selectedAppointment.appointmentTime }}
            </el-descriptions-item>
            <el-descriptions-item label="咨询师">
              {{ getCounselorName(selectedAppointment.counselorId) }}
            </el-descriptions-item>
            <el-descriptions-item label="问题描述">
              {{ selectedAppointment.problemDesc }}
            </el-descriptions-item>
            <el-descriptions-item label="费用">
              ¥ {{ selectedAppointment.fee }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedAppointment.status)">
                {{ getStatusText(selectedAppointment.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <span slot="footer">
            <el-button type="primary" @click="detailVisible = false">关闭</el-button>
          </span>
        </el-dialog>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'MyAppointment',
  data() {
    return {
      activeMenu: 'myAppointment',
      username: '',
      appointmentList: [],
      counselorMap: {},
      detailVisible: false,
      selectedAppointment: null
    }
  },
  created() {
    this.getUserInfo()
    this.fetchCounselors()
    this.fetchAppointments()
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
          const map = {}
          res.data.data.forEach(c => {
            map[c.id] = c.realName
          })
          this.counselorMap = map
        }
      } catch (error) {
        console.error('获取咨询师失败:', error)
      }
    },
    async fetchAppointments() {
      try {
        const res = await axios.get('/appointment/list')
        if (res.data.code === 200) {
          this.appointmentList = res.data.data
        }
      } catch (error) {
        console.error('获取预约列表失败:', error)
        this.$message.error('加载预约列表失败')
      }
    },
    getCounselorName(id) {
      return this.counselorMap[id] || '未知'
    },
    getStatusType(status) {
      const map = { 0: 'warning', 1: 'info', 2: 'success', 3: 'success', 4: 'success', 5: 'danger' }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = { 0: '待确认', 1: '预约失败', 2: '已确认', 3: '已诊断', 4: '已评价', 5: '被终止' }
      return map[status] || '未知'
    },
    viewDetail(row) {
      this.selectedAppointment = row
      this.detailVisible = true
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

.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #909399;
}
</style>

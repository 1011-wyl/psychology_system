<template>
  <PortalShell role="user">
    <div class="report-header-card">
      <h2>我的评估报告</h2>
      <p>查看所有历史评估记录，了解您的心理健康状况变化</p>
    </div>

    <!-- 报告列表 -->
    <el-row :gutter="20" class="report-grid">
      <el-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        v-for="report in reportList"
        :key="report.id"
      >
        <el-card
          class="report-card"
          shadow="hover"
          role="link"
          tabindex="0"
          @keydown.enter.native="viewReport(report.id)"
          @click.native="viewReport(report.id)"
        >
          <div class="report-date">{{ formatDate(report.createTime) }}</div>
          <div class="report-field">{{ getFieldName(report.fieldId) }}</div>
          <div class="report-score">
            得分：<span :class="getScoreClass(report.totalScore)">{{
              report.totalScore
            }}</span>
          </div>
          <div
            class="report-conclusion"
            :class="getConclusionClass(report.totalScore)"
          >
            {{ report.conclusion }}
          </div>
          <div class="report-link">查看详情 →</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty
      v-if="reportList.length === 0"
      description="暂无评估报告，快去完成一次评估吧！"
    ></el-empty>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>
  </PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
import axios from '@/api/http'

export default {
  name: 'UserReport',
  components: { PortalShell },
  data() {
    return {
      activeMenu: 'report',
      username: '',
      reportList: [],
      fieldMap: {},
      currentPage: 1,
      pageSize: 8,
      total: 0,
    }
  },
  created() {
    this.getUserInfo()
    this.fetchFields()
    this.fetchReports()
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
    async fetchFields() {
      try {
        const res = await axios.get('/evaluate/fields')
        if (res.data.code === 200) {
          const map = {}
          res.data.data.forEach((field) => {
            map[field.id] = field.fieldName
          })
          this.fieldMap = map
        }
      } catch (error) {
        console.error('获取领域失败:', error)
      }
    },
    async fetchReports() {
      try {
        const res = await axios.get('/evaluate/reports')
        if (res.data.code === 200) {
          this.reportList = res.data.data
          this.total = this.reportList.length
        }
      } catch (error) {
        console.error('获取报告失败:', error)
        this.$message.error('加载报告失败')
      }
    },
    getFieldName(fieldId) {
      return this.fieldMap[fieldId] || '未知领域'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
        2,
        '0'
      )}-${String(date.getDate()).padStart(2, '0')}`
    },
    getScoreClass(score) {
      if (score <= 10) return 'score-good'
      if (score <= 15) return 'score-warning'
      if (score <= 20) return 'score-danger'
      return 'score-severe'
    },
    getConclusionClass(score) {
      if (score <= 10) return 'conclusion-good'
      if (score <= 15) return 'conclusion-warning'
      if (score <= 20) return 'conclusion-danger'
      return 'conclusion-severe'
    },
    viewReport(id) {
      this.$router.push(`/report/detail/${id}`)
    },
    handleMenuSelect(index) {
      const routes = {
        appointment: '/appointment',
        report: '/report',
        evaluate: '/evaluate',
        account: '/account',
      }
      if (routes[index]) {
        this.$router.push(routes[index])
      }
    },
    handlePageChange(page) {
      this.currentPage = page
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
    },
  },
}
</script>

<style scoped>
.report-container {
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

.report-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.report-header-card h2 {
  margin: 0 0 8px;
}

.report-header-card p {
  margin: 0;
  opacity: 0.9;
}

.report-grid {
  margin-bottom: 20px;
}

.report-card {
  margin-bottom: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.report-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.report-date {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eaeef2;
}

.report-field {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.report-score {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.score-good {
  color: #67c23a;
  font-weight: bold;
  font-size: 18px;
}

.score-warning {
  color: #e6a23c;
  font-weight: bold;
  font-size: 18px;
}

.score-danger {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.score-severe {
  color: #ff0000;
  font-weight: bold;
  font-size: 18px;
}

.report-conclusion {
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 12px;
}

.conclusion-good {
  background-color: #d4edda;
  color: #155724;
}

.conclusion-warning {
  background-color: #fff3cd;
  color: #856404;
}

.conclusion-danger {
  background-color: #f8d7da;
  color: #721c24;
}

.conclusion-severe {
  background-color: #f5c6cb;
  color: #721c24;
}

.report-link {
  font-size: 13px;
  color: #409eff;
  text-align: right;
  margin-top: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

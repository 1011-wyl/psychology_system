<template>
  <div class="role-page admin-page">
    <div class="role-page-heading">
      <div><span>平台概览</span><h1>管理工作台</h1></div>
      <p>快速了解平台用户、咨询师与预约运行情况</p>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover" @click.native="goToUser">
          <div class="stat-icon"><i class="el-icon-user"></i></div>
          <div class="stat-info">
            <div class="stat-title">用户总数</div>
            <div class="stat-value">{{ statistics.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover" @click.native="goToCounselor">
          <div class="stat-icon"><i class="el-icon-s-custom"></i></div>
          <div class="stat-info">
            <div class="stat-title">咨询师总数</div>
            <div class="stat-value">{{ statistics.counselorCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover" @click.native="goToAppointment">
          <div class="stat-icon"><i class="el-icon-date"></i></div>
          <div class="stat-info">
            <div class="stat-title">预约总数</div>
            <div class="stat-value">{{ statistics.appointmentCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-card" style="margin-top: 20px">
      <div slot="header">
        <span>最近预约记录</span>
        <el-button type="text" style="float: right" @click="goToAppointment">查看更多</el-button>
      </div>
      <el-table :data="recentAppointments" stripe>
        <el-table-column prop="appointmentDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="时间" width="120"></el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
        <el-table-column prop="counselorId" label="咨询师ID" width="100"></el-table-column>
        <el-table-column label="状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'AdminHome',
  data() {
    return {
      statistics: {},
      recentAppointments: []
    }
  },
  created() {
    this.fetchStatistics()
    this.fetchAppointments()
  },
  methods: {
    async fetchStatistics() {
      try {
        const res = await axios.get('/admin/statistics')
        if (res.data.code === 200) {
          this.statistics = res.data.data
        }
      } catch (error) {
        console.error('获取统计失败:', error)
      }
    },
    async fetchAppointments() {
      try {
        const res = await axios.get('/admin/appointments')
        if (res.data.code === 200) {
          this.recentAppointments = res.data.data.slice(0, 5)
        }
      } catch (error) {
        console.error('获取预约失败:', error)
      }
    },
    getStatusType(status) {
      const map = { 0: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'danger' }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = { 0: '待确认', 2: '待诊断', 3: '已诊断', 4: '已评价', 5: '已取消' }
      return map[status] || '未知'
    },
    goToUser() { this.$router.push('/admin/user') },
    goToCounselor() { this.$router.push('/admin/counselor') },
    goToAppointment() { this.$router.push('/admin/appointment') }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
}
.stat-icon i {
  font-size: 48px;
  color: #409eff;
}
.stat-info {
  text-align: right;
}
.stat-title {
  font-size: 14px;
  color: #909399;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}
</style>

<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">今日预约</div>
          <div class="stat-value">{{ todayCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">待确认</div>
          <div class="stat-value">{{ pendingCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">总收入</div>
          <div class="stat-value">¥ {{ totalIncome }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-card" style="margin-top: 20px">
      <div slot="header">
        <span>最近预约</span>
      </div>
      <el-table :data="recentAppointments" stripe>
        <el-table-column prop="appointmentDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="时间" width="120"></el-table-column>
        <el-table-column label="用户" width="120">
          <template slot-scope="scope">用户{{ scope.row.userId }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'">
              {{ scope.row.status === 0 ? '待确认' : '已确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorHome',
  data() {
    return {
      todayCount: 0,
      pendingCount: 0,
      totalIncome: 0,
      recentAppointments: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const res = await axios.get('/counselor/appointments')
        if (res.data.code === 200) {
          const list = res.data.data
          this.recentAppointments = list.slice(0, 5)
          this.pendingCount = list.filter(item => item.status === 0).length
          this.todayCount = list.filter(item => item.appointmentDate === new Date().toISOString().slice(0, 10)).length
          this.totalIncome = list.filter(item => item.status >= 2).reduce((sum, item) => sum + (item.fee || 0), 0)
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    },
    viewDetail(id) {
      this.$router.push(`/counselor/appointment/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
  border-radius: 12px;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}
</style>

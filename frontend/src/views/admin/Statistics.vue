<template>
  <div class="role-page admin-page">
    <div class="role-page-heading">
      <div><span>运营数据</span><h1>统计报表</h1></div>
      <p>查看平台当前的核心服务规模</p>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon"><i class="el-icon-user"></i></div>
          <div class="stat-info">
            <div class="stat-title">用户总数</div>
            <div class="stat-value">{{ statistics.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon"><i class="el-icon-s-custom"></i></div>
          <div class="stat-info">
            <div class="stat-title">咨询师总数</div>
            <div class="stat-value">{{ statistics.counselorCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon"><i class="el-icon-date"></i></div>
          <div class="stat-info">
            <div class="stat-title">预约总数</div>
            <div class="stat-value">{{ statistics.appointmentCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'AdminStatistics',
  data() {
    return {
      statistics: {}
    }
  },
  created() {
    this.fetchStatistics()
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
    }
  }
}
</script>

<style scoped>
.stat-card {
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

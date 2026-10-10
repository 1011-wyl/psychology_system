<template>
  <div>
    <el-card class="balance-card" shadow="hover">
      <div class="balance-header">
        <span class="balance-title">账户余额</span>
        <span class="balance-value">¥ {{ counselor.balance || 0 }}</span>
      </div>
      <div class="balance-stats">
        <div class="stat-item">
          <div class="stat-label">累计收入</div>
          <div class="stat-number">¥ {{ totalIncome }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">成功订单</div>
          <div class="stat-number">{{ successCount }} 单</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">待诊断</div>
          <div class="stat-number">{{ pendingCount }} 单</div>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <div slot="header">
        <span>收入明细</span>
        <el-button type="text" style="float: right" @click="fetchData">刷新</el-button>
      </div>
      
      <el-table :data="incomeList" stripe v-loading="loading">
        <el-table-column prop="createTime" label="时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
        <el-table-column prop="problemDesc" label="咨询内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="scope">
            <span style="color: #67c23a">+ ¥ {{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default>
            <el-tag type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row.appointmentId)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="incomeList.length === 0" description="暂无收入记录"></el-empty>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorAccount',
  data() {
    return {
      counselor: {},
      incomeList: [],
      totalIncome: 0,
      successCount: 0,
      pendingCount: 0,
      loading: false
    }
  },
  created() {
    this.getCounselorInfo()
    this.fetchData()
  },
  methods: {
    getCounselorInfo() {
      const counselorStr = localStorage.getItem('counselor')
      if (counselorStr) {
        this.counselor = JSON.parse(counselorStr)
      }
    },
    async fetchData() {
  this.loading = true
  try {
    // 先获取最新的咨询师信息
    const counselorRes = await axios.get('/counselor/info')
    if (counselorRes.data.code === 200) {
      this.counselor = counselorRes.data.data
      // 更新 localStorage
      localStorage.setItem('counselor', JSON.stringify(this.counselor))
    }
    
    const res = await axios.get('/counselor/appointments')
    if (res.data.code === 200) {
      const list = res.data.data
      
      const completed = list.filter(item => item.status === 3 || item.status === 4)
      this.successCount = completed.length
      this.totalIncome = completed.reduce((sum, item) => sum + (item.fee || 0), 0)
      
      this.pendingCount = list.filter(item => item.status === 2).length
      
      this.incomeList = completed.sort((a, b) => 
        new Date(b.confirmTime || b.createTime) - new Date(a.confirmTime || a.createTime)
      ).map(item => ({
        appointmentId: item.id,
        userId: item.userId,
        problemDesc: item.problemDesc,
        amount: item.fee,
        createTime: item.confirmTime || item.createTime
      }))
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    this.$message.error('加载失败')
  } finally {
    this.loading = false
  }
},
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    viewDetail(id) {
      this.$router.push(`/counselor/appointment/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}
.balance-header {
  text-align: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.2);
}
.balance-title {
  font-size: 14px;
  opacity: 0.8;
  display: block;
  margin-bottom: 10px;
}
.balance-value {
  font-size: 48px;
  font-weight: bold;
}
.balance-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px;
}
.stat-item {
  text-align: center;
}
.stat-label {
  font-size: 13px;
  opacity: 0.8;
  margin-bottom: 8px;
}
.stat-number {
  font-size: 24px;
  font-weight: bold;
}
</style>

<template>
  <PortalShell role="user">
    <div class="page-header">
      <h2>我的账户</h2>
      <p>查看账户余额和收支明细</p>
    </div>

    <!-- 余额卡片 -->
    <el-card class="balance-card" shadow="hover">
      <div class="balance-header">
        <span class="balance-title">账户余额</span>
        <span class="balance-value">¥ {{ user.balance || 0 }}</span>
      </div>
      <div class="balance-stats">
        <div class="stat-item">
          <div class="stat-label">累计消费</div>
          <div class="stat-number">¥ {{ totalExpense }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">累计预约</div>
          <div class="stat-number">{{ appointmentCount }} 次</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">已完成</div>
          <div class="stat-number">{{ completedCount }} 次</div>
        </div>
      </div>
      <div v-if="rechargeEnabled" class="recharge-btn">
        <el-button type="success" @click="showRechargeDialog = true"
          >充值</el-button
        >
      </div>
    </el-card>

    <!-- 收支明细 -->
    <el-card style="margin-top: 20px">
      <div slot="header">
        <span>收支明细</span>
        <el-button type="text" style="float: right" @click="fetchData"
          >刷新</el-button
        >
      </div>

      <el-table :data="transactionList" stripe v-loading="loading">
        <el-table-column prop="createTime" label="时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="事项"
          min-width="150"
        ></el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.type === 'income'" style="color: #67c23a">
              + ¥ {{ scope.row.amount }}
            </span>
            <span v-else style="color: #f56c6c">
              - ¥ {{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="120">
          <template slot-scope="scope"> ¥ {{ scope.row.balance }} </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 充值弹窗 -->
    <el-dialog
      v-if="rechargeEnabled"
      title="充值"
      :visible.sync="showRechargeDialog"
      width="400px"
    >
      <el-form>
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeAmount"
            :min="1"
            :max="10000"
            :step="10"
          ></el-input-number>
          <span style="margin-left: 10px">元</span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showRechargeDialog = false">取消</el-button>
        <el-button type="primary" @click="recharge" :loading="rechargeLoading"
          >确认充值</el-button
        >
      </span>
    </el-dialog>
  </PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
import axios, { getErrorMessage } from '@/api/http'

export default {
  name: 'UserAccount',
  components: { PortalShell },
  data() {
    return {
      activeMenu: 'account',
      user: {},
      transactionList: [],
      totalExpense: 0,
      appointmentCount: 0,
      completedCount: 0,
      loading: false,
      rechargeEnabled: process.env.VUE_APP_DEMO_RECHARGE_ENABLED === 'true',
      showRechargeDialog: false,
      rechargeAmount: 100,
      rechargeLoading: false,
    }
  },
  created() {
    this.getUserInfo()
    this.fetchData()
  },
  methods: {
    getUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      } else {
        this.$router.push('/login')
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
        2,
        '0'
      )}-${String(date.getDate()).padStart(2, '0')} ${String(
        date.getHours()
      ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    async fetchData() {
      this.loading = true
      try {
        const appointmentsRes = await axios.get('/appointment/list')
        const transactionsRes = await axios.get('/user/transactions')

        if (appointmentsRes.data.code === 200) {
          const list = appointmentsRes.data.data
          this.appointmentCount = list.length
          this.completedCount = list.filter(
            (item) => item.status === 3 || item.status === 4
          ).length
        }

        if (transactionsRes.data.code === 200) {
          const ledger = transactionsRes.data.data
          this.totalExpense = ledger
            .filter((item) => item.transactionType === 1)
            .reduce((sum, item) => sum + Number(item.amount || 0), 0)
          this.transactionList = ledger.map((item) => ({
            createTime: item.createTime,
            description: item.description,
            amount: item.amount,
            type: item.transactionType === 0 ? 'income' : 'expense',
            balance: item.balanceAfter,
          }))
          if (ledger.length > 0) {
            this.user.balance = ledger[0].balanceAfter
            localStorage.setItem('user', JSON.stringify(this.user))
          }
        }
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error(getErrorMessage(error, '获取账户数据失败'))
      } finally {
        this.loading = false
      }
    },

    // 刷新所有数据
    async refresh() {
      await this.fetchData() // 刷新统计数据
      this.$message.success('刷新成功')
    },
    async recharge() {
      if (!this.rechargeAmount || this.rechargeAmount <= 0) {
        this.$message.warning('请输入正确的充值金额')
        return
      }
      this.rechargeLoading = true
      try {
        const res = await axios.post('/user/recharge', {
          amount: this.rechargeAmount,
        })
        if (res.data.code === 200) {
          this.user.balance = res.data.data.balance
          localStorage.setItem('user', JSON.stringify(this.user))
          this.$message.success(`充值成功！充值金额：¥${this.rechargeAmount}`)
          this.showRechargeDialog = false
          this.fetchData() // 刷新数据
        } else {
          this.$message.error(res.data.msg || '充值失败')
        }
      } catch (error) {
        console.error('充值失败:', error)
        this.$message.error(getErrorMessage(error, '充值失败，请稍后重试'))
      } finally {
        this.rechargeLoading = false
      }
    },
    handleMenuSelect(index) {
      const routes = {
        appointment: '/appointment',
        myAppointment: '/appointment/list',
        evaluate: '/evaluate',
        report: '/report',
        account: '/account',
      }
      if (routes[index]) {
        this.$router.push(routes[index])
      }
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$message.info('个人资料功能开发中...')
      } else if (command === 'account') {
        this.$router.push('/account')
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

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.balance-header {
  text-align: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
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

.recharge-btn {
  text-align: center;
  padding-bottom: 20px;
}
</style>

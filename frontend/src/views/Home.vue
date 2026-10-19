<template>
  <PortalShell role="user">
    <div class="home-heading">
      <div>
        <h1>欢迎回来，{{ user.username }}</h1>
        <p>{{ currentDate }} · 今天也可以按自己的节奏来</p>
      </div>
      <button class="soft-account" type="button" @click="goToAccount">
        <span>账户余额</span>
        <strong>¥ {{ user.balance || 0 }}</strong>
        <i class="el-icon-arrow-right"></i>
      </button>
    </div>

    <el-row :gutter="22" class="welcome-row">
      <el-col :xs="24" :lg="16">
        <el-card class="welcome-card" shadow="never">
          <div class="welcome-content">
            <span class="welcome-label">给自己，一点补给</span>
            <div class="welcome-text">
              <h2>先歇一会儿，再出发。</h2>
              <p>预约专业咨询，或从一次心理评估开始。</p>
            </div>
            <el-button
              class="welcome-action"
              type="primary"
              @click="goToPage('appointment')"
            >
              找一位咨询师聊聊
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div
          class="bottle-scene"
          role="img"
          aria-label="薄荷绿与明黄色的微笑情绪瓶，陈列在补给架上"
        >
          <span class="bottle-scene-arch" aria-hidden="true"></span>
          <span class="bottle-scene-halo" aria-hidden="true"></span>
          <span
            class="bottle-scene-mint refill-character refill-character--mint"
            aria-hidden="true"
          ></span>
          <span
            class="bottle-scene-gold refill-character refill-character--gold"
            aria-hidden="true"
          ></span>
          <span class="bottle-scene-shelf" aria-hidden="true"></span>
        </div>
      </el-col>
    </el-row>

    <div class="section-heading">
      <div>
        <span>从这里开始</span>
        <p>选择此刻最适合你的支持方式</p>
      </div>
    </div>

    <el-row :gutter="20" class="quick-entry">
      <el-col
        :xs="24"
        :sm="12"
        :lg="6"
        v-for="item in quickEntries"
        :key="item.key"
      >
        <el-card
          :class="['entry-card', `entry-card--${item.accent}`]"
          shadow="never"
          role="link"
          tabindex="0"
          @keydown.enter.native="goToPage(item.key)"
          @click.native="goToPage(item.key)"
        >
          <div class="entry-content">
            <div class="entry-topline">
              <i :class="item.icon" class="entry-icon"></i>
              <i class="el-icon-right entry-arrow"></i>
            </div>
            <span class="entry-kicker">{{ item.kicker }}</span>
            <strong class="entry-title">{{ item.title }}</strong>
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
            <div>
              <span>最近的心理记录</span>
              <small>回看变化，也看见自己走过的路</small>
            </div>
            <el-link type="primary" @click="goToReport">查看全部</el-link>
          </div>
          <el-table :data="recentReports" stripe style="width: 100%">
            <el-table-column
              prop="createTime"
              label="评估日期"
              width="180"
            ></el-table-column>
            <el-table-column
              prop="fieldName"
              label="评估领域"
            ></el-table-column>
            <el-table-column
              prop="conclusion"
              label="评测结果"
            ></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click="viewReport(scope.row.id)"
                  >查看报告</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
import axios from '@/api/http'

export default {
  name: 'UserHome',
  components: { PortalShell },
  data() {
    return {
      user: {},
      activeMenu: '',
      currentDate: '',
      quickEntries: [
        {
          key: 'appointment',
          title: '预约咨询',
          kicker: '有人倾听',
          desc: '选择一位让你感到安心的咨询师',
          icon: 'el-icon-date',
          accent: 'coral',
        },
        {
          key: 'evaluate',
          title: '在线评估',
          kicker: '认识自己',
          desc: '用几分钟了解最近的心理状态',
          icon: 'el-icon-edit',
          accent: 'sage',
        },
        {
          key: 'report',
          title: '心理报告',
          kicker: '看见变化',
          desc: '回顾评估记录与阶段性变化',
          icon: 'el-icon-document',
          accent: 'sun',
        },
        {
          key: 'account',
          title: '我的账户',
          kicker: '清晰安心',
          desc: '查看余额、预约与收支记录',
          icon: 'el-icon-wallet',
          accent: 'plum',
        },
      ],
      recentReports: [],
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
    },
  },
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

/* Warm user dashboard */
.home-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  max-width: 1320px;
  margin: 0 auto 28px;
}

.heading-kicker,
.welcome-label,
.quiet-kicker,
.entry-kicker {
  display: block;
  color: #9a665c;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.home-heading h1 {
  margin: 8px 0 5px;
  color: #4a2f3a;
  font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
  font-size: clamp(28px, 3vw, 40px);
  font-weight: 700;
  letter-spacing: 0.02em;
}

.home-heading p {
  margin: 0;
  color: #806f74;
  font-size: 14px;
}

.soft-account {
  display: grid;
  grid-template-columns: auto auto 18px;
  align-items: center;
  gap: 10px;
  min-height: 52px;
  padding: 8px 10px 8px 18px;
  border: 1px solid rgba(105, 72, 82, 0.14);
  border-radius: 18px;
  color: #6d4b58;
  background: rgba(255, 249, 245, 0.82);
  cursor: pointer;
  transition: transform 180ms ease, box-shadow 180ms ease;
}

.soft-account:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(91, 57, 68, 0.1);
}

.soft-account strong {
  color: #4a2f3a;
  font-size: 20px;
}

.welcome-row,
.quick-entry,
.recent-reports,
.section-heading {
  max-width: 1320px;
  margin-right: auto;
  margin-left: auto;
}

.welcome-row {
  margin-bottom: 34px;
}

.welcome-row > .el-col {
  margin-bottom: 18px;
}

.welcome-card {
  position: relative;
  min-height: 286px;
  border: 0;
  background: #d77867 !important;
  box-shadow: 0 24px 54px rgba(157, 84, 71, 0.2);
}

.welcome-card::after {
  position: absolute;
  right: -38px;
  bottom: -70px;
  width: 240px;
  height: 160px;
  border: 24px solid rgba(255, 249, 245, 0.17);
  border-radius: 50%;
  content: '';
  transform: rotate(-18deg);
}

.welcome-content {
  position: relative;
  z-index: 2;
  display: flex;
  min-height: 244px;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  padding: 18px 22px;
}

.welcome-label {
  color: rgba(255, 255, 255, 0.78);
}

.welcome-text {
  max-width: 590px;
}

.welcome-text h2 {
  margin: 16px 0 12px;
  color: #fffaf6;
  font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
  font-size: clamp(25px, 2.8vw, 38px);
  line-height: 1.25;
}

.welcome-text p {
  max-width: 520px;
  color: rgba(255, 250, 246, 0.86);
  font-size: 15px;
  line-height: 1.8;
}

.welcome-action {
  border-color: #fff9f5 !important;
  color: #4a2f3a !important;
  background: #fff9f5 !important;
}

.sun-shape {
  position: absolute;
  z-index: 1;
  top: 34px;
  right: 46px;
  width: 92px;
  height: 92px;
  border-radius: 50%;
  background: #f3c76d;
  box-shadow: 0 0 0 16px rgba(243, 199, 109, 0.18);
}

.quiet-card {
  display: flex;
  min-height: 286px;
  flex-direction: column;
  justify-content: space-between;
  padding: 28px;
  overflow: hidden;
  border: 1px solid rgba(105, 72, 82, 0.14);
  border-radius: 20px;
  background: #f8e9bd;
  box-shadow: 0 10px 30px rgba(91, 57, 68, 0.06);
}

.quiet-kicker {
  color: #7a6640;
}

.quiet-card p {
  max-width: 230px;
  margin: 7px 0 0;
  color: #4a2f3a;
  font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.45;
}

.quiet-illustration {
  position: relative;
  height: 142px;
  border-bottom: 2px solid rgba(74, 47, 58, 0.28);
}

.chair {
  position: absolute;
  bottom: 1px;
  width: 72px;
  height: 76px;
  border-radius: 30px 30px 13px 13px;
}

.chair::before,
.chair::after {
  position: absolute;
  bottom: -15px;
  width: 5px;
  height: 20px;
  border-radius: 3px;
  background: #4a2f3a;
  content: '';
}

.chair::before {
  left: 13px;
  transform: rotate(7deg);
}
.chair::after {
  right: 13px;
  transform: rotate(-7deg);
}
.chair-left {
  left: 10px;
  background: #8b9e86;
  transform: rotate(2deg);
}
.chair-right {
  right: 8px;
  background: #d77867;
  transform: rotate(-3deg);
}

.small-table {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 48px;
  height: 7px;
  border-radius: 8px;
  background: #4a2f3a;
  transform: translateX(-50%);
}

.small-table::after {
  position: absolute;
  top: 6px;
  left: 21px;
  width: 5px;
  height: 36px;
  background: #4a2f3a;
  content: '';
}

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 15px;
}

.section-heading span {
  color: #4a2f3a;
  font-size: 20px;
  font-weight: 700;
}

.section-heading p {
  margin: 5px 0 0;
  color: #806f74;
  font-size: 13px;
}

.entry-card {
  min-height: 218px;
  margin-bottom: 20px;
  border: 0;
  cursor: pointer;
  transition: transform 180ms ease, box-shadow 180ms ease;
}

.entry-card--coral {
  background: #f4d2c8 !important;
}
.entry-card--sage {
  background: #dce6d8 !important;
}
.entry-card--sun {
  background: #fae8b7 !important;
}
.entry-card--plum {
  background: #e7d9df !important;
}

.entry-content {
  display: flex;
  min-height: 168px;
  flex-direction: column;
  padding: 4px;
  text-align: left;
}

.entry-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.entry-icon {
  display: grid;
  width: 48px;
  height: 48px;
  margin: 0;
  place-items: center;
  border-radius: 15px;
  color: #fff9f5;
  background: #4a2f3a;
  font-size: 22px;
}

.entry-arrow {
  color: #6d4b58;
  font-size: 18px;
}

.entry-kicker {
  margin-bottom: 5px;
  color: #8c5f59;
}

.entry-title {
  margin-bottom: 7px;
  color: #4a2f3a;
  font-size: 19px;
}

.entry-desc {
  color: #6d5b61;
  font-size: 13px;
  line-height: 1.65;
}

.reports-card {
  border-radius: 20px;
  background: rgba(255, 249, 245, 0.92);
}

.card-header > div {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #4a2f3a;
  font-weight: 700;
}

.card-header small {
  color: #806f74;
  font-size: 12px;
  font-weight: 400;
}

@media (max-width: 1199px) {
  .quiet-card {
    min-height: 240px;
    flex-direction: row;
    align-items: center;
    gap: 28px;
  }

  .quiet-illustration {
    width: 250px;
    flex: none;
  }
}

@media (max-width: 700px) {
  .home-heading {
    align-items: flex-start;
    flex-direction: column;
    margin-bottom: 22px;
  }

  .soft-account {
    width: 100%;
    grid-template-columns: 1fr auto 18px;
    text-align: left;
  }

  .sun-shape {
    top: 26px;
    right: 25px;
    width: 62px;
    height: 62px;
  }

  .welcome-text {
    padding-right: 15px;
  }

  .quiet-card {
    min-height: 250px;
    flex-direction: column;
    align-items: stretch;
  }

  .quiet-illustration {
    width: 100%;
  }
}
</style>

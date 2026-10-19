<template>
  <div class="portal-shell" :class="`portal-shell--${role}`">
    <header class="portal-topbar">
      <router-link
        :to="homeRoute"
        class="portal-brand"
        aria-label="情绪补给站首页"
      >
        <span class="portal-brand-mark" aria-hidden="true"
          ><i></i><i></i><i></i
        ></span>
        <span>情绪补给站</span>
      </router-link>
      <span class="portal-role">{{ roleLabel }}</span>
      <div class="portal-session">
        <span class="portal-username"
          ><i class="el-icon-user" aria-hidden="true"></i
          >{{ accountName }}</span
        >
        <button type="button" class="portal-logout" @click="logout">
          退出登录 <i class="el-icon-right" aria-hidden="true"></i>
        </button>
      </div>
    </header>
    <div class="portal-awning" aria-hidden="true"></div>
    <div class="portal-workspace">
      <aside class="portal-sidebar">
        <div class="portal-nav-label">
          {{ role === 'user' ? '你的补给清单' : '工作导航' }}
        </div>
        <nav class="portal-nav" aria-label="主要导航">
          <router-link
            v-for="item in menu"
            :key="item.path"
            :to="item.path"
            custom
            v-slot="{ href, navigate }"
          >
            <a
              :href="href"
              :class="{ 'is-current': activePath === item.path }"
              :aria-current="activePath === item.path ? 'page' : null"
              @click="navigate"
            >
              <i :class="item.icon" aria-hidden="true"></i
              ><span>{{ item.label }}</span
              ><i
                class="el-icon-top-right portal-nav-arrow"
                aria-hidden="true"
              ></i>
            </a>
          </router-link>
        </nav>
        <div class="portal-mascot" aria-hidden="true">
          <span class="refill-character refill-character--coral"></span>
          <span class="portal-mascot-shelf"></span>
        </div>
      </aside>
      <main id="portal-main" class="portal-main">
        <div class="portal-content"><slot /></div>
      </main>
    </div>
  </div>
</template>

<script>
const menus = {
  user: [
    { path: '/home', label: '补给站首页', icon: 'el-icon-house' },
    { path: '/appointment', label: '预约咨询', icon: 'el-icon-chat-dot-round' },
    { path: '/appointment/list', label: '我的预约', icon: 'el-icon-date' },
    { path: '/evaluate', label: '在线评估', icon: 'el-icon-edit-outline' },
    { path: '/report', label: '我的报告', icon: 'el-icon-document' },
    { path: '/account', label: '我的账户', icon: 'el-icon-wallet' },
  ],
  counselor: [
    { path: '/counselor/home', label: '工作台', icon: 'el-icon-house' },
    { path: '/counselor/appointment', label: '预约列表', icon: 'el-icon-date' },
    { path: '/counselor/time-config', label: '时间设置', icon: 'el-icon-time' },
    { path: '/counselor/account', label: '资金账户', icon: 'el-icon-wallet' },
  ],
  admin: [
    { path: '/admin/home', label: '工作台', icon: 'el-icon-house' },
    { path: '/admin/user', label: '用户管理', icon: 'el-icon-user' },
    { path: '/admin/counselor', label: '咨询师管理', icon: 'el-icon-s-custom' },
    { path: '/admin/appointment', label: '预约管理', icon: 'el-icon-date' },
    { path: '/admin/statistics', label: '统计报表', icon: 'el-icon-data-line' },
  ],
}

export default {
  name: 'PortalShell',
  props: {
    role: {
      type: String,
      default: 'user',
      validator: (value) => Object.prototype.hasOwnProperty.call(menus, value),
    },
  },
  computed: {
    menu() {
      return menus[this.role]
    },
    homeRoute() {
      return this.menu[0].path
    },
    roleLabel() {
      return { user: '用户空间', counselor: '咨询师工作室', admin: '管理中心' }[
        this.role
      ]
    },
    accountName() {
      try {
        const account = JSON.parse(localStorage.getItem(this.role) || '{}')
        return account.realName || account.username || this.roleLabel
      } catch {
        return this.roleLabel
      }
    },
    activePath() {
      const path = this.$route.path
      const match = [...this.menu]
        .sort((a, b) => b.path.length - a.path.length)
        .find((item) => path === item.path || path.startsWith(`${item.path}/`))
      return match ? match.path : ''
    },
  },
  methods: {
    logout() {
      localStorage.removeItem(this.role)
      localStorage.removeItem('token')
      this.$router.push(this.role === 'user' ? '/login' : `/${this.role}/login`)
      this.$message.success('已退出登录')
    },
  },
}
</script>

<template>
  <main
    class="refill-gate"
    :class="[`refill-gate--${role}`, { 'is-paused': paused }]"
    @pointermove="moveScene"
    @pointerleave="resetScene"
  >
    <div ref="scene" class="refill-scene" aria-hidden="true">
      <img
        class="store-backdrop"
        src="@/assets/login/refill-store-v3.webp"
        alt=""
      />
      <div class="scene-light"></div>
      <div class="store-reflection store-reflection--left"></div>
      <div class="store-reflection store-reflection--right"></div>
      <div class="store-lamp"></div>
    </div>
    <header class="refill-header">
      <nav class="refill-roles" aria-label="选择登录身份">
        <span
          class="role-slider"
          :style="{ transform: `translateX(${roleIndex * 100}%)` }"
          aria-hidden="true"
        ></span
        ><router-link
          v-for="entry in roles"
          :key="entry.key"
          :to="entry.path"
          :aria-current="role === entry.key ? 'page' : null"
          :class="{ 'is-selected': role === entry.key }"
          >{{ entry.label }}</router-link
        >
      </nav>
    </header>
    <h1 class="store-sign">
      情绪补给站<span class="store-sign-dot" aria-hidden="true"></span>
    </h1>
    <section class="refill-stage" aria-label="情绪补给站登录">
      <div class="bottle-display bottle-display--mint" aria-hidden="true">
        <div class="bottle-float">
          <div class="bottle-art bottle-art--mint"></div>
        </div>
        <div class="bottle-ground"></div>
      </div>
      <div class="bottle-display bottle-display--coral" aria-hidden="true">
        <div class="bottle-float">
          <div class="bottle-art bottle-art--coral"></div>
        </div>
        <div class="bottle-ground"></div>
      </div>
      <div class="receipt-wrap">
        <div class="receipt-clip" aria-hidden="true"></div>
        <section
          class="refill-receipt"
          :aria-labelledby="`receipt-title-${role}`"
        >
          <transition name="receipt-copy" mode="out-in"
            ><div :key="role" class="receipt-copy">
              <h2 :id="`receipt-title-${role}`">
                {{ config.title }}
              </h2>
            </div></transition
          >
          <form
            ref="loginForm"
            class="refill-form"
            novalidate
            @submit.prevent="submitLogin"
          >
            <div class="refill-field" :class="{ 'has-error': errors.username }">
              <label for="refill-username">{{
                role === 'user' ? '用户名' : '工作账号'
              }}</label>
              <div class="field-box">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <circle cx="12" cy="8" r="3.5" />
                  <path d="M5 21v-2a7 7 0 0 1 14 0v2" /></svg
                ><input
                  id="refill-username"
                  ref="username"
                  v-model.trim="form.username"
                  name="username"
                  autocomplete="username"
                  placeholder="请输入你的账号"
                  :aria-invalid="Boolean(errors.username)"
                  :aria-describedby="errors.username ? 'username-error' : null"
                  :disabled="loading"
                  @input="errors.username = ''"
                />
              </div>
              <span
                v-if="errors.username"
                id="username-error"
                class="field-error"
                >{{ errors.username }}</span
              >
            </div>
            <div class="refill-field" :class="{ 'has-error': errors.password }">
              <label for="refill-password">密码</label>
              <div class="field-box">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <rect x="5" y="10" width="14" height="11" rx="2" />
                  <path d="M8 10V6a4 4 0 0 1 8 0v4M12 14v3" /></svg
                ><input
                  id="refill-password"
                  ref="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  name="password"
                  autocomplete="current-password"
                  placeholder="请输入你的密码"
                  :aria-invalid="Boolean(errors.password)"
                  :aria-describedby="errors.password ? 'password-error' : null"
                  :disabled="loading"
                  @input="errors.password = ''"
                /><button
                  class="password-toggle"
                  type="button"
                  :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                  :aria-pressed="showPassword"
                  @click="showPassword = !showPassword"
                >
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path
                      d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"
                    />
                    <circle cx="12" cy="12" r="3" />
                    <path v-if="!showPassword" d="m3 3 18 18" />
                  </svg>
                </button>
              </div>
              <span
                v-if="errors.password"
                id="password-error"
                class="field-error"
                >{{ errors.password }}</span
              >
            </div>
            <p v-if="errorMsg" class="request-error" role="alert">
              {{ errorMsg }}
            </p>
            <button
              class="refill-submit"
              type="submit"
              :disabled="loading"
              :aria-busy="loading"
            >
              <span>{{ loading ? '正在登录…' : config.action }}</span
              ><span
                v-if="loading"
                class="submit-spinner"
                aria-hidden="true"
              ></span
              ><svg v-else viewBox="0 0 24 24" aria-hidden="true">
                <path d="M4 12h16m-6-6 6 6-6 6" />
              </svg>
            </button>
          </form>
          <div class="receipt-footer">
            <template v-if="role === 'user'"
              ><span>还没有账号？</span
              ><router-link to="/register">注册账号</router-link></template
            ><template v-else
              ><span>{{ config.footer }}</span></template
            >
          </div>
        </section>
        <span class="receipt-shadow" aria-hidden="true"></span>
      </div>
    </section>
  </main>
</template>

<script>
import http, { getErrorMessage } from '@/api/http'

const ROLES = [
  {
    key: 'user',
    label: '用户',
    path: '/login',
    endpoint: '/user/login',
    home: '/home',
    title: '欢迎回来',
    action: '登录',
  },
  {
    key: 'counselor',
    label: '咨询师',
    path: '/counselor/login',
    endpoint: '/counselor/login',
    home: '/counselor/home',
    title: '咨询师登录',
    action: '登录',
    footer: '请使用已开通的咨询师账号',
  },
  {
    key: 'admin',
    label: '管理端',
    path: '/admin/login',
    endpoint: '/admin/login',
    home: '/admin/home',
    title: '管理员登录',
    action: '登录',
    footer: '仅限已授权的平台管理人员',
  },
]

export default {
  name: 'RefillLogin',
  props: { role: { type: String, default: 'user' } },
  data() {
    return {
      roles: ROLES,
      form: { username: '', password: '' },
      errors: { username: '', password: '' },
      errorMsg: '',
      loading: false,
      showPassword: false,
      paused: false,
    }
  },
  computed: {
    roleIndex() {
      return Math.max(
        0,
        ROLES.findIndex((entry) => entry.key === this.role)
      )
    },
    config() {
      return ROLES[this.roleIndex]
    },
  },
  watch: {
    role() {
      this.cancelRequest()
      this.form = { username: '', password: '' }
      this.errors = { username: '', password: '' }
      this.errorMsg = ''
      this.showPassword = false
    },
    paused(value) {
      if (value) this.resetScene()
    },
  },
  mounted() {
    this.motionQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
    this.paused = this.motionQuery.matches
    this.onMotionChange = (event) => {
      this.paused = event.matches
    }
    this.motionQuery.addEventListener('change', this.onMotionChange)
  },
  beforeDestroy() {
    this.cancelRequest()
    if (this.motionQuery)
      this.motionQuery.removeEventListener('change', this.onMotionChange)
    if (this.sceneFrame) cancelAnimationFrame(this.sceneFrame)
  },
  methods: {
    cancelRequest() {
      this.requestVersion = (this.requestVersion || 0) + 1
      if (this.requestController) this.requestController.abort()
      this.loading = false
    },
    moveScene(event) {
      if (this.paused || event.pointerType !== 'mouse') return
      if (this.sceneFrame) cancelAnimationFrame(this.sceneFrame)
      const x = (event.clientX / window.innerWidth - 0.5) * 14
      const y = (event.clientY / window.innerHeight - 0.5) * 10
      this.sceneFrame = requestAnimationFrame(() => {
        if (this.$refs.scene)
          this.$refs.scene.style.transform = `translate3d(${x}px, ${y}px, 0)`
      })
    },
    resetScene() {
      if (this.sceneFrame) cancelAnimationFrame(this.sceneFrame)
      if (this.$refs.scene)
        this.$refs.scene.style.transform = 'translate3d(0, 0, 0)'
    },
    async submitLogin() {
      if (this.loading) return
      this.errors.username = this.form.username ? '' : '请先填写账号'
      this.errors.password = this.form.password ? '' : '还需要填写密码'
      if (this.errors.username || this.errors.password) {
        this.$refs[this.errors.username ? 'username' : 'password'].focus()
        return
      }
      this.errorMsg = ''
      this.loading = true
      const role = this.role
      const config = this.config
      const version = (this.requestVersion = (this.requestVersion || 0) + 1)
      const controller = (this.requestController = new AbortController())
      try {
        const response = await http.post(config.endpoint, null, {
          params: { ...this.form },
          signal: controller.signal,
        })
        if (version !== this.requestVersion || role !== this.role) return
        if (response.data.code !== 200) {
          this.errorMsg = response.data.msg || '账号或密码不正确，请再检查一下'
          return
        }
        const account = response.data.data
        if (!account || !account.token) {
          this.errorMsg = '登录信息不完整，请稍后重试'
          return
        }
        localStorage.setItem('token', account.token)
        localStorage.setItem(role, JSON.stringify(account))
        this.$router.push(config.home)
      } catch (error) {
        if (controller.signal.aborted || version !== this.requestVersion) return
        this.errorMsg = getErrorMessage(error, '暂时无法连接服务，请稍后再试')
      } finally {
        if (version === this.requestVersion) this.loading = false
      }
    },
  },
}
</script>

<style src="@/styles/refill-login.css"></style>

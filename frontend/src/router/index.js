import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'
import Evaluate from '@/views/Evaluate.vue'
import EvaluateQuestion from '@/views/EvaluateQuestion.vue'
import EvaluateReport from '@/views/EvaluateReport.vue'
import Report from '@/views/Report.vue'
import ReportDetail from '@/views/ReportDetail.vue'
import CounselorList from '@/views/CounselorList.vue'
import AppointmentForm from '@/views/AppointmentForm.vue'
import MyAppointment from '@/views/MyAppointment.vue'
import Account from '@/views/Account.vue'
// 咨询师端页面
import CounselorLogin from '@/views/counselor/CounselorLogin.vue'
import CounselorLayout from '@/views/counselor/CounselorLayout.vue'
import CounselorHome from '@/views/counselor/CounselorHome.vue'
import CounselorAppointment from '@/views/counselor/CounselorAppointment.vue'
import CounselorTimeConfig from '@/views/counselor/CounselorTimeConfig.vue'
// 管理员端页面
import AdminLogin from '@/views/admin/AdminLogin.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import UserManage from '@/views/admin/UserManage.vue'
import CounselorManage from '@/views/admin/CounselorManage.vue'
import AppointmentManage from '@/views/admin/AppointmentManage.vue'
import Statistics from '@/views/admin/Statistics.vue'

// 解决重复导航报错的问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err && err.name === 'NavigationDuplicated') {
      return err
    }
    return Promise.reject(err)
  })
}

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/home', name: 'Home', component: Home, meta: { auth: 'user' } },
  { path: '/evaluate', name: 'Evaluate', component: Evaluate, meta: { auth: 'user' } },
  { path: '/evaluate/question', name: 'EvaluateQuestion', component: EvaluateQuestion, meta: { auth: 'user' } },
  { path: '/evaluate/report', name: 'EvaluateReport', component: EvaluateReport, meta: { auth: 'user' } },
  { path: '/report', name: 'Report', component: Report, meta: { auth: 'user' } },
  { path: '/report/detail/:id', name: 'ReportDetail', component: ReportDetail, meta: { auth: 'user' } },
  { path: '/appointment', name: 'CounselorList', component: CounselorList, meta: { auth: 'user' } },
  { path: '/appointment/form', name: 'AppointmentForm', component: AppointmentForm, meta: { auth: 'user' } },
  { path: '/appointment/list', name: 'MyAppointment', component: MyAppointment, meta: { auth: 'user' } },
  { path: '/account', name: 'Account', component: Account, meta: { auth: 'user' } },

  // 咨询师端路由
  { path: '/counselor/login', name: 'CounselorLogin', component: CounselorLogin },
  {
    path: '/counselor',
    component: CounselorLayout,
    meta: { auth: 'counselor' },
    children: [
      { path: 'home', component: CounselorHome },
      { path: 'appointment', component: CounselorAppointment },
      { path: 'appointment/detail/:id', component: () => import('@/views/counselor/CounselorAppointmentDetail.vue') },
      { path: 'time-config', component: CounselorTimeConfig },
      { path: 'account', component: () => import('@/views/counselor/CounselorAccount.vue') }
    ]
  },

    // 管理员端路由
  { path: '/admin/login', name: 'AdminLogin', component: AdminLogin },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { auth: 'admin' },
    children: [
      { path: 'home', component: AdminHome },
      { path: 'user', component: UserManage },
      { path: 'counselor', component: CounselorManage },
      { path: 'appointment', component: AppointmentManage },
      { path: 'statistics', component: Statistics }
    ]
  }
]
const router = new VueRouter({
  mode: 'history',
  routes
})



// 路由守卫：未登录不能访问需要登录的页面
router.beforeEach((to, from, next) => {
  const authRedirects = {
    user: '/login',
    counselor: '/counselor/login',
    admin: '/admin/login'
  }
  const homeRoutes = {
    user: '/home',
    counselor: '/counselor/home',
    admin: '/admin/home'
  }
  const loginRoutes = {
    '/login': 'user',
    '/counselor/login': 'counselor',
    '/admin/login': 'admin'
  }

  const requiredRole = to.matched
    .map(record => record.meta.auth)
    .find(Boolean)

  if (requiredRole && !localStorage.getItem(requiredRole)) {
    next(authRedirects[requiredRole])
    return
  }

  const loginRole = loginRoutes[to.path]
  if (loginRole && localStorage.getItem(loginRole)) {
    next(homeRoutes[loginRole])
    return
  }

  next()
})

export default router

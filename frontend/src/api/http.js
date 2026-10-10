import axios from 'axios'

const http = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || '/api',
  timeout: 15000
})

export function configureHttp(router) {
  http.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  http.interceptors.response.use(
    response => response,
    error => {
      if (error.response && error.response.status === 401) {
        const currentPath = router.currentRoute.path
        let loginPath = '/login'
        if (currentPath.startsWith('/counselor')) {
          loginPath = '/counselor/login'
        } else if (currentPath.startsWith('/admin')) {
          loginPath = '/admin/login'
        }

        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('counselor')
        localStorage.removeItem('admin')

        if (currentPath !== loginPath) {
          router.replace(loginPath)
        }
      }
      return Promise.reject(error)
    }
  )
}

export function getErrorMessage(error, fallback = '操作失败，请稍后重试') {
  return error.response?.data?.msg || fallback
}

export default http

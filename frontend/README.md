# 心理咨询与诊断平台前端

基于 Vue 2、Vue Router、Element UI 和 Axios 的前端项目。

## 本地运行

```bash
npm ci
npm run serve
```

开发环境默认请求 `http://localhost:8080`。生产环境通过 Nginx 的 `/api` 路径访问后端。

## 环境变量

- `VUE_APP_API_BASE_URL`：后端 API 根地址。
- `VUE_APP_DEMO_RECHARGE_ENABLED`：是否显示模拟充值入口。开发环境已开启，生产环境保持关闭。

模拟充值只会直接增加测试账户余额并记录流水，不连接微信或支付宝；真实支付接入完成前，不应在生产环境启用。

## 质量检查

```bash
npm run lint
npm run build
```

项目使用统一的 [HTTP 客户端](src/api/http.js) 设置超时、Token 和登录失效处理。路由权限仅用于
前端导航体验，最终权限由后端强制校验。

## 账号角色

- 普通用户：`/login`
- 咨询师：`/counselor/login`
- 管理员：`/admin/login`

管理员创建咨询师或重置用户密码后，页面只展示一次随机临时密码，请通过安全渠道交付。

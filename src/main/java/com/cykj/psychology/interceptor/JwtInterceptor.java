package com.cykj.psychology.interceptor;

import com.cykj.psychology.annotation.PassToken;
import com.cykj.psychology.entity.SysAdmin;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.mapper.SysAdminMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private static final String REQUEST_START_TIME = "requestStartTime";

    private final JwtUtils jwtUtils;
    private final SysUserMapper userMapper;
    private final SysCounselorMapper counselorMapper;
    private final SysAdminMapper adminMapper;

    public JwtInterceptor(
            JwtUtils jwtUtils,
            SysUserMapper userMapper,
            SysCounselorMapper counselorMapper,
            SysAdminMapper adminMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.counselorMapper = counselorMapper;
        this.adminMapper = adminMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (canPassWithoutToken(handlerMethod)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            return reject(response, 401, "请先登录");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!jwtUtils.validateToken(token)) {
            return reject(response, 401, "Token无效或已过期");
        }

        Long userId = jwtUtils.getUserId(token);
        String role = jwtUtils.getRole(token);
        String requiredRole = requiredRole(request.getRequestURI());
        if (requiredRole != null && !requiredRole.equals(role)) {
            return reject(response, 403, "没有权限访问该资源");
        }
        if (!isAccountActive(userId, role)) {
            return reject(response, 401, "账号不存在或已停用");
        }

        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {
        Object userId = request.getAttribute("userId");
        Object role = request.getAttribute("role");
        Object startedAt = request.getAttribute(REQUEST_START_TIME);
        if (userId == null || role == null || !(startedAt instanceof Long)) {
            return;
        }
        log.info(
                "access role={} principal={} method={} path={} status={} durationMs={}",
                role,
                userId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                System.currentTimeMillis() - (Long) startedAt
        );
    }

    private boolean canPassWithoutToken(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        PassToken methodAnnotation = method.getAnnotation(PassToken.class);
        if (methodAnnotation != null && methodAnnotation.required()) {
            return true;
        }
        PassToken classAnnotation = handlerMethod.getBeanType().getAnnotation(PassToken.class);
        return classAnnotation != null && classAnnotation.required();
    }

    private String requiredRole(String path) {
        if (path.startsWith("/admin/")) {
            return "admin";
        }
        if (path.startsWith("/counselor/")) {
            return "counselor";
        }
        if (path.startsWith("/user/")
                || path.startsWith("/appointment/")
                || path.startsWith("/evaluate/")) {
            return "user";
        }
        return null;
    }

    private boolean isAccountActive(Long userId, String role) {
        if ("user".equals(role)) {
            SysUser user = userMapper.selectById(userId);
            return user != null && Integer.valueOf(1).equals(user.getStatus());
        }
        if ("counselor".equals(role)) {
            SysCounselor counselor = counselorMapper.selectById(userId);
            return counselor != null && Integer.valueOf(1).equals(counselor.getStatus());
        }
        if ("admin".equals(role)) {
            SysAdmin admin = adminMapper.selectById(userId);
            return admin != null && Integer.valueOf(1).equals(admin.getStatus());
        }
        return false;
    }

    private boolean reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":" + status + ",\"msg\":\"" + message + "\",\"data\":null}");
        return false;
    }
}

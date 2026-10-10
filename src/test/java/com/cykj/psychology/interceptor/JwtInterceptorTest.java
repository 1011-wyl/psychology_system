package com.cykj.psychology.interceptor;

import com.cykj.psychology.utils.JwtUtils;
import com.cykj.psychology.entity.SysAdmin;
import com.cykj.psychology.mapper.SysAdminMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtInterceptorTest {
    private JwtUtils jwtUtils;
    private JwtInterceptor interceptor;
    private HandlerMethod handler;
    private SysUserMapper userMapper;
    private SysCounselorMapper counselorMapper;
    private SysAdminMapper adminMapper;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        jwtUtils = new JwtUtils("test-jwt-secret-with-at-least-32-characters", 60_000);
        userMapper = mock(SysUserMapper.class);
        counselorMapper = mock(SysCounselorMapper.class);
        adminMapper = mock(SysAdminMapper.class);
        interceptor = new JwtInterceptor(jwtUtils, userMapper, counselorMapper, adminMapper);
        handler = new HandlerMethod(
                new SecuredHandler(),
                SecuredHandler.class.getDeclaredMethod("handle")
        );
    }

    @Test
    void rejectsUserTokenFromAdminEndpoints() throws Exception {
        HttpServletRequest request = request("/admin/users", "user");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        assertFalse(interceptor.preHandle(request, response, handler));
        verify(response).setStatus(403);
    }

    @Test
    void acceptsMatchingAdminRole() throws Exception {
        HttpServletRequest request = request("/admin/users", "admin");
        HttpServletResponse response = mock(HttpServletResponse.class);
        SysAdmin admin = new SysAdmin();
        admin.setStatus(1);
        when(adminMapper.selectById(7L)).thenReturn(admin);

        assertTrue(interceptor.preHandle(request, response, handler));
        verify(request).setAttribute("userId", 7L);
        verify(request).setAttribute("role", "admin");
    }

    @Test
    void rejectsTokenForDisabledAccount() throws Exception {
        HttpServletRequest request = request("/admin/users", "admin");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        SysAdmin admin = new SysAdmin();
        admin.setStatus(0);
        when(adminMapper.selectById(7L)).thenReturn(admin);

        assertFalse(interceptor.preHandle(request, response, handler));
        verify(response).setStatus(401);
    }

    private HttpServletRequest request(String path, String role) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(path);
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwtUtils.generateToken(7L, role));
        return request;
    }

    private static class SecuredHandler {
        public void handle() {
        }
    }
}

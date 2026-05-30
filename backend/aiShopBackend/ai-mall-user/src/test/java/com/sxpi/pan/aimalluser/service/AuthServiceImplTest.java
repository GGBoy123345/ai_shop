package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallcommon.utils.JwtUtils;
import com.sxpi.pan.aimallcommon.utils.RedisUtils;
import com.sxpi.pan.aimalluser.dto.LoginDTO;
import com.sxpi.pan.aimalluser.dto.RegisterDTO;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import com.sxpi.pan.aimalluser.service.impl.AuthServiceImpl;
import com.sxpi.pan.aimalluser.vo.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RedisUtils redisUtils;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setPhone("13800001111");
        registerDTO.setPassword("password123");

        loginDTO = new LoginDTO();
        loginDTO.setPhone("13800001111");
        loginDTO.setPassword("password123");

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPhone("13800001111");
        existingUser.setPassword(new BCryptPasswordEncoder().encode("password123"));
        existingUser.setNickname("用户1111");
        existingUser.setRole("user");
        existingUser.setStatus(1);
    }

    @Test
    @DisplayName("注册成功")
    void register_success() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });
        when(jwtUtils.generateToken(anyLong(), anyString())).thenReturn("test-token");

        LoginVO result = authService.register(registerDTO);

        assertNotNull(result);
        assertEquals("13800001111", result.getPhone());
        assertEquals("用户1111", result.getNickname());
        assertEquals("user", result.getRole());
        assertEquals("test-token", result.getToken());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败-手机号已存在")
    void register_phoneExists() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(registerDTO));
        assertEquals(40001, ex.getCode());
    }

    @Test
    @DisplayName("登录成功")
    void login_success() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);
        when(jwtUtils.generateToken(anyLong(), anyString())).thenReturn("test-token");

        LoginVO result = authService.login(loginDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("13800001111", result.getPhone());
        assertEquals("test-token", result.getToken());
    }

    @Test
    @DisplayName("登录失败-用户不存在")
    void login_userNotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginDTO));
        assertEquals(40101, ex.getCode());
    }

    @Test
    @DisplayName("登录失败-密码错误")
    void login_wrongPassword() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);
        loginDTO.setPassword("wrong-password");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginDTO));
        assertEquals(40101, ex.getCode());
    }

    @Test
    @DisplayName("登录失败-账号已禁用")
    void login_disabled() {
        existingUser.setStatus(0);
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingUser);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginDTO));
        assertEquals(40301, ex.getCode());
    }

    @Test
    @DisplayName("登出成功")
    void logout_success() {
        doNothing().when(redisUtils).set(anyString(), anyString(), anyLong(), any());

        assertDoesNotThrow(() -> authService.logout("Bearer test-token"));
        verify(redisUtils).set(eq("token:blacklist:test-token"), eq("1"), eq(24L), any());
    }

    @Test
    @DisplayName("登出-token无Bearer前缀")
    void logout_withoutBearer() {
        doNothing().when(redisUtils).set(anyString(), anyString(), anyLong(), any());

        assertDoesNotThrow(() -> authService.logout("test-token"));
        verify(redisUtils).set(eq("token:blacklist:test-token"), eq("1"), eq(24L), any());
    }
}

package com.sxpi.pan.aimallcommon.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtils 单元测试")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "ai-mall-test-secret-key-2024-very-long-key-for-testing");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 86400000L);
    }

    @Test
    @DisplayName("生成Token-成功")
    void generateToken_success() {
        String token = jwtUtils.generateToken(1L, "user");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("解析Token-获取用户ID")
    void getUserId_success() {
        String token = jwtUtils.generateToken(100L, "user");

        Long userId = jwtUtils.getUserId(token);

        assertEquals(100L, userId);
    }

    @Test
    @DisplayName("解析Token-获取角色")
    void getRole_success() {
        String token = jwtUtils.generateToken(1L, "admin");

        String role = jwtUtils.getRole(token);

        assertEquals("admin", role);
    }

    @Test
    @DisplayName("验证Token-有效")
    void validateToken_valid() {
        String token = jwtUtils.generateToken(1L, "user");

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("验证Token-无效")
    void validateToken_invalid() {
        assertFalse(jwtUtils.validateToken("invalid-token"));
    }

    @Test
    @DisplayName("验证Token-空字符串")
    void validateToken_empty() {
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    @DisplayName("Token未过期")
    void isTokenExpired_notExpired() {
        String token = jwtUtils.generateToken(1L, "user");

        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("Token已过期")
    void isTokenExpired_expired() {
        ReflectionTestUtils.setField(jwtUtils, "expiration", -1000L);
        String token = jwtUtils.generateToken(1L, "user");

        assertTrue(jwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("不同用户生成不同Token")
    void generateToken_differentUsers() {
        String token1 = jwtUtils.generateToken(1L, "user");
        String token2 = jwtUtils.generateToken(2L, "user");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("解析篡改的Token-失败")
    void parseToken_tampered() {
        String token = jwtUtils.generateToken(1L, "user");
        String tamperedToken = token + "tampered";

        assertThrows(Exception.class, () -> jwtUtils.parseToken(tamperedToken));
    }
}

package com.sxpi.pan.aimalluser.service;

import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.PasswordDTO;
import com.sxpi.pan.aimalluser.dto.UserUpdateDTO;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import com.sxpi.pan.aimalluser.service.impl.UserServiceImpl;
import com.sxpi.pan.aimalluser.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("13800001111");
        testUser.setPassword(new BCryptPasswordEncoder().encode("oldPassword"));
        testUser.setNickname("测试用户");
        testUser.setAvatar("http://example.com/avatar.jpg");
        testUser.setGender(1);
        testUser.setRole("user");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("根据ID查询用户-成功")
    void getUserById_success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        UserVO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("13800001111", result.getPhone());
        assertEquals("测试用户", result.getNickname());
    }

    @Test
    @DisplayName("根据ID查询用户-不存在")
    void getUserById_notFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.getUserById(999L));
        assertEquals(40401, ex.getCode());
    }

    @Test
    @DisplayName("更新用户信息-成功")
    void updateUserInfo_success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");
        dto.setGender(2);

        assertDoesNotThrow(() -> userService.updateUserInfo(1L, dto));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户信息-用户不存在")
    void updateUserInfo_notFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updateUserInfo(999L, dto));
        assertEquals(40401, ex.getCode());
    }

    @Test
    @DisplayName("修改密码-成功")
    void updatePassword_success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        PasswordDTO dto = new PasswordDTO();
        dto.setOldPassword("oldPassword");
        dto.setNewPassword("newPassword");

        assertDoesNotThrow(() -> userService.updatePassword(1L, dto));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    @DisplayName("修改密码-旧密码错误")
    void updatePassword_wrongOldPassword() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        PasswordDTO dto = new PasswordDTO();
        dto.setOldPassword("wrongOldPassword");
        dto.setNewPassword("newPassword");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updatePassword(1L, dto));
        assertEquals(40005, ex.getCode());
    }

    @Test
    @DisplayName("修改密码-用户不存在")
    void updatePassword_userNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        PasswordDTO dto = new PasswordDTO();
        dto.setOldPassword("oldPassword");
        dto.setNewPassword("newPassword");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updatePassword(999L, dto));
        assertEquals(40401, ex.getCode());
    }
}

package com.sxpi.pan.aimalluser;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false"
})
@DisplayName("创建管理员账号")
class AdminAccountTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("插入admin管理员账号")
    void createAdmin() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, "admin");
        Long count = userMapper.selectCount(wrapper);
        assertTrue(count == 0, "admin账号已存在，无需重复创建");

        User admin = new User();
        admin.setPhone("admin");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
        admin.setNickname("管理员");
        admin.setRole("admin");
        admin.setGender(0);
        admin.setStatus(1);
        userMapper.insert(admin);

        User saved = userMapper.selectOne(wrapper);
        assertNotNull(saved);
        assertEquals("admin", saved.getPhone());
        assertEquals("admin", saved.getRole());
        assertEquals(1, saved.getStatus());
        assertTrue(new BCryptPasswordEncoder().matches("admin", saved.getPassword()));

        System.out.println("管理员账号创建成功！ID: " + saved.getId());
    }
}

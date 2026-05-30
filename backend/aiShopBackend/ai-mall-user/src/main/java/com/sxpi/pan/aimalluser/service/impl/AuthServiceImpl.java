package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallcommon.utils.JwtUtils;
import com.sxpi.pan.aimallcommon.utils.RedisUtils;
import com.sxpi.pan.aimalluser.dto.LoginDTO;
import com.sxpi.pan.aimalluser.dto.RegisterDTO;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import com.sxpi.pan.aimalluser.service.AuthService;
import com.sxpi.pan.aimalluser.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO register(RegisterDTO dto) {
        // 检查手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(40001, "手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname("用户" + dto.getPhone().substring(7));
        user.setRole("user");
        user.setStatus(1);
        userMapper.insert(user);

        // 生成token
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        return vo;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(40101, "账号或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(40101, "账号或密码错误");
        }

        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException(40301, "账号已被禁用");
        }

        // 生成token
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        return vo;
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 将token加入黑名单，24小时过期
        redisUtils.set("token:blacklist:" + token, "1", 24, TimeUnit.HOURS);
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        Long userId = jwtUtils.getUserIdFromRefreshToken(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(40102, "refreshToken无效或已过期");
        }
        String newToken = jwtUtils.generateToken(user.getId(), user.getRole());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getRole());
        Map<String, String> result = new HashMap<>();
        result.put("token", newToken);
        result.put("refreshToken", newRefreshToken);
        return result;
    }

    @Override
    public void sendSmsCode(String phone, String scene) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        // 存入Redis，5分钟过期
        redisUtils.set("sms:code:" + scene + ":" + phone, code, 5, TimeUnit.MINUTES);
        // TODO: 调用实际短信服务发送验证码
        System.out.println("【短信验证码】手机号:" + phone + " 验证码:" + code + " 场景:" + scene);
    }
}

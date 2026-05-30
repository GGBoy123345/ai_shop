package com.sxpi.pan.aimalluser.service;

import com.sxpi.pan.aimalluser.dto.LoginDTO;
import com.sxpi.pan.aimalluser.dto.RegisterDTO;
import com.sxpi.pan.aimalluser.vo.LoginVO;

import java.util.Map;

public interface AuthService {
    LoginVO register(RegisterDTO dto);
    LoginVO login(LoginDTO dto);
    void logout(String token);
    Map<String, String> refreshToken(String refreshToken);
    void sendSmsCode(String phone, String scene);
}

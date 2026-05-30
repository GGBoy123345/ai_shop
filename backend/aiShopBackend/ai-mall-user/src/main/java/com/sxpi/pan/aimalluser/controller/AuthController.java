package com.sxpi.pan.aimalluser.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.dto.LoginDTO;
import com.sxpi.pan.aimalluser.dto.RegisterDTO;
import com.sxpi.pan.aimalluser.service.AuthService;
import com.sxpi.pan.aimalluser.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        return Result.success(authService.refreshToken(body.get("refreshToken")));
    }

    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@RequestBody Map<String, String> body) {
        authService.sendSmsCode(body.get("phone"), body.get("scene"));
        return Result.success();
    }
}

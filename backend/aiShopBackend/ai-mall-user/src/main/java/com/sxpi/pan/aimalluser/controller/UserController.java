package com.sxpi.pan.aimalluser.controller;

import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.dto.PasswordDTO;
import com.sxpi.pan.aimalluser.dto.UserUpdateDTO;
import com.sxpi.pan.aimalluser.service.UserService;
import com.sxpi.pan.aimalluser.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(userService.getUserById(userId));
    }

    @PutMapping("/me")
    public Result<Void> updateCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                           @Valid @RequestBody UserUpdateDTO dto) {
        if (userId == null) return Result.error(401, "未登录");
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    @PutMapping("/me/password")
    public Result<Void> updatePassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @Valid @RequestBody PasswordDTO dto) {
        if (userId == null) return Result.error(401, "未登录");
        userService.updatePassword(userId, dto);
        return Result.success();
    }
}

package com.sxpi.pan.aimalluser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.PageResult;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.entity.Config;
import com.sxpi.pan.aimalluser.entity.OperationLog;
import com.sxpi.pan.aimalluser.mapper.ConfigMapper;
import com.sxpi.pan.aimalluser.mapper.OperationLogMapper;
import com.sxpi.pan.aimalluser.service.UserService;
import com.sxpi.pan.aimalluser.vo.LoginVO;
import com.sxpi.pan.aimalluser.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ConfigMapper configMapper;
    private final OperationLogMapper operationLogMapper;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody Map<String, String> body) {
        return Result.success(userService.adminLogin(body.get("username"), body.get("password")));
    }

    @GetMapping("/users")
    public Result<PageResult<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.getUserList(page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(id, body.get("status"));
        return Result.success();
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.countUsers());
        data.put("merchantCount", userService.countMerchants());
        data.put("pendingMerchantAudit", userService.countPendingMerchants());
        return Result.success(data);
    }

    @GetMapping("/configs")
    public Result<List<Config>> getConfigList(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        if (group != null) wrapper.eq(Config::getConfigGroup, group);
        if (keyword != null) {
            wrapper.and(w -> w.like(Config::getConfigKey, keyword)
                    .or().like(Config::getConfigDesc, keyword));
        }
        wrapper.orderByAsc(Config::getId);
        return Result.success(configMapper.selectList(wrapper));
    }

    @PutMapping("/configs/{id}")
    public Result<Void> updateConfig(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            return Result.error(40406, "配置项不存在");
        }
        if (body.get("configValue") != null) config.setConfigValue(body.get("configValue"));
        if (body.get("configDesc") != null) config.setConfigDesc(body.get("configDesc"));
        configMapper.updateById(config);
        return Result.success();
    }

    @GetMapping("/operation-logs")
    public Result<PageResult<OperationLog>> getOperationLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (module != null) wrapper.eq(OperationLog::getModule, module);
        if (operatorId != null) wrapper.eq(OperationLog::getOperatorId, operatorId);
        if (startTime != null) wrapper.ge(OperationLog::getCreateTime, startTime);
        if (endTime != null) wrapper.le(OperationLog::getCreateTime, endTime);
        wrapper.orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> result = operationLogMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @DeleteMapping("/operation-logs")
    public Result<Map<String, Object>> clearOperationLogs(@RequestBody Map<String, String> body) {
        String beforeTime = body.get("beforeTime");
        if (beforeTime == null) return Result.error(40001, "参数错误");
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperationLog::getCreateTime, beforeTime);
        int count = operationLogMapper.delete(wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("deletedCount", count);
        return Result.success(data);
    }
}

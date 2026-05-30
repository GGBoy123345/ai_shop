package com.sxpi.pan.aimalluser.service;

import com.sxpi.pan.aimallcommon.result.PageResult;
import com.sxpi.pan.aimalluser.dto.PasswordDTO;
import com.sxpi.pan.aimalluser.dto.UserUpdateDTO;
import com.sxpi.pan.aimalluser.vo.LoginVO;
import com.sxpi.pan.aimalluser.vo.UserVO;

public interface UserService {
    UserVO getUserById(Long id);
    UserVO getUserByPhone(String phone);
    void updateUserInfo(Long userId, UserUpdateDTO dto);
    void updatePassword(Long userId, PasswordDTO dto);
    LoginVO adminLogin(String username, String password);
    PageResult<UserVO> getUserList(Integer page, Integer size);
    void updateUserStatus(Long id, Integer status);
    long countUsers();
    long countMerchants();
    long countPendingMerchants();
}

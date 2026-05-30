package com.sxpi.pan.aimalluser.service;

import com.sxpi.pan.aimalluser.dto.AddressDTO;
import com.sxpi.pan.aimalluser.vo.AddressVO;
import java.util.List;

public interface AddressService {
    List<AddressVO> getAddressList(Long userId);
    void addAddress(Long userId, AddressDTO dto);
    void updateAddress(Long userId, Long addressId, AddressDTO dto);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
}

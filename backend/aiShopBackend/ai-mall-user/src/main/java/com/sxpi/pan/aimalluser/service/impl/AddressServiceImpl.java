package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.AddressDTO;
import com.sxpi.pan.aimalluser.entity.Address;
import com.sxpi.pan.aimalluser.mapper.AddressMapper;
import com.sxpi.pan.aimalluser.service.AddressService;
import com.sxpi.pan.aimalluser.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<AddressVO> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime);
        List<Address> addresses = addressMapper.selectList(wrapper);
        return addresses.stream().map(a -> {
            AddressVO vo = new AddressVO();
            BeanUtils.copyProperties(a, vo);
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public void addAddress(Long userId, AddressDTO dto) {
        // 检查地址数量上限
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        if (addressMapper.selectCount(wrapper) >= 20) {
            throw new BusinessException(40027, "收货地址数量已达上限");
        }

        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);

        // 如果设为默认，取消其他默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        } else {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Long addressId, AddressDTO dto) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(40403, "地址不存在");
        }
        BeanUtils.copyProperties(dto, address);

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(40403, "地址不存在");
        }
        addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(40403, "地址不存在");
        }
        clearDefaultAddress(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    private void clearDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0);
        addressMapper.update(null, updateWrapper);
    }
}

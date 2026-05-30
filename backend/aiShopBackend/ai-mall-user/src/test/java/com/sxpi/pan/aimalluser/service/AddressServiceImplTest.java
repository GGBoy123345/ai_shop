package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.AddressDTO;
import com.sxpi.pan.aimalluser.entity.Address;
import com.sxpi.pan.aimalluser.mapper.AddressMapper;
import com.sxpi.pan.aimalluser.service.impl.AddressServiceImpl;
import com.sxpi.pan.aimalluser.vo.AddressVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService 单元测试")
class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressMapper addressMapper;

    private Address testAddress;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setReceiverName("张三");
        testAddress.setReceiverPhone("13800001111");
        testAddress.setProvince("北京市");
        testAddress.setCity("北京市");
        testAddress.setDistrict("朝阳区");
        testAddress.setDetailAddress("某某路123号");
        testAddress.setIsDefault(0);

        addressDTO = new AddressDTO();
        addressDTO.setReceiverName("李四");
        addressDTO.setReceiverPhone("13800002222");
        addressDTO.setProvince("上海市");
        addressDTO.setCity("上海市");
        addressDTO.setDistrict("浦东新区");
        addressDTO.setDetailAddress("某某路456号");
        addressDTO.setIsDefault(0);
    }

    @Test
    @DisplayName("查询地址列表-成功")
    void getAddressList_success() {
        when(addressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(testAddress));

        List<AddressVO> result = addressService.getAddressList(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getReceiverName());
    }

    @Test
    @DisplayName("添加地址-成功")
    void addAddress_success() {
        when(addressMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        assertDoesNotThrow(() -> addressService.addAddress(1L, addressDTO));
        verify(addressMapper).insert(any(Address.class));
    }

    @Test
    @DisplayName("添加地址-设为默认地址")
    void addAddress_setDefault() {
        addressDTO.setIsDefault(1);
        when(addressMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(addressMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        assertDoesNotThrow(() -> addressService.addAddress(1L, addressDTO));
        verify(addressMapper).update(isNull(), any(LambdaUpdateWrapper.class));
        verify(addressMapper).insert(any(Address.class));
    }

    @Test
    @DisplayName("添加地址-数量已达上限")
    void addAddress_limitReached() {
        when(addressMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(20L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> addressService.addAddress(1L, addressDTO));
        assertEquals(40027, ex.getCode());
    }

    @Test
    @DisplayName("更新地址-成功")
    void updateAddress_success() {
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.updateById(any(Address.class))).thenReturn(1);

        assertDoesNotThrow(() -> addressService.updateAddress(1L, 1L, addressDTO));
        verify(addressMapper).updateById(any(Address.class));
    }

    @Test
    @DisplayName("更新地址-地址不存在")
    void updateAddress_notFound() {
        when(addressMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> addressService.updateAddress(1L, 999L, addressDTO));
        assertEquals(40403, ex.getCode());
    }

    @Test
    @DisplayName("更新地址-不属于该用户")
    void updateAddress_wrongUser() {
        when(addressMapper.selectById(1L)).thenReturn(testAddress);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> addressService.updateAddress(999L, 1L, addressDTO));
        assertEquals(40403, ex.getCode());
    }

    @Test
    @DisplayName("删除地址-成功")
    void deleteAddress_success() {
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> addressService.deleteAddress(1L, 1L));
        verify(addressMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除地址-地址不存在")
    void deleteAddress_notFound() {
        when(addressMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> addressService.deleteAddress(1L, 999L));
        assertEquals(40403, ex.getCode());
    }

    @Test
    @DisplayName("设置默认地址-成功")
    void setDefaultAddress_success() {
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(addressMapper.updateById(any(Address.class))).thenReturn(1);

        assertDoesNotThrow(() -> addressService.setDefaultAddress(1L, 1L));
        verify(addressMapper).update(isNull(), any(LambdaUpdateWrapper.class));
        verify(addressMapper).updateById(any(Address.class));
    }

    @Test
    @DisplayName("设置默认地址-地址不存在")
    void setDefaultAddress_notFound() {
        when(addressMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> addressService.setDefaultAddress(1L, 999L));
        assertEquals(40403, ex.getCode());
    }
}

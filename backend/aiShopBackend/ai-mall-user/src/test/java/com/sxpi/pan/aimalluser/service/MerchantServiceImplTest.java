package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.MerchantApplyDTO;
import com.sxpi.pan.aimalluser.entity.Merchant;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.MerchantMapper;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import com.sxpi.pan.aimalluser.service.impl.MerchantServiceImpl;
import com.sxpi.pan.aimalluser.vo.MerchantVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MerchantService 单元测试")
class MerchantServiceImplTest {

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private UserMapper userMapper;

    private Merchant testMerchant;
    private MerchantApplyDTO applyDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setUserId(1L);
        testMerchant.setShopName("测试店铺");
        testMerchant.setLicenseNo("LICENSE001");
        testMerchant.setContactPhone("13800001111");
        testMerchant.setContactName("张三");
        testMerchant.setStatus(0);

        applyDTO = new MerchantApplyDTO();
        applyDTO.setShopName("新店铺");
        applyDTO.setLicenseNo("LICENSE002");
        applyDTO.setContactPhone("13800002222");
        applyDTO.setContactName("李四");

        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("13800001111");
        testUser.setRole("user");
    }

    @Test
    @DisplayName("商家入驻申请-成功")
    void apply_success() {
        when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> merchantService.apply(1L, applyDTO));
        verify(merchantMapper).insert(any(Merchant.class));
        assertEquals("merchant", testUser.getRole());
    }

    @Test
    @DisplayName("商家入驻申请-重复申请")
    void apply_duplicate() {
        when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> merchantService.apply(1L, applyDTO));
        assertEquals(40018, ex.getCode());
    }

    @Test
    @DisplayName("根据用户ID查询商家-成功")
    void getMerchantByUserId_success() {
        when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testMerchant);

        MerchantVO result = merchantService.getMerchantByUserId(1L);

        assertNotNull(result);
        assertEquals("测试店铺", result.getShopName());
    }

    @Test
    @DisplayName("根据用户ID查询商家-不存在")
    void getMerchantByUserId_notFound() {
        when(merchantMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        MerchantVO result = merchantService.getMerchantByUserId(999L);
        assertNull(result);
    }

    @Test
    @DisplayName("根据ID查询商家-成功")
    void getMerchantById_success() {
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);

        MerchantVO result = merchantService.getMerchantById(1L);

        assertNotNull(result);
        assertEquals("测试店铺", result.getShopName());
    }

    @Test
    @DisplayName("根据ID查询商家-不存在")
    void getMerchantById_notFound() {
        when(merchantMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> merchantService.getMerchantById(999L));
        assertEquals(40402, ex.getCode());
    }

    @Test
    @DisplayName("审核商家-通过")
    void auditMerchant_approve() {
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);
        when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

        assertDoesNotThrow(() -> merchantService.auditMerchant(1L, 1, "审核通过"));
        assertEquals(1, testMerchant.getStatus());
        assertEquals("审核通过", testMerchant.getAuditRemark());
    }

    @Test
    @DisplayName("审核商家-拒绝")
    void auditMerchant_reject() {
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);
        when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

        assertDoesNotThrow(() -> merchantService.auditMerchant(1L, 2, "资质不符"));
        assertEquals(2, testMerchant.getStatus());
        assertEquals("资质不符", testMerchant.getAuditRemark());
    }

    @Test
    @DisplayName("审核商家-不在待审核状态")
    void auditMerchant_notPending() {
        testMerchant.setStatus(1); // 已审核通过
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> merchantService.auditMerchant(1L, 1, "审核通过"));
        assertEquals(40021, ex.getCode());
    }

    @Test
    @DisplayName("审核商家-商家不存在")
    void auditMerchant_notFound() {
        when(merchantMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> merchantService.auditMerchant(999L, 1, "审核通过"));
        assertEquals(40402, ex.getCode());
    }

    @Test
    @DisplayName("更新商家状态-成功")
    void updateMerchantStatus_success() {
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);
        when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

        assertDoesNotThrow(() -> merchantService.updateMerchantStatus(1L, 3));
        assertEquals(3, testMerchant.getStatus());
    }

    @Test
    @DisplayName("查询商家列表-成功")
    void getMerchantList_success() {
        Page<Merchant> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(testMerchant));
        mockPage.setTotal(1);

        when(merchantMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<MerchantVO> result = merchantService.getMerchantList(0, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }
}

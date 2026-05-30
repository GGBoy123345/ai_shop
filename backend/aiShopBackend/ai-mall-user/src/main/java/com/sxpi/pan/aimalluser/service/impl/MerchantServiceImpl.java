package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.MerchantApplyDTO;
import com.sxpi.pan.aimalluser.entity.Merchant;
import com.sxpi.pan.aimalluser.entity.User;
import com.sxpi.pan.aimalluser.mapper.MerchantMapper;
import com.sxpi.pan.aimalluser.mapper.UserMapper;
import com.sxpi.pan.aimalluser.service.MerchantService;
import com.sxpi.pan.aimalluser.vo.MerchantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void apply(Long userId, MerchantApplyDTO dto) {
        // 检查是否已有申请（待审核或已通过的不允许重复提交）
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        wrapper.in(Merchant::getStatus, 0, 1); // 0待审核 1已通过
        if (merchantMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(40018, "已有审核中的申请或已是商家，请勿重复提交");
        }

        // 如果有被拒绝的记录，删除旧记录再重新申请
        LambdaQueryWrapper<Merchant> rejectWrapper = new LambdaQueryWrapper<>();
        rejectWrapper.eq(Merchant::getUserId, userId);
        rejectWrapper.eq(Merchant::getStatus, 2); // 2已拒绝
        merchantMapper.delete(rejectWrapper);

        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setShopName(dto.getShopName());
        merchant.setLicenseNo(dto.getLicenseNo());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setContactName(dto.getContactName());
        merchant.setDescription(dto.getDescription());
        merchant.setLogo(dto.getLogo());
        merchant.setStatus(0); // 待审核
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        merchantMapper.insert(merchant);
    }

    @Override
    public MerchantVO getMerchantByUserId(Long userId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if (merchant == null) {
            return null;
        }
        MerchantVO vo = new MerchantVO();
        BeanUtils.copyProperties(merchant, vo);
        return vo;
    }

    @Override
    public void updateMerchant(Long userId, MerchantApplyDTO dto) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant merchant = merchantMapper.selectOne(wrapper);
        if (merchant == null) {
            throw new BusinessException(40402, "商家不存在");
        }
        merchant.setShopName(dto.getShopName());
        merchant.setLicenseNo(dto.getLicenseNo());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setContactName(dto.getContactName());
        merchant.setDescription(dto.getDescription());
        merchant.setLogo(dto.getLogo());
        merchantMapper.updateById(merchant);
    }

    @Override
    public MerchantVO getMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BusinessException(40402, "商家不存在");
        }
        MerchantVO vo = new MerchantVO();
        BeanUtils.copyProperties(merchant, vo);
        return vo;
    }

    @Override
    public Page<MerchantVO> getMerchantList(Integer status, Integer page, Integer size) {
        Page<Merchant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        Page<Merchant> result = merchantMapper.selectPage(pageParam, wrapper);

        Page<MerchantVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(m -> {
            MerchantVO vo = new MerchantVO();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).toList());
        return voPage;
    }

    @Override
    @Transactional
    public void auditMerchant(Long id, Integer status, String remark) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BusinessException(40402, "商家不存在");
        }
        if (merchant.getStatus() != 0) {
            throw new BusinessException(40021, "该商家不在待审核状态");
        }
        merchant.setStatus(status);
        merchant.setAuditRemark(remark);
        merchantMapper.updateById(merchant);

        // 审核通过：将用户角色改为merchant
        User user = userMapper.selectById(merchant.getUserId());
        if (user != null) {
            if (status == 1) {
                user.setRole("merchant");
            } else if (status == 2) {
                user.setRole("user");
            }
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateMerchantStatus(Long id, Integer status) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BusinessException(40402, "商家不存在");
        }
        merchant.setStatus(status);
        merchantMapper.updateById(merchant);
    }
}

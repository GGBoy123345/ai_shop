package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimalluser.dto.MerchantApplyDTO;
import com.sxpi.pan.aimalluser.entity.Merchant;
import com.sxpi.pan.aimalluser.vo.MerchantVO;

public interface MerchantService {
    void apply(Long userId, MerchantApplyDTO dto);
    MerchantVO getMerchantByUserId(Long userId);
    void updateMerchant(Long userId, MerchantApplyDTO dto);
    MerchantVO getMerchantById(Long id);
    Page<MerchantVO> getMerchantList(Integer status, Integer page, Integer size);
    void auditMerchant(Long id, Integer status, String remark);
    void updateMerchantStatus(Long id, Integer status);
}

package com.sxpi.pan.aimallorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallorder.dto.RefundDTO;
import com.sxpi.pan.aimallorder.vo.RefundVO;

public interface RefundService {
    void applyRefund(Long userId, RefundDTO dto);
    Page<RefundVO> getRefundList(Long userId, Integer page, Integer size);
    RefundVO getRefundDetail(Long userId, Long refundId);
    void approveRefund(Long refundId, String remark);
    void rejectRefund(Long refundId, String reason);
}

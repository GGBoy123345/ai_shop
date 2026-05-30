package com.sxpi.pan.aimallorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallorder.dto.RefundDTO;
import com.sxpi.pan.aimallorder.entity.Order;
import com.sxpi.pan.aimallorder.entity.Refund;
import com.sxpi.pan.aimallorder.mapper.OrderMapper;
import com.sxpi.pan.aimallorder.mapper.RefundMapper;
import com.sxpi.pan.aimallorder.service.RefundService;
import com.sxpi.pan.aimallorder.vo.RefundVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundMapper refundMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void applyRefund(Long userId, RefundDTO dto) {
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40421, "订单不存在");
        }
        if (order.getStatus() == 0 || order.getStatus() == 4) {
            throw new BusinessException(40045, "当前订单状态不可退款");
        }

        Refund refund = new Refund();
        refund.setOrderId(dto.getOrderId());
        refund.setUserId(userId);
        refund.setRefundNo(generateRefundNo());
        refund.setAmount(dto.getAmount());
        refund.setReason(dto.getReason());
        refund.setDescription(dto.getDescription());
        refund.setImages(dto.getImages());
        refund.setStatus(0); // 待审核
        refundMapper.insert(refund);
    }

    @Override
    public Page<RefundVO> getRefundList(Long userId, Integer page, Integer size) {
        Page<Refund> pageParam = new Page<>(page, size);
        Page<Refund> result = refundMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Refund>()
                        .eq(Refund::getUserId, userId)
                        .orderByDesc(Refund::getCreateTime));

        Page<RefundVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toRefundVO).toList());
        return voPage;
    }

    @Override
    public RefundVO getRefundDetail(Long userId, Long refundId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null || !refund.getUserId().equals(userId)) {
            throw new BusinessException(40422, "退款记录不存在");
        }
        return toRefundVO(refund);
    }

    @Override
    public void approveRefund(Long refundId, String remark) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(40422, "退款记录不存在");
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(40046, "该退款不在待审核状态");
        }
        refund.setStatus(1); // 已同意
        refundMapper.updateById(refund);

        // 更新订单状态
        Order order = orderMapper.selectById(refund.getOrderId());
        if (order != null) {
            order.setStatus(5); // 已退款
            orderMapper.updateById(order);
        }
    }

    @Override
    public void rejectRefund(Long refundId, String reason) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(40422, "退款记录不存在");
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(40046, "该退款不在待审核状态");
        }
        refund.setStatus(2); // 已拒绝
        refund.setRejectReason(reason);
        refundMapper.updateById(refund);
    }

    private RefundVO toRefundVO(Refund refund) {
        RefundVO vo = new RefundVO();
        BeanUtils.copyProperties(refund, vo);
        return vo;
    }

    private String generateRefundNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "R" + timestamp + random;
    }
}

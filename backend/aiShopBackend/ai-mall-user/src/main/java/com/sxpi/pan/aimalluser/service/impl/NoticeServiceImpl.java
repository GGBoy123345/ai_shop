package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.NoticeDTO;
import com.sxpi.pan.aimalluser.entity.Notice;
import com.sxpi.pan.aimalluser.mapper.NoticeMapper;
import com.sxpi.pan.aimalluser.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public Page<Notice> getNoticeList(Integer page, Integer size) {
        return noticeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
    }

    @Override
    public Page<Notice> getActiveNoticeList(Integer page, Integer size) {
        return noticeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getCreateTime));
    }

    @Override
    public void addNotice(NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setStatus(1);
        noticeMapper.insert(notice);
    }

    @Override
    public void updateNotice(Long id, NoticeDTO dto) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        BeanUtils.copyProperties(dto, notice);
        noticeMapper.updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        noticeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        notice.setStatus(status);
        noticeMapper.updateById(notice);
    }
}

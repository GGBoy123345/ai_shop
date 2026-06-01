package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimalluser.dto.NoticeDTO;
import com.sxpi.pan.aimalluser.entity.Notice;

public interface NoticeService {
    Page<Notice> getNoticeList(Integer page, Integer size);
    Page<Notice> getActiveNoticeList(Integer page, Integer size);
    void addNotice(NoticeDTO dto);
    void updateNotice(Long id, NoticeDTO dto);
    void deleteNotice(Long id);
    void updateStatus(Long id, Integer status);
}

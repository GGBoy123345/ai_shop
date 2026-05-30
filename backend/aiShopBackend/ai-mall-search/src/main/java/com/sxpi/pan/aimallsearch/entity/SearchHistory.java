package com.sxpi.pan.aimallsearch.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("search_history")
public class SearchHistory extends BaseEntity {
    private Long userId;
    private String keyword;
}

package com.sxpi.pan.aimallfile.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_info")
public class FileInfo extends BaseEntity {
    private String fileName;
    private String originalName;
    private String url;
    private String thumbnailUrl;
    private Long size;
    private String type;
    private String extension;
    private Integer width;
    private Integer height;
    private String bucket;
    private String objectKey;
    private Long uploaderId;
    private String bizType;
}

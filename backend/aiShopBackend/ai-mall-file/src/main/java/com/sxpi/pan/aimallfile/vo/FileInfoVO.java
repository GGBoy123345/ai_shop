package com.sxpi.pan.aimallfile.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileInfoVO {
    private Long id;
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
    private LocalDateTime createTime;
    private String status;
    private String errorMsg;
}

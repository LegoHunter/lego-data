package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/** One file of an upload batch and its outcome. state is one of PhotoUploadBatchFile.PENDING, ACCEPTED, REJECTED, DUPLICATE. */
@Data
@Builder
public class PhotoUploadBatchFile {
    public static final String PENDING = "pending";
    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";
    public static final String DUPLICATE = "duplicate";

    private Long photoUploadBatchFileId;
    private String batchId;
    private String fileName;
    private String state;
    private String reason;
    private Integer itemInventoryPhotoId;
    private String md5;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}

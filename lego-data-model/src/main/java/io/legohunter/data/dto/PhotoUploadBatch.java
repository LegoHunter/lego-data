package io.legohunter.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/** One presign request of the Lightroom publisher (HOMELAB-135). */
@Data
@Builder
public class PhotoUploadBatch {
    private String batchId;
    private String environment;
    private String tokenName;
    private ZonedDateTime createdAt;
}

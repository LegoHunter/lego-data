package io.legohunter.data.enums;

/** Whether a photo-upload token is a short-lived access token or a rotating refresh token (HOMELAB-134). */
public enum PhotoUploadTokenKind {
    ACCESS,
    REFRESH
}

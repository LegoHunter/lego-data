package io.legohunter.data.dao;

public enum ImageHostingSyncCandidateReason {
    MISSING_ALBUM_LINK,
    MISSING_PHOTO_LINK,
    MISSING_ALBUM_MEMBERSHIP,
    FAILED_SYNC,
    PENDING_SYNC,
    METADATA_CHANGED,
    /** A published photo's stored file differs from the one that was uploaded. */
    CONTENT_CHANGED
}

package io.legohunter.data.dao;

public enum ImageHostingSyncCandidateReason {
    MISSING_ALBUM_LINK,
    MISSING_PHOTO_LINK,
    FAILED_SYNC,
    PENDING_SYNC,
    METADATA_CHANGED
}

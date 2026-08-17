package io.legohunter.data.bricklink;

import java.util.Locale;

public final class BricklinkInventoryColorPolicy {
    public static final int NOT_APPLICABLE_COLOR_ID = 0;
    public static final String MISSING_COLOR_ID = "MISSING_BRICKLINK_COLOR_ID";
    public static final String INVALID_COLOR_ID = "INVALID_BRICKLINK_COLOR_ID";

    private BricklinkInventoryColorPolicy() {
    }

    public static Resolution resolve(String itemTypeCode, Integer requestedColorId) {
        if (isSet(itemTypeCode)) {
            if (requestedColorId == null || requestedColorId == NOT_APPLICABLE_COLOR_ID) {
                return Resolution.valid(NOT_APPLICABLE_COLOR_ID);
            }
            return Resolution.invalid(
                    INVALID_COLOR_ID,
                    "BrickLink SET inventory must use colorId 0 (Not Applicable)"
            );
        }
        if (requestedColorId == null) {
            return Resolution.invalid(
                    MISSING_COLOR_ID,
                    "BrickLink colorId is required for item type " + displayItemType(itemTypeCode)
            );
        }
        if (requestedColorId <= NOT_APPLICABLE_COLOR_ID) {
            return Resolution.invalid(
                    INVALID_COLOR_ID,
                    "BrickLink colorId must be positive for item type " + displayItemType(itemTypeCode)
            );
        }
        return Resolution.valid(requestedColorId);
    }

    private static boolean isSet(String itemTypeCode) {
        String normalized = normalize(itemTypeCode);
        return "S".equals(normalized) || "SET".equals(normalized);
    }

    private static String displayItemType(String itemTypeCode) {
        String normalized = normalize(itemTypeCode);
        return normalized.isEmpty() ? "UNKNOWN" : normalized;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    public record Resolution(boolean valid, Integer effectiveColorId, String errorCode, String message) {
        private static Resolution valid(Integer effectiveColorId) {
            return new Resolution(true, effectiveColorId, null, null);
        }

        private static Resolution invalid(String errorCode, String message) {
            return new Resolution(false, null, errorCode, message);
        }
    }
}

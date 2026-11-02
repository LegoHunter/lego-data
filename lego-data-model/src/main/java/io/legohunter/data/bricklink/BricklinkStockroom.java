package io.legohunter.data.bricklink;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The three BrickLink stockrooms. A lot in a stockroom is hidden from buyers.
 *
 * <p>Ownership is fixed per environment (see {@link io.legohunter.data.enums.LegoEnvironment}):
 * {@link #A} belongs to PROD, for deliberately hiding a real PROD lot; {@link #B} to DEV;
 * {@link #C} to SANDBOX (and local).
 */
public enum BricklinkStockroom {
    A,
    B,
    C;

    /** The stockroom id BrickLink uses ({@code "A"}, {@code "B"} or {@code "C"}). */
    public String id() {
        return name();
    }

    /** Parses a configured stockroom id, ignoring surrounding spaces and case. */
    public static Optional<BricklinkStockroom> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        String normalized = id.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(stockroom -> stockroom.name().equals(normalized))
                .findFirst();
    }
}

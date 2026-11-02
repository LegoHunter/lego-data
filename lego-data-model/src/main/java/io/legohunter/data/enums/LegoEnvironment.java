package io.legohunter.data.enums;

import io.legohunter.data.bricklink.BricklinkStockroom;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The LCMS runtime environments and the facts that depend on them.
 *
 * <p>All environments share one BrickLink account (and one ShipStation account). Only PROD may own
 * publicly visible BrickLink lots or write to real orders. Every other environment may create and
 * update real lots only as hidden stockroom lots, in its own stockroom, so buyers never see them
 * and environments never touch each other's lots (owner decision, 2026-10-31):
 *
 * <table>
 *   <caption>Environments</caption>
 *   <tr><th>Environment</th><th>Code</th><th>Production</th><th>Stockroom for its lots</th></tr>
 *   <tr><td>{@link #LOCAL}</td><td>local</td><td>no</td><td>C (marketplace sync always runs DRY_RUN)</td></tr>
 *   <tr><td>{@link #SANDBOX}</td><td>sandbox</td><td>no</td><td>C</td></tr>
 *   <tr><td>{@link #DEV}</td><td>dev</td><td>no</td><td>B</td></tr>
 *   <tr><td>{@link #PROD}</td><td>prod</td><td>yes</td><td>none forced; A is PROD's own, for deliberately hiding a lot</td></tr>
 * </table>
 *
 * <p>The code is the value used in configuration ({@code environment-code}) and in the system
 * remarks block written on BrickLink lots. An unknown code is a configuration error, not a new
 * environment: adding an environment means adding a constant here.
 */
public enum LegoEnvironment {
    LOCAL("local", false, BricklinkStockroom.C),
    SANDBOX("sandbox", false, BricklinkStockroom.C),
    DEV("dev", false, BricklinkStockroom.B),
    PROD("prod", true, null);

    private final String code;
    private final boolean production;
    private final BricklinkStockroom lowerEnvironmentStockroom;

    LegoEnvironment(String code, boolean production, BricklinkStockroom lowerEnvironmentStockroom) {
        this.code = code;
        this.production = production;
        this.lowerEnvironmentStockroom = lowerEnvironmentStockroom;
    }

    /** The configuration code, for example {@code "sandbox"}. */
    public String code() {
        return code;
    }

    /** True only for PROD. */
    public boolean isProduction() {
        return production;
    }

    /**
     * The stockroom every lot this environment creates or updates must be in. Empty for PROD, whose
     * lots are not forced into a stockroom.
     */
    public Optional<BricklinkStockroom> lowerEnvironmentStockroom() {
        return Optional.ofNullable(lowerEnvironmentStockroom);
    }

    /** Looks up an environment by its configuration code, ignoring surrounding spaces and case. */
    public static Optional<LegoEnvironment> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        String normalized = code.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(environment -> environment.code.equals(normalized))
                .findFirst();
    }
}

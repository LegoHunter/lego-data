package io.legohunter.data.enums;

import io.legohunter.data.bricklink.BricklinkStockroom;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The LCMS runtime environments and the facts that depend on them.
 *
 * <p>All environments share one BrickLink account (and one ShipStation account). Only PROD may own
 * publicly visible BrickLink lots or write to real orders. Every environment owns one BrickLink
 * stockroom (owner decision, 2026-10-31):
 *
 * <table>
 *   <caption>Environments</caption>
 *   <tr><th>Environment</th><th>Code</th><th>Production</th><th>Stockroom it owns</th><th>Forces every lot into it</th></tr>
 *   <tr><td>{@link #LOCAL}</td><td>local</td><td>no</td><td>C (shared with SANDBOX; marketplace sync always runs DRY_RUN)</td><td>yes</td></tr>
 *   <tr><td>{@link #SANDBOX}</td><td>sandbox</td><td>no</td><td>C</td><td>yes</td></tr>
 *   <tr><td>{@link #DEV}</td><td>dev</td><td>no</td><td>B</td><td>yes</td></tr>
 *   <tr><td>{@link #PROD}</td><td>prod</td><td>yes</td><td>A</td><td>no</td></tr>
 * </table>
 *
 * <p>Owning and forcing are different things. A lower environment <em>forces</em> every lot it
 * creates or updates into its stockroom, so buyers never see it. PROD's lots are public by default;
 * PROD owns stockroom A only for deliberately hiding a particular lot, and must never force every
 * lot into it, which would hide the whole store.
 *
 * <p>The code is the value used in configuration ({@code environment-code}) and in the system
 * remarks block written on BrickLink lots. An unknown code is a configuration error, not a new
 * environment: adding an environment means adding a constant here.
 */
public enum LegoEnvironment {
    LOCAL("local", false, BricklinkStockroom.C),
    SANDBOX("sandbox", false, BricklinkStockroom.C),
    DEV("dev", false, BricklinkStockroom.B),
    PROD("prod", true, BricklinkStockroom.A);

    private final String code;
    private final boolean production;
    private final BricklinkStockroom stockroom;

    LegoEnvironment(String code, boolean production, BricklinkStockroom stockroom) {
        this.code = code;
        this.production = production;
        this.stockroom = stockroom;
    }

    /** The configuration code, for example {@code "sandbox"}. */
    public String code() {
        return code;
    }

    /** True only for PROD. */
    public boolean isProduction() {
        return production;
    }

    /** The BrickLink stockroom this environment owns. Never null. */
    public BricklinkStockroom stockroom() {
        return stockroom;
    }

    /**
     * Whether every lot this environment creates or updates must go into {@link #stockroom()}.
     * True for every environment except PROD, whose lots are public unless deliberately hidden.
     */
    public boolean forcesStockroom() {
        return !production;
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

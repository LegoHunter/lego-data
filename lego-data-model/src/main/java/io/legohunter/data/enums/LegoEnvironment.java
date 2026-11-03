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
 *   <tr><th>Environment</th><th>Code</th><th>Production</th><th>Stockroom it owns</th><th>Forces every lot into it</th><th>Database schema</th></tr>
 *   <tr><td>{@link #LOCAL}</td><td>local</td><td>no</td><td>C (shared with SANDBOX; marketplace sync always runs DRY_RUN)</td><td>yes</td><td>any except {@code lego_prod}</td></tr>
 *   <tr><td>{@link #SANDBOX}</td><td>sandbox</td><td>no</td><td>C</td><td>yes</td><td>{@code lego_sandbox}</td></tr>
 *   <tr><td>{@link #DEV}</td><td>dev</td><td>no</td><td>B</td><td>yes</td><td>{@code lego_dev}</td></tr>
 *   <tr><td>{@link #PROD}</td><td>prod</td><td>yes</td><td>A</td><td>no</td><td>{@code lego_prod}</td></tr>
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
    LOCAL("local", false, BricklinkStockroom.C, Optional.empty()),
    SANDBOX("sandbox", false, BricklinkStockroom.C, Optional.of("lego_sandbox")),
    DEV("dev", false, BricklinkStockroom.B, Optional.of("lego_dev")),
    PROD("prod", true, BricklinkStockroom.A, Optional.of("lego_prod"));

    private final String code;
    private final boolean production;
    private final BricklinkStockroom stockroom;
    private final Optional<String> databaseSchema;

    LegoEnvironment(String code, boolean production, BricklinkStockroom stockroom, Optional<String> databaseSchema) {
        this.code = code;
        this.production = production;
        this.stockroom = stockroom;
        this.databaseSchema = databaseSchema;
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
     * The database schema this environment must be connected to. Empty only for LOCAL: a developer
     * run may use any schema except PROD's, including an in-memory test database.
     */
    public Optional<String> databaseSchema() {
        return databaseSchema;
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

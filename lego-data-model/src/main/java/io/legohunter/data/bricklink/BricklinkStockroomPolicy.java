package io.legohunter.data.bricklink;

import io.legohunter.data.enums.LegoEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The BrickLink stockroom rules shared by every service that creates or updates BrickLink lots.
 *
 * <p>Services run these rules at startup and refuse to start on any violation, so a lower
 * environment can never write into PROD's stockroom or another environment's stockroom. The
 * stockroom facts come from {@link LegoEnvironment}: an environment that
 * {@linkplain LegoEnvironment#forcesStockroom() forces} its lots into a stockroom must be configured
 * with exactly {@linkplain LegoEnvironment#stockroom() the one it owns}; PROD forces nothing, so it
 * has no rule here.
 */
public final class BricklinkStockroomPolicy {

    private BricklinkStockroomPolicy() {
    }

    /**
     * Every way the given settings break the stockroom rules, or an empty list.
     *
     * @param settingPrefix       the configuration prefix, used to name settings in the messages
     *                            (for example {@code "lego.bricklink.marketplace-sync"})
     * @param stockroomSetting    the stockroom setting's name under that prefix
     *                            (for example {@code "non-prod-stock-room-id"})
     * @param environmentCode     the configured {@code environment-code}
     * @param production          the configured {@code production} flag
     * @param configuredStockroom the configured non-prod stockroom id; not used for PROD
     */
    public static List<String> violations(String settingPrefix, String stockroomSetting, String environmentCode, boolean production, String configuredStockroom) {
        List<String> violations = new ArrayList<>();
        LegoEnvironment environment = LegoEnvironment.fromCode(environmentCode).orElse(null);
        if (environment == null) {
            violations.add("%s.environment-code '%s' is not a known environment (%s)".formatted(
                    settingPrefix, environmentCode, knownCodes()));
            return violations;
        }
        if (environment.isProduction() != production) {
            violations.add("%s.production is %s but environment-code '%s' is %s".formatted(
                    settingPrefix, production, environment.code(), environment.isProduction() ? "PROD" : "not PROD"));
            return violations;
        }
        if (!environment.forcesStockroom()) {
            return violations;
        }
        BricklinkStockroom expected = environment.stockroom();
        BricklinkStockroom prodStockroom = LegoEnvironment.PROD.stockroom();
        if (configuredStockroom == null || configuredStockroom.isBlank()) {
            violations.add("%s.%s must be set for environment '%s' (expected %s)".formatted(
                    settingPrefix, stockroomSetting, environment.code(), expected.id()));
            return violations;
        }
        BricklinkStockroom stockroom = BricklinkStockroom.fromId(configuredStockroom).orElse(null);
        if (stockroom == null) {
            violations.add("%s.%s '%s' is not a BrickLink stockroom (A, B or C)".formatted(
                    settingPrefix, stockroomSetting, configuredStockroom.trim()));
            return violations;
        }
        if (stockroom == prodStockroom) {
            violations.add("%s.%s must not be %s for environment '%s': stockroom %s is PROD's own, for deliberately hidden lots (expected %s)".formatted(
                    settingPrefix, stockroomSetting, prodStockroom.id(), environment.code(), prodStockroom.id(), expected.id()));
            return violations;
        }
        if (stockroom != expected) {
            violations.add("%s.%s for environment '%s' must be %s, not %s".formatted(
                    settingPrefix, stockroomSetting, environment.code(), expected.id(), stockroom.id()));
        }
        return violations;
    }

    private static String knownCodes() {
        return Arrays.stream(LegoEnvironment.values()).map(LegoEnvironment::code).collect(Collectors.joining(", "));
    }
}

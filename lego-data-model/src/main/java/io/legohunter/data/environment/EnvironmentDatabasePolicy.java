package io.legohunter.data.environment;

import io.legohunter.data.enums.LegoEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that a service's configured environment matches the database it is connected to.
 *
 * <p>The {@code production} flag switches off every lower-environment guard (stockroom-only lots,
 * environment remarks, order-write suppression). Only the PROD database may own public BrickLink lots
 * or write to real orders, so a flag that disagrees with the connected database is unsafe in both
 * directions: PROD settings on a lower-environment database would publish its test lots, and
 * lower-environment settings on {@code lego_prod} would rewrite real PROD lots as stockroom lots.
 * Services run these rules at startup and refuse to start on any violation.
 *
 * <p>Schema names are compared ignoring case, because some databases (H2) report them in upper case.
 */
public final class EnvironmentDatabasePolicy {

    private EnvironmentDatabasePolicy() {
    }

    /**
     * Every way the settings disagree with the connected database, or an empty list.
     *
     * @param codeSetting       the environment code setting's full name, used in the messages
     *                          (for example {@code "lego.environment.code"})
     * @param productionSetting the production flag setting's full name, used in the messages
     *                          (for example {@code "lego.environment.production"})
     * @param environmentCode the configured {@code environment-code}
     * @param production      the configured {@code production} flag
     * @param connectedSchema the schema the service's data source is connected to, as reported by
     *                        the database (for example by {@code SELECT DATABASE()})
     */
    public static List<String> violations(String codeSetting, String productionSetting, String environmentCode, boolean production, String connectedSchema) {
        List<String> violations = new ArrayList<>();
        LegoEnvironment environment = LegoEnvironment.fromCode(environmentCode).orElse(null);
        if (environment == null) {
            violations.add("%s '%s' is not a known environment (%s)".formatted(
                    codeSetting, environmentCode, knownCodes()));
            return violations;
        }
        if (environment.isProduction() != production) {
            violations.add("%s is %s but %s '%s' is %s".formatted(
                    productionSetting, production, codeSetting, environment.code(), environment.isProduction() ? "PROD" : "not PROD"));
            return violations;
        }
        if (connectedSchema == null || connectedSchema.isBlank()) {
            violations.add("could not determine the connected database schema, so environment '%s' cannot be verified".formatted(
                    environment.code()));
            return violations;
        }
        String schema = connectedSchema.trim();
        Optional<String> required = environment.databaseSchema();
        if (required.isPresent() && !required.get().equalsIgnoreCase(schema)) {
            violations.add("%s '%s' must be connected to database schema '%s', not '%s'".formatted(
                    codeSetting, environment.code(), required.get(), schema));
            return violations;
        }
        if (!environment.isProduction() && isProdSchema(schema)) {
            violations.add("%s '%s' is not PROD but is connected to PROD's database schema '%s'".formatted(
                    codeSetting, environment.code(), schema));
        }
        return violations;
    }

    private static boolean isProdSchema(String schema) {
        return LegoEnvironment.PROD.databaseSchema()
                .map(prodSchema -> prodSchema.equalsIgnoreCase(schema))
                .orElse(false);
    }

    private static String knownCodes() {
        return Arrays.stream(LegoEnvironment.values()).map(LegoEnvironment::code).collect(Collectors.joining(", "));
    }
}

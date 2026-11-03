package io.legohunter.lego.data.environment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The one declaration of which LCMS environment a service is running as ({@code lego.environment}).
 *
 * <p>All environments share one BrickLink account and one ShipStation account, so the environment
 * decides which guards apply: a lower environment (local, sandbox, dev) keeps every lot it touches in
 * its own stockroom and never writes to real orders. The facts per environment live in
 * {@link io.legohunter.data.enums.LegoEnvironment}.
 *
 * <p>The defaults are safe by construction. A service that sets nothing runs as {@code local}, which
 * may use any database except {@code lego_prod}; a PROD runtime must say {@code code: prod} and
 * {@code production: true}, or it refuses to start on {@code lego_prod}.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "lego.environment")
public class LegoEnvironmentProperties {

    /** The environment code: {@code local}, {@code sandbox}, {@code dev} or {@code prod}. */
    private String code = "local";

    /**
     * True only for the PROD runtime. Switches off every lower-environment guard, including the
     * suppression of order and fulfillment writes (ShipStation orders, BrickLink order updates,
     * Drive Thru emails).
     */
    private boolean production = false;

    /**
     * Whether to check at startup that {@link #code} and {@link #production} match the connected
     * database. Leave it on; turn it off only for tools that deliberately target different schemas.
     */
    private boolean verifyDatabase = true;
}

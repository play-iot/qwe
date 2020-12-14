package io.github.zero88.msa.bp.component;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Unit context after deployment
 *
 * @see Unit
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitContext {

    public static final UnitContext VOID = new UnitContext();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private String deployId;

    UnitContext registerDeployId(String deployId) {
        this.deployId = deployId;
        return this;
    }

}

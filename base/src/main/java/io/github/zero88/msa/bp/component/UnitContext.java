package io.github.zero88.msa.bp.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

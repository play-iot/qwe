package io.github.zero88.qwe.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Component context after deployment
 *
 * @see Component
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComponentContext {

    public static final ComponentContext VOID = new ComponentContext();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private String deployId;

    ComponentContext registerDeployId(String deployId) {
        this.deployId = deployId;
        return this;
    }

}

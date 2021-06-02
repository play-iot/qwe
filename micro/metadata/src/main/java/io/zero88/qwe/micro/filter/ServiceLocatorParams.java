package io.zero88.qwe.micro.filter;

import io.zero88.qwe.micro.transfomer.RecordTransformer.ViewType;

public final class ServiceLocatorParams {

    /**
     * Identifier parameter
     */
    public static final String IDENTIFIER = "identifier";
    /**
     * Indicator parameter. Used in conjunction with {@link #IDENTIFIER}
     */
    public static final String BY = "by";
    /**
     * Service type parameter
     */
    public static final String TYPE = "type";
    /**
     * Service scope parameter
     */
    public static final String SCOPE = "scope";
    /**
     * Service status parameter
     */
    public static final String STATUS = "status";
    /**
     * Record view
     *
     * @see ViewType
     */
    public static final String VIEW = "view";

    /**
     * Event action to identify. Used in conjunction with {@link #BY} is {@code location}
     */
    public static final String ACTION = "action";

}

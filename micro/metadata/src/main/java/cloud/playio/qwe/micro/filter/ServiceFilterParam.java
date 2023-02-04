package cloud.playio.qwe.micro.filter;

import cloud.playio.qwe.micro.transfomer.RecordTransformer;
import cloud.playio.qwe.micro.transfomer.RecordTransformer.ViewType;

public final class ServiceFilterParam {

    /**
     * Identifier parameter
     */
    public static final String IDENTIFIER = "identifier";

    /**
     * Indicator parameter. Used in conjunction with {@link #IDENTIFIER}
     *
     * @see ByPredicateFactory
     */
    public static final String BY = "by";

    /**
     * Service name parameter
     *
     * @see ServiceNamePredicateFactory
     */
    public static final String NAME = "name";

    /**
     * Service type parameter
     *
     * @see ServiceTypePredicateFactory
     */
    public static final String TYPE = "type";

    /**
     * Service scope parameter
     *
     * @see ServiceScopePredicateFactory
     */
    public static final String SCOPE = "scope";

    /**
     * Service status parameter
     *
     * @see ServiceStatusPredicateFactory
     */
    public static final String STATUS = "status";

    /**
     * Record view
     *
     * @see ViewType
     * @see RecordTransformer
     */
    public static final String VIEW = "view";

    /**
     * Event action to identify. Used in conjunction with {@link #BY} is {@code location}
     *
     * @see EventMessageServicePathPredicate
     */
    public static final String ACTION = "action";

}

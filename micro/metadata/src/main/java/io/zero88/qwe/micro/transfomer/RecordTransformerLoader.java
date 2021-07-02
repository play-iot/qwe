package io.zero88.qwe.micro.transfomer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.ServiceHelper;
import io.zero88.qwe.micro.filter.PredicateFactoryLoader;
import io.zero88.qwe.micro.transfomer.RecordTransformer.ViewType;

public final class RecordTransformerLoader {

    static RecordTransformerLoader instance;

    public static RecordTransformerLoader instance() {
        if (Objects.nonNull(instance)) {
            return instance;
        }
        synchronized (PredicateFactoryLoader.class) {
            if (Objects.nonNull(instance)) {
                return instance;
            }
            return instance = new RecordTransformerLoader();
        }
    }

    private final Map<ViewType, RecordTransformer> def = new HashMap<>();
    private final Map<String, RecordTransformer> transformers;

    private RecordTransformerLoader() {
        this.transformers = ServiceHelper.loadFactories(RecordTransformer.class)
                                         .stream()
                                         .collect(Collectors.toMap(rt -> createKey(rt.serviceType(), rt.viewType()),
                                                                   Function.identity()));
        def.put(ViewType.END_USER, Optional.ofNullable(ServiceHelper.loadFactoryOrNull(PublicRecordView.class))
                                           .orElseGet(DefaultPublicRecordView::new));
        def.put(ViewType.TECHNICAL, Optional.ofNullable(ServiceHelper.loadFactoryOrNull(TechnicalRecordView.class))
                                            .orElseGet(DefaultTechnicalRecordView::new));
    }

    private String createKey(String serviceType, ViewType viewType) {
        return serviceType + "::" + viewType;
    }

    public RecordTransformer lookup(String serviceType, ViewType view) {
        return Optional.ofNullable(transformers.get(createKey(serviceType, view))).orElseGet(() -> def.get(view));
    }

}

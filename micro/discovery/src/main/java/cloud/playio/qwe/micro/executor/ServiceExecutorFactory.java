package cloud.playio.qwe.micro.executor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.ServiceHelper;

public final class ServiceExecutorFactory {

    private final Map<String, ServiceExecutor> mapping;

    public ServiceExecutorFactory() {
        mapping = ServiceHelper.loadFactories(ServiceExecutor.class)
                               .stream()
                               .collect(Collectors.toMap(ServiceExecutor::serviceType, Function.identity()));
    }

    public ServiceExecutor lookup(String serviceType) {
        return mapping.get(serviceType);
    }

}

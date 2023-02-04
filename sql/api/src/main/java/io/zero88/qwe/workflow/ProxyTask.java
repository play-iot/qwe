package cloud.playio.qwe.workflow;

import cloud.playio.qwe.transport.ProxyService;
import cloud.playio.qwe.transport.Transporter;

/**
 * Represents for {@code proxy task}.
 * <p>
 * A proxy task is a task that delegates the execution process on remote service
 *
 * @param <DC> Type of {@code TaskDefinitionContext}
 * @param <EC> Type of {@code TaskExecutionContext}
 * @param <R>  Type of {@code Result}
 * @param <T>  Type of {@code Transporter}
 * @see TaskDefinitionContext
 * @see TaskExecutionContext
 * @see Transporter
 * @since 1.0.0
 */
public interface ProxyTask<DC extends TaskDefinitionContext, EC extends TaskExecutionContext, R, T extends Transporter>
    extends ProxyService<T>, Task<DC, EC, R> {}

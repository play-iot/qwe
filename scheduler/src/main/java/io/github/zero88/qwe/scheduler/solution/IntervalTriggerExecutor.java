package io.github.zero88.qwe.scheduler.solution;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Timed;

import lombok.NonNull;

public class IntervalTriggerExecutor extends AbstractTriggerExecutor<IntervalTrigger>
    implements TriggerExecutor<IntervalTrigger> {

    protected IntervalTriggerExecutor(@NonNull IntervalTrigger trigger) {
        super(trigger);
    }

    @Override
    public Observable<Timed<Long>> run(Scheduler scheduler) {
        return Observable.just(get().getKey())
                         .doOnEach(triggerKey -> logger.info("Run trigger: {}", triggerKey))
                         .flatMap(key -> Observable.intervalRange(0, get().getRepeatCount(), get().getInitialDelay(),
                                                                  get().getPeriod(), TimeUnit.MILLISECONDS, scheduler))
                         .timestamp();
    }

}

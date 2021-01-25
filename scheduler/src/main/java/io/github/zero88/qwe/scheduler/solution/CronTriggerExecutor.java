package io.github.zero88.qwe.scheduler.solution;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Timed;

import lombok.NonNull;

public class CronTriggerExecutor extends AbstractTriggerExecutor<CronTrigger> implements TriggerExecutor<CronTrigger> {

    public CronTriggerExecutor(@NonNull CronTrigger trigger) {
        super(trigger);
    }

    @Override
    public Observable<Timed<Long>> run(Scheduler scheduler) {
        return Observable.just(get().getKey())
                         .doOnEach(triggerKey -> logger.info("Run trigger: {}", triggerKey))
                         .map(triggerKey -> get().getNextFireTime())
                         .map(nextFire -> nextFire.getTime() - Instant.now().toEpochMilli())
                         .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS, scheduler))
                         .timestamp()
                         .repeat();
    }

}

package io.github.zero88.qwe.scheduler.solution;

import java.util.function.Supplier;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Timed;

public interface TriggerExecutor<T extends Trigger> extends Supplier<T> {

    Observable<Timed<Long>> run(Scheduler scheduler);

}

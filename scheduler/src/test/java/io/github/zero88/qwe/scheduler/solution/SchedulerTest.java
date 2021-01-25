package io.github.zero88.qwe.scheduler.solution;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.TestHelper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.RxHelper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(VertxUnitRunner.class)
public class SchedulerTest {

    private Vertx vertx;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
        ((Logger) LoggerFactory.getLogger("iot.github.zero88.qwe")).setLevel(Level.TRACE);
    }

    @Before
    public void before() {
        vertx = Vertx.vertx();
    }

    @Test
    public void test_timer() {
        TestObserver<Long> subscriber = new TestObserver<>();
        Scheduler scheduler = RxHelper.scheduler(vertx);
        Observable.timer(2, TimeUnit.SECONDS, scheduler)
                  .repeat()
                  .take(5)
                  .doOnEach(System.out::println)
                  .subscribeOn(scheduler)
                  .subscribe(subscriber);
        subscriber.assertSubscribed();
        //        subscriber.assertComplete();
    }

    @Test
    public void test_interval() {
        List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> subscriber = new TestObserver<>();
        Observable<Long> tick = Observable.interval(1, TimeUnit.SECONDS, scheduler);
        Observable<String> observable = Observable.fromIterable(letters)
                                                  .zipWith(tick, (string, index) -> index + "-" + string);
        observable.subscribeOn(scheduler).subscribe(subscriber);
        subscriber.assertNoValues();
        scheduler.advanceTimeTo(6, TimeUnit.SECONDS);
        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(5);
        subscriber.assertValues("0-A", "1-B", "2-C", "3-D", "4-E");
    }

}

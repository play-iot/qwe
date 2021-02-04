package io.github.zero88.qwe.scheduler;

import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.skyscreamer.jsonassert.Customization;

import io.github.zero88.qwe.EventbusHelper;
import io.github.zero88.qwe.JsonHelper;
import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.component.ComponentTestHelper;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.Waybill;
import io.github.zero88.qwe.scheduler.mock.JobModelCreator;
import io.github.zero88.qwe.scheduler.mock.MockEventScheduler;
import io.github.zero88.qwe.scheduler.mock.MockEventScheduler.FailureProcessEventSchedulerListener;
import io.github.zero88.qwe.scheduler.mock.MockEventScheduler.MockProcessEventSchedulerListener;
import io.github.zero88.qwe.scheduler.model.job.EventbusJobModel;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.CronTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.PeriodicTriggerModel;
import io.github.zero88.qwe.scheduler.model.trigger.QWETriggerModel;
import io.github.zero88.qwe.scheduler.service.SchedulerRegisterArgs;
import io.github.zero88.qwe.scheduler.service.SchedulerRegisterResp;
import io.github.zero88.qwe.scheduler.service.SchedulerRegisterResp.Fields;
import io.github.zero88.utils.DateTimes.Iso8601Parser;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.UUID64;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class SchedulerVerticleTest {

    private static final Function<String, Customization> UTC_DATE = timeKey -> new Customization(
        "data." + timeKey + ".utc", (o1, o2) -> true);
    private static final Function<String, Customization> LOCAL_DATE = timeKey -> new Customization(
        "data." + timeKey + ".local", (o1, o2) -> true);
    private static final Customization SKIP_LOCAL_DATE = LOCAL_DATE.apply(Fields.firstFireTime);
    private static final Customization SKIP_UTC_DATE = UTC_DATE.apply(Fields.firstFireTime);
    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Vertx vertx;
    private SchedulerConfig config;
    private EventbusClient eventbus;

    @BeforeClass
    public static void beforeSuite() {
        TestHelper.setup();
    }

    @Before
    public void before(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();
        config = SchedulerConfig.builder().schedulerName(UUID64.random()).build();
        SchedulerVerticle verticle = ComponentTestHelper.deploy(vertx, context, config.toJson(),
                                                                new SchedulerProvider(), folder.getRoot().toPath());
        eventbus = EventbusClient.create(verticle.sharedData());
        //TODO fix it after create Application.ready
        Thread.sleep(1000);
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test(timeout = 70 * 1000)
    public void test_add_cron_schedule_success(TestContext context) {
        final Async async = context.async(3);
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        Waybill event = initRegisterWaybill(JobModelCreator.create("abc"),
                                            CronTriggerModel.builder().name("t1").expr("0 0/1 * 1/1 * ? *").build());
        eventbus.fire(event, EventbusHelper.replyAsserter(context, registerAsserter(context, async, "t1", "abc")));
        EventbusHelper.assertReceivedData(vertx, async, MockEventScheduler.CALLBACK_EVENT.getAddress(),
                                          JsonHelper.asserter(context, async, countResp(0)));
    }

    @Test
    public void test_add_periodic_schedule_success(TestContext context) throws InterruptedException {
        final Async async = context.async(4);
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        eventbus.fire(initRegisterWaybill(JobModelCreator.create("xxx"), PeriodicTriggerModel.builder()
                                                                                             .name("t2")
                                                                                             .intervalInSeconds(3)
                                                                                             .repeat(1)
                                                                                             .build()),
                      EventbusHelper.replyAsserter(context, registerAsserter(context, async, "t2", "xxx")));
        final String addr = MockEventScheduler.CALLBACK_EVENT.getAddress();
        CountDownLatch latch = new CountDownLatch(1);
        EventbusHelper.assertReceivedData(vertx, async, addr, o -> {
            latch.countDown();
            JsonHelper.asserter(context, async, countResp(0)).accept(o);
        });
        latch.await(4, TimeUnit.SECONDS);
        EventbusHelper.assertReceivedData(vertx, async, addr, JsonHelper.asserter(context, async, countResp(1)));
    }

    @Test
    public void test_add_one_job_to_different_trigger_should_success(TestContext context) throws InterruptedException {
        final Async async = context.async(2);
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        CronTriggerModel cronTrigger = CronTriggerModel.builder().name("t1").expr("0 0/1 * 1/1 * ? *").build();
        PeriodicTriggerModel periodicTrigger = PeriodicTriggerModel.builder().name("t2").intervalInSeconds(3).build();
        Waybill event1 = initRegisterWaybill(JobModelCreator.create("abc"), cronTrigger);
        Waybill event2 = initRegisterWaybill(JobModelCreator.create("abc"), periodicTrigger);
        CountDownLatch latch = new CountDownLatch(1);
        eventbus.fire(event1, e -> {
            latch.countDown();
            EventbusHelper.replyAsserter(context, async, registerResponse("t1", "abc"), SKIP_LOCAL_DATE, SKIP_UTC_DATE)
                          .handle(e);
        });
        latch.await(1, TimeUnit.SECONDS);
        eventbus.fire(event2,
                      EventbusHelper.replyAsserter(context, async, registerResponse("t2", "abc"), SKIP_LOCAL_DATE,
                                                   SKIP_UTC_DATE));
    }

    @Test
    public void test_add_same_trigger_should_failed(TestContext context) throws InterruptedException {
        final Async async = context.async(2);
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        PeriodicTriggerModel periodicTrigger = PeriodicTriggerModel.builder()
                                                                   .name("tr3")
                                                                   .repeat(10)
                                                                   .intervalInSeconds(100)
                                                                   .build();
        Waybill event1 = initRegisterWaybill(JobModelCreator.create("j1"), periodicTrigger);
        Waybill event2 = initRegisterWaybill(JobModelCreator.create("j2"), periodicTrigger);
        CountDownLatch latch = new CountDownLatch(1);
        eventbus.fire(event1, e -> {
            latch.countDown();
            EventbusHelper.replyAsserter(context, async, registerResponse("tr3", "j1"), SKIP_LOCAL_DATE, SKIP_UTC_DATE)
                          .handle(e);
        });
        latch.await(1, TimeUnit.SECONDS);
        eventbus.fire(event2, EventbusHelper.replyAsserter(context, async, new JsonObject(
            "{\"status\":\"FAILED\",\"action\":\"CREATE\",\"error\":{\"code\":\"ALREADY_EXIST\"," +
            "\"message\":\"Trigger DEFAULT.tr3 is already assigned to another job DEFAULT.j1\"}}")));
    }

    @Test
    public void test_get_trigger_should_success(TestContext context) throws InterruptedException {
        final Async async = context.async(4);
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        final EventbusJobModel j2 = JobModelCreator.create("j2");
        CronTriggerModel cron1Trigger = CronTriggerModel.builder().name("tr1").expr("0 0/1 * 1/1 * ? *").build();
        CronTriggerModel cron2Trigger = CronTriggerModel.builder().name("tr2").expr("0 0 12 1/1 * ? *").build();
        PeriodicTriggerModel periodicTrigger = PeriodicTriggerModel.builder()
                                                                   .name("tr3")
                                                                   .intervalInSeconds(1)
                                                                   .repeat(10)
                                                                   .build();
        Waybill event1 = initRegisterWaybill(JobModelCreator.create("j1"), periodicTrigger);
        Waybill event2 = initRegisterWaybill(JobModelCreator.create("j1"), cron1Trigger);
        Waybill event3 = initRegisterWaybill(j2, cron2Trigger);
        CountDownLatch latch = new CountDownLatch(3);
        eventbus.fire(event1, e -> {
            latch.countDown();
            EventbusHelper.replyAsserter(context, async, registerResponse("tr3", "j1"), SKIP_LOCAL_DATE, SKIP_UTC_DATE)
                          .handle(e);
        });
        eventbus.fire(event2, e -> {
            latch.countDown();
            EventbusHelper.replyAsserter(context, async, registerResponse("tr1", "j1"), SKIP_LOCAL_DATE, SKIP_UTC_DATE)
                          .handle(e);
        });
        eventbus.fire(event3, e -> {
            latch.countDown();
            EventbusHelper.replyAsserter(context, async, registerResponse("tr2", "j2"), SKIP_LOCAL_DATE, SKIP_UTC_DATE)
                          .handle(e);
        });
        context.assertTrue(latch.await(1, TimeUnit.SECONDS));
        final SchedulerRegisterResp resp = SchedulerRegisterResp.builder()
                                                                .triggerKey(cron2Trigger.getKey())
                                                                .jobKey(j2.getKey())
                                                                .nextFireTime(mockDate())
                                                                .build();
        final JsonObject payload = new JsonObject().put(Fields.jobKey, j2.getKey().toString())
                                                   .put(Fields.triggerKey, cron2Trigger.getKey().toString());
        eventbus.fire(config.getRegisterAddress(), EventPattern.REQUEST_RESPONSE,
                      EventMessage.initial(EventAction.GET_ONE, RequestData.builder().body(payload).build()),
                      EventbusHelper.replyAsserter(context, async,
                                                   EventMessage.success(EventAction.GET_ONE, resp).toJson(),
                                                   LOCAL_DATE.apply(Fields.nextFireTime),
                                                   UTC_DATE.apply(Fields.nextFireTime)));
    }

    @Test
    public void test_execute_job_failed_then_forward_should_success(TestContext context) {
        final Async async = context.async(2);
        final EventModel processEvent = EventModel.clone(MockEventScheduler.PROCESS_EVENT, "event.job.test.failure");
        eventbus.register(processEvent, new FailureProcessEventSchedulerListener());
        PeriodicTriggerModel periodicTrigger = PeriodicTriggerModel.builder().name("tr2").intervalInSeconds(5).build();
        Waybill event1 = initRegisterWaybill(JobModelCreator.create("abc", processEvent), periodicTrigger);
        eventbus.fire(event1,
                      e -> EventbusHelper.replyAsserter(context, async, registerResponse("tr2", "abc"), SKIP_LOCAL_DATE,
                                                        SKIP_UTC_DATE).handle(e));
        final JsonObject failedResp = new JsonObject(
            "{\"status\":\"FAILED\",\"action\":\"PUBLISH\",\"prevAction\":\"CREATE\"," +
            "\"error\":{\"code\":\"INVALID_ARGUMENT\",\"message\":\"Failed\"}}");
        EventbusHelper.assertReceivedData(vertx, async, MockEventScheduler.CALLBACK_EVENT.getAddress(),
                                          JsonHelper.asserter(context, async, failedResp));
    }

    @Test
    public void test_remove_should_success(TestContext context) throws InterruptedException {
        final Async async = context.async(2);
        final EventbusJobModel job = JobModelCreator.create("abc");
        final CronTriggerModel cron = CronTriggerModel.builder().name("tr1").expr("0 0/1 * 1/1 * ? *").build();
        eventbus.register(MockEventScheduler.PROCESS_EVENT, new MockProcessEventSchedulerListener());
        final JsonObject payload = new JsonObject().put("jobKey", JobKey.jobKey("abc").toString());
        final Waybill waybill = Waybill.builder()
                                       .address(config.getRegisterAddress())
                                       .pattern(EventPattern.REQUEST_RESPONSE)
                                       .action(EventAction.REMOVE)
                                       .payload(RequestData.builder().body(payload).build())
                                       .build();
        JsonObject r = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"REMOVE\",\"data\":{\"removed\":false}}");
        eventbus.fire(waybill, EventbusHelper.replyAsserter(context, async, r));
        Waybill event = initRegisterWaybill(job, cron);
        CountDownLatch latch = new CountDownLatch(1);
        eventbus.fire(event, e -> {
            latch.countDown();
            final JsonObject resp = registerResponse("tr1", "abc");
            EventbusHelper.replyAsserter(context, async, resp, SKIP_LOCAL_DATE, SKIP_UTC_DATE).handle(e);
        });
        context.assertTrue(latch.await(1, TimeUnit.SECONDS));
        r = new JsonObject("{\"status\":\"SUCCESS\",\"action\":\"REMOVE\",\"data\":{\"removed\":true}}");
        eventbus.fire(waybill, EventbusHelper.replyAsserter(context, async, r));
    }

    private Waybill initRegisterWaybill(QWEJobModel job, QWETriggerModel trigger) {
        return Waybill.builder()
                      .address(config.getRegisterAddress())
                      .pattern(EventPattern.REQUEST_RESPONSE)
                      .action(EventAction.CREATE)
                      .payload(RequestData.builder()
                                          .body(SchedulerRegisterArgs.builder()
                                                                     .job(job)
                                                                     .trigger(trigger)
                                                                     .build()
                                                                     .toJson())
                                          .build())
                      .build();
    }

    private Handler<JsonObject> registerAsserter(TestContext context, Async async, String triggerName, String jobName) {
        return body -> {
            JsonHelper.assertJson(context, async, registerResponse(triggerName, jobName), body, SKIP_LOCAL_DATE,
                                  SKIP_UTC_DATE);
            JsonObject fft = body.getJsonObject("data").getJsonObject(Fields.firstFireTime, null);
            context.assertNotNull(fft);
            context.assertTrue(Strings.isNotBlank(fft.getString("local")));
            context.assertTrue(Strings.isNotBlank(fft.getString("utc")));
            context.assertTrue(
                Iso8601Parser.parseZonedDateTime(fft.getString("utc")).getOffset().equals(ZoneOffset.UTC));
        };
    }

    private JsonObject registerResponse(String triggerName, String jobName) {
        final SchedulerRegisterResp resp = SchedulerRegisterResp.builder()
                                                                .jobKey(JobKey.jobKey(jobName))
                                                                .triggerKey(TriggerKey.triggerKey(triggerName))
                                                                .firstFireTime(mockDate())
                                                                .build();
        return EventMessage.success(EventAction.CREATE, resp).toJson();
    }

    private JsonObject mockDate() {
        return new JsonObject().put("local", "").put("utc", "");
    }

    private JsonObject countResp(int c) {
        return new JsonObject(
            "{\"status\":\"SUCCESS\",\"action\":\"PUBLISH\",\"prevAction\":\"CREATE\",\"data\":{\"count\":" + c + "}}");
    }

}

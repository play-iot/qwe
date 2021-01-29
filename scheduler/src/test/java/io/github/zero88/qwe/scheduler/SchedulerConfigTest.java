package io.github.zero88.qwe.scheduler;

import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.JsonHelper;
import io.github.zero88.qwe.scheduler.mock.MockSchedulerService;
import io.github.zero88.qwe.scheduler.service.SchedulerService;

public class SchedulerConfigTest {

    @Test
    public void test_default() throws JSONException {
        final SchedulerConfig def = SchedulerConfig.builder().build();
        final SchedulerConfig cfg = IConfig.fromClasspath("scheduler.json", SchedulerConfig.class);
        System.out.println(def.toJson().encodePrettily());
        JsonHelper.assertJson(def.toJson(), cfg.toJson());
    }

    @Test
    public void test_under_app_cfg() throws JSONException {
        final SchedulerConfig def = SchedulerConfig.builder().build();
        final SchedulerConfig cfg = IConfig.fromClasspath("app-cfg.json", SchedulerConfig.class);
        System.out.println(def.toJson().encodePrettily());
        JsonHelper.assertJson(def.toJson(), cfg.toJson());
    }

    @Test
    public void serialize_custom_schedule_name() {
        SchedulerConfig config = SchedulerConfig.builder().schedulerName("abc").build();
        System.out.println(config.toJson());
        Assert.assertEquals("abc", config.getSchedulerName());
        Assert.assertEquals("qwe.scheduler.register", config.getRegisterAddress());
        Assert.assertEquals("qwe.scheduler.monitor", config.getMonitorAddress());
        Assert.assertEquals(SchedulerService.class, config.schedulerServiceClass());
        Assert.assertNotNull(config.getWorkerConfig());
        Assert.assertEquals("worker-pool-scheduler-abc", config.getWorkerConfig().getPoolName());
        Assert.assertEquals(5, config.getWorkerConfig().getPoolSize());
        Assert.assertEquals(60, config.getWorkerConfig().getMaxExecuteTime());
        Assert.assertEquals(TimeUnit.SECONDS, config.getWorkerConfig().getMaxExecuteTimeUnit());
    }

    @Test
    public void deserialize_custom_schedule_name() {
        SchedulerConfig from = IConfig.from("{\"schedulerName\":\"hello\"}", SchedulerConfig.class);
        Assert.assertEquals("hello", from.getSchedulerName());
        Assert.assertEquals("qwe.scheduler.register", from.getRegisterAddress());
        Assert.assertEquals("qwe.scheduler.monitor", from.getMonitorAddress());
        Assert.assertEquals("worker-pool-scheduler-hello", from.getWorkerConfig().getPoolName());
        Assert.assertEquals(5, from.getWorkerConfig().getPoolSize());
        Assert.assertEquals(60, from.getWorkerConfig().getMaxExecuteTime());
        Assert.assertEquals(TimeUnit.SECONDS, from.getWorkerConfig().getMaxExecuteTimeUnit());
    }

    @Test
    public void deserialize_custom() {
        SchedulerConfig from = IConfig.from(
            "{\"schedulerServiceClass\":\"io.github.zero88.qwe.scheduler.mock.MockSchedulerService\"," +
            "\"schedulerName\":\"hello\",\"registerAddress\":\"qwe.scheduler.register.abc\"," +
            "\"monitorAddress\":\"qwe.scheduler.monitor.abc\"," +
            "\"__schedule_worker__\":{\"poolName\":\"worker-pool-scheduler-abc\"," +
            "\"poolSize\":3,\"maxExecuteTime\":1,\"maxExecuteTimeUnit\":\"MINUTES\"}}", SchedulerConfig.class);
        Assert.assertEquals("hello", from.getSchedulerName());
        Assert.assertEquals("qwe.scheduler.register.abc", from.getRegisterAddress());
        Assert.assertEquals(MockSchedulerService.class, from.schedulerServiceClass());
        Assert.assertEquals("qwe.scheduler.monitor.abc", from.getMonitorAddress());
        Assert.assertEquals("worker-pool-scheduler-abc", from.getWorkerConfig().getPoolName());
        Assert.assertEquals(3, from.getWorkerConfig().getPoolSize());
        Assert.assertEquals(1, from.getWorkerConfig().getMaxExecuteTime());
        Assert.assertEquals(TimeUnit.MINUTES, from.getWorkerConfig().getMaxExecuteTimeUnit());
    }

    @Test
    public void deserialize_invalid_service_class_then_fallback() {
        final SchedulerConfig config = IConfig.from(
            "{\"schedulerServiceClass\":\"io.github.zero88.qwe.MockSchedulerService\"}", SchedulerConfig.class);
        Assert.assertEquals(SchedulerService.class, config.schedulerServiceClass());
    }

}

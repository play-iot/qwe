package io.github.zero88.msa.bp.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.msa.bp.cluster.ClusterDelegate;
import io.github.zero88.msa.bp.cluster.ClusterType;
import io.github.zero88.msa.bp.cluster.IClusterDelegate;
import io.github.zero88.utils.Reflections.ReflectionClass;

public class ReflectionClassExtraTest {

    @Test
    public void test_get_mock_classes_by_annotation() {
        List<Class<IClusterDelegate>> classes = ReflectionClass.stream("io.zero88.core.utils", IClusterDelegate.class,
                                                                       ClusterDelegate.class)
                                                               .collect(Collectors.toList());
        Assert.assertEquals(1, classes.size());
        IClusterDelegate delegate = ReflectionClass.createObject(classes.get(0));
        Assert.assertNotNull(delegate);
        Assert.assertEquals(ClusterType.IGNITE, delegate.getTypeName());
    }

    @Test
    public void test_get_classes_by_annotation() {
        List<Class<IClusterDelegate>> classes = ReflectionClass.stream("io.zero88.core.cluster",
                                                                       IClusterDelegate.class, ClusterDelegate.class)
                                                               .collect(Collectors.toList());
        Assert.assertEquals(1, classes.size());
        IClusterDelegate delegate = ReflectionClass.createObject(classes.get(0));
        Assert.assertNotNull(delegate);
        Assert.assertEquals(ClusterType.HAZELCAST, delegate.getTypeName());
    }

}

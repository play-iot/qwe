package io.github.zero88.qwe.scheduler.solution;

import io.github.zero88.utils.Strings;

import lombok.NonNull;

public class JobKey extends Key<JobKey> {

    public JobKey(String group, @NonNull String name) { super(group, name); }

    public static JobKey createUnique()               { return createUnique(null); }

    public static JobKey createUnique(String group) {
        String g = Strings.isBlank(group) ? DEFAULT_GROUP : group;
        return new JobKey(g, createUniqueName(g));
    }

}

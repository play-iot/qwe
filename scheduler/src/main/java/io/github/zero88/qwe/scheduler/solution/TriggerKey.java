package io.github.zero88.qwe.scheduler.solution;

import io.github.zero88.utils.Strings;

import lombok.NonNull;

/**
 * Uniquely identifies a {@link Trigger}
 */
public class TriggerKey extends Key<TriggerKey> {

    public TriggerKey(String group, @NonNull String name) { super(group, name); }

    public static TriggerKey createUnique()               { return createUnique(null); }

    public static TriggerKey createUnique(String group) {
        String g = Strings.isBlank(group) ? DEFAULT_GROUP : group;
        return new TriggerKey(g, createUniqueName(g));
    }

}

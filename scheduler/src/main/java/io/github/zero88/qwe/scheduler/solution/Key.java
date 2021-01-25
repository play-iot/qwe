package io.github.zero88.qwe.scheduler.solution;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import io.github.zero88.utils.Strings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class Key<T extends Key> implements Serializable, Comparable<T> {

    /**
     * The default group for scheduling entities, with the value "DEFAULT".
     */
    public static final String DEFAULT_GROUP = "DEFAULT";
    private static final long serialVersionUID = -7141167957642391350L;
    @NonNull
    private final String group;
    @NonNull
    private final String name;

    protected Key(String group, String name) {
        this.group = Strings.isBlank(group) ? DEFAULT_GROUP : group;
        this.name = Strings.isBlank(name) ? createUniqueName(this.group) : name;
    }

    public static String createUniqueName(String group) {
        String n1 = UUID.randomUUID().toString();
        String n2 = UUID.nameUUIDFromBytes((Strings.isBlank(group) ? DEFAULT_GROUP : group).getBytes()).toString();
        return String.format("%s-%s", n2.substring(24), n1);
    }

    /**
     * Return the string representation of the key. The format will be: {@code <group>.<name>}
     *
     * @return the string representation of the key
     */
    @Override
    public final String toString() {
        return group + '.' + name;
    }

    public final int compareTo(T o) {
        if (Objects.isNull(o)) {
            return -1;
        }
        if (group.equals(DEFAULT_GROUP) && !o.getGroup().equals(DEFAULT_GROUP)) {
            return -1;
        }
        if (!group.equals(DEFAULT_GROUP) && o.getName().equals(DEFAULT_GROUP)) {
            return 1;
        }

        int r = group.compareTo(o.getGroup());
        if (r != 0) {
            return r;
        }

        return name.compareTo(o.getName());
    }

}

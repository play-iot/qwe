package io.zero88.qwe.scheduler.model.job;

import org.quartz.JobKey;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractQWEJobModel implements QWEJobModel {

    @Getter
    @Include
    private final JobKey key;
    @Include
    private final boolean forwardIfFailure;

    @Override
    public boolean forwardIfFailure() { return forwardIfFailure; }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractJobModelBuilder<T extends QWEJobModel, B extends AbstractJobModelBuilder> {

        @Getter
        boolean forwardIfFailure = true;
        String name;
        String group;

        public B group(String group) {
            this.group = group;
            return (B) this;
        }

        public B name(String name) {
            this.name = name;
            return (B) this;
        }

        public B forwardIfFailure(boolean forwardIfFailure) {
            this.forwardIfFailure = forwardIfFailure;
            return (B) this;
        }

        protected JobKey key() { return QWEJobModel.createKey(group, name); }

        public abstract T build();

    }

}

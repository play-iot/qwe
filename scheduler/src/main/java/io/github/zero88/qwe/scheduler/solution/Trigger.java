package io.github.zero88.qwe.scheduler.solution;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public interface Trigger extends Serializable, Comparable<Trigger> {

    /**
     * Trigger key to identify unique instance
     *
     * @return key
     */
    TriggerKey getKey();

    /**
     * Get the time at which the {@code Trigger} should occur.
     *
     * @return started time
     */
    Date getStartTime();

    /**
     * Get the time at which the {@code Trigger} should quit repeating - regardless of any remaining repeats (based on
     * the trigger's particular repeat settings).
     *
     * @return end time
     * @see #getFinalFireTime()
     */
    Date getEndTime();

    /**
     * Returns the next time at which the {@code Trigger} is scheduled to fire.
     * <p>
     * <i>Note:</i> that the time returned can possibly be in the past, if the time that was
     * computed for the trigger to next fire has already arrived, but the scheduler has not yet been able to fire the
     * trigger (which would likely be due to lack of resources e.g. threads).
     * <p>
     * The value returned is not guaranteed to be valid until after the {@code Trigger} has been added to the
     * scheduler.
     *
     * @return next fire time. Might be {@code null} if {@code Trigger} will not fire again
     */
    Date getNextFireTime();

    /**
     * Returns the previous time at which the {@code Trigger} fired.
     *
     * @return previous fire time. Might be {@code null} if {@code Trigger} has not yet fired
     */
    Date getPreviousFireTime();

    /**
     * Returns the next time at which the {@code Trigger} will fire, after the given time.
     *
     * @return next fire time. Might be {@code null} if {@code Trigger} will not fire after the given time
     */
    Date getFireTimeAfter(Date afterTime);

    /**
     * Returns the last time at which the {@code Trigger} will fire.
     * <p>
     * <i>Note:</i> that the return time *may* be in the past.
     *
     * @return last time. Might be {@code null} if {@code Trigger} will repeat indefinitely
     */
    Date getFinalFireTime();

    /**
     * A Comparator that compares trigger's next fire times, or in other words, sorts them according to earliest next
     * fire time.  If the fire times are the same, then the triggers are sorted according to priority (highest value
     * first), if the priorities are the same, then they are sorted by key.
     */
    class TriggerTimeComparator implements Comparator<Trigger>, Serializable {

        private static final long serialVersionUID = -3904243490805975570L;

        static int compare(Date nextFireTime1, TriggerKey key1, Date nextFireTime2, TriggerKey key2) {
            if (nextFireTime1 != null || nextFireTime2 != null) {
                if (nextFireTime1 == null) {
                    return 1;
                }

                if (nextFireTime2 == null) {
                    return -1;
                }

                if (nextFireTime1.before(nextFireTime2)) {
                    return -1;
                }

                if (nextFireTime1.after(nextFireTime2)) {
                    return 1;
                }
            }

            return key1.compareTo(key2);
        }

        public int compare(Trigger t1, Trigger t2) {
            return compare(t1.getNextFireTime(), t1.getKey(), t2.getNextFireTime(), t2.getKey());
        }

    }

}

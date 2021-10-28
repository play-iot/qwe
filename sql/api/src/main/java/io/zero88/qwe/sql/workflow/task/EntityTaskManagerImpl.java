package io.zero88.qwe.sql.workflow.task;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class EntityTaskManagerImpl implements EntityTaskManager {

    private final EntityTask prePersistTask;
    private final EntityTask postPersistTask;
    private final EntityTask postPersistAsyncTask;

}

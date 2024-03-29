package cloud.playio.qwe;

import lombok.NonNull;

public interface HasSharedData {

    /**
     * Get shared data proxy
     *
     * @return shared data proxy
     * @see SharedDataLocalProxy
     */
    @NonNull SharedDataLocalProxy sharedData();

}

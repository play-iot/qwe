package cloud.playio.qwe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface HasLogger {

    static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    default Logger logger() {
        return LogManager.getLogger(getClass());
    }

}

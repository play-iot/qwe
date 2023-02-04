package cloud.playio.qwe.crypto;

import java.net.URI;

final class JarFileUtils {

    static boolean isJarUrl(String path) {
        return "jar".equals(URI.create(path).getScheme());
    }

    static String normalize(String path) {
        if (isJarUrl(path)) {
            return path;
        }
        // to replace prefix file:/
        return URI.create(path).getPath();
    }

}

package edu.ccrm.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Persistable<T> {
    void save(List<T> items, Path filePath) throws IOException;
    List<T> load(Path filePath) throws IOException;

    default boolean isValidPath(Path path) {
        return path != null && !path.toString().isEmpty();
    }

    static String getDefaultExtension() {
        return ".csv";
    }
}


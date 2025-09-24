package edu.ccrm.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class RecursiveUtil {


    public static long calculateDirectorySize(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return 0;
        }

        return calculateDirectorySizeRecursive(directory);
    }

    private static long calculateDirectorySizeRecursive(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }

        if (Files.isDirectory(path)) {
            long totalSize = 0;
            try (Stream<Path> children = Files.list(path)) {
                for (Path child : children.toArray(Path[]::new)) {
                    totalSize += calculateDirectorySizeRecursive(child);
                }
            }
            return totalSize;
        }

        return 0;
    }


    public static void printDirectoryStructure(Path directory, int depth) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        String indent = "  ".repeat(depth);
        String fileName = directory.getFileName().toString();

        if (Files.isDirectory(directory)) {
            System.out.println(indent + "[DIR] " + fileName);
            try (Stream<Path> children = Files.list(directory)) {
                children.sorted()
                        .forEach(child -> {
                            try {
                                printDirectoryStructure(child, depth + 1);
                            } catch (IOException e) {
                                System.err.println(indent + "  [ERROR] " + e.getMessage());
                            }
                        });
            }
        } else {
            long size = Files.size(directory);
            System.out.println(indent + "[FILE] " + fileName + " (" + size + " bytes)");
        }
    }


    public static long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static long factorial(int n) {
        return factorialTailRecursive(n, 1);
    }

    private static long factorialTailRecursive(int n, long accumulator) {
        if (n <= 1) {
            return accumulator;
        }
        return factorialTailRecursive(n - 1, n * accumulator);
    }
}

package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class BackupService {
    private final AppConfig config;

    public BackupService() {
        this.config = AppConfig.getInstance();
    }

    public Path createBackup() throws IOException {
        Path dataDir = Paths.get(config.getDataDirectory());
        Path exportDir = dataDir.resolve("exports");
        Path backupDir = dataDir.resolve("backups");


        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path timestampedBackupDir = backupDir.resolve("backup_" + timestamp);

        Files.createDirectories(timestampedBackupDir);

        if (Files.exists(exportDir)) {
            copyDirectoryContents(exportDir, timestampedBackupDir);
        }

        createBackupManifest(timestampedBackupDir);

        return timestampedBackupDir;
    }

    private void copyDirectoryContents(Path source, Path target) throws IOException {
        try (Stream<Path> paths = Files.walk(source)) {
            paths.filter(Files::isRegularFile)
                    .forEach(sourcePath -> {
                        try {
                            Path targetPath = target.resolve(source.relativize(sourcePath));
                            Files.createDirectories(targetPath.getParent());
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Error copying file: " + e.getMessage());
                        }
                    });
        }
    }

    private void createBackupManifest(Path backupDir) throws IOException {
        Path manifestPath = backupDir.resolve("backup_manifest.txt");

        try (var writer = Files.newBufferedWriter(manifestPath)) {
            writer.write("CCRM Backup Manifest\n");
            writer.write("====================\n");
            writer.write("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Backup Directory: " + backupDir.toString() + "\n\n");
            writer.write("Files included:\n");

            try (Stream<Path> files = Files.walk(backupDir)) {
                files.filter(Files::isRegularFile)
                        .filter(p -> !p.getFileName().toString().equals("backup_manifest.txt"))
                        .forEach(file -> {
                            try {
                                long size = Files.size(file);
                                writer.write(String.format("- %s (%d bytes)\n",
                                        backupDir.relativize(file), size));
                            } catch (IOException e) {
                                System.err.println("Error reading file size: " + e.getMessage());
                            }
                        });
            }
        }
    }

    public void cleanOldBackups(int keepCount) throws IOException {
        Path backupDir = Paths.get(config.getDataDirectory(), "backups");

        if (!Files.exists(backupDir)) {
            return;
        }

        try (Stream<Path> backupDirs = Files.list(backupDir)) {
            backupDirs.filter(Files::isDirectory)
                    .sorted((p1, p2) -> {
                        try {
                            return Files.getLastModifiedTime(p2)
                                    .compareTo(Files.getLastModifiedTime(p1));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .skip(keepCount)
                    .forEach(this::deleteDirectoryRecursively);
        }
    }

    private void deleteDirectoryRecursively(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error deleting directory: " + e.getMessage());
        }
    }
}


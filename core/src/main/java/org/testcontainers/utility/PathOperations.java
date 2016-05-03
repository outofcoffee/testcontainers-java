package org.testcontainers.utility;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filesystem operation utility methods.
 */
public class PathOperations {
    /**
     * Example:
     * <pre>/C:/Users/Pete/projects/testcontainers-java/test-resource.txt</pre>
     */
    private static final Pattern WINDOWS_BINDMOUNT_PATTERN = Pattern.compile("(\\/[a-zA-Z]):(\\/.*)");

    /**
     * Recursively delete a directory and all its subdirectories and files.
     * @param directory path to the directory to delete.
     */
    public static void recursiveDeleteDir(final @NonNull Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
        }
    }

    /**
     * Make a directory, plus any required parent directories.
     *
     * @param directory the directory path to make
     */
    public static void mkdirp(Path directory) {
        boolean result = directory.toFile().mkdirs();
        if (!result) {
            throw new IllegalStateException("Failed to create directory at: " + directory);
        }
    }

    /**
     * Formats the host side of a bindmount spec, allowing for different OS behaviour.
     * <p>
     *     See https://docs.docker.com/engine/userguide/containers/dockervolumes/#mount-a-host-directory-as-a-data-volume
     * </p>
     * Example:
     * <pre>/C:/Users/Pete/projects/testcontainers-java/test-resource.txt</pre>
     * becomes:
     * <pre>/c/Users/Pete/projects/testcontainers-java/test-resource.txt</pre>
     *
     * @param path the host side of a bindmount spec
     * @return correctly formatted spec fragment
     */
    public static String formatForCurrentEnvironment(String path) {
        final Matcher matcher = WINDOWS_BINDMOUNT_PATTERN.matcher(path);

        if (!matcher.matches()) {
            // short-circuit
            return path;
        } else {
            return matcher.group(1).toLowerCase() + matcher.group(2);
        }
    }
}

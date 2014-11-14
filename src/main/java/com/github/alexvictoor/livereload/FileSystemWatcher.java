package com.github.alexvictoor.livereload;


import io.netty.util.internal.ConcurrentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Send notifications when files are modified in a folder
 * Strongly inspired from https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 */
public class FileSystemWatcher {

    public static final Logger logger = LoggerFactory.getLogger(FileSystemWatcher.class);

    private String rootFolder;
    private Set<Callback> callbacks = new ConcurrentSet<Callback>();
    private ExecutorService executor;
    private WatchService watcher;
    private Map<WatchKey, Path> folders = new ConcurrentHashMap<WatchKey, Path>();

    public FileSystemWatcher(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerFolder(final Path folder) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                logger.debug("registering folder {}", dir);
                folders.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void start() {
        final Path rootPath = Paths.get(rootFolder);
        File root = rootPath.toFile();
        if (!root.exists() || root.isFile()) {
            throw new RuntimeException("Cannot watch a directory that does not exist; " + rootFolder);
        }
        try {
            logger.info("Starting to watch {}", root.getCanonicalPath());
            watcher = FileSystems.getDefault().newWatchService();
            registerFolder(rootPath);
            rootPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            WatchKey key = watcher.take();
                            List<WatchEvent<?>> events = key.pollEvents();
                            for (WatchEvent<?> event : events) {
                                WatchEvent.Kind kind = event.kind();

                                if (kind == OVERFLOW) {
                                    sendNotification(rootPath);
                                    continue;
                                }

                                Path path = (Path) event.context();
                                Path folder = folders.get(key);
                                Path fullPath = folder.resolve(path);

                                // print out event
                                logger.debug("Filesystem event {} {}", event.kind().name(), fullPath);

                                // if directory is created, and watching recursively, then
                                // register it and its sub-directories
                                if (kind == ENTRY_CREATE) {
                                    try {
                                        if (Files.isDirectory(fullPath, NOFOLLOW_LINKS)) {
                                            registerFolder(fullPath);
                                        }
                                    } catch (IOException ex) {
                                        logger.error("Folder registration error", ex);
                                    }
                                }

                                if (!Files.isDirectory(fullPath, NOFOLLOW_LINKS)) {

                                    sendNotification(fullPath);
                                }

                                key.reset();
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.warn("Filesystem watcher interrupted", e);
                    } catch (IOException e) {
                        logger.warn("File path error", e);
                    }


                }
            });
        } catch (IOException e) {
            logger.error("Error while creating file system watcher", e);
        }

    }

    private void sendNotification(Path fullPath) throws IOException {
        String canonicalPath = fullPath.toFile().getCanonicalPath();
        for (Callback callback : callbacks) {
            callback.onFileChanged(canonicalPath);
        }
    }

    public void stop() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    public static interface Callback {
        void onFileChanged(String path);
    }
}

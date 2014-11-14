package com.github.alexvictoor.livereload;


import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;

public class FileSystemWatcherTest {

    File watchedFolder;

    String fileModifiedPath;
    FileSystemWatcher watcher;

    @Before
    public void setUp() throws Exception {
        watchedFolder = Files.newTemporaryFolder();
        watcher = new FileSystemWatcher(watchedFolder.getAbsolutePath());
        watcher.addCallback(new WatcherCallback());
    }

    @After
    public void tearDown() throws Exception {
        watcher.stop();
        Files.delete(watchedFolder);

    }

    @Test
    public void should_send_one_file_notification_when_a_file_is_created() throws IOException {
        // given
        watcher.start();
        // when
        File someFile = new File(watchedFolder, "someFile");
        someFile.createNewFile();
        // then
        await().until(fileModificationDetected(someFile));
    }

    @Test
    public void should_send_one_file_notification_when_a_file_is_modified() throws IOException {
        // given
        File someFile = new File(watchedFolder, "someFile");
        someFile.createNewFile();
        watcher.start();
        // when
        update(someFile);
        // then
        await().until(fileModificationDetected(someFile));
    }

    @Test
    public void should_send_one_file_notification_when_a_file_in_a_subdirectory_is_modified() throws IOException {
        // given
        File subdirectory = new File(watchedFolder, "subdirectory");
        subdirectory.mkdir();
        File someFile = new File(subdirectory, "someFile");
        someFile.createNewFile();
        watcher.start();
        // when
        update(someFile);
        // then
        await().until(fileModificationDetected(someFile));
    }

    @Test
    public void should_send_one_notification_when_a_file_is_created_in_a_new_subdirectory() throws IOException, InterruptedException {
        // given
        watcher.start();
        File subdirectory = new File(watchedFolder, "subdirectory");
        subdirectory.mkdir();
        File someFile = new File(subdirectory, "someFile");
        // when
        waitALittleBit();
        someFile.createNewFile();
        // then
        await().until(fileModificationDetected(someFile));
    }

    @Test
    public void should_send_one_notification_when_a_file_is_updated_in_a_new_subdirectory() throws IOException, InterruptedException {
        // given
        watcher.start();
        File subdirectory = new File(watchedFolder, "subdirectory");
        subdirectory.mkdir();
        File someFile = new File(subdirectory, "someFile");
        someFile.createNewFile();
        // when
        waitALittleBit();
        update(someFile);
        // then
        await().until(fileModificationDetected(someFile));
    }

    @Test
    public void should_keep_sending_notifications_when_a_file_change_several_times() throws IOException, InterruptedException {
        // given
        watcher.start();
        File someFile = new File(watchedFolder, "someFile");
        someFile.createNewFile();
        // when
        waitALittleBit();
        update(someFile);
        fileModifiedPath = null;
        waitALittleBit();
        update(someFile);
        // then
        await().until(fileModificationDetected(someFile));
    }

    private void waitALittleBit() throws InterruptedException {
        Object lock = 1;
        synchronized (lock) {
            lock.wait(200);
        }
    }

    private void update(File someFile) {
        someFile.setLastModified(someFile.lastModified()+1);
    }

    private Callable<Boolean> fileModificationDetected(final File file) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return file.getCanonicalPath().endsWith(fileModifiedPath);
            }
        };
    }

    public class WatcherCallback implements FileSystemWatcher.Callback {

        @Override
        public void onFileChanged(String path) {
            fileModifiedPath = path;
        }
    }


}
package ru.macrobit.upload;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOG = Logger.getLogger(App.class);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        scheduler.scheduleWithFixedDelay(new Task(), 0, 3000, TimeUnit.MILLISECONDS);
    }

    public static class Task implements Runnable {
        @Override
        public void run() {
            try {
                LOG.debug("started ...");
                Uploader uploader = UploaderSingleton.getInstance();
                File folder = new File(uploader.getProperties().getProperty("work_dir"));
                List<File> files = Arrays.asList(folder.listFiles());
                if (files.size() != 0)
                    files.parallelStream()
                            .filter(File::isFile)
                            .forEach(file -> uploader.upload(file));
                LOG.debug("finish!");
            } catch (Exception e) {
                LOG.error("Exception", e);
            }
        }
    }
}

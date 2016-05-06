package ru.macrobit.upload;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUploader;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by david on 06.05.16.
 */
public enum UploaderSingleton implements Uploader {

    INSTANCE {
        public void upload(File file) {
            try (InputStream in = new FileInputStream(file)) {
                client.files()
                        .uploadBuilder(DEFAULT_PATH + file.getName())
                        .withMode(WriteMode.ADD)
                        .uploadAndFinish(in);
                log.debug(file.getName());
                file.delete();
            } catch (IOException | DbxException e) {
                log.error("error", e);
            }
        }

        public Properties getProperties() {
            return properties;
        }
    };

    private static final Logger log = Logger.getLogger(UploaderSingleton.class);
    private static final Properties properties;

    /** Use a static initializer to read from file. */
    static {
        properties = new Properties();
        try (InputStream inputStream = UploaderSingleton.class.getResourceAsStream("/app.properties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read properties file", e);
        }
    }

    private static final DbxRequestConfig config = new DbxRequestConfig(properties.getProperty("dropbox.client_identifier"), "en_US");
    private static final DbxClientV2 client = new DbxClientV2(config, properties.getProperty("dropbox.access_token"));
    private static final String DEFAULT_PATH = properties.getProperty("dropbox.path");

    public static Uploader getInstance() {
        return UploaderSingleton.INSTANCE;
    }
}

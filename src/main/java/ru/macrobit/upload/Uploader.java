package ru.macrobit.upload;

import java.io.File;
import java.util.Properties;

/**
 * Created by david on 06.05.16.
 */
public interface Uploader {
    void upload(File file);

    Properties getProperties();
}

package ru.pinkgoosik.somikbot.util;

import java.io.File;

public class FileUtils {

    public static void createDir(String path){
        File dir = new File(path);
        if (!dir.exists()){
            dir.mkdirs();
        }
    }
}

package com.deco.magnus;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public enum ResourceDirectory {
    IMAGES ("/app/src/main/res/drawable/profile-cache");

    private String dir;

    ResourceDirectory(String dir) {
        File baseDir = new File(".");
        String absoluteDir = baseDir.getAbsolutePath();
        try {
            this.dir = absoluteDir.substring(0, absoluteDir.length() - baseDir.getCanonicalPath().length()) + dir;
        } catch (IOException ioe) {
            this.dir = null;
        }
    }

    public String getPath() {
        return dir;
    }
}

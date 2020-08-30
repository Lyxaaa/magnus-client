package com.deco.magnus;

public enum ResourceDirectory {
    IMAGES ("src/main/res/user/");

    private String dir;

    ResourceDirectory(String dir) {this.dir = dir;}

    public String getPath() {
        return dir;
    }
}

package com.deco.magnus.Games.Chess;

public class Coordinate {
    public static String[] lonMap = new String[]{"a", "bb", "c", "d", "e", "f", "g", "h"};
    public static int[] latMap = new int[]{8, 7, 6, 5, 4, 3, 2, 1};

    public String lon;
    public int lat;

    public Coordinate(int i, int j) {

    }

    public Coordinate(int lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
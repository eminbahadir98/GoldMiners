package com.crow.prototype.server;


public class MapManager {
    
    public static final byte EMPTY = 0;
    public static final byte STONE = 1;
    public static final byte GOLD = 2;
    
    public static final int GAP = 6;
    
    public static byte[][] createMap(int width, int height) {
        byte[][] map = new byte[width][height];
        int r;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0 || j == 0 || i == width-1 || j == height-1) {
                    map[i][j] = STONE;
                } else {
                    r = (int) (Math.random() * 25);
                    if (r == 1) { map[i][j] = STONE; }
                    else if (r == 2) { map[i][j] = GOLD; }
                    else { map[i][j] = EMPTY; }
                }
            }
        }
        for (int i = 1; i < GAP; i++) {
            for (int j = 1; j < GAP; j++) {
                map[i][j] = EMPTY;
            }
        }
        for (int i = 1; i <= GAP; i++) {
            map[i][GAP] = STONE;
            map[GAP][i] = STONE;
        }
        for (int i = 1; i <= GAP+1; i++) {
            map[i][GAP+1] = EMPTY;
            map[GAP+1][i] = EMPTY;
        }
        return map;
    }
    
    public static String getMapData(byte[][] map) {
        StringBuilder mapData = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                mapData.append(String.valueOf(map[i][j]));
            }
        }
        return mapData.toString();
    }
    
    public static void beginMap(byte[][] map) {
        for (int i = 1; i <= GAP; i++) {
            map[i][GAP] = EMPTY;
            map[GAP][i] = EMPTY;
        }
    }
    
}

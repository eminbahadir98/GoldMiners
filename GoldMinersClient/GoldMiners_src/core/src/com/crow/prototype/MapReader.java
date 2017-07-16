package com.crow.prototype;


public class MapReader {
    
    public static final byte EMPTY = 0;
    public static final byte STONE = 1;
    public static final byte GOLD = 2;
    
    public static byte[][] translate(String mapData, int width, int height) {
        byte[][] map = new byte[width][height];
        int r = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = Byte.parseByte(mapData.substring(r, r+1));
                r++;
            }
        }
        return map;
    }
    
}

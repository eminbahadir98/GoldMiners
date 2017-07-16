package com.crow.prototype.server;


public class PlayerData {
    
    public static final String PICKAXE = "P";
    public static final String COIN = "C";
    
    int tileX;
    int tileY;
    String inventory;
    int[] inventoryCounts;
    boolean isMale;
    String name;
    
    public PlayerData(String name, boolean isMale) {
        tileX = 2;
        tileY = 2;
        inventory = "P";
        inventoryCounts = new int[64];
        inventoryCounts[0] = 1;
        this.isMale = isMale;
        this.name = name;
    }
    
    public void giveCoin(int amount) {
        if (!inventory.contains("C")) {
            inventory += "C";
        }
        inventoryCounts[inventory.indexOf("C")] += amount;
    }
    
    // Bahadir true 23 4 PC 1 150
    public String getData() {
        String dataStr = name + " " + isMale +
            " " + tileX + " " + tileY + " " + inventory;
        for (int i = 0; i < inventory.length(); i++) {
            dataStr += " " + inventoryCounts[i];
        }
        return dataStr;
    }
    
    // Bahadir true 23 4
    public String getPublicData() {
        String dataStr = name + " " + isMale +
            " " + tileX + " " + tileY;
        return dataStr;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}

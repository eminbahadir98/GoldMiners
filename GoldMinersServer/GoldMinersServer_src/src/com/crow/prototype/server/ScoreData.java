package com.crow.prototype.server;

public class ScoreData {
    
    public String playerName;
    public int extractNum;
    public int goldNum;
    
    public ScoreData(String playerName) {
        this.playerName = playerName;
    }
    
    // Bahadir 3 7
    public String getData() {
        return playerName + " " + extractNum + " " + goldNum;
    }
    
}

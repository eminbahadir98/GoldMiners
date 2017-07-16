package com.crow.prototype;

import com.crow.prototype.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import java.util.ArrayList;


public class Player {

    public static final int IDLE = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int DOWN = 3;
    public static final int RIGHT = 4;
    
    public static final int BLOCK = 32;
    
    private final GameScreen parent;
    
    public String name;
    public boolean isMale;
    
    public int state;
    public int speed;
    public int interactDir;
    
    public int x;
    public int y;
    public int tileX;
    public int tileY;
    public int relX;
    public int relY;
    
    public boolean walking;
    public boolean mining;
    
    public ArrayList<Item> inventory;
    
    private int path;
    private TiledMapTileLayer solidLayer;
    private TiledMapTileLayer interactLayer;
    
    public GlyphLayout nameGlyph;
    
    public Player(String name, boolean isMale, int tileX, int tileY, GameScreen parent) {
        this.name = name;
        this.isMale = isMale;
        this.tileX = tileX;
        this.tileY = tileY;
        this.relX = tileX;
        this.relY = tileY;
        this.x = tileX * BLOCK;
        this.y = tileY * BLOCK;
        
        this.state = IDLE;
        this.speed = 2;
        this.walking = false;
        this.path = 0;
        
        this.parent = parent;
        this.solidLayer = parent.solidLayer;
        this.interactLayer = parent.interactLayer;
        
        nameGlyph = new GlyphLayout(parent.game.nicknameFont, name);
        this.inventory = new ArrayList<Item>();
    }
    
    public boolean move(int direction) {
        boolean moved = false;
        if (mining && direction != IDLE) {
            if (interactDir != direction) {
                parent.interruptMining();
            }
        }
        else if (!walking) {
            if (direction == IDLE)
                state = direction;
            else if (direction != IDLE) {
                if (isMovable(direction) && checkInteraction(direction)) {
                    if (direction == UP) { tileY++; relY++; moved = true; }
                    else if (direction == LEFT) { tileX--; relX--; moved = true; }
                    else if (direction == DOWN) { tileY--; relY--; moved = true; }
                    else if (direction == RIGHT) { tileX++; relX++; moved = true; }
                    state = direction;
                    walking = true;
                }
                else {
                    state = IDLE;
                }
            }
        }
        return moved;
    }
    
    private boolean isMovable(int direction) {
        if (direction == UP) { return solidLayer.getCell(tileX, tileY + 1) == null; }
        else if (direction == LEFT) { return solidLayer.getCell(tileX - 1, tileY) == null; }
        else if (direction == DOWN) { return solidLayer.getCell(tileX, tileY - 1) == null; }
        else if (direction == RIGHT) { return solidLayer.getCell(tileX + 1, tileY) == null; }
        return true;
    }
    
    private boolean checkInteraction(int direction) {
        if (direction == UP) {
            Cell cell = interactLayer.getCell(tileX, tileY + 1);
            if (cell != null) {
                interactDir = direction;
                parent.interact(tileX, tileY + 1);
                return false;
            }
        }
        else if (direction == LEFT) {
            Cell cell = interactLayer.getCell(tileX - 1, tileY);
            if (cell != null) {
                interactDir = direction;
                parent.interact(tileX - 1, tileY);
                return false;
            }
        }
        else if (direction == DOWN) {
            Cell cell = interactLayer.getCell(tileX, tileY - 1);
            if (cell != null) {
                interactDir = direction;
                parent.interact(tileX, tileY - 1);
                return false;
            }
        }
        else if (direction == RIGHT) {
            Cell cell = interactLayer.getCell(tileX + 1, tileY);
            if (cell != null) {
                interactDir = direction;
                parent.interact(tileX + 1, tileY);
                return false;
            }
        }
        return true;
    }
  
    public void tick() {
        if (state != IDLE && walking) {
            if (state == UP) { y += speed; }
            else if (state == LEFT) { x -= speed; }
            else if (state == DOWN) { y -= speed; }
            else if (state == RIGHT) { x += speed; }
            
            path += speed;
            if (path >= 32) {
                walking = false;
                path = 0;
                parent.checkCamera();
            }
        }
    }
    
    public void giveItem(Item item) {
        for (Item i : inventory) {
            if (i.itemCode.equals(item.itemCode)) {
                i.count += item.count;
                return;
            }
        }
        inventory.add(item);
    }
}

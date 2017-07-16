package com.crow.prototype;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;


public class Stranger {
        
    public static final int BLOCK = 32;
    public static final int FPS = 60;
    public static final double PI = Math.PI;
    
    public static final int IDLE = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int DOWN = 3;
    public static final int RIGHT = 4;
    
    public static final int UPLEFT = 5;
    public static final int UPRIGHT = 6;
    public static final int DOWNLEFT = 7;
    public static final int DOWNRIGHT = 8;
    
    public String name;
    public boolean isMale;
    public int state;

    public int tileX;
    public int tileY;
    
    public float x;
    public float y;
    
    public float destX;
    public float destY;
    private float dx;
    private float dy;
    private float speed;
    // private double theta;
    
    public GlyphLayout nameGlyph;
    
    public Stranger(GoldMiners game, String name, boolean isMale, int tileX, int tileY) {
        this.name = name;
        this.isMale = isMale;
        this.tileX = tileX;
        this.tileY = tileY;
        this.x = tileX * BLOCK;
        this.y = tileY * BLOCK;
        speed = 2f;
        nameGlyph = new GlyphLayout(game.nicknameFont, name);
    }
    
    public void updatePos(int syncX, int syncY) {        
        tileX = syncX;
        tileY = syncY;
    }
    
    public void tick() {
        destX = 32 * tileX;
        destY = 32 * tileY;
        
        dx = destX - x;
        dy = destY - y;

        if (dx == 0 && dy == 0) {
            state = IDLE;
            return;
        }

        /* theta = Math.atan2(dy, dx);
        if        ( -1*PI/8 <= theta && theta <  1*PI/8) {
            state = RIGHT;
        } else if (  1*PI/8 <= theta && theta <  3*PI/8) {
            state = UPRIGHT;
        } else if (  3*PI/8 <= theta && theta <  5*PI/8) {
            state = UP;
        } else if (  5*PI/8 <= theta && theta <  7*PI/8) {
            state = UPLEFT;
        } else if (  7*PI/8 <= theta || theta <  -7*PI/8) {
            state = LEFT;
        } else if ( -7*PI/8 <= theta && theta < -5*PI/8) {
            state = DOWNLEFT;
        } else if ( -5*PI/8 <= theta && theta < -3*PI/8) {
            state = DOWN;
        } else if ( -3*PI/8 <= theta && theta < -1*PI/8) {
            state = DOWNRIGHT;
        } */
        
        if (dx > 0 && dy == 0) {
            state = RIGHT;
        } else if (dx > 0 && dy > 0) {
            state = UPRIGHT;
        } else if (dx == 0 && dy > 0) {
            state = UP;
        } else if (dx < 0 && dy > 0) {
            state = UPLEFT;
        } else if (dx < 0 && dy == 0) {
            state = LEFT;
        } else if (dx < 0 && dy < 0) {
            state = DOWNLEFT;
        } else if (dx == 0 && dy < 0) {
            state = DOWN;
        } else if (dx > 0 && dy < 0) {
            state = DOWNRIGHT;
        }
        
        if (dx != 0) {
            x += speed * (dx > 0 ? 1 : -1);
        }
        if (dy != 0) {
            y += speed * (dy > 0 ? 1 : -1);
        }
        
    }
    
}

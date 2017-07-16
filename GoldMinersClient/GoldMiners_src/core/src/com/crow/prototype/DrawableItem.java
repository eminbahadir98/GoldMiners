package com.crow.prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class DrawableItem {

    public String name;
    public TextureRegion icon;

    public DrawableItem(String name, TextureRegion icon) {
        this.name = name;
        this.icon = icon;
    }
    
}

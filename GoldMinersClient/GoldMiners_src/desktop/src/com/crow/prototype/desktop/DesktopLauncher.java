package com.crow.prototype.desktop;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;


public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Gold Miners";
        config.width = GoldMiners.WIDTH;
        config.height = GoldMiners.HEIGHT;
        config.initialBackgroundColor = new Color(0, 0.75f, 0, 1);
        config.vSyncEnabled = true;
        config.allowSoftwareMode = true;
        config.addIcon("icons/icon_128.png", Files.FileType.Internal);
        config.addIcon("icons/icon_32.png", Files.FileType.Internal);
        new LwjglApplication(new GoldMiners(), config);
    }
}

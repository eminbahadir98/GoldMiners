package com.crow.prototype.screens;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


public class LoadingScreen implements Screen {

    private final GoldMiners game;
    private Timer loadingTimer;
    private MutableBoolean started;
    private BitmapFont loadingFont;
    private SpriteBatch loadingBatch;
    
    private class MutableBoolean {
        private boolean value;
        public void setValue(boolean value) {
            this.value = value;
        }
        public boolean getValue() {
            return value;
        }
    }
    
    public LoadingScreen(GoldMiners game) {
        this.game = game;
        started = new MutableBoolean();
        loadingTimer = new Timer(100, new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               started.setValue(true);
               loadingTimer.stop();
           }
        });
        loadingTimer.start(); 
        loadingFont = new BitmapFont();
        loadingBatch = new SpriteBatch();
    }
    
    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0.75f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        loadingBatch.begin();
        loadingFont.draw(loadingBatch, "Please wait. Loading...", (game.WIDTH / 2) - 128, game.HEIGHT / 2);
        loadingBatch.end();
        if (started.value) {
            game.startLoading();
        }
    }
    
    @Override
    public void dispose() {
        loadingFont.dispose();
        loadingBatch.dispose();
    }
    
    @Override
    public void show() {}

    @Override
    public void resize(int i, int i1) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

}

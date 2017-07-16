package com.crow.prototype.screens;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class CreditsScreen implements Screen {
    
    private GoldMiners game;
    private Screen menuScreen;
    private SpriteBatch creditsBatch;
    private Stage creditsStage;
    private String credits;
    
    public CreditsScreen(GoldMiners game, Screen menuScreen, boolean isEnglish) {
        this.menuScreen = menuScreen;
        this.game = game;
        creditsBatch = new SpriteBatch();
        creditsStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT), creditsBatch);
        credits = isEnglish ? game.creditsEn : game.creditsTr;
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        creditsStage.act();
        creditsStage.draw();
        creditsBatch.begin();
        game.gameuiFont.draw(creditsBatch, credits, 50, game.HEIGHT - 50, game.WIDTH - 100, Align.left, true);
        creditsBatch.end();
        if (Gdx.input.justTouched()) {
            game.setScreen(menuScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        creditsStage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        creditsBatch.dispose();
        creditsStage.dispose();
    }

    @Override
    public void show() {}
    
    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

}

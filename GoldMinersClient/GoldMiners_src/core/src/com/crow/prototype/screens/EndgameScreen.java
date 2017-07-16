package com.crow.prototype.screens;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;


public class EndgameScreen implements Screen {

    private GoldMiners game;
    
    private SpriteBatch endgameBatch;
    private Stage endgameStage;
    private BitmapFont bigFont;
    private BitmapFont listFont;
    private BitmapFont smallFont;
    private GlyphLayout titleGlyph;
    private Map<String, String> language;
    private ArrayList<String> scoreLines;
    private float titleX;

    public EndgameScreen(GoldMiners game, String endgameStr) {
        this.game = game;
        this.language = game.language;
        this.bigFont = game.buttonFont;
        this.listFont = game.gameuiFont;
        this.listFont = game.textFieldFont;
        this.smallFont = game.nicknameFont;
        
        endgameBatch = new SpriteBatch();
        endgameStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT), endgameBatch);
        
        titleGlyph = new GlyphLayout(bigFont, language.get("game_over_msg"));
        titleX = (GoldMiners.WIDTH - titleGlyph.width) / 2;
        
        scoreLines = new ArrayList<String>();
        Scanner reader = new Scanner(endgameStr);
        reader.next();
        int index = 1;
        while(reader.hasNext()) {
            scoreLines.add(index + ". " + reader.next() + " (" + reader.next() + " - " + reader.next() + ")");
            index++;
        }
        
        Gdx.input.setInputProcessor(new InputProcessor() {
            public boolean touchDown(int x, int y, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    EndgameScreen.this.game.setScreen(new MenuScreen(EndgameScreen.this.game, language.get("welcome_msg")));
                    return true;
                }
                return false;
            }
            public boolean keyDown(int i) { return false; }
            public boolean keyUp(int i) { return false; }
            public boolean keyTyped(char c) { return false; }
            public boolean touchUp(int i, int i1, int i2, int i3) { return false; }
            public boolean touchDragged(int i, int i1, int i2) { return false; }
            public boolean mouseMoved(int i, int i1) { return false; }
            public boolean scrolled(int i) { return false; }
        });
    }
    
    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        endgameBatch.begin();
        bigFont.draw(endgameBatch, titleGlyph, titleX, 510);
        listFont.setColor(new Color(0, 0, 1, 1));
        listFont.draw(endgameBatch, language.get("scores_msg"), 32, 420);
        listFont.setColor(Color.WHITE);
        for (int i = 0; i < scoreLines.size(); i++) {
            listFont.draw(endgameBatch, scoreLines.get(i), 32, 420 - 48*(i+1));
        }
        smallFont.draw(endgameBatch, language.get("goback_msg"), 32, 32);
        endgameBatch.end();
        endgameStage.act();
        endgameStage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        endgameStage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        endgameBatch.dispose();
        endgameStage.dispose();
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

package com.crow.prototype.screens;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;


public class MenuScreen implements Screen {
    
    private final GoldMiners game;

    public static final int UI_BLOCK = 32;
    public static final String VALID_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._öçüÖÇÜ";
    
    private Map<String, String> language;
    private TextureRegion uiLeftUp;
    private TextureRegion uiUp;
    private TextureRegion uiRightUp;
    private TextureRegion uiLeft;
    private TextureRegion uiMiddle;
    private TextureRegion uiRight;
    private TextureRegion uiLeftDown;
    private TextureRegion uiDown;
    private TextureRegion uiRightDown;
    private TextureRegion background;
    private TextureRegion yellowRegion;
    private TextureRegion grayRegion;
    private TextureRegion cursorRegion;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private BitmapFont gameuiFont;
    private BitmapFont textFieldFont;
    
    private Vector3 touchPos;
    private SpriteBatch uiBatch;
    private Stage stage;
    private TextField nameField;
    private TextField ipField;
    private TextField portField;
    private GlyphLayout titleGlyph;
    private GlyphLayout playGlyph;
    
    private float titleX, titleY;
    private float playX, playY, playW, playH;
    private float playFontX, playFontY;
    private float nameFieldX, nameFieldY, nameFieldW, nameFieldH;
    private float ipFieldX, ipFieldY, ipFieldW, ipFieldH;
    private float portFieldX, portFieldY, portFieldW, portFieldH;
    private int maleBoxX, maleBoxY, maleBoxW, maleBoxH;
    private int femaleBoxX, femaleBoxY, femaleBoxW, femaleBoxH;
    private int settingsBoxX, settingsBoxY, settingsBoxW, settingsBoxH;
    private int settingsX, settingsY, settingsW, settingsH;
    private int creditsX, creditsY, creditsW, creditsH;
    private int englishBoxX, englishBoxY, englishBoxW, englishBoxH;
    private int turkishBoxX, turkishBoxY, turkishBoxW, turkishBoxH;
    
    private boolean isMale;
    private String name;
    private String ip;
    private String port;
    
    private InetAddress serverAddress;
    private int portNum;
    private DatagramSocket connection;
    private String warning;
    
    private TextureRegion settings;
    private boolean settingsOpen;
    private boolean isEnglish;
    
    public MenuScreen(GoldMiners game, String warning) {
        this.game = game;
        this.warning = warning;
        getResources();
        calculateCoords();
        initDisplay();
        isMale = game.recentIsMale;
        isEnglish = game.recentIsEnglish;
    }
    
    private void calculateCoords() {
        playX = 465; playY = 90 +32;
        playW = 256; playH = 96;
        
        nameFieldX = 72; nameFieldY = 100 +32;
        nameFieldW = 224+16; nameFieldH = 48;
        
        ipFieldX = 72; ipFieldY = 230 +32;
        ipFieldW = 224+16; ipFieldH = 48;
        
        portFieldX = 336; portFieldY = 230 +32;
        portFieldW = 128+16; portFieldH = 48;
        
        maleBoxX = 325; maleBoxY = 130 +32;
        femaleBoxX = 325; femaleBoxY = 90 +32;
        
        maleBoxW = 32; maleBoxH = 32;
        femaleBoxW = 32; femaleBoxH = 32;
        
        settingsBoxX = 657; settingsBoxY = 222 +32;
        settingsBoxW = 64; settingsBoxH = 64;
        
        settingsX = 60; settingsY = 60;
        settingsW = 320; settingsH = 320;
        
        creditsX = 80; creditsY = 76;
        creditsW = 160; creditsH = 48;
        
        englishBoxX = 80; englishBoxY = 258;
        turkishBoxX = 80; turkishBoxY = 198;
        
        englishBoxW = 32; englishBoxH = 32;
        turkishBoxW = 32; turkishBoxH = 32;
        
        titleGlyph = new GlyphLayout(titleFont, "Gold Miners");
        titleX = (GoldMiners.WIDTH - titleGlyph.width) / 2;
        titleY = 480;
        
        playGlyph = new GlyphLayout(buttonFont, language.get("PLAY"));
        playFontX = playX + (playW - playGlyph.width) / 2;
        playFontY = playY + (playH + playGlyph.height) / 2;
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiBatch.begin();
        uiBatch.draw(background, 0, 0);
        drawButton(uiBatch, playX, playY, playW, playH);
        drawButton(uiBatch, nameFieldX-8, nameFieldY-8, nameFieldW+32, nameFieldH+32);
        drawButton(uiBatch, ipFieldX-8, ipFieldY-8, ipFieldW+32, ipFieldH+32);
        drawButton(uiBatch, portFieldX-8, portFieldY-8, portFieldW+32, portFieldH+32);
        drawButton(uiBatch, maleBoxX, maleBoxY, 2, 2, maleBoxW/2);
        drawButton(uiBatch, femaleBoxX, femaleBoxY, 2, 2, femaleBoxW/2);
        drawButton(uiBatch, settingsBoxX, settingsBoxY, 2, 2, settingsBoxW/2);
        if (isMale) {
            uiBatch.draw(yellowRegion, maleBoxX + 6, maleBoxY + 6, 20, 20);
        } else {
            uiBatch.draw(yellowRegion, femaleBoxX + 6, femaleBoxY + 6, 20, 20);
        }
        titleFont.draw(uiBatch, titleGlyph, titleX, titleY);
        buttonFont.draw(uiBatch, playGlyph, playFontX, playFontY);
        gameuiFont.draw(uiBatch, language.get("Nickname:"), nameFieldX, nameFieldY + nameFieldH + 32);
        gameuiFont.draw(uiBatch, language.get("Server IP:"), ipFieldX, ipFieldY + nameFieldH + 32);
        gameuiFont.draw(uiBatch, language.get("Port:"), portFieldX, portFieldY + portFieldH + 32);
        textFieldFont.draw(uiBatch, language.get("Male"), maleBoxX + 32, maleBoxY + 24);
        textFieldFont.draw(uiBatch, language.get("Female"), femaleBoxX + 32, femaleBoxY + 24);
        textFieldFont.setColor(new Color(0, 0.5f, 1f, 1));
        textFieldFont.draw(uiBatch, warning, 32, 64);
        textFieldFont.setColor(Color.WHITE);
        uiBatch.draw(settings, settingsBoxX + 14, settingsBoxY + 14);
        uiBatch.end();
        stage.act();
        stage.draw();
        if (settingsOpen) {
            uiBatch.begin();
            drawSettings();
            uiBatch.end();
        }
        handleInput();
    }
    
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            stage.getViewport().unproject(touchPos);
            if (settingsOpen) {
                if (englishBoxX <= touchPos.x && touchPos.x <= englishBoxX + englishBoxW &&
                    englishBoxY <= touchPos.y && touchPos.y <= englishBoxY + englishBoxH) {
                    isEnglish = true;
                    game.language = game.english;
                } else if (turkishBoxX <= touchPos.x && touchPos.x <= turkishBoxX + turkishBoxW &&
                           turkishBoxY <= touchPos.y && touchPos.y <= turkishBoxY + turkishBoxH) {
                    isEnglish = false;
                    game.language = game.turkish;
                } else if (creditsX <= touchPos.x && touchPos.x <= creditsX + creditsW &&
                           creditsY <= touchPos.y && touchPos.y <= creditsY + creditsH) {
                    showCredits();
                } else if (settingsX <= touchPos.x && touchPos.x <= settingsX + settingsW &&
                           settingsY <= touchPos.y && touchPos.y <= settingsY + settingsH) {
                } else {
                    settingsOpen = false;
                }
                warning = "";
                language = game.language;
                playGlyph = new GlyphLayout(buttonFont, language.get("PLAY"));
                playFontX = playX + (playW - playGlyph.width) / 2;
                playFontY = playY + (playH + playGlyph.height) / 2;
            } else {
                if (playX <= touchPos.x && touchPos.x <= playX + playW &&
                    playY <= touchPos.y && touchPos.y <= playY + playH) {
                    name = nameField.getText();
                    ip = ipField.getText();
                    port = portField.getText();
                    game.recentName = name;
                    game.recentIp = ip;
                    game.recentPort = port;
                    game.recentIsMale = isMale;
                    game.recentIsEnglish = isEnglish;
                    if (isValid(name)) {
                        try {
                            serverAddress = InetAddress.getByName(ip);
                            portNum = Integer.parseInt(port);
                            connection = new DatagramSocket();
                            this.dispose();
                            game.setScreen(new ConnectingScreen(game, connection, serverAddress, portNum, name, isMale));
                        } catch (NumberFormatException e) {
                            warning = language.get("port_warn");
                        } catch (UnknownHostException e) {
                            warning = language.get("ip_warn");
                        } catch (SocketException e) {
                            warning = language.get("connection_failed_err");
                        }
                    } else {
                        warning = language.get("name_warn");
                    }
                } else if (maleBoxX <= touchPos.x && touchPos.x <= maleBoxX + maleBoxW &&
                           maleBoxY <= touchPos.y && touchPos.y <= maleBoxY + maleBoxH) {
                    isMale = true;
                } else if (femaleBoxX <= touchPos.x && touchPos.x <= femaleBoxX + femaleBoxW &&
                           femaleBoxY <= touchPos.y && touchPos.y <= femaleBoxY + femaleBoxH) {
                    isMale = false;
                } else if (settingsBoxX <= touchPos.x && touchPos.x <= settingsBoxX + settingsBoxW &&
                           settingsBoxY <= touchPos.y && touchPos.y <= settingsBoxY + settingsBoxH) {
                    settingsOpen = true;
                }
            }
        }
    }
    
    private void getResources() {
        uiLeftUp = game.uiLeftUp;
        uiUp = game.uiUp;
        uiRightUp = game.uiRightUp;
        uiLeft = game.uiLeft;
        uiMiddle = game.uiMiddle;
        uiRight = game.uiRight;
        uiLeftDown = game.uiLeftDown;
        uiDown = game.uiDown;
        uiRightDown = game.uiRightDown;
        background = game.background;
        titleFont = game.titleFont;
        buttonFont = game.buttonFont;
        language = game.language;
        yellowRegion = game.yellowRegion;
        grayRegion = game.grayRegion;
        cursorRegion = game.cursorRegion;
        gameuiFont = game.gameuiFont;
        textFieldFont = game.textFieldFont;
        settings = game.settingsRegion;
    }
    
    private void initDisplay() {
        uiBatch = new SpriteBatch();
        touchPos = new Vector3(0, 0, 0);
        stage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT), uiBatch);
        TextFieldStyle style = new TextFieldStyle();
        style.background = new TextureRegionDrawable(grayRegion);
        style.cursor = new TextureRegionDrawable(cursorRegion);
        style.selection = new TextureRegionDrawable(yellowRegion);
        style.fontColor = new Color(1, 1, 1, 1);
        style.font = textFieldFont;
        nameField = new TextField(game.recentName, style);
        nameField.setBounds(nameFieldX, nameFieldY, nameFieldW, nameFieldH);
        ipField = new TextField(game.recentIp, style);
        ipField.setBounds(ipFieldX, ipFieldY, ipFieldW, ipFieldH);
        portField = new TextField(game.recentPort, style);
        portField.setBounds(portFieldX, portFieldY, portFieldW, portFieldH);
        stage.addActor(nameField);
        stage.addActor(ipField);
        stage.addActor(portField);
        Gdx.input.setInputProcessor(stage);
    }
    
    private void drawButton(SpriteBatch batch, float x, float y, float w, float h) {
        drawButton(batch, (int)x, (int)y, (int)(w/UI_BLOCK), (int)(h/UI_BLOCK), UI_BLOCK);
    }
    private void drawButton(SpriteBatch batch, int x, int y, int w, int h, int b) {
        batch.draw(uiLeftDown, x, y, b, b);
        for (int i = 1; i < w - 1; i++)
            batch.draw(uiDown, x + i*b, y, b, b);
        batch.draw(uiRightDown, x + (w-1)*b, y, b, b);
        for (int j = 1; j < h - 1; j++) {
            batch.draw(uiLeft, x, y + j*b, b, b);
            for (int i = 1; i < w - 1; i++)
                batch.draw(uiMiddle, x + i*b, y + j*b, b, b);
            batch.draw(uiRight, x + (w-1)*b, y + j*b, b, b);
        }
        batch.draw(uiLeftUp, x, y + (h-1)*b, b, b);
        for (int i = 1; i < w - 1; i++)
            batch.draw(uiUp, x + i*b, y + (h-1)*b, b, b);
        batch.draw(uiRightUp, x + (w-1)*b, y + (h-1)*b, b, b);
    }
    
    private void drawSettings() {
        drawButton(uiBatch, settingsX, settingsY, settingsW/32, settingsH/32, 32);
        drawButton(uiBatch, creditsX, creditsY, creditsW/16, creditsH/16, 16);
        drawButton(uiBatch, englishBoxX, englishBoxY, 2, 2, englishBoxW/2);
        drawButton(uiBatch, turkishBoxX, turkishBoxY, 2, 2, turkishBoxW/2);
        if (isEnglish) {
            uiBatch.draw(yellowRegion, englishBoxX + 6, englishBoxY + 6, 20, 20);
        } else {
            uiBatch.draw(yellowRegion, turkishBoxX + 6, turkishBoxY + 6, 20, 20);
        }
        gameuiFont.draw(uiBatch, language.get("Language:"), settingsX+18, settingsY + settingsH - 32);
        gameuiFont.draw(uiBatch, "English", settingsX+18+48, settingsY + settingsH - 32 - 64);
        gameuiFont.draw(uiBatch, "Türkçe", settingsX+18+48, settingsY + settingsH - 32 - 128);
        gameuiFont.draw(uiBatch, language.get("Credits"), creditsX+8, creditsY + 32);
    }
    
    private void showCredits() {
        game.setScreen(new CreditsScreen(game, this, isEnglish));
    }
    
    private boolean isValid(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!VALID_CHARS.contains(name.substring(i, i+1))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        uiBatch.dispose();
        stage.dispose();
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

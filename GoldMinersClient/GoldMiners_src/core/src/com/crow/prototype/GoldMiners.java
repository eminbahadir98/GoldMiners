package com.crow.prototype;

import com.crow.prototype.screens.LoadingScreen;
import com.crow.prototype.screens.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;


public class GoldMiners extends Game {
    
    public static final int WIDTH = 768;
    public static final int HEIGHT = 576;
    public static final int PANEL_THICKNESS = 160;
    
    private Texture uiSheet;
    private Texture backgroundSheet;
    private Texture terrainSheet;
    private Texture itemSheet;
    private Texture yellow;
    private Texture gray;
    private Texture maleSheet;
    private Texture femaleSheet;
    private Texture maleSheetDiag;
    private Texture femaleSheetDiag;
    private Texture settings;
    
    public TextureRegion uiLeftUp;
    public TextureRegion uiUp;
    public TextureRegion uiRightUp;
    public TextureRegion uiLeft;
    public TextureRegion uiMiddle;
    public TextureRegion uiRight;
    public TextureRegion uiLeftDown;
    public TextureRegion uiDown;
    public TextureRegion uiRightDown;
    public TextureRegion background;
    public TextureRegion panelBackground;
    public TextureRegion grass;
    public TextureRegion stone;
    public TextureRegion ore;
    public TextureRegion pickaxe;
    public TextureRegion coin;
    public TextureRegion cursorRegion;
    public TextureRegion yellowRegion;
    public TextureRegion grayRegion;
    public TextureRegion settingsRegion;

    public AnimationLoader animations;
    
    public BitmapFont titleFont;
    public BitmapFont buttonFont;
    public BitmapFont nicknameFont;
    public BitmapFont gameuiFont;
    public BitmapFont textFieldFont;
    public BitmapFont smallFont;

    public Map<String, String> language;
    public Map<String, String> english;
    public Map<String, String> turkish;
    public String creditsEn;
    public String creditsTr;
    
    public Map<String, DrawableItem> itemList;
    
    public String recentIp = "127.0.0.1";
    public String recentPort = "3232";
    public String recentName = "Player";
    public boolean recentIsMale = true;
    public boolean recentIsEnglish = true;
    
    public Sound coinSound;
    public Music bgSound;
    
    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }
    
    public void startLoading() {
        loadTextures();
        loadAnimations();
        loadFonts();
        loadLanguages();
        loadSounds();
        loadItems();
        setScreen(new MenuScreen(this, ""));
    }
    
    private void loadTextures() {
        uiSheet      = new Texture(Gdx.files.internal("textures/ui_sheet.png"));
        uiLeftUp     = new TextureRegion(uiSheet,  0,  0, 16, 16);
        uiUp         = new TextureRegion(uiSheet, 16,  0, 16, 16);
        uiRightUp    = new TextureRegion(uiSheet, 32,  0, 16, 16);
        uiLeft       = new TextureRegion(uiSheet,  0, 16, 16, 16);
        uiMiddle     = new TextureRegion(uiSheet, 16, 16, 16, 16);
        uiRight      = new TextureRegion(uiSheet, 32, 16, 16, 16);
        uiLeftDown   = new TextureRegion(uiSheet,  0, 32, 16, 16);
        uiDown       = new TextureRegion(uiSheet, 16, 32, 16, 16);
        uiRightDown  = new TextureRegion(uiSheet, 32, 32, 16, 16);
        
        backgroundSheet = new Texture(Gdx.files.internal("textures/background.png"));
        backgroundSheet.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        background  = new TextureRegion(backgroundSheet, 0, 0, WIDTH, HEIGHT);
        panelBackground = new TextureRegion(backgroundSheet, 0, 0, WIDTH, PANEL_THICKNESS);
        
        terrainSheet = new Texture(Gdx.files.internal("textures/terrain_sheet.png"));
        grass = new TextureRegion(terrainSheet, 0, 0, 32, 32);
        stone = new TextureRegion(terrainSheet, 32, 0, 32, 32);
        ore = new TextureRegion(terrainSheet, 64, 0, 32, 32);
        
        itemSheet = new Texture(Gdx.files.internal("textures/item_sheet.png"));
        pickaxe = new TextureRegion(itemSheet, 0, 0, 24, 24);
        coin = new TextureRegion(itemSheet, 24, 0, 24, 24);
        
        yellow = new Texture(Gdx.files.internal("textures/yellow.png"));
        yellowRegion = new TextureRegion(yellow, 0, 0, 32, 32);
        cursorRegion = new TextureRegion(yellow, 0, 0, 4, 32);
        gray = new Texture(Gdx.files.internal("textures/gray.png"));
        grayRegion = new TextureRegion(gray, 0, 0, 32, 32);
        
        maleSheet = new Texture(Gdx.files.internal("textures/male_sheet.png"));
        femaleSheet = new Texture(Gdx.files.internal("textures/female_sheet.png"));
        maleSheetDiag = new Texture(Gdx.files.internal("textures/male_sheet_diag.png"));
        femaleSheetDiag = new Texture(Gdx.files.internal("textures/female_sheet_diag.png"));
        
        settings = new Texture(Gdx.files.internal("textures/settings.png"));
        settingsRegion = new TextureRegion(settings);
    }
    
    private void loadAnimations() {        
        animations = new AnimationLoader(maleSheet, maleSheetDiag, femaleSheet, femaleSheetDiag);
    }
    
    private void loadFonts() { 
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OCRAEXT.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        
        parameter.size = 90;
        parameter.borderWidth = 5;
        parameter.color = Color.GOLD;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = new Color(0, 0, 0, 1);
        titleFont = generator.generateFont(parameter);
        
        parameter.size = 50;
        parameter.borderWidth = 3;
        parameter.color = Color.YELLOW;
        parameter.shadowOffsetX = 0;
        parameter.shadowOffsetY = 0;
        buttonFont = generator.generateFont(parameter);
        
        parameter.size = 14;
        parameter.borderWidth = 1;
        parameter.color = Color.WHITE;
        nicknameFont = generator.generateFont(parameter);
        
        parameter.size = 25;
        parameter.borderWidth = 2;
        parameter.color = Color.YELLOW;
        gameuiFont = generator.generateFont(parameter);
        
        parameter.color = Color.WHITE;
        textFieldFont = generator.generateFont(parameter);
            
        smallFont = new BitmapFont();
        
        generator.dispose();
    }
    
    private void loadLanguages() {
        try {
            english = loadLanguage("languages/en.xml");
            turkish = loadLanguage("languages/tr.xml");
            creditsEn = loadCredits("languages/credits_en.txt");
            creditsTr = loadCredits("languages/credits_tr.txt");
            language = english;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Map<String, String> loadLanguage(String xmlAddress) throws Exception {
        Map<String, String> langMap = new HashMap<String, String>();
        XmlReader reader = new XmlReader();
        Element root = reader.parse(Gdx.files.internal(xmlAddress));
        Iterator<Element> children = root.getChildrenByName("text").iterator();
        while(children.hasNext()) {
            Element child = children.next();
            langMap.put(child.getAttribute("key"), child.getText());
        }
        return langMap;
    }
    
    private String loadCredits(String creditsAddress) throws Exception {
        String credits = "";
        BufferedReader enReader = new BufferedReader(new InputStreamReader(
                Gdx.files.internal(creditsAddress).read(), StandardCharsets.UTF_8));
        String readLine;
        while ((readLine = enReader.readLine()) != null) {
            credits += readLine + "\n";
        }
        return credits;
    }
    
    private void loadSounds() {
        coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
        bgSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/birds.mp3"));
        bgSound.setLooping(true);
    }
    
    private void loadItems() {
        itemList = new HashMap<String, DrawableItem>();
        itemList.put("P", new DrawableItem(language.get("Pickaxe"), pickaxe));
        itemList.put("C", new DrawableItem(language.get("Coin"), coin));
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        uiSheet.dispose();
        backgroundSheet.dispose();
        terrainSheet.dispose();
        itemSheet.dispose();
        yellow.dispose();
        gray.dispose();
        maleSheet.dispose();
        femaleSheet.dispose();
        maleSheetDiag.dispose();
        femaleSheetDiag.dispose();
        settings.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        nicknameFont.dispose();
        gameuiFont.dispose();
        textFieldFont.dispose();
        smallFont.dispose();
        coinSound.dispose();
        bgSound.dispose();
    }
    
}

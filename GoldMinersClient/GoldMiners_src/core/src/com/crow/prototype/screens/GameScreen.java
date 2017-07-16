package com.crow.prototype.screens;

import com.crow.prototype.ChatMessage;
import com.crow.prototype.DrawableItem;
import com.crow.prototype.GoldMiners;
import com.crow.prototype.Item;
import com.crow.prototype.MapReader;
import com.crow.prototype.Player;
import com.crow.prototype.Stranger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import javax.swing.Timer;


public class GameScreen implements Screen {
    
    public final GoldMiners game;
    
    public static final int BUFFER = 4096;
    
    public static final int BLOCK = 32;
    public static final int UI_BLOCK = 16;
    
    public static final float ZOOM = 1f;
    public static final int[] INVENTORY_BUTTON_C = {432, 96};
    public static final int[] MENU_BUTTON_C = {432, 24};
    public static final int[] MINIMAP_C = {610, 1};
    public static final int[] INVENTORY_C = {528, 208};
    public static final int[] CHATLOG_BUTTON_C = {368, 24};
    
    public static final int MAP_WIDTH = 48;
    public static final int MAP_HEIGHT = 50;
    
    public static final int CHAT_WIDTH = 480;
    public static final int CHAT_HEIGHT = 48;
    
    public static final int MINING_DELAY = 1500;
    public static final int EFFECT_FRAMES = 80;
    
    public static final int DROP_TIME = 15000;
    public static final int PING_PERIOD = 1000;
    public static final int ENDGAME_DELAY = 5000;
    
    public static final long LARGE_EPOCH = 1500000000000L;
    
    public TiledMapTileLayer solidLayer;
    public TiledMapTileLayer interactLayer;
    
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
    private TextureRegion grass;
    private TextureRegion stone;
    private TextureRegion ore;
    private TextureRegion coin;
    
    private BitmapFont nicknameFont;
    private BitmapFont gameuiFont;
    private BitmapFont smallFont;
    
    private byte[][] mapData;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private OrthographicCamera camera;
    private SpriteBatch gameBatch;
    
    private float stateTime;
    private Animation<TextureRegion> maleUp;
    private Animation<TextureRegion> maleLeft;
    private Animation<TextureRegion> maleDown;
    private Animation<TextureRegion> maleRight;
    private Animation<TextureRegion> maleUpLeft;
    private Animation<TextureRegion> maleUpRight;
    private Animation<TextureRegion> maleDownLeft;
    private Animation<TextureRegion> maleDownRight;
    private Animation<TextureRegion> femaleUp;
    private Animation<TextureRegion> femaleLeft;
    private Animation<TextureRegion> femaleDown;
    private Animation<TextureRegion> femaleRight;
    private Animation<TextureRegion> femaleUpLeft;
    private Animation<TextureRegion> femaleUpRight;
    private Animation<TextureRegion> femaleDownLeft;
    private Animation<TextureRegion> femaleDownRight;
    private TextureRegion maleIdle;
    private TextureRegion femaleIdle;
    private TextureRegion playerFrame;
    
    private Vector3 touchPos;
    private Stage stage;
    private SpriteBatch stageBatch;
    private Image panelBackground;
    private int tileCapX;
    private int tileCapY;
    private int[] minimapPos;
    private ShapeRenderer minimapRenderer;
    private boolean interacted;
    private int interactX;
    private int interactY;
    
    private boolean inventoryOpen;
    private Item item;
    private int itemX;
    private int itemY;
    private int effectElapsedTime;
    private int effectNumber;
    private boolean effectActive;
    private Map<String, DrawableItem> itemList;
    
    private Player player;
    private Timer miningTimer;
    private float miningPercent;
    private ShapeRenderer interactRenderer;
    
    private Sound coinSound;
    private Music bgSound;
    
    private long ping;
    private long bigPing;
    private long pingStart;
    private long pingEnd;
    
    private Scanner reader;
    private DatagramSocket connection;
    private InetAddress serverAddress;
    private int serverPort;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramPacket pingPacket;
    private byte[] receiveData;
    private byte[] sendData;
    private byte[] pingData;
    private String sync;
    
    private Thread syncThread;
    private Timer pingTimer;
    
    private String playerName;
    private boolean playerIsMale;
    private ArrayList<Stranger> strangers;
    private Iterator<Stranger> strangerIter;
    private Stranger stranger;
    private TextureRegion strangerFrame;
    private ArrayList<ChatMessage> chatBuffer;
    private ChatMessage chatLine;
    private ArrayList<Point> busyOres;
    private boolean chatOpen;
    private TextField chatField;
    private boolean interactedBusy;
    
    private String goBackMessage;
    private Timer goBackTimer;
    
    private Timer endgameTimer;
    private String endgameStr;
    private boolean gameEnded;
    
    public GameScreen(GoldMiners game, DatagramSocket connection, InetAddress serverAddress,
        int serverPort, String name, boolean isMale, String firstResponse) {
        this.game = game;
        this.connection = connection;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.playerName = name;
        this.playerIsMale = isMale;
        strangers = new ArrayList<Stranger>();
        chatBuffer = new ArrayList<ChatMessage>();
        busyOres = new ArrayList<Point>();
        getResources();
        initRenderers();
        initTiledMap();
        initUi();
        initCamera();
        initPlayer();
        parseUpdate(firstResponse);
        startSync();
    }
    
    private void parseUpdate(String update) {
        if (update.startsWith("LARGE_UPDATE_PLAYER")) {
            String strangerName;
            reader = new Scanner(update);
            reader.next();
            ArrayList<Stranger> updatedStrangers = new ArrayList<Stranger>();
            while (reader.hasNext()) {
                strangerName = reader.next();
                if (strangerName.equals(playerName)) {
                    reader.nextBoolean();
                    reader.nextInt();
                    reader.nextInt();
                } else {
                    updatedStrangers.add(new Stranger(game, strangerName,
                        reader.nextBoolean(), reader.nextInt(), reader.nextInt()));
                }
            }
            strangers = updatedStrangers;
        }
        else if (update.startsWith("LARGE_UPDATE_MAP")) {
            String rawMapData = update.substring("LARGE_UPDATE_MAP ".length());
            mapData = MapReader.translate(rawMapData, MAP_WIDTH, MAP_HEIGHT);
            updateTiledMap();
        }
    }
    
    private void startSync() {
        GameSynchronizer synchronizer = new GameSynchronizer();
        syncThread = new Thread(synchronizer);
        syncThread.start();
        
        pingTimer = new Timer(PING_PERIOD, new PingSender());
        pingTimer.start();
        
        endgameTimer = new Timer(ENDGAME_DELAY, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEnded = true;
                endgameTimer.stop();
            }
        });
    }
    
    private class GameSynchronizer implements Runnable {
        
        private Stranger syncStranger;
        private String syncName;
        private int syncX;
        private int syncY;
        
        @Override
        public void run() {
            while (true) {
                try {
                    receiveData = new byte[BUFFER];
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    connection.receive(receivePacket);
                    sync = new String(receivePacket.getData()).trim();
                    reader = new Scanner(sync);
                    reader.next();
                    
                    if (endgameStr != null) {}
                    
                    else if (sync.startsWith("PING")) {
                        pingEnd = System.currentTimeMillis();
                        ping = (pingEnd - reader.nextLong()) / 2;
                    }
                    
                    else if (sync.startsWith("POSITION")) {
                        syncName = reader.next();
                        syncX = reader.nextInt();
                        syncY = reader.nextInt();
                        syncStranger = findStranger(syncName);
                        if (syncStranger != null) {
                            syncStranger.updatePos(syncX, syncY);
                        }
                    }
                    
                    else if (sync.startsWith("CHAT")) {
                        chatBuffer.add(new ChatMessage(reader.next() + ":" + reader.nextLine(), Color.WHITE));
                    }
                    
                    else if (sync.startsWith("MINED")) {
                        syncX = reader.nextInt();
                        syncY = reader.nextInt();
                        mapData[syncX][syncY] = MapReader.EMPTY;
                        interactLayer.setCell(syncX, syncY, null);
                        busyOres.remove(new Point(syncX, syncY));
                    }
                    
                    else if (sync.startsWith("START_MINING")) {
                        syncX = reader.nextInt();
                        syncY = reader.nextInt();
                        busyOres.add(new Point(syncX, syncY));
                    }
                    
                    else if (sync.startsWith("STOP_MINING")) {
                        syncX = reader.nextInt();
                        syncY = reader.nextInt();
                        while (busyOres.remove(new Point(syncX, syncY)));
                    }
                    
                    else if (sync.startsWith("JOIN")) {
                        syncName = reader.next();
                        strangers.add(new Stranger(game, syncName, reader.nextBoolean(), 2, 2));
                        chatBuffer.add(new ChatMessage("> " + syncName  + " " + language.get("joined_notify_msg"), Color.YELLOW));
                    }
                    
                    else if (sync.startsWith("LEAVE")) {
                        syncName = reader.next();
                        syncStranger = findStranger(syncName);
                        if (syncStranger != null) {
                            strangers.remove(syncStranger);
                            chatBuffer.add(new ChatMessage("> " + syncName + " " + language.get("left_notify_msg"), Color.YELLOW));
                        }
                    }
                    
                    else if (sync.startsWith("DROP")) {
                        syncName = reader.next();
                        syncStranger = findStranger(syncName);
                        if (syncStranger != null) {
                            strangers.remove(syncStranger);
                            chatBuffer.add(new ChatMessage("> " + syncName + " " + language.get("disconnect_notify_msg"), Color.RED));
                        }
                    }
                    
                    else if (sync.startsWith("KICK")) {
                        syncName = reader.next();
                        syncStranger = findStranger(syncName);
                        if (syncStranger != null) {
                            strangers.remove(syncStranger);
                            chatBuffer.add(new ChatMessage("> " + syncName + " " + language.get("kicked_notify_msg"), Color.RED));
                        }
                    }
                    
                    else if (sync.startsWith("LARGE_UPDATE")) {
                        parseUpdate(sync);
                    }
                    
                    else if (sync.startsWith("NOTIFY")) {
                        chatBuffer.add(new ChatMessage("> ADMIN:" + reader.nextLine(), Color.YELLOW));
                    }
                    
                    else if (sync.startsWith("BEGIN")) {
                        chatBuffer.add(new ChatMessage("> " + language.get("begin_msg"), Color.YELLOW));
                    }
                    
                    else if (sync.startsWith("END")) {
                        chatBuffer.add(new ChatMessage("> " + language.get("end_msg"), Color.YELLOW));
                        endgameStr = sync;
                        endgameTimer.start();
                        syncThread.interrupt();
                    }
                    
                    else if (sync.startsWith("SHOULD_RECONNECT")) {
                        goBackToMenuDelayed(language.get("kicked_msg"));
                    }
                    
                    else if (sync.startsWith("DONE")) {
                        chatBuffer.add(new ChatMessage("> " + language.get("connected_msg"), Color.YELLOW));
                        chatBuffer.add(new ChatMessage("> " + language.get("info1_msg"), Color.WHITE));
                        chatBuffer.add(new ChatMessage("> " + language.get("info2_msg"), Color.WHITE));
                    }
                    
                    else if (sync.startsWith("SERVER_CLOSED")) {
                        goBackToMenuDelayed(language.get("server_closed_msg"));
                    }
                    
                    else if (sync.startsWith("UNKNOWN_COMMAND")) {
                        System.out.println("An unknown command was sent to the server.");
                    }
                    
                    else {
                        System.out.println("An unknown command came from the server: " + sync);
                    }
                    
                    reader.close();
                    
                } catch (Exception e) {
                    chatBuffer.add(new ChatMessage("> " + language.get("connection_err"), Color.RED));
                    e.printStackTrace();
                }
            }
        }
    }
    
    private class PingSender implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pingStart = System.currentTimeMillis();
                pingData = ("SEND_PING " + pingStart).getBytes();
                pingPacket = new DatagramPacket(pingData, pingData.length, serverAddress, serverPort);
                connection.send(pingPacket);
                bigPing = (pingStart - pingEnd);
                if (DROP_TIME < bigPing && bigPing < LARGE_EPOCH) {
                    goBackToMenuDelayed(language.get("disconnect_msg"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                chatBuffer.add(new ChatMessage("> " + language.get("connection_err"),Color.RED));
            }
        }
    }
    
    private void goBackToMenuDelayed(final String message) {
        goBackTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackMessage = message;
                goBackTimer.stop();
            }
        });
        goBackTimer.start();
    }
    
    private void goBackToMenu(String message) {
        interruptMining();
        pingTimer.stop();
        sendToServer("LEAVE");
        syncThread.interrupt();
        bgSound.pause();
        game.setScreen(new MenuScreen(game, message));
    }
    
    private void sendChatMessage() {
        try {
            String chatString = chatField.getText().trim();
            if (chatString.length() > 140) {
                chatBuffer.add(new ChatMessage(language.get("long_message_err"), Color.RED));
            } else if (chatString.length() > 0) {
                sendData = ("CHAT " + chatField.getText()).getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                connection.send(sendPacket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            chatBuffer.add(new ChatMessage("> " + language.get("connection_err"),Color.RED));
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
        grass = game.grass;
        stone = game.stone;
        ore = game.ore;
        coin = game.coin;
        nicknameFont = game.nicknameFont;
        gameuiFont = game.gameuiFont;
        smallFont = game.smallFont;
        language = game.language;
        maleUp = game.animations.maleUp;
        maleLeft = game.animations.maleLeft;
        maleDown = game.animations.maleDown;
        maleRight = game.animations.maleRight;
        maleUpLeft = game.animations.maleUpLeft;
        maleUpRight = game.animations.maleUpRight;
        maleDownLeft = game.animations.maleDownLeft;
        maleDownRight = game.animations.maleDownRight;
        femaleUp = game.animations.femaleUp;
        femaleLeft = game.animations.femaleLeft;
        femaleDown = game.animations.femaleDown;
        femaleRight = game.animations.femaleRight;
        femaleUpLeft = game.animations.femaleUpLeft;
        femaleUpRight = game.animations.femaleUpRight;
        femaleDownLeft = game.animations.femaleDownLeft;
        femaleDownRight = game.animations.femaleDownRight;
        maleIdle = game.animations.maleIdle;
        femaleIdle = game.animations.femaleIdle;
        coinSound = game.coinSound;
        bgSound = game.bgSound;
        itemList = game.itemList;
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.WIDTH * ZOOM,  game.HEIGHT * ZOOM);
        camera.position.y -= game.PANEL_THICKNESS * ZOOM;
        camera.position.x += BLOCK / 2;
        camera.position.y += BLOCK / 2;
        camera.update();
        tileCapX = (int) Math.floor((ZOOM / BLOCK) * game.WIDTH);
        tileCapY = (int) Math.floor((ZOOM / BLOCK) * (game.HEIGHT - game.PANEL_THICKNESS));
        minimapPos = new int[]{0, 0};
    }
    
    private void initTiledMap() {
        map = new TiledMap();
        TiledMapTileLayer grassLayer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, 32, 32);
        solidLayer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, 32, 32);
        interactLayer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, 32, 32);
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                Cell cell = new Cell();
                cell.setTile(new StaticTiledMapTile(grass));
                grassLayer.setCell(x, y, cell);
            }
        }
        map.getLayers().add(grassLayer);
        map.getLayers().add(solidLayer);
        map.getLayers().add(interactLayer);
        renderer = new OrthogonalTiledMapRenderer(map);
    }
    
    private void updateTiledMap() {
        Cell cell;
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (mapData[x][y] == MapReader.STONE) {
                    cell = new Cell();
                    cell.setTile(new StaticTiledMapTile(stone));
                    solidLayer.setCell(x, y, cell);
                } else if (mapData[x][y] == MapReader.GOLD) {
                    cell = new Cell();
                    cell.setTile(new StaticTiledMapTile(ore));
                    interactLayer.setCell(x, y, cell);
                } else if (mapData[x][y] == MapReader.EMPTY) {
                    solidLayer.setCell(x, y, null);
                    interactLayer.setCell(x, y, null);
                }
            }
        }
    }
    
    private void initRenderers() {
        gameBatch = new SpriteBatch();
        stageBatch = new SpriteBatch();
        minimapRenderer = new ShapeRenderer();
        interactRenderer = new ShapeRenderer();
        Gdx.gl.glLineWidth(3);
    }
    
    private void initUi() {
        touchPos = new Vector3(0, 0, 0);
        stage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT), stageBatch);
        panelBackground = new Image(game.panelBackground);
        panelBackground.setBounds(0, 0, game.WIDTH, game.PANEL_THICKNESS);
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.background = new TextureRegionDrawable(game.grayRegion);
        style.cursor = new TextureRegionDrawable(game.cursorRegion);
        style.selection = new TextureRegionDrawable(game.yellowRegion);
        style.fontColor = new Color(1, 1, 1, 1);
        style.font = game.textFieldFont;
        chatField = new TextField("sa", style);
        chatField.setBounds((game.WIDTH - CHAT_WIDTH) / 2 + 4,
            (game.HEIGHT - CHAT_HEIGHT) / 2 + 4, CHAT_WIDTH - 8, CHAT_HEIGHT - 8);
        chatField.setVisible(false);
        stage.addActor(panelBackground);
        stage.addActor(chatField);
        Gdx.input.setInputProcessor(stage);
    }
    
    private void initPlayer() {
        player = new Player(playerName, playerIsMale, 2, 2, this);
        player.giveItem(new Item("P", 1));
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        checkScreen();
        camera.update();
        renderer.setView(camera);
        renderer.render();
        drawInteractions(delta);
        drawPlayers();
        handleInput();
        drawUi();
        updatePlayers();
    }
    
    private void checkScreen() {
        if (gameEnded) {
            pingTimer.stop();
            syncThread.interrupt();
            bgSound.pause();
            game.setScreen(new EndgameScreen(game, endgameStr));
        } else if (goBackMessage != null) {
            goBackToMenu(goBackMessage);
        }
    }
    
    private void drawInteractions(float delta) {
        if (interacted) {
            interactRenderer.setProjectionMatrix(camera.combined);
            interactRenderer.begin(ShapeType.Line);
            interactRenderer.setColor(0, 0, 1, 1);
            interactRenderer.rect(interactX*BLOCK, interactY*BLOCK, BLOCK, BLOCK);
            interactRenderer.end();
        }
        for (Point p : busyOres) {
            interactRenderer.setProjectionMatrix(camera.combined);
            interactRenderer.begin(ShapeType.Line);
            interactRenderer.setColor(1, 0, 0, 1);
            interactRenderer.rect(p.x*BLOCK, p.y*BLOCK, BLOCK, BLOCK);
            interactRenderer.end();
        }
        if (player.mining) {
            miningPercent += delta * (1000.0 / MINING_DELAY);
            interactRenderer.setProjectionMatrix(camera.combined);
            interactRenderer.begin(ShapeType.Filled);
            interactRenderer.setColor(0, 0, 1, 1);
            interactRenderer.rect(player.x - 20, player.y + 26, 72 * miningPercent, 16);
            interactRenderer.end();
            interactRenderer.begin(ShapeType.Line);
            interactRenderer.setColor(0, 0, 0, 1);
            interactRenderer.rect(player.x - 20, player.y + 26, 72, 16);
            interactRenderer.end();
        }
    }
    
    private void updatePlayers() {
        player.tick();
        strangerIter = strangers.iterator();
        while (strangerIter.hasNext()) {
            strangerIter.next().tick();
        }
    }
    
    private void drawStrangers() {
        strangerIter = strangers.iterator();
        while (strangerIter.hasNext()) {
            stranger = strangerIter.next();
            if (stranger.state == Stranger.UP) {
            strangerFrame = (stranger.isMale ? maleUp : femaleUp).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.LEFT) {
                strangerFrame = (stranger.isMale ? maleLeft : femaleLeft).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.DOWN) {
                strangerFrame = (stranger.isMale ? maleDown : femaleDown).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.RIGHT) {
                strangerFrame = (stranger.isMale ? maleRight : femaleRight).getKeyFrame(stateTime, true);
            }
            else if (stranger.state == Stranger.UPLEFT) {
                strangerFrame = (stranger.isMale ? maleUpLeft : femaleUpLeft).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.UPRIGHT) {
                strangerFrame = (stranger.isMale ? maleUpRight : femaleUpRight).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.DOWNLEFT) {
                strangerFrame = (stranger.isMale ? maleDownLeft : femaleDownLeft).getKeyFrame(stateTime, true);
            } else if (stranger.state == Stranger.DOWNRIGHT) {
                strangerFrame = (stranger.isMale ? maleDownRight : femaleDownRight).getKeyFrame(stateTime, true);
            }
            else {
                strangerFrame = (stranger.isMale? maleIdle : femaleIdle);
            }
            gameBatch.draw(strangerFrame, stranger.x, stranger.y);
            nicknameFont.draw(gameBatch, stranger.name, stranger.x + (BLOCK - stranger.nameGlyph.width) / 2, stranger.y + 32 + 8);
        }
    }
    
    private void drawPlayers() {
        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();
        drawStrangers();
        if (player.state == Player.UP) {
            playerFrame = (playerIsMale ? maleUp : femaleUp).getKeyFrame(stateTime, true);
        } else if (player.state == Player.LEFT) {
            playerFrame = (playerIsMale ? maleLeft : femaleLeft).getKeyFrame(stateTime, true);
        } else if (player.state == Player.DOWN) {
            playerFrame = (playerIsMale ? maleDown : femaleDown).getKeyFrame(stateTime, true);
        } else if (player.state == Player.RIGHT) {
            playerFrame = (playerIsMale ? maleRight : femaleRight).getKeyFrame(stateTime, true);
        } else {
            playerFrame = (playerIsMale? maleIdle : femaleIdle);
        }
        gameBatch.draw(playerFrame, player.x, player.y);
        nicknameFont.draw(gameBatch, player.name, player.x + (BLOCK - player.nameGlyph.width) / 2, player.y + 32 + 8);
        if(effectActive) {
            effectElapsedTime++;
            gameBatch.setColor(1, 1, 1, (1f - (((float) effectElapsedTime) / EFFECT_FRAMES)));
            gameBatch.draw(coin, player.x, player.y+32 + (effectElapsedTime / 4));
            gameuiFont.setColor(1, 1, 0, 0.3f);
            gameuiFont.draw(gameBatch, "" + effectNumber, player.x, player.y+32 + (effectElapsedTime / 4));
            gameBatch.setColor(1, 1, 1, 1);
            gameuiFont.setColor(1, 1, 0, 1);
            if (effectElapsedTime >= EFFECT_FRAMES) {
                effectActive = false;
            }
        }
        gameBatch.end();
    }
    
    private void drawUi() {
        stageBatch.begin();
        drawChatBar();
        stageBatch.end();
        stage.act();
        stage.draw();
        stageBatch.begin();
        drawUiPanel();
        drawInventory();
        drawStats();
        drawChatLog();
        stageBatch.end();
        drawMinimap();
    }
    
    private void drawUiPanel() {
        drawButton(stageBatch, INVENTORY_BUTTON_C[0], INVENTORY_BUTTON_C[1], 10, 3, UI_BLOCK);
        drawButton(stageBatch, MENU_BUTTON_C[0], MENU_BUTTON_C[1], 10, 3, UI_BLOCK);
        drawButton(stageBatch, CHATLOG_BUTTON_C[0], CHATLOG_BUTTON_C[1], 3, 3, UI_BLOCK);
        gameuiFont.draw(stageBatch, language.get("Inventory"), INVENTORY_BUTTON_C[0] + 8, INVENTORY_BUTTON_C[1] + 32);
        gameuiFont.draw(stageBatch, language.get("Main Menu"), MENU_BUTTON_C[0] + 8, MENU_BUTTON_C[1] + 32);
        gameuiFont.draw(stageBatch, "M", CHATLOG_BUTTON_C[0] + 16, CHATLOG_BUTTON_C[1] + 32);
        if (interacted) {
            drawButton(stageBatch, 37, INVENTORY_BUTTON_C[1], 10, 3, UI_BLOCK);
            drawButton(stageBatch, 215, INVENTORY_BUTTON_C[1], 10, 3, UI_BLOCK);
            gameuiFont.draw(stageBatch, language.get("Mine") + " (Z)", 45, INVENTORY_BUTTON_C[1] + 32);
            gameuiFont.draw(stageBatch, language.get("Leave") + " (X)" , 223, INVENTORY_BUTTON_C[1] + 32);
        } else if (interactedBusy) {
            gameuiFont.draw(stageBatch, language.get("busy_ore_msg"), 16, INVENTORY_BUTTON_C[1] + 32, 400, Align.left, true);
        }
    }
    
    private void drawInventory() {
        if (inventoryOpen) {
                drawButton(stageBatch, INVENTORY_C[0], INVENTORY_C[1] + 256, 14, 4, UI_BLOCK);
                drawButton(stageBatch, INVENTORY_C[0], INVENTORY_C[1], 14, 16, UI_BLOCK);
                gameuiFont.draw(stageBatch, language.get("Inventory"), INVENTORY_C[0] + 16, INVENTORY_C[1] + 256 + 64 - 24);
                for (int i = 0; i < player.inventory.size(); i++) {
                    item = player.inventory.get(i);
                    itemX = INVENTORY_C[0] + 8;
                    itemY = INVENTORY_C[1] + 256 - 64 - (56)*(i);
                    drawButton(stageBatch,itemX, itemY, 3, 3, UI_BLOCK);
                    if (item.count > 1) {
                        gameuiFont.draw(stageBatch, language.get(itemList.get(item.itemCode).name) + "(" + item.count + ")", itemX + 48 + 8, itemY + 32);
                    } else {
                        gameuiFont.draw(stageBatch, language.get(itemList.get(item.itemCode).name), itemX + 48 + 8, itemY + 32);
                    }
                    stageBatch.draw(itemList.get(item.itemCode).icon, itemX + 8, itemY + 8, 32, 32);
                }
       }
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
    
    private void drawMinimap() {
        int x = MINIMAP_C[0] + 8;
        int y = MINIMAP_C[1] + 2;
        minimapRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        minimapRenderer.begin(ShapeType.Filled);
        minimapRenderer.setColor(0, 0.85f, 0, 1);
        minimapRenderer.rect(x, y, 3*MAP_WIDTH, 3*MAP_HEIGHT);
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                if (mapData[i][j] == MapReader.STONE)
                    minimapRenderer.setColor(0, 0, 0, 1);
                else if (mapData[i][j] == MapReader.GOLD)
                    minimapRenderer.setColor(1, 1, 0, 1);
                else continue;
                minimapRenderer.rect(x + 3*i, y + 3*j, 3, 3);
            }
        }
        minimapRenderer.setColor(1, 0, 0, 1);
        for (Stranger s : strangers) {
            minimapRenderer.rect(x + 3*s.tileX, y + 3*s.tileY, 3, 3);
        }
        minimapRenderer.setColor(0, 0, 1, 1);
        minimapRenderer.rect(x + 3*player.tileX, y + 3*player.tileY, 3, 3);
        minimapRenderer.end();
        minimapRenderer.begin(ShapeType.Line);
        minimapRenderer.setColor(1, 1, 1, 1);
        
        minimapRenderer.rect(
            x + minimapPos[0] * tileCapX * 3 - minimapPos[0]*3,
            y + minimapPos[1] * tileCapY * 3 - minimapPos[1]*3,
            tileCapX * 3, tileCapY * 3);
        minimapRenderer.end();
    }
    
    private void drawChatLine(ChatMessage line, int drawIndex) {
        smallFont.setColor(line.color);
        smallFont.draw(stageBatch, line.content, 24, 32 + 16*drawIndex);
    }
    
    private void drawChatLog() {
        if (chatBuffer.isEmpty()) { return; }
        if (chatOpen || (!chatOpen && Gdx.input.isKeyPressed(Input.Keys.M)) || (Gdx.input.isTouched() &&
            CHATLOG_BUTTON_C[0] <= touchPos.x && touchPos.x <= CHATLOG_BUTTON_C[0] + 48 &&
            CHATLOG_BUTTON_C[1] <= touchPos.y && touchPos.y <= CHATLOG_BUTTON_C[1] + 48)) {
            int i = 0;
            for (int j = chatBuffer.size() - 1; j >= 0 && i <= 15; j--) {
                chatLine = chatBuffer.get(j);
                drawChatLine(chatLine, i);
                i++;
            }
        } else {
            int i = 0;
            for (int j = chatBuffer.size() - 1; j >= 0; j--) {
                chatLine = chatBuffer.get(j);
                if (chatLine.visible) {
                    drawChatLine(chatLine, i);
                    i++;
                }
            }
        }
    }
    
    private void drawChatBar() {
        if (chatOpen) {
            drawButton(stageBatch, (game.WIDTH - CHAT_WIDTH) / 2,
            (game.HEIGHT - CHAT_HEIGHT) / 2, CHAT_WIDTH / 16, CHAT_HEIGHT / 16, UI_BLOCK);
            gameuiFont.draw(stageBatch, language.get("Chat"), (game.WIDTH - CHAT_WIDTH) / 2,
            (game.HEIGHT - CHAT_HEIGHT) / 2 + 72);
        }
    }
    
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            stage.getViewport().unproject(touchPos);
            if (432 <= touchPos.x && touchPos.x <= 592 &&
                24 <= touchPos.y && touchPos.y <= 72) {
                goBackToMenu(language.get("welcome_msg"));
            }
            else if (528 <= touchPos.x && touchPos.x <= 720 &&
                208 <= touchPos.y && touchPos.y <= 528) { }
            else if (432 <= touchPos.x && touchPos.x <= 592 &&
                96 <= touchPos.y && touchPos.y <= 144) {
                inventoryOpen = !inventoryOpen;
            }
            else if (inventoryOpen) {
                inventoryOpen = false;
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (chatOpen) {
                sendChatMessage();
                chatOpen = false;
                chatField.setVisible(false);
            }
            else {
                chatOpen = true;
                chatField.setText("");
                stage.setKeyboardFocus(chatField);
                chatField.setVisible(true);
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            chatOpen = false;
            chatField.setVisible(false);
        }
        
        if (chatOpen) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (player.interactDir != Player.UP) {
                interacted = false;
            }
            if (player.move(Player.UP)) {
                sendToServer("POSITION " + player.tileX + " " + player.tileY);
            }
            interactedBusy = false;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (player.interactDir != Player.LEFT) {
                interacted = false;
            }
            if (player.move(Player.LEFT)) {
                sendToServer("POSITION " + player.tileX + " " + player.tileY);
            }
            interactedBusy = false;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (player.interactDir != Player.DOWN) {
                interacted = false;
            }
            if (player.move(Player.DOWN)) {
                sendToServer("POSITION " + player.tileX + " " + player.tileY);
            }
            interactedBusy = false;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (player.interactDir != Player.RIGHT) {
                interacted = false;
            }
            if (player.move(Player.RIGHT)) {
                sendToServer("POSITION " + player.tileX + " " + player.tileY);
            }
            interactedBusy = false;
        }
        else {
            player.move(Player.IDLE);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) || (Gdx.input.justTouched() &&
            37 <= touchPos.x && touchPos.x <= 197 &&
            96 <= touchPos.y && touchPos.y <= 144)) {
            if (interacted) {
                if (!busyOres.contains(new Point(interactX, interactY))) {
                    startMining(interactX, interactY);
                } else {
                    interactedBusy = true;
                }
                interacted = false;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X) || (Gdx.input.justTouched() &&
            215 <= touchPos.x && touchPos.x <= 375 &&
            96 <= touchPos.y && touchPos.y <= 144)) {
            interacted = false;
        }
        
    }
    
    private void drawStats() {
        smallFont.setColor(Color.WHITE);
        smallFont.draw(stageBatch,  "FPS: " + Gdx.graphics.getFramesPerSecond() + 
            "  |  Ping: " + ping +
            "  |  Population: " + (strangers.size() + 1)
            , 10, game.HEIGHT - 4);
    }
    
    public void checkCamera() {
        if (player.relX == 0) {
            camera.position.x -= (tileCapX-1) * BLOCK;
            player.relX = tileCapX - 1;
            minimapPos[0]--;
        } else if (player.relY == 0) {
            camera.position.y -= (tileCapY-1) * BLOCK;
            player.relY = tileCapY - 1;
            minimapPos[1]--;
        } else if (player.relX == tileCapX) {
            camera.position.x += (tileCapX-1) * BLOCK;
            player.relX = 1;
            minimapPos[0]++;
        } else if (player.relY == tileCapY) {
            camera.position.y += (tileCapY-1) * BLOCK;
            player.relY = 1;
            minimapPos[1]++;
        }
    }
    
    public void interact(int tileX, int tileY) {
        if (!interacted || (interacted && (interactX != tileX) && (interactY != tileY))) {
            interactX = tileX;
            interactY = tileY;
            interacted = true;
        }
    }
    
    private void startMining(int tileX, int tileY) {
        player.mining = true;
        miningPercent = 0;
        final int tileX_ = tileX;
        final int tileY_ = tileY;
        miningTimer = new Timer(MINING_DELAY, new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               player.mining = false;
               extractTile(tileX_, tileY_);
               miningTimer.stop();
           }
        });
        miningTimer.start();
        sendToServer("START_MINING " + interactX + " " + interactY);
    }
    
    private void extractTile(int tileX, int tileY) {
        interactLayer.setCell(tileX, tileY, null);
        mapData[tileX][tileY] = MapReader.EMPTY;
        int amount = (int) (Math.random() * 3 + 2);
        player.giveItem(new Item("C", amount));
        effectElapsedTime = 0;
        effectActive = true;
        effectNumber = amount;
        coinSound.play(0.4f);
        sendToServer("MINED " + tileX + " " + tileY + " " + amount);
    }
    
    public void interruptMining() {
        if (player.mining) {
            player.mining = false;
            miningTimer.stop();
            sendToServer("STOP_MINING " + interactX + " " + interactY);
        }
    }
    
    private Stranger findStranger(String strangerName) {
        Iterator<Stranger> iter = strangers.iterator();
        Stranger s;
        while (iter.hasNext()) {
            s = iter.next();
            if (s.name.equals(strangerName)) {
                return s;
            }
        }
        return null;
    }
    
    private void sendToServer(String message) {
        try {
            sendData = message.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            connection.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
            chatBuffer.add(new ChatMessage("> " + language.get("connection_err"),Color.RED));
        }
    }
    
    @Override
    public void show() {
        bgSound.play();
    }
    
    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        gameBatch.dispose();
        map.dispose();
        stage.dispose();
        stageBatch.dispose();
        minimapRenderer.dispose();
        interactRenderer.dispose();
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
}

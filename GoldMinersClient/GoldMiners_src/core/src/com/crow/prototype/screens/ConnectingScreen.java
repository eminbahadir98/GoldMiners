package com.crow.prototype.screens;

import com.crow.prototype.GoldMiners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import javax.swing.Timer;


public class ConnectingScreen implements Screen {

    public static int BUFFER = 4096;
    public static int TIMEOUT_PERIOD = 3000;
    
    private final GoldMiners game;
    private Timer loadingTimer;
    private MutableBoolean started;
    private MutableBoolean connected;
    private BitmapFont connectingFont;
    private SpriteBatch connectingBatch;
    private Stage connectionStage;
    private Map<String, String> language;
    
    private DatagramSocket connection;
    private InetAddress serverAddress;
    private int serverPort;
    private String name;
    private boolean isMale;
    private String response;
    private Timer timeoutTimer;
    
    private class MutableBoolean {
        private boolean value;
        public void setValue(boolean value) {
            this.value = value;
        }
        public boolean getValue() {
            return value;
        }
    }
    
    public ConnectingScreen(GoldMiners game, DatagramSocket connection,
        InetAddress serverAddress, int serverPort, String name, boolean isMale) {
        
        this.game = game;
        this.connection = connection;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.name = name;
        this.isMale = isMale;
        this.language = game.language;
        response = "NO_RESPONSE";
        
        connectingFont = new BitmapFont();
        connectingBatch = new SpriteBatch();
        connectionStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT), connectingBatch);
        
        started = new MutableBoolean();
        connected = new MutableBoolean();
        loadingTimer = new Timer(100, new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               started.setValue(true);
               loadingTimer.stop();
           }
        });
        loadingTimer.start();
        
        timeoutTimer = new Timer(TIMEOUT_PERIOD, new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               response = "TIMED_OUT";
               connected.value = true;
               timeoutTimer.stop();
           }
        });
        timeoutTimer.start();
    }
    
    private void startConnecting() {
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
        connectionThread.start();
    }
    
    private void connect() {
        try {
            byte[] sendData = ("CONNECT " + name + " " + isMale).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            connection.send(sendPacket);
            byte[] receiveData = new byte[BUFFER];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            connection.receive(receivePacket);
            response = new String(receivePacket.getData()).trim();
            connected.value = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            response = "CONNECTION_FAILED";
            connected.value = true;
        }
    }
    
    private void switchGameScreen() {
        this.dispose();
        if (response.equals("CLIENT_DENIED")) {
           game.setScreen(new MenuScreen(game, language.get("name_exists_err")));
        } else if (response.equals("ALREADY_IN")) {
            game.setScreen(new MenuScreen(game, language.get("already_connected_err")));
        } else if (response.equals("CONNECTION_FAILED")) {
            game.setScreen(new MenuScreen(game, language.get("connection_failed_err")));
        } else if (response.equals("TIMED_OUT")) {
            game.setScreen(new MenuScreen(game, language.get("connection_timeout_err")));
        } else if (response.startsWith("LARGE_UPDATE")) {
            game.setScreen(new GameScreen(game, connection, serverAddress, serverPort, name, isMale, response));
        } else {
            game.setScreen(new MenuScreen(game, language.get("unexpected_err")));
        }
    }
    
    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        connectingBatch.begin();
        connectingFont.draw(connectingBatch, language.get("connecting_msg"),
            (game.WIDTH / 2) - 128, game.HEIGHT / 2);
        connectingBatch.end();
        connectionStage.act();
        connectionStage.draw();
        if (started.value) {
            startConnecting();
            started.value = false;
        }
        if (connected.value) {
            switchGameScreen();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        connectionStage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        connectingFont.dispose();
        connectingBatch.dispose();
        connectionStage.dispose();
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

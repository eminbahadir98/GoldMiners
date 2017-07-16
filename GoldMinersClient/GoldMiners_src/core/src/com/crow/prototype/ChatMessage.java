package com.crow.prototype;

import com.badlogic.gdx.graphics.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


public class ChatMessage {
    
    public static int DISAPPEAR_DELAY = 3000;
    
    public boolean visible;
    public String content;
    public Color color;
    private Timer disappearTimer;
    
    public ChatMessage(String content, Color color) {
        this.content = content;
        this.color = color;
        this.visible = true;
        disappearTimer = new Timer(DISAPPEAR_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatMessage.this.visible = false;
                disappearTimer.stop();
            }
        });
        disappearTimer.start();
    }
    
}

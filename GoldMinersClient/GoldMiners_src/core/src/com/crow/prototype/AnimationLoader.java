package com.crow.prototype;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class AnimationLoader {
    
    private Texture maleSheet;
    private Texture maleSheetDiag;
    private Texture femaleSheet;
    private Texture femaleSheetDiag;
    
    public TextureRegion maleIdle;
    public Animation<TextureRegion> maleUp;
    public Animation<TextureRegion> maleLeft;
    public Animation<TextureRegion> maleDown;
    public Animation<TextureRegion> maleRight;
    public Animation<TextureRegion> maleUpLeft;
    public Animation<TextureRegion> maleUpRight;
    public Animation<TextureRegion> maleDownLeft;
    public Animation<TextureRegion> maleDownRight;
    
    public TextureRegion femaleIdle;
    public Animation<TextureRegion> femaleUp;
    public Animation<TextureRegion> femaleLeft;
    public Animation<TextureRegion> femaleDown;
    public Animation<TextureRegion> femaleRight;
    public Animation<TextureRegion> femaleUpLeft;
    public Animation<TextureRegion> femaleUpRight;
    public Animation<TextureRegion> femaleDownLeft;
    public Animation<TextureRegion> femaleDownRight;
    
    public AnimationLoader(Texture maleSheet, Texture maleSheetDiag,
        Texture femaleSheet, Texture femaleSheetDiag) {
        this.maleSheet = maleSheet;
        this.maleSheetDiag = maleSheetDiag;
        this.femaleSheet = femaleSheet;
        this.femaleSheetDiag = femaleSheetDiag;
        load();
    }
    
    private void load() {
        
        final float SPEED = 0.1f;
        
        TextureRegion[][] maleFrames = TextureRegion.split(maleSheet, 32, 32);
        maleIdle = maleFrames[0][1];
        maleFrames[0] = new TextureRegion[] {maleFrames[0][0], maleFrames[0][1], maleFrames[0][2], maleFrames[0][1]};
        maleFrames[1] = new TextureRegion[] {maleFrames[1][0], maleFrames[1][1], maleFrames[1][2], maleFrames[1][1]};
        maleFrames[2] = new TextureRegion[] {maleFrames[2][0], maleFrames[2][1], maleFrames[2][2], maleFrames[2][1]};
        maleFrames[3] = new TextureRegion[] {maleFrames[3][0], maleFrames[3][1], maleFrames[3][2], maleFrames[3][1]};
        maleDown  = new Animation<TextureRegion>(SPEED, maleFrames[0]);
        maleLeft  = new Animation<TextureRegion>(SPEED, maleFrames[1]);
        maleRight = new Animation<TextureRegion>(SPEED, maleFrames[2]);
        maleUp    = new Animation<TextureRegion>(SPEED, maleFrames[3]);
        
        TextureRegion[][] maleFramesDiag = TextureRegion.split(maleSheetDiag, 32, 32);
        maleFramesDiag[0] = new TextureRegion[] {maleFramesDiag[0][0], maleFramesDiag[0][1], maleFramesDiag[0][2], maleFramesDiag[0][1]};
        maleFramesDiag[1] = new TextureRegion[] {maleFramesDiag[1][0], maleFramesDiag[1][1], maleFramesDiag[1][2], maleFramesDiag[1][1]};
        maleFramesDiag[2] = new TextureRegion[] {maleFramesDiag[2][0], maleFramesDiag[2][1], maleFramesDiag[2][2], maleFramesDiag[2][1]};
        maleFramesDiag[3] = new TextureRegion[] {maleFramesDiag[3][0], maleFramesDiag[3][1], maleFramesDiag[3][2], maleFramesDiag[3][1]};
        maleDownLeft  = new Animation<TextureRegion>(SPEED, maleFramesDiag[0]);
        maleUpLeft    = new Animation<TextureRegion>(SPEED, maleFramesDiag[1]);
        maleDownRight = new Animation<TextureRegion>(SPEED, maleFramesDiag[2]);
        maleUpRight   = new Animation<TextureRegion>(SPEED, maleFramesDiag[3]);
        
        TextureRegion[][] femaleFrames = TextureRegion.split(femaleSheet, 32, 32);
        femaleIdle = femaleFrames[0][1];
        femaleFrames[0] = new TextureRegion[] {femaleFrames[0][0], femaleFrames[0][1], femaleFrames[0][2], femaleFrames[0][1]};
        femaleFrames[1] = new TextureRegion[] {femaleFrames[1][0], femaleFrames[1][1], femaleFrames[1][2], femaleFrames[1][1]};
        femaleFrames[2] = new TextureRegion[] {femaleFrames[2][0], femaleFrames[2][1], femaleFrames[2][2], femaleFrames[2][1]};
        femaleFrames[3] = new TextureRegion[] {femaleFrames[3][0], femaleFrames[3][1], femaleFrames[3][2], femaleFrames[3][1]};
        femaleDown  = new Animation<TextureRegion>(SPEED, femaleFrames[0]);
        femaleLeft  = new Animation<TextureRegion>(SPEED, femaleFrames[1]);
        femaleRight = new Animation<TextureRegion>(SPEED, femaleFrames[2]);
        femaleUp    = new Animation<TextureRegion>(SPEED, femaleFrames[3]);
        
        TextureRegion[][] femaleFramesDiag = TextureRegion.split(femaleSheetDiag, 32, 32);
        femaleFramesDiag[0] = new TextureRegion[] {femaleFramesDiag[0][0], femaleFramesDiag[0][1], femaleFramesDiag[0][2], femaleFramesDiag[0][1]};
        femaleFramesDiag[1] = new TextureRegion[] {femaleFramesDiag[1][0], femaleFramesDiag[1][1], femaleFramesDiag[1][2], femaleFramesDiag[1][1]};
        femaleFramesDiag[2] = new TextureRegion[] {femaleFramesDiag[2][0], femaleFramesDiag[2][1], femaleFramesDiag[2][2], femaleFramesDiag[2][1]};
        femaleFramesDiag[3] = new TextureRegion[] {femaleFramesDiag[3][0], femaleFramesDiag[3][1], femaleFramesDiag[3][2], femaleFramesDiag[3][1]};
        femaleDownLeft  = new Animation<TextureRegion>(SPEED, femaleFramesDiag[0]);
        femaleUpLeft    = new Animation<TextureRegion>(SPEED, femaleFramesDiag[1]);
        femaleDownRight = new Animation<TextureRegion>(SPEED, femaleFramesDiag[2]);
        femaleUpRight   = new Animation<TextureRegion>(SPEED, femaleFramesDiag[3]);
        
    }
    
}

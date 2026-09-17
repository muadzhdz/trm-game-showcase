package utils;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;
    
    public Animation(BufferedImage[] frames, long delay) {
        this.frames = frames;
        this.delay = delay;
        currentFrame = 0;
        startTime = System.currentTimeMillis();
        playedOnce = false;
    }
    
    public void update() {
        if (delay == -1) return;
        
        long elapsed = (System.currentTimeMillis() - startTime);
        
        if (elapsed > delay) {
            currentFrame++;
            startTime = System.currentTimeMillis();
        }
        
        if (currentFrame == frames.length) {
            currentFrame = 0;
            playedOnce = true;
        }
    }
    
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }
    
    public int getFrame() { return currentFrame; }
    public void setFrame(int i) { currentFrame = i; }
    public boolean hasPlayedOnce() { return playedOnce; }
}
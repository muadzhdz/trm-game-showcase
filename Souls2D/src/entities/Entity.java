package entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class Entity {
    // World position
    public int worldX, worldY;
    public int speed;
    
    // Sprite animation
    public BufferedImage front1, front2, front3, front4;
    public BufferedImage back1, back2, back3, back4;
    public BufferedImage left1, left2, left3, left4;
    public BufferedImage right1, right2, right3, right4;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    
    // Collision
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    
    // Game state
    public boolean hasSoul = false;
    public int soulType = -1;
    
    // Movement prediction
    protected int tempX, tempY;
    
    // Helper methods
    public int getScreenX(GamePanel gp) {
        return worldX - gp.screenX;
    }
    
    public int getScreenY(GamePanel gp) {
        return worldY - gp.screenY;
    }
    
    public boolean isOnScreen(GamePanel gp) {
        int screenX = getScreenX(gp);
        int screenY = getScreenY(gp);
        
        return screenX + gp.tileSize > 0 && 
               screenX < gp.screenWidth &&
               screenY + gp.tileSize > 0 && 
               screenY < gp.screenHeight;
    }
    
    public void resetPosition() {
        worldX = 100;
        worldY = 100;
    }
    
    // Getters
    public int getTempX() { return tempX; }
    public int getTempY() { return tempY; }
    public boolean hasSoul() { return hasSoul; }
    public void setHasSoul(boolean hasSoul) { this.hasSoul = hasSoul; }
    public int getSoulType() { return soulType; }
    public void setSoulType(int soulType) { this.soulType = soulType; }
    
    // For collision checking
    public Rectangle getWorldSolidArea() {
        return new Rectangle(
            worldX + solidArea.x,
            worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
    }
}
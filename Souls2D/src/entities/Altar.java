package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Altar extends Entity {
    
    GamePanel gp;
    private boolean activated = false;
    private BufferedImage offImage;
    private BufferedImage[] onAnimation;
    private int type; // 0, 1, 2 untuk altar A, B, C
    private int animationCounter = 0;
    private int animationFrame = 0;
    
    public Altar(GamePanel gp, int worldX, int worldY, int type) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.type = type;
        
        getAltarImages();
        setSolidArea();
    }
    
    private void getAltarImages() {
        try {
            // Load off image berdasarkan type
            switch (type) {
                case 0: 
                    offImage = ImageIO.read(getClass().getResourceAsStream("/altar/Altar.png"));
                    break;
                case 1: 
                    offImage = ImageIO.read(getClass().getResourceAsStream("/altar/Altar2.png"));
                    break;
                case 2: 
                    offImage = ImageIO.read(getClass().getResourceAsStream("/altar/Altar3.png"));
                    break;
            }
            
            // Load on animation berdasarkan type
            onAnimation = new BufferedImage[4];
            String basePath = "";
            switch (type) {
                case 0: basePath = "/altar/AltarSoul"; break;
                case 1: basePath = "/altar/AltarSoulB"; break;
                case 2: basePath = "/altar/AltarSoulC"; break;
            }
            
            for (int i = 0; i < 4; i++) {
                onAnimation[i] = ImageIO.read(getClass().getResourceAsStream(
                    basePath + (i+1) + ".png"
                ));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            // Create placeholder
            offImage = new BufferedImage(gp.tileSize * 2, gp.tileSize * 2, BufferedImage.TYPE_INT_ARGB);
            onAnimation = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                onAnimation[i] = new BufferedImage(gp.tileSize * 2, gp.tileSize * 2, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }
    
    private void setSolidArea() {
        // Altar memiliki collision area di bagian bawah (tempat player berdiri)
        solidArea = new Rectangle(gp.tileSize / 4, gp.tileSize * 3/2, 
                                 gp.tileSize * 3/2, gp.tileSize / 2);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        collisionOn = true; // Altar memiliki collision
    }
    
    public void update() {
        if (activated) {
            animationCounter++;
            if (animationCounter > 12) {
                animationFrame = (animationFrame + 1) % 4;
                animationCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.screenX;
        int screenY = worldY - gp.screenY;
        
        if (screenX + gp.tileSize * 2 > 0 && 
            screenX < gp.screenWidth &&
            screenY + gp.tileSize * 2 > 0 && 
            screenY < gp.screenHeight) {
            
            if (activated) {
                g2.drawImage(onAnimation[animationFrame], screenX, screenY, 
                           gp.tileSize * 2, gp.tileSize * 2, null);
            } else {
                g2.drawImage(offImage, screenX, screenY, 
                           gp.tileSize * 2, gp.tileSize * 2, null);
            }
            
            // DEBUG: Draw solid area
            if (gp.keyH.shiftPressed && gp.keyH.ctrlPressed) {
                g2.setColor(activated ? Color.GREEN : Color.RED);
                g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, 
                           solidArea.width, solidArea.height);
            }
        }
    }
    
    // Method untuk mengaktifkan altar
    public boolean activate(Player player) {
        System.out.println("=== ALTAR ACTIVATION ATTEMPT ===");
        System.out.println("Player has soul: " + player.hasSoul());
        System.out.println("Player soul type: " + (player.hasSoul() ? player.getSoulType() : "none"));
        System.out.println("Altar type: " + type);
        System.out.println("Altar already activated: " + activated);
        
        if (activated) {
            System.out.println("FAIL: Altar already activated");
            return false;
        }
        
        if (!player.hasSoul()) {
            System.out.println("FAIL: Player doesn't have a soul");
            return false;
        }
        
        if (player.getSoulType() != type) {
            System.out.println("FAIL: Wrong soul type. Player has " + 
                             player.getSoulType() + ", altar needs " + type);
            return false;
        }
        
        // Cek collision dengan player
        boolean isColliding = gp.cChecker.checkTwoEntities(this, player);
        System.out.println("Player-altar collision check: " + isColliding);
        
        if (!isColliding) {
            System.out.println("FAIL: Player not close enough to altar");
            return false;
        }
        
        // SUCCESS - Activate altar
        activated = true;
        player.setHasSoul(false);
        player.setSoulType(-1);
        collisionOn = false;
        
        // Tandai soul yang sesuai sebagai placed
        markCorrespondingSoulAsPlaced();
        
        System.out.println("SUCCESS: Altar " + type + " activated!");
        return true;
    }

    private void markCorrespondingSoulAsPlaced() {
        for (Soul soul : gp.gameProgress.getSouls()) {
            if (soul.getType() == this.type) {
                soul.placeOnAltar();
                System.out.println("Soul " + type + " marked as placed on altar");
                break;
            }
        }
    }
    
    // Method untuk cek apakah player bisa mengaktifkan altar
    public boolean canActivate(Player player) {
        return !activated && player.hasSoul() && player.getSoulType() == type;
    }
    
    // Getters
    public boolean isActivated() {
        return activated;
    }
    
    public int getType() {
        return type;
    }
    
    // Method untuk reset altar (saat game restart)
    public void reset() {
        activated = false;
        collisionOn = true;
        animationFrame = 0;
        animationCounter = 0;
    }
    
    // Static method untuk clear semua altar
    public static void clear() {
        // Tidak perlu implementasi khusus karena setiap altar direset individual
    }
}
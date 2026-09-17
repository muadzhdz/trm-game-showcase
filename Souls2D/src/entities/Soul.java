package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Soul extends Entity {
    
    GamePanel gp;
    private boolean collected = false;
    private boolean placedOnAltar = false;  // Flag baru
    private BufferedImage[] animationFrames = new BufferedImage[4];
    private int animationCounter = 0;
    private int animationFrame = 0;
    private int type; // 0: Soul A, 1: Soul B, 2: Soul C
    
    public Soul(GamePanel gp, int worldX, int worldY, int type) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.type = type;
        
        // Verifikasi tile sebelum spawn
        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;
        
        System.out.println("Creating Soul type " + type + " at tile: (" + tileCol + "," + tileRow + ")");
        
        if (gp.tileM.mapTileNum != null && 
            tileRow >= 0 && tileRow < gp.tileM.rows &&
            tileCol >= 0 && tileCol < gp.tileM.cols) {
            
            int tileNum = gp.tileM.mapTileNum[tileRow][tileCol];
            System.out.println("  Tile number: " + tileNum + " (1 = floor, should be 1)");
            
            if (tileNum != 1) {
                System.out.println("  WARNING: Soul not placed on floor tile!");
            }
        }
        
        getSoulImage();
        setSolidArea();
    }
    
    private void getSoulImage() {
        try {
            String basePath = "";
            switch (type) {
                case 0: basePath = "/items/Soul"; break;
                case 1: basePath = "/items/SoulB"; break;
                case 2: basePath = "/items/SoulC"; break;
            }
            
            for (int i = 0; i < 4; i++) {
                var stream = getClass().getResourceAsStream(basePath + (i+1) + ".png");
                if (stream != null) {
                    animationFrames[i] = ImageIO.read(stream);
                } else {
                    animationFrames[i] = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Create placeholder
            for (int i = 0; i < 4; i++) {
                animationFrames[i] = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }
    
    private void setSolidArea() {
        solidArea = new Rectangle(8, 8, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
    
    public void update() {
        if (!collected && !placedOnAltar) {
            animationCounter++;
            if (animationCounter > 10) {
                animationFrame = (animationFrame + 1) % 4;
                animationCounter = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        // Hanya gambar jika belum dikoleksi DAN belum ditaruh di altar
        if (!collected && !placedOnAltar) {
            int screenX = worldX - gp.screenX;
            int screenY = worldY - gp.screenY;
            
            // Gambar hanya jika di dalam layar
            if (screenX + gp.tileSize > 0 && 
                screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && 
                screenY < gp.screenHeight) {
                
                g2.drawImage(animationFrames[animationFrame], screenX, screenY, 
                           gp.tileSize, gp.tileSize, null);
            }
        }
    }
    
    public boolean checkCollection(Player player) {
        // PERBAIKAN: Player hanya bisa mengambil 1 soul
        if (!collected && !placedOnAltar && !player.hasSoul()) {
            if (gp.cChecker.checkTwoEntities(this, player)) {
                collected = true;
                player.setHasSoul(true);
                player.setSoulType(type);
                System.out.println("Player collected soul type " + type);
                return true;
            }
        }
        return false;
    }
    
    // Method baru: soul ditaruh di altar
    public void placeOnAltar() {
        this.placedOnAltar = true;
        this.collected = false; // Reset collected status
        System.out.println("Soul " + type + " placed on altar");
    }
    
    // Method untuk reset soul (jika game restart)
    public void reset() {
        collected = false;
        placedOnAltar = false;
        animationFrame = 0;
        animationCounter = 0;
    }
    
    // Getter methods
    public boolean isCollected() {
        return collected;
    }
    
    public boolean isPlacedOnAltar() {
        return placedOnAltar;
    }
    
    public int getType() {
        return type;
    }
    
    // Static method untuk clear (jika diperlukan)
    public static void clear() {
        // Tidak perlu implementasi khusus
    }
    
    // Alias untuk collect (untuk kompatibilitas)
    public void collect() {
        // Tidak digunakan secara langsung
    }
}
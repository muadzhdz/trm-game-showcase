package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

import java.awt.Font;
import java.awt.Color;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    private Font interactFont;
    private BufferedImage soulIndicator;

    // movement prediction
    private int tempX, tempY;
    private boolean isMoving = false;
    
    // Stamina system
    private float maxStamina = 100f;
    private float currentStamina = 100f;
    private float staminaRegenRate = 0.5f;
    private float staminaDrainRate = 2.0f;
    private boolean isSprinting = false;
 // Di Player.java
    private float normalSpeed = 4f;  // 64/4 = 16 (perfect)
    private float sprintMultiplier = 2.0f;  // 4 * 2 = 8 (64/8 = 8, perfect!)

    

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        solidArea = new Rectangle(20, 24, 30, 30); // Adjusted for better collision
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        
        System.out.println("TileSize: " + gp.tileSize);
        System.out.println("SolidArea: " + solidArea);

        setDefaultValues();
        getPlayerImage();
        getSoulIndicatorImage();
        loadFont();
    }

    // ================= FONT =================
    private void loadFont() {
        try {
            interactFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.PLAIN, 14f);
        } catch (Exception e) {
            interactFont = new Font("Monospaced", Font.BOLD, 14);
        }
    }

    // ================= SOUL INDICATOR =================
    private void getSoulIndicatorImage() {
        try {
            soulIndicator = ImageIO.read(getClass().getResourceAsStream("/items/Soul1.png"));
        } catch (IOException e) {
            soulIndicator = null;
        }
    }

    // ================= DEFAULT =================
    public void setDefaultValues() {
        System.out.println("\n=== SETTING PLAYER DEFAULT VALUES ===");
        
        // Cari spawn point dari map
        int[] spawnPoint = gp.tileM.findSpawnPoint();
        
        // Snap ke tile center
        int tileCol = (spawnPoint[0] + gp.tileSize/2) / gp.tileSize;
        int tileRow = (spawnPoint[1] + gp.tileSize/2) / gp.tileSize;
        
        worldX = tileCol * gp.tileSize;
        worldY = tileRow * gp.tileSize;
        
        System.out.println("Player spawned at:");
        System.out.println("  Tile: [" + tileCol + "," + tileRow + "]");
        System.out.println("  World position: " + worldX + ", " + worldY);
        
        // Verifikasi tile
        if (tileRow >= 0 && tileRow < gp.tileM.rows && 
            tileCol >= 0 && tileCol < gp.tileM.cols) {
            
            int tileNum = gp.tileM.mapTileNum[tileRow][tileCol];
            System.out.println("  Tile number: " + tileNum);
            System.out.println("  Tile is floor (1): " + (tileNum == 1));
            
            // Cek posisi relatif terhadap altar kiri
            int altarLeftCol = (2 + 20) / 2; // Tengah room kiri
            int altarLeftRow = (2 + 16) / 2;
            int distanceToAltar = Math.abs(tileCol - altarLeftCol) + 
                                 Math.abs(tileRow - altarLeftRow);
            System.out.println("  Distance to altar left: " + distanceToAltar + " tiles");
        }
        
        normalSpeed = 4f;
        speed = (int) normalSpeed;
        direction = "down";
        collisionOn = false;
        hasSoul = false;
        soulType = -1;
        currentStamina = maxStamina;
        isSprinting = false;
    }
    
    private int[] findNearestWalkableTile(int startX, int startY) {
        int maxDistance = Math.max(gp.tileM.cols, gp.tileM.rows);
        
        for (int distance = 1; distance < maxDistance; distance++) {
            // Cari dalam bentuk spiral dari start point
            for (int dy = -distance; dy <= distance; dy++) {
                for (int dx = -distance; dx <= distance; dx++) {
                    if (Math.abs(dx) == distance || Math.abs(dy) == distance) {
                        int checkX = startX + dx;
                        int checkY = startY + dy;
                        
                        if (checkX >= 0 && checkX < gp.tileM.cols && 
                            checkY >= 0 && checkY < gp.tileM.rows) {
                            
                            int tileNum = gp.tileM.mapTileNum[checkY][checkX];
                            if (!gp.tileM.tile[tileNum].collision) {
                                return new int[]{checkX, checkY};
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // ================= IMAGE =================
    public void getPlayerImage() {
        try {
            // Front (down)
            front1 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Front1.png"));
            front2 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Front2.png"));
            front3 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Front3.png"));
            front4 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Front4.png"));

            // Back (up)
            back1 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Back1.png"));
            back2 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Back2.png"));
            back3 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Back3.png"));
            back4 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_Back4.png"));

            // Left
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideL1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideL2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideL3.png"));
            left4 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideL4.png"));

            // Right
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideR1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideR2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideR3.png"));
            right4 = ImageIO.read(getClass().getResourceAsStream("/player/Player_Walk_SideR4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= UPDATE =================
    public void update() {
        // Update stamina
        updateStamina();
        
        // Update movement speed based on sprint
        updateSpeed();
        
        isMoving = false;

        if (keyH.upPressed) {
            direction = "up";
            isMoving = true;
        } else if (keyH.downPressed) {
            direction = "down";
            isMoving = true;
        } else if (keyH.leftPressed) {
            direction = "left";
            isMoving = true;
        } else if (keyH.rightPressed) {
            direction = "right";
            isMoving = true;
        }

        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;

        if (isMoving) {
            tempX = worldX;
            tempY = worldY;

            switch (direction) {
                case "up": tempY -= speed; break;
                case "down": tempY += speed; break;
                case "left": tempX -= speed; break;
                case "right": tempX += speed; break;
            }

            collisionOn = false;
            
            // Check tile collision
            gp.cChecker.checkTile(this, tempX, tempY);
            
            // Check entity collision only if tile collision passes
            if (!collisionOn) {
                checkEntityCollisions();
            }

            if (!collisionOn) {
                worldX = tempX;
                worldY = tempY;
            }

            spriteCounter++;
            if (spriteCounter > (isSprinting ? 5 : 10)) {
                spriteNum = (spriteNum % 4) + 1;
                spriteCounter = 0;
            }

        } else {
            spriteNum = 1;
            spriteCounter = 0;
        }
        
        // PERBAIKAN: Validasi soul - maksimal 1 soul
        validateSoulState();
        
        // Keep player within world boundaries
        worldX = Math.max(0, Math.min(worldX, gp.worldWidth - solidArea.width - solidArea.x));
        worldY = Math.max(0, Math.min(worldY, gp.worldHeight - solidArea.height - solidArea.y));
    }
    
 // Validasi bahwa player hanya bisa membawa 1 soul
    private void validateSoulState() {
        // Jika hasSoul false tapi soulType bukan -1, reset
        if (!hasSoul && soulType != -1) {
            soulType = -1;
            System.out.println("DEBUG: Reset soul type karena hasSoul false");
        }
        
        // Jika hasSoul true tapi soulType -1, cari soul yang sesuai
        if (hasSoul && soulType == -1) {
            // Cari soul yang dikoleksi oleh player
            for (Soul soul : gp.gameProgress.getSouls()) {
                if (soul.isCollected()) {
                    soulType = soul.getType();
                    System.out.println("DEBUG: Set soul type to " + soulType);
                    break;
                }
            }
        }
    }
    
 // Method untuk menaruh soul di altar
    public boolean placeSoulOnAltar(Altar altar) {
        if (hasSoul && soulType == altar.getType() && !altar.isActivated()) {
            if (gp.cChecker.checkTwoEntities(this, altar)) {
                hasSoul = false;
                soulType = -1;
                System.out.println("Player placed soul type " + altar.getType() + " on altar");
                return true;
            }
        }
        return false;
    }
    
    private void checkEntityCollisions() {
        // Check collision with enemies
        for (Entity enemy : gp.gameProgress.getEnemies()) {
            if (gp.cChecker.checkTwoEntities(this, enemy)) {
                collisionOn = true;
                return;
            }
        }
     
        
        // Check collision with doors (only if door is closed)
        for (Entity door : gp.gameProgress.getDoors()) {
            if (gp.cChecker.checkTwoEntities(this, door) && door.collisionOn) {
                collisionOn = true;
                return;
            }
        }
        if (hasSoul) {
            System.out.println("Player carrying soul type: " + soulType);
        }
    }
    
    private void updateStamina() {
        // Check if shift is pressed for sprinting
        boolean tryingToSprint = keyH.shiftPressed && isMoving;
        
        if (tryingToSprint && currentStamina > 0) {
            isSprinting = true;
            currentStamina -= staminaDrainRate;
            if (currentStamina < 0) currentStamina = 0;
        } else {
            isSprinting = false;
            // Regenerate stamina when not sprinting
            currentStamina += staminaRegenRate;
            if (currentStamina > maxStamina) currentStamina = maxStamina;
        }
    }
    
    private void updateSpeed() {
        if (isSprinting && currentStamina > 0) {
            speed = (int) (normalSpeed * sprintMultiplier);  // = 8
        } else {
            speed = (int) normalSpeed;  // = 4
        }
        System.out.println("Current speed: " + speed);
    }

    // ================= DRAW =================
 // Di Player.draw():
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up": image = getSprite(back1, back2, back3, back4); break;
            case "down": image = getSprite(front1, front2, front3, front4); break;
            case "left": image = getSprite(left1, left2, left3, left4); break;
            case "right": image = getSprite(right1, right2, right3, right4); break;
        }

        if (image != null) {
            // PERBAIKAN: Gunakan gp.screenX dan gp.screenY untuk camera offset
            int screenX = worldX - gp.screenX;
            int screenY = worldY - gp.screenY;
            
            // Gambar player di posisi relatif terhadap kamera
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            
            // Draw soul indicator jika membawa soul
            if (hasSoul) {
                // Cari gambar soul yang tepat
                BufferedImage soulImage = getSoulImage();
                if (soulImage != null) {
                    // Gambar di atas kepala player
                    int soulScreenX = screenX + gp.tileSize/2 - 8;
                    int soulScreenY = screenY - 20;
                    g2.drawImage(soulImage, soulScreenX, soulScreenY, 16, 16, null);
                }
            }
            
            // DEBUG: Draw player collision box
            if (gp.keyH.shiftPressed && gp.keyH.ctrlPressed) {
                g2.setColor(Color.RED);
                g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, 
                           solidArea.width, solidArea.height);
                
                // Draw player center point
                g2.setColor(Color.GREEN);
                g2.fillRect(screenX + gp.tileSize/2 - 2, screenY + gp.tileSize/2 - 2, 5, 5);
            }
        }
        
        // Draw interaction prompts
        drawInteractionPrompts(g2);
    }

    private BufferedImage getSoulImage() {
        try {
            switch (soulType) {
                case 0: return ImageIO.read(getClass().getResourceAsStream("/items/Soul1.png"));
                case 1: return ImageIO.read(getClass().getResourceAsStream("/items/SoulB1.png"));
                case 2: return ImageIO.read(getClass().getResourceAsStream("/items/SoulC1.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Di Player.drawInteractionPrompts():
    private void drawInteractionPrompts(Graphics2D g2) {
        int screenX = worldX - gp.screenX;
        int screenY = worldY - gp.screenY;
        
        // Cek altars
        for (Altar altar : gp.gameProgress.getAltars()) {
            if (gp.cChecker.checkTwoEntities(this, altar)) {
                drawAltarPrompt(g2, altar, screenX, screenY);
                return;
            }
        }
        
        // Cek doors
        for (Door door : gp.gameProgress.getDoors()) {
            if (gp.cChecker.checkTwoEntities(this, door)) {
                drawDoorPrompt(g2, door, screenX, screenY);
                return;
            }
        }
    }
    
    

    private void drawAltarPrompt(Graphics2D g2, Altar altar, int playerScreenX, int playerScreenY) {
        // Position prompt di atas player
        int promptX = playerScreenX + gp.tileSize/2 - 80;
        int promptY = playerScreenY - 50;
        
        // Jika prompt di atas layar, taruh di bawah
        if (promptY < 0) {
            promptY = playerScreenY + gp.tileSize + 10;
        }
        
        // Background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(promptX, promptY, 160, 40);
        g2.setColor(Color.WHITE);
        g2.drawRect(promptX, promptY, 160, 40);
        
        // Text berdasarkan status
        g2.setFont(interactFont);
        
        if (altar.isActivated()) {
            g2.setColor(Color.GREEN);
            g2.drawString("Altar Active", promptX + 50, promptY + 25);
        } else if (hasSoul && soulType == altar.getType()) {
            g2.setColor(Color.YELLOW);
            g2.drawString("[E] Place Soul (Type " + altar.getType() + ")", 
                         promptX + 15, promptY + 25);
        } else if (hasSoul) {
            g2.setColor(Color.RED);
            g2.drawString("Wrong Soul Type!", promptX + 40, promptY + 25);
        } else {
            g2.setColor(Color.WHITE);
            g2.drawString("Needs Soul Type " + altar.getType(), 
                         promptX + 30, promptY + 25);
        }
    }
    
  

    private void drawDoorPrompt(Graphics2D g2, Door door, int playerScreenX, int playerScreenY) {
        // Position prompt di atas player
        int promptX = playerScreenX + gp.tileSize/2 - 100;
        int promptY = playerScreenY - 50;
        
        if (promptY < 0) {
            promptY = playerScreenY + gp.tileSize + 10;
        }
        
        // Background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(promptX, promptY, 200, 40);
        g2.setColor(Color.WHITE);
        g2.drawRect(promptX, promptY, 200, 40);
        
        // Text
        g2.setFont(interactFont);
        
        if (door.isOpen()) {
            g2.setColor(Color.GREEN);
            g2.drawString("[E] ESCAPE THROUGH DOOR", promptX + 20, promptY + 25);
            
            // Tambahkan instruksi win
            g2.setColor(Color.YELLOW);
            g2.drawString("Go through the door to win!", promptX, promptY - 10);
        } else {
            // Tampilkan progress door
            g2.setColor(Color.YELLOW);
            int activeAltars = 0;
            for (Altar altar : gp.gameProgress.getAltars()) {
                if (altar.isActivated()) activeAltars++;
            }
            
            if (activeAltars == 0) {
                g2.drawString("Door Locked - Find and activate altars", promptX + 10, promptY + 25);
            } else {
                g2.drawString("Door Progress: " + activeAltars + "/3 Altars Activated", 
                             promptX + 20, promptY + 25);
            }
        }
    }
    
    // Draw stamina bar
    public void drawStaminaBar(Graphics2D g2) {
        int barWidth = 200;
        int barHeight = 20;
        int barX = 10;
        int barY = gp.screenHeight - barHeight - 10;
        
        // Background
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(barX, barY, barWidth, barHeight);
        
        // Stamina fill
        float staminaPercent = currentStamina / maxStamina;
        int fillWidth = (int)(barWidth * staminaPercent);
        
        // Color based on stamina level
        Color fillColor;
        if (staminaPercent > 0.5) {
            fillColor = new Color(50, 200, 50);
        } else if (staminaPercent > 0.2) {
            fillColor = new Color(200, 200, 50);
        } else {
            fillColor = new Color(200, 50, 50);
        }
        
        g2.setColor(fillColor);
        g2.fillRect(barX, barY, fillWidth, barHeight);
        
        // Border
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
        
        // Text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("STAMINA", barX + 5, barY + 15);
        
        // Sprint indicator
        if (isSprinting) {
            g2.setColor(Color.YELLOW);
            g2.drawString("SPRINTING", barX + barWidth + 10, barY + 15);
        }
    }

    private BufferedImage getSprite(
        BufferedImage i1, BufferedImage i2,
        BufferedImage i3, BufferedImage i4) {

        switch (spriteNum) {
            case 1: return i1;
            case 2: return i2;
            case 3: return i3;
            case 4: return i4;
            default: return i1;
        }
    
    }

    public void resetPosition() {
        setDefaultValues();
    }
    
    // Stamina getters
    public float getCurrentStamina() {
        return currentStamina;
    }
    
    public float getMaxStamina() {
        return maxStamina;
    }
    
    public boolean isSprinting() {
        return isSprinting;
    }
    
    // Getters for collision checking
    public int getWorldX() {
        return worldX;
    }
    
    public int getWorldY() {
        return worldY;
    }
    
    public Rectangle getSolidArea() {
        return new Rectangle(
            worldX + solidArea.x,
            worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
    }
}
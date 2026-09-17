package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Enemy extends Entity {
    
    GamePanel gp;
    private BufferedImage[][] walkFrames = new BufferedImage[4][4];
    private int patrolRange;  // JANGAN langsung diinisialisasi di sini
    private int patrolStartX, patrolStartY;
    private boolean movingRight = true;
    private boolean isChasing = false;
    private int chaseRange;   // JANGAN langsung diinisialisasi di sini
    private int collisionCheckCounter = 0;
    
    // Untuk obstacle avoidance
    private int stuckCounter = 0;
    private final int MAX_STUCK_FRAMES = 30;
    
    public Enemy(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        
        // INISIALISASI DI SINI SETELAH this.gp = gp
        this.patrolRange = 3 * gp.tileSize;  // 3 tiles dalam pixel
        this.chaseRange = 5 * gp.tileSize;   // 5 tiles dalam pixel
        
        this.worldX = worldX;
        this.worldY = worldY;
        this.patrolStartX = worldX;
        this.patrolStartY = worldY;
        
        getEnemyImages();
        setDefaultValues();
        setSolidArea();
        
        System.out.println("Enemy created at: " + worldX + ", " + worldY);
        System.out.println("Enemy speed: " + speed);
        System.out.println("Patrol range: " + patrolRange + " pixels");
    }
    
    // ... sisanya tetap sama ...
    
    private void getEnemyImages() {
        try {
            // Down (ShadowEnemy)
            for (int i = 0; i < 4; i++) {
                var stream = getClass().getResourceAsStream(String.format("/enemy/ShadowEnemy%d.png", i+1));
                walkFrames[0][i] = stream != null ? ImageIO.read(stream) : new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            }
            
            // Up (ShadowEnemyB)
            for (int i = 0; i < 4; i++) {
                var stream = getClass().getResourceAsStream(String.format("/enemy/ShadowEnemyB%d.png", i+1));
                walkFrames[1][i] = stream != null ? ImageIO.read(stream) : new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            }
            
            // Left (ShadowEnemyL)
            for (int i = 0; i < 4; i++) {
                var stream = getClass().getResourceAsStream(String.format("/enemy/ShadowEnemyL%d.png", i+1));
                walkFrames[2][i] = stream != null ? ImageIO.read(stream) : new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            }
            
            // Right (ShadowEnemyR)
            for (int i = 0; i < 4; i++) {
                var stream = getClass().getResourceAsStream(String.format("/enemy/ShadowEnemyR%d.png", i+1));
                walkFrames[3][i] = stream != null ? ImageIO.read(stream) : new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            createPlaceholderImages();
        }
    }
    
    private void createPlaceholderImages() {
        for (int dir = 0; dir < 4; dir++) {
            for (int frame = 0; frame < 4; frame++) {
                walkFrames[dir][frame] = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2 = walkFrames[dir][frame].createGraphics();
                g2.setColor(new java.awt.Color(255, 0, 0, 200));
                g2.fillRect(0, 0, gp.tileSize, gp.tileSize);
                g2.setColor(java.awt.Color.WHITE);
                g2.drawString("Enemy", 20, 40);
                g2.dispose();
            }
        }
    }
    
    private void setDefaultValues() {
        // Speed harus kelipatan yang bagus dari tileSize
        // tileSize = 64, gunakan speed = 2, 4, 8, 16, 32
        speed = 2;  // 2 pixel per frame, 30 frame untuk 1 tile (cepat cukup)
        direction = "right";
        collisionOn = false;
    }
    
    private void setSolidArea() {
        // Gunakan solidArea yang sama dengan player
        solidArea = new Rectangle(16, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
    
    public void update(Player player) { // player sudah dideklarasikan sebagai parameter
        // Check distance to player
        int distanceX = Math.abs(worldX - player.worldX);
        int distanceY = Math.abs(worldY - player.worldY);
        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        
        isChasing = (distance < chaseRange);
        
        // Reset temp position ke current position
        tempX = worldX;
        tempY = worldY;
        
        if (isChasing) {
            chasePlayer(player);
        } else {
            patrol();
        }
        
        // Check collision dengan posisi YANG AKAN DATANG
        collisionOn = false;
        gp.cChecker.checkTile(this, tempX, tempY);
        
        // Jika tidak ada collision, update position
        if (!collisionOn) {
            worldX = tempX;
            worldY = tempY;
            stuckCounter = 0;  // Reset stuck counter
        } else {
            // Jika ada collision, coba direction lain
            avoidObstacle();
            stuckCounter++;
            
            // Jika stuck terlalu lama, coba teleport ke posisi aman
            if (stuckCounter > MAX_STUCK_FRAMES) {
                System.out.println("Enemy stuck! Attempting recovery...");
                attemptUnstuck();
                stuckCounter = 0;
            }
        }
        
        // Update animation
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum % 4) + 1;
            spriteCounter = 0;
        }
        
        // PERBAIKAN: Hapus deklarasi ulang variabel player
        // Check collision dengan player
        collisionCheckCounter++;
        if (collisionCheckCounter > 10) {
            checkCollisionWithPlayer(player);
            collisionCheckCounter = 0;
        }
    }
        
        private boolean isPositionValid(int x, int y) {
            int tileCol = x / gp.tileSize;
            int tileRow = y / gp.tileSize;
            
            if (tileCol < 0 || tileCol >= gp.tileM.cols || 
                tileRow < 0 || tileRow >= gp.tileM.rows) {
                return false;
            }
            
            int tileNum = gp.tileM.mapTileNum[tileRow][tileCol];
            return (tileNum >= 1 && tileNum <= 12); // Tile floor
        }
        private void teleportToSafePosition() {
            System.out.println("Enemy stuck! Teleporting to safe position...");
            
            int currentCol = worldX / gp.tileSize;
            int currentRow = worldY / gp.tileSize;
            
            // Cari tile floor terdekat dalam radius 5 tiles
            for (int radius = 1; radius <= 5; radius++) {
                for (int dr = -radius; dr <= radius; dr++) {
                    for (int dc = -radius; dc <= radius; dc++) {
                        int col = currentCol + dc;
                        int row = currentRow + dr;
                        
                        if (col >= 0 && col < gp.tileM.cols && 
                            row >= 0 && row < gp.tileM.rows) {
                            
                            int tileNum = gp.tileM.mapTileNum[row][col];
                            if (tileNum >= 1 && tileNum <= 12) {
                                worldX = col * gp.tileSize + gp.tileSize/2;
                                worldY = row * gp.tileSize + gp.tileSize/2;
                                System.out.println("Enemy teleported to (" + col + "," + row + ")");
                                return;
                            }
                        }
                    }
                }
            }
        }
    
    private void chasePlayer(Player player) {
        // Hitung perbedaan X dan Y
        int diffX = player.worldX - worldX;
        int diffY = player.worldY - worldY;
        
        // Pilih arah dengan perbedaan terbesar
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Bergerak horizontal
            if (diffX > 0) {
                direction = "right";
                tempX += speed;
            } else {
                direction = "left";
                tempX -= speed;
            }
        } else {
            // Bergerak vertikal
            if (diffY > 0) {
                direction = "down";
                tempY += speed;
            } else {
                direction = "up";
                tempY -= speed;
            }
        }
    }
    
    private void patrol() {
        if (movingRight) {
            direction = "right";
            tempX += speed;
            
            // Cek apakah sudah melebihi patrol range
            if (tempX > patrolStartX + patrolRange) {
                movingRight = false;
                tempX = worldX;  // Jangan bergerak melewati batas
            }
        } else {
            direction = "left";
            tempX -= speed;
            
            // Cek apakah sudah melebihi patrol range
            if (tempX < patrolStartX - patrolRange) {
                movingRight = true;
                tempX = worldX;  // Jangan bergerak melewati batas
            }
        }
    }
    
    private void avoidObstacle() {
        // Coba direction lain secara acak
        String[] directions = {"up", "down", "left", "right"};
        int randomIndex = (int)(Math.random() * directions.length);
        direction = directions[randomIndex];
        
        // Reset temp position untuk coba direction baru
        tempX = worldX;
        tempY = worldY;
        
        // Apply movement berdasarkan direction baru
        switch (direction) {
            case "up": tempY -= speed; break;
            case "down": tempY += speed; break;
            case "left": tempX -= speed; break;
            case "right": tempX += speed; break;
        }
        
        System.out.println("Enemy avoiding obstacle, new direction: " + direction);
    }
    
    private void attemptUnstuck() {
        // Cari tile walkable terdekat
        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;
        
        // Cari dalam radius 3 tiles
        for (int r = tileRow - 3; r <= tileRow + 3; r++) {
            for (int c = tileCol - 3; c <= tileCol + 3; c++) {
                if (r >= 0 && r < gp.tileM.rows && c >= 0 && c < gp.tileM.cols) {
                    int tileNum = gp.tileM.mapTileNum[r][c];
                    if (!gp.tileM.tile[tileNum].collision) {
                        // Teleport ke tile ini
                        worldX = c * gp.tileSize + gp.tileSize/2;
                        worldY = r * gp.tileSize + gp.tileSize/2;
                        System.out.println("Enemy teleported to: " + worldX + ", " + worldY);
                        return;
                    }
                }
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.screenX;
        int screenY = worldY - gp.screenY;
        
        // Gambar hanya jika di dalam layar
        if (screenX + gp.tileSize > 0 && 
            screenX < gp.screenWidth &&
            screenY + gp.tileSize > 0 && 
            screenY < gp.screenHeight) {
            
            int dirIndex = 0;
            switch (direction) {
                case "up": dirIndex = 1; break;
                case "down": dirIndex = 0; break;
                case "left": dirIndex = 2; break;
                case "right": dirIndex = 3; break;
            }
            
            BufferedImage image = walkFrames[dirIndex][spriteNum - 1];
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            
            // Debug: Draw collision box dan chase range
            if (gp.keyH.shiftPressed && gp.keyH.ctrlPressed) { // Debug mode
                // Solid area
                g2.setColor(Color.RED);
                g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, 
                           solidArea.width, solidArea.height);
                
                // Chase range (hanya jika tidak chasing)
                if (!isChasing) {
                    g2.setColor(new Color(255, 255, 0, 50));
                    int chaseRadiusScreen = chaseRange - gp.screenX + worldX;
                    g2.fillOval(screenX - chaseRange/2, screenY - chaseRange/2, 
                               chaseRange, chaseRange);
                }
                
                // Status text
                g2.setColor(Color.WHITE);
                g2.drawString("Chasing: " + isChasing, screenX, screenY - 10);
                g2.drawString("Stuck: " + stuckCounter + "/" + MAX_STUCK_FRAMES, 
                             screenX, screenY - 20);
            }
        }
    }
    
    public void checkCollisionWithPlayer(Player player) {
        if (gp.cChecker.checkTwoEntities(this, player)) {
            System.out.println("Enemy caught player!");
            player.resetPosition();
            player.setHasSoul(false);
            player.setSoulType(-1);
            
            // Game over logic handled in GameProgress
            gp.gameProgress.setGameOver(true);
        }
    }
}
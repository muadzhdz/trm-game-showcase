package states;

import java.awt.Graphics2D;
import java.util.ArrayList;

import entities.*;
import main.GamePanel;

public class GameProgress {
    
    private GamePanel gp;
    
    // Game objects
    private ArrayList<Soul> souls;
    private ArrayList<Altar> altars;
    private ArrayList<Enemy> enemies;
    private ArrayList<Door> doors;
    
    // Game progress
    private boolean[] altarsActivated;
    private int soulsCollected;
    private int soulsDelivered;
    private boolean gameOver;
    private boolean gameWon;
    private boolean winAnnounced;  // <-- TAMBAHKAN VARIABLE INI
    
    public GameProgress(GamePanel gp) {
        this.gp = gp;
        this.souls = new ArrayList<>();
        this.altars = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.altarsActivated = new boolean[3];
        
        reset();
    }
    
    public void reset() {
        soulsCollected = 0;
        soulsDelivered = 0;
        gameOver = false;
        gameWon = false;
        winAnnounced = false;  // <-- RESET
        
        for (int i = 0; i < 3; i++) {
            altarsActivated[i] = false;
        }
        
        // Reset semua entities
        for (Soul soul : souls) {
            if (soul != null) soul.reset();
        }
        
        for (Altar altar : altars) {
            if (altar != null) altar.reset();
        }
        
        for (Door door : doors) {
            if (door != null) door.reset();
        }
        
        for (Enemy enemy : enemies) {
            // Reset enemy position jika ada method reset
            // enemy.reset();
        }
    }
    
    public void update(Player player) {
        if (gameOver || gameWon) return;
        
        // Update souls
        for (Soul soul : souls) {
            if (soul != null) {
                soul.update();
                soul.checkCollection(player);
            }
        }
        
        // Update altars dan cek interaksi
        for (Altar altar : altars) {
            if (altar != null) {
                altar.update();
                
                // Cek interaksi dengan tombol E
                if (gp.keyH.interactPressed && !altar.isActivated()) {
                    if (gp.cChecker.checkTwoEntities(altar, player)) {
                        if (altar.activate(player)) {
                            // Sukses mengaktifkan altar
                            altarsActivated[altar.getType()] = true;
                            soulsDelivered++;
                            System.out.println("Altar " + altar.getType() + " activated! Souls delivered: " + soulsDelivered);
                            
                            // Update door states
                            updateDoorStates();
                            
                            // Reset tombol
                            gp.keyH.interactPressed = false;
                        }
                    }
                }
            }
        }
        
        // Update enemies
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                enemy.update(player);
                
                // Cek collision dengan player untuk game over
                if (gp.cChecker.checkTwoEntities(player, enemy)) {
                    gameOver = true;
                    System.out.println("Game Over - Player caught by enemy!");
                    return;
                }
            }
        }
        
        // Update doors dan cek WIN CONDITION
        updateDoorsAndCheckWin(player);
        
        // Reset tombol jika tidak digunakan
        if (gp.keyH.interactPressed) {
            gp.keyH.interactPressed = false;
        }
        
        // Update souls count
        updateSoulsCount();  // <-- PERBAIKAN NAMA METHOD
    }
    
    private void updateDoorsAndCheckWin(Player player) {
        boolean allAltarsActive = checkAllAltarsActivated();
        System.out.println("DEBUG: All altars active? " + allAltarsActive);
        
        if (doors.isEmpty()) {
            System.out.println("DEBUG: No doors found!");
            return;
        }
        
        for (Door door : doors) {
            if (door != null) {
                door.update();
                
                // Debug door state
                System.out.println("DEBUG: Door at (" + door.worldX/gp.tileSize + "," + 
                                 door.worldY/gp.tileSize + ") - Open: " + door.isOpen());
                
                // Jika semua altar aktif, buka door
                if (allAltarsActive && !door.isOpen()) {
                    door.open();
                    System.out.println("DEBUG: Opening door because all altars active");
                }
                
                // Cek jika door terbuka dan player berinteraksi
                if (door.isOpen() && gp.keyH.interactPressed) {
                    boolean isNearDoor = gp.cChecker.checkTwoEntities(player, door);
                    System.out.println("DEBUG: Player near open door? " + isNearDoor);
                    
                    if (isNearDoor) {
                        gameWon = true;
                        System.out.println("=== WIN CONDITION TRIGGERED ===");
                        System.out.println("Door open: YES");
                        System.out.println("Player near door: YES");
                        System.out.println("Interact pressed: YES");
                        gp.keyH.interactPressed = false;
                        return;
                    }
                }
                if (door.isOpen() && gp.keyH.interactPressed) {
                    // Force check dengan radius yang lebih besar
                    int playerCenterX = player.worldX + player.solidArea.width/2;
                    int playerCenterY = player.worldY + player.solidArea.height/2;
                    int doorCenterX = door.worldX + gp.tileSize;
                    int doorCenterY = (int) (door.worldY + gp.tileSize * 1.5f);
                    
                    int distance = (int) Math.sqrt(
                        Math.pow(playerCenterX - doorCenterX, 2) + 
                        Math.pow(playerCenterY - doorCenterY, 2)
                    );
                    
                    System.out.println("Distance to door: " + distance + " pixels");
                    
                    if (distance < gp.tileSize * 2) { // 2 tiles radius
                        gameWon = true;
                        System.out.println("WIN by distance check!");
                        gp.keyH.interactPressed = false;
                        return;
                    }
                }
                
                // Otomatis win jika menyentuh door terbuka (optional)
                if (door.isOpen() && gp.cChecker.checkTwoEntities(player, door)) {
                    if (!player.hasSoul()) { // Pastikan tidak membawa soul
                        gameWon = true;
                        System.out.println("=== AUTO WIN: Player touched open door ===");
                        return;
                    }
                }
            }
        }
    }
    
    // PERBAIKAN: Method updateSoulsCount yang benar
    private void updateSoulsCount() {
        int collected = 0;
        int delivered = 0;
        
        for (Soul soul : souls) {
            if (soul != null) {
                if (soul.isCollected()) {
                    collected++;
                }
                if (soul.isPlacedOnAltar()) {
                    delivered++;
                }
            }
        }
        
        soulsCollected = collected;
        soulsDelivered = delivered;
        
        // Debug info
        if (soulsCollected > 0 || soulsDelivered > 0) {
            System.out.println("Souls - Collected: " + soulsCollected + ", Delivered: " + soulsDelivered);
        }
    }
    
    // PERBAIKAN: Method updateSoulsCollected dihapus karena sudah diganti dengan updateSoulsCount
    
    private void updateDoorStates() {
        // Update door state berdasarkan altar yang aktif
        for (Door door : doors) {
            if (door != null) {
                door.updateDoorState();
            }
        }
    }
    
    private boolean checkAllAltarsActivated() {
        int activatedCount = 0;
        for (Altar altar : altars) {
            if (altar != null && altar.isActivated()) {
                activatedCount++;
            }
        }
        
        boolean allActivated = (activatedCount >= 3); // Minimal 3 altar aktif
        
        if (allActivated && !winAnnounced) {
            System.out.println("All 3 altars activated! Door should open!");
            winAnnounced = true;
        }
        
        return allActivated;
    }
    
    public void draw(Graphics2D g2) {
        // Draw souls (hanya yang belum ditaruh di altar)
        for (Soul soul : souls) {
            if (soul != null && !soul.isPlacedOnAltar()) {
                soul.draw(g2);
            }
        }
        
        // Draw altars
        for (Altar altar : altars) {
            if (altar != null) {
                altar.draw(g2);
            }
        }
        
        // Draw enemies
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                enemy.draw(g2);
            }
        }
        
        // Draw doors
        for (Door door : doors) {
            if (door != null) {
                door.draw(g2);
            }
        }
    }
    
    // Setter untuk gameWon
    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
        if (gameWon) {
            System.out.println("Game set to WON state!");
        }
    }
    
    // Getters and setters
    public ArrayList<Soul> getSouls() { return souls; }
    public ArrayList<Altar> getAltars() { return altars; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<Door> getDoors() { return doors; }
    
    public void addSoul(Soul soul) { 
        if (soul != null) souls.add(soul); 
    }
    
    public void addAltar(Altar altar) { 
        if (altar != null) altars.add(altar); 
    }
    
    public void addEnemy(Enemy enemy) { 
        if (enemy != null) enemies.add(enemy); 
    }
    
    public void addDoor(Door door) { 
        if (door != null) doors.add(door); 
    }
    
    public int getSoulsCollected() { return soulsCollected; }
    public int getSoulsDelivered() { return soulsDelivered; }
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    
    public boolean[] getAltarsActivated() { return altarsActivated; }
}
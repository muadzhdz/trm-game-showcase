package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Door extends Entity {
    
    GamePanel gp;
    private boolean opened = false;
    private BufferedImage[][] doorStates; // [state][frame]
    private int currentState = 0; // 0: closed, 1: A, 2: B, 3: C, 4: AB, 5: AC, 6: BC, 7: ABC, 8: Opened
    private int animationFrame = 0;
    private int animationCounter = 0;
    private boolean[] altarsActivated = new boolean[3]; // Track A, B, C altars
    
    private final int DOOR_WIDTH = 64;
    private final int DOOR_HEIGHT = 64;

    public Door(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        
        getDoorImages();
        setSolidArea();
        updateDoorState(); // Initialize state
    }
    
    private void getDoorImages() {
        try {
            // Initialize array untuk 9 state
            doorStates = new BufferedImage[9][4]; // Semua state punya 4 frame
            
            // State 0: SoulDoor.png (closed, no lanterns)
            doorStates[0][0] = loadImage("/door/SoulDoor.png");
            // Duplikat untuk frame lainnya
            doorStates[0][1] = doorStates[0][0];
            doorStates[0][2] = doorStates[0][0];
            doorStates[0][3] = doorStates[0][0];
            
            // State 1-7: Load berdasarkan pattern
            String[] stateNames = {"A", "B", "C", "AB", "AC", "BC", "ABC"};
            for (int state = 1; state <= 7; state++) {
                String name = stateNames[state - 1];
                for (int frame = 0; frame < 4; frame++) {
                    String path = String.format("/door/SoulDoor%s%d.png", name, frame + 1);
                    BufferedImage img = loadImage(path);
                    if (img != null) {
                        doorStates[state][frame] = img;
                    } else {
                        // Jika tidak ditemukan, gunakan placeholder
                        doorStates[state][frame] = createPlaceholderImage(state);
                    }
                }
            }
            
            // State 8: SoulDoorOpened.png (opened door)
            doorStates[8][0] = loadImage("/door/SoulDoorOpened.png");
            if (doorStates[8][0] == null) {
                doorStates[8][0] = createPlaceholderImage(8);
            }
            // Duplikat untuk frame lainnya
            doorStates[8][1] = doorStates[8][0];
            doorStates[8][2] = doorStates[8][0];
            doorStates[8][3] = doorStates[8][0];
            
            System.out.println("Door images loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading door images: " + e.getMessage());
            createPlaceholderDoors();
        }
    }
    
    private BufferedImage loadImage(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }
    
    private void createPlaceholderDoors() {
        // Create placeholder images if real ones aren't found
        doorStates = new BufferedImage[9][4];
        for (int state = 0; state < 9; state++) {
            for (int frame = 0; frame < 4; frame++) {
                doorStates[state][frame] = createPlaceholderImage(state);
            }
        }
    }
    
    private BufferedImage createPlaceholderImage(int state) {
        BufferedImage img = new BufferedImage(gp.tileSize * 2, gp.tileSize * 3, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        // Background color based on state
        switch(state) {
            case 0: g2.setColor(new Color(100, 100, 100, 200)); break; // Gray - closed
            case 1: g2.setColor(new Color(200, 100, 100, 200)); break; // Red - A
            case 2: g2.setColor(new Color(100, 200, 100, 200)); break; // Green - B
            case 3: g2.setColor(new Color(100, 100, 200, 200)); break; // Blue - C
            case 4: g2.setColor(new Color(200, 200, 100, 200)); break; // Yellow - AB
            case 5: g2.setColor(new Color(200, 100, 200, 200)); break; // Magenta - AC
            case 6: g2.setColor(new Color(100, 200, 200, 200)); break; // Cyan - BC
            case 7: g2.setColor(new Color(200, 200, 200, 200)); break; // White - ABC
            case 8: g2.setColor(new Color(50, 200, 50, 200)); break; // Green - Opened
        }
        
        g2.fillRect(0, 0, gp.tileSize * 2, gp.tileSize * 3);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, gp.tileSize * 2 - 1, gp.tileSize * 3 - 1);
        g2.setColor(Color.WHITE);
        g2.drawString("Door " + getStateName(state), 10, 30);
        g2.dispose();
        
        return img;
    }
    
    private String getStateName(int state) {
        switch(state) {
            case 0: return "Closed";
            case 1: return "A Lit";
            case 2: return "B Lit";
            case 3: return "C Lit";
            case 4: return "AB Lit";
            case 5: return "AC Lit";
            case 6: return "BC Lit";
            case 7: return "ABC Lit";
            case 8: return "OPENED";
            default: return "Unknown";
        }
    }
    
    private void setSolidArea() {
        // Door solid area (when closed)
        solidArea = new Rectangle(gp.tileSize / 2, 0, gp.tileSize, gp.tileSize * 3);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        collisionOn = true; // Door default memiliki collision
    }
    
    public void update() {
        // Update animation hanya untuk state 1-7 (state dengan animasi lantern)
        if (currentState >= 1 && currentState <= 7) {
            animationCounter++;
            if (animationCounter > 12) { // 12 frame delay untuk animasi halus
                animationFrame = (animationFrame + 1) % 4;
                animationCounter = 0;
            }
        }
        
        // Update door state berdasarkan altar yang aktif
        updateDoorState();
        
        // Jika door terbuka, hilangkan collision
        if (opened) {
            solidArea = new Rectangle(0, 0, 0, 0);
            collisionOn = false;
        }
    }
    
    public void updateDoorState() {
        // Perbarui status altar
        updateAltarsStatus();
        
        // Hitung berapa altar yang aktif
        int activatedCount = 0;
        for (boolean activated : altarsActivated) {
            if (activated) activatedCount++;
        }
        
        // Tentukan state baru berdasarkan altar yang aktif
        int newState = calculateDoorState();
        
        // Jika state berubah, reset animasi
        if (currentState != newState) {
            currentState = newState;
            animationFrame = 0;
            animationCounter = 0;
            System.out.println("Door state changed to: " + getStateName(currentState));
        }
        
        // Jika semua altar aktif, buka pintu
        if (activatedCount == 3 && !opened) {
            open();
        }
    }
    
    private void updateAltarsStatus() {
        // Reset array
        altarsActivated[0] = false;
        altarsActivated[1] = false;
        altarsActivated[2] = false;
        
        // Update berdasarkan altar yang aktif di game
        if (gp.gameProgress != null) {
            for (Altar altar : gp.gameProgress.getAltars()) {
                if (altar.isActivated()) {
                    int type = altar.getType();
                    if (type >= 0 && type < 3) {
                        altarsActivated[type] = true;
                    }
                }
            }
        }
    }
    
    private int calculateDoorState() {
        int activatedCount = 0;
        for (boolean activated : altarsActivated) {
            if (activated) activatedCount++;
        }
        
        // Jika pintu sudah terbuka, tetap di state 8
        if (opened) {
            return 8;
        }
        
        // Tentukan state berdasarkan kombinasi altar
        switch (activatedCount) {
            case 0:
                return 0; // Closed, no lanterns
            case 1:
                if (altarsActivated[0]) return 1; // A
                else if (altarsActivated[1]) return 2; // B
                else return 3; // C
            case 2:
                if (altarsActivated[0] && altarsActivated[1]) return 4; // AB
                else if (altarsActivated[0] && altarsActivated[2]) return 5; // AC
                else return 6; // BC
            case 3:
                return 7; // ABC (all lanterns lit, but door still closed)
            default:
                return 0;
        }
    }
    
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.screenX;
        int screenY = worldY - gp.screenY;
        
        // Gambar hanya jika di dalam layar
        if (screenX + gp.tileSize * 2 > 0 && 
            screenX < gp.screenWidth &&
            screenY + gp.tileSize * 3 > 0 && 
            screenY < gp.screenHeight) {
            
            BufferedImage image = doorStates[currentState][animationFrame];
            if (image != null) {
            	int doorSize = gp.tileSize * 4;

            	g2.drawImage(
            	    image,
            	    screenX,
            	    screenY,
            	    doorSize,
            	    doorSize,
            	    null
            	);

            }
            
            // DEBUG: Draw collision box dan info
            if (gp.keyH.shiftPressed && gp.keyH.ctrlPressed) {
                g2.setColor(opened ? Color.GREEN : Color.RED);
                g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, 
                           solidArea.width, solidArea.height);
                
                // Draw state text
                g2.setColor(Color.WHITE);
                g2.drawString("State: " + getStateName(currentState), 
                             screenX, screenY - 10);
                g2.drawString("Altars: " + getAltarsStatusString(), 
                             screenX, screenY - 25);
            }
        }
    }
    
    private String getAltarsStatusString() {
        return (altarsActivated[0] ? "A" : "_") + " " +
               (altarsActivated[1] ? "B" : "_") + " " +
               (altarsActivated[2] ? "C" : "_");
    }
    
    // Method untuk membuka pintu
    public void open() {
        if (!opened) {
            opened = true;
            currentState = 8;
            collisionOn = false;
            animationFrame = 0;
            animationCounter = 0;
            System.out.println("Door opened!");
        }
    }
    // Method untuk menutup pintu
    public void close() {
        opened = false;
        collisionOn = true;
        updateDoorState(); // Kembali ke state berdasarkan altar
    }
    
    // Getter methods
    public boolean isOpen() {
        return opened;
    }
    
    public int getCurrentState() {
        return currentState;
    }
    
    public boolean[] getAltarsActivated() {
        return altarsActivated.clone();
    }
    
    // Method untuk reset door (saat game restart)
    public void reset() {
        opened = false;
        collisionOn = true;
        altarsActivated[0] = false;
        altarsActivated[1] = false;
        altarsActivated[2] = false;
        currentState = 0;
        animationFrame = 0;
        animationCounter = 0;
        setSolidArea(); // Reset collision area
    }
    
    // Static method untuk clear
    public static void clear() {
        // Tidak perlu implementasi khusus
    }
}
package effects;

import java.awt.*;
import java.awt.image.BufferedImage;
import main.GamePanel;
import entities.Player;

public class DarknessEffect {

    private GamePanel gp;
    private Player player;

    private int minRadius = 120;
    private int maxRadius = 260;
    private int lightRadius = 200;

    private BufferedImage darknessLayer;

    public DarknessEffect(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        createDarknessLayer();
    }

    private void createDarknessLayer() {
        darknessLayer = new BufferedImage(
            gp.screenWidth,
            gp.screenHeight,
            BufferedImage.TYPE_INT_ARGB
        );
    }

    // ================= UPDATE =================
    public void update() {
        // Adjust light radius based on player's stamina
        if (player.isSprinting() && player.getCurrentStamina() > 0) {
            lightRadius = 180; // Slightly smaller when sprinting (tension)
        } else {
            lightRadius = 220; // Normal light radius
        }
    }

    // ================= DRAW =================
    public void draw(Graphics2D g2) {
        // Only draw darkness during gameplay
        if (gp.gameState != main.GameStateManager.PLAYING) {
            return;
        }

        Graphics2D gDark = darknessLayer.createGraphics();

        // Clear the layer
        gDark.setComposite(AlphaComposite.Clear);
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Set composite back to normal
        gDark.setComposite(AlphaComposite.SrcOver);

        // 1️⃣ HAMPIR HITAM TOTAL
        gDark.setColor(new Color(0, 0, 0, 250));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2️⃣ POSISI CAHAYA (PLAYER DI POSISI SCREEN)
        // Konversi world position ke screen position
        int screenX = player.getWorldX() - gp.screenX;
        int screenY = player.getWorldY() - gp.screenY;
        
        // Pastikan posisi dalam batas screen (untuk edge cases)
        screenX = Math.max(0, Math.min(screenX, gp.screenWidth));
        screenY = Math.max(0, Math.min(screenY, gp.screenHeight));

        // 3️⃣ LUBANG CAHAYA
        gDark.setComposite(AlphaComposite.DstOut);

        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point(screenX, screenY),
            lightRadius,
            new float[]{0f, 0.45f, 1f},
            new Color[]{
                new Color(0, 0, 0, 255), // Pusat
                new Color(0, 0, 0, 200), // Transisi
                new Color(0, 0, 0, 0)    // Lengkap transparan
            }
        );

        gDark.setPaint(gradient);
        gDark.fillOval(
            screenX - lightRadius,
            screenY - lightRadius,
            lightRadius * 2,
            lightRadius * 2
        );

        gDark.dispose();

        // 4️⃣ DRAW KE SCREEN
        g2.drawImage(darknessLayer, 0, 0, null);
        
        // DEBUG: Gambar posisi cahaya
        if (gp.keyH.shiftPressed && gp.keyH.ctrlPressed) {
            g2.setColor(Color.RED);
            g2.drawOval(screenX - lightRadius, screenY - lightRadius, 
                       lightRadius * 2, lightRadius * 2);
            g2.setColor(Color.GREEN);
            g2.fillRect(screenX - 2, screenY - 2, 5, 5);
        }
    }

    // ================= HELPER METHODS =================
    public Point getLightPositionOnScreen() {
        int screenX = player.getWorldX() - gp.screenX;
        int screenY = player.getWorldY() - gp.screenY;
        return new Point(screenX, screenY);
    }
    
    public Point getLightPositionInWorld() {
        return new Point(player.getWorldX(), player.getWorldY());
    }
    
    public boolean isPositionInLight(int worldX, int worldY) {
        // Konversi world position ke screen position
        int screenPosX = worldX - gp.screenX;
        int screenPosY = worldY - gp.screenY;
        
        // Dapatkan posisi cahaya di screen
        Point lightPos = getLightPositionOnScreen();
        
        // Hitung jarak dari cahaya player
        double distance = Math.sqrt(
            Math.pow(screenPosX - lightPos.x, 2) + 
            Math.pow(screenPosY - lightPos.y, 2)
        );
        
        return distance <= lightRadius;
    }
    
    public Rectangle getLightAreaOnScreen() {
        Point lightPos = getLightPositionOnScreen();
        return new Rectangle(
            lightPos.x - lightRadius,
            lightPos.y - lightRadius,
            lightRadius * 2,
            lightRadius * 2
        );
    }
    
    public Rectangle getLightAreaInWorld() {
        int worldX = player.getWorldX();
        int worldY = player.getWorldY();
        return new Rectangle(
            worldX - lightRadius,
            worldY - lightRadius,
            lightRadius * 2,
            lightRadius * 2
        );
    }

    public void setLightRadius(int radius) {
        lightRadius = Math.max(minRadius, Math.min(maxRadius, radius));
    }

    public int getLightRadius() {
        return lightRadius;
    }
    
    public void resize() {
        // Jika screen size berubah, recreate layer
        createDarknessLayer();
    }
}
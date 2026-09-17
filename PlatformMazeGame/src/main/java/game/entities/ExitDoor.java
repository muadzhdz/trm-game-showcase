package game.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ExitDoor {

    private int x, y, size;
    private boolean unlocked = false;

    private BufferedImage lockedSprite;
    private BufferedImage openSprite;

    public ExitDoor(int x, int y, int size, boolean unlockedFromStart) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.unlocked = unlockedFromStart;

        try {
            lockedSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/exit_locked.png"));
            openSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/exit_open.png"));
        } catch (Exception e) {
            System.out.println("Gagal load exit sprites.");
            e.printStackTrace();
        }
    }

    public void setUnlocked(boolean value) {
        unlocked = value;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void render(Graphics g) {
        BufferedImage img = unlocked ? openSprite : lockedSprite;
        if (img != null) g.drawImage(img, x, y, size, size, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}

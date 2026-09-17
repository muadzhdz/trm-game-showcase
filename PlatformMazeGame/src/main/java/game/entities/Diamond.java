package game.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Diamond {

    private int x, y, size;
    private boolean collected = false;
    private BufferedImage sprite;

    public Diamond(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;

        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/sprites/diamond.png"));
        } catch (Exception e) {
            System.out.println("Gagal load diamond.png");
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        if (collected) return;
        if (sprite != null) g.drawImage(sprite, x, y, size, size, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}

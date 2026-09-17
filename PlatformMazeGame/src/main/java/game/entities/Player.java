package game.entities;

import game.map.CollisionManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {

    private int x, y;
    private int size;

    private int dx = 1;
    private int dy = 0;
    private int speed = 3;

    private int spawnX, spawnY;

    private Direction direction = Direction.RIGHT;

    // animasi
    private int animTick = 0;
    private int animFrame = 0;
    private final int animSpeed = 12;

    private BufferedImage right1, right2;
    private BufferedImage left1, left2;
    private BufferedImage up1, up2;
    private BufferedImage down1, down2;

    public Player(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;

        this.spawnX = x;
        this.spawnY = y;

        loadSprites();
    }

    private void loadSprites() {
        try {
            right1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/right2.png"));

            left1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/left2.png"));

            up1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/up2.png"));

            down1 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/sprites/player/down2.png"));

            System.out.println("✅ Sprite player berhasil di-load!");

        } catch (Exception e) {
            System.out.println("❌ GAGAL load sprite animasi player! Pastikan path & nama file benar.");
            e.printStackTrace();
        }
    }

    public void update(CollisionManager collision) {
        int nextX = x + dx * speed;
        int nextY = y + dy * speed;

        boolean moving = (dx != 0 || dy != 0);

        if (moving) {
            animTick++;
            if (animTick >= animSpeed) {
                animTick = 0;
                animFrame = (animFrame + 1) % 2;
            }
        } else {
            animFrame = 0;
            animTick = 0;
        }

        if (collision.isWall(nextX, nextY, size)) {
            resetToCheckpoint();
        } else {
            x = nextX;
            y = nextY;
        }
    }

    public void render(Graphics g) {
        BufferedImage sprite = getCurrentSprite();

        // ✅ FIX: jika sprite null, tetap gambar kotak merah (biar player gak "hilang")
        if (sprite != null) {
            g.drawImage(sprite, x, y, size, size, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, size, size);
        }
    }

    private BufferedImage getCurrentSprite() {
        return switch (direction) {
            case RIGHT -> (animFrame == 0) ? right1 : right2;
            case LEFT  -> (animFrame == 0) ? left1 : left2;
            case UP    -> (animFrame == 0) ? up1 : up2;
            case DOWN  -> (animFrame == 0) ? down1 : down2;
        };
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;

        if (dx == 1 && dy == 0) direction = Direction.RIGHT;
        else if (dx == -1 && dy == 0) direction = Direction.LEFT;
        else if (dx == 0 && dy == -1) direction = Direction.UP;
        else if (dx == 0 && dy == 1) direction = Direction.DOWN;
    }

    public void resetToCheckpoint() {
        x = spawnX;
        y = spawnY;

        dx = 1;
        dy = 0;
        direction = Direction.RIGHT;

        animFrame = 0;
        animTick = 0;
    }
    
    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

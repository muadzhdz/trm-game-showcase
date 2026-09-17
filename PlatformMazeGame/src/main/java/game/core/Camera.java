package game.core;

public class Camera {

    private float x, y;
    private float smooth = 0.12f;

    private int worldWidth;
    private int worldHeight;

    public void setWorldSize(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void follow(float targetX, float targetY) {
        x += (targetX - x) * smooth;
        y += (targetY - y) * smooth;
        clamp();
    }

    public void snapTo(float targetX, float targetY) {
        x = targetX;
        y = targetY;
        clamp();
    }

    private void clamp() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        int maxX = Math.max(0, worldWidth - Game.WIDTH);
        int maxY = Math.max(0, worldHeight - Game.HEIGHT);

        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;
    }

    public int getX() { return (int) x; }
    public int getY() { return (int) y; }

    public void setSmooth(float smooth) {
        this.smooth = smooth;
    }
}

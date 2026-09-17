package game.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

public class GameLoop implements Runnable {

    private Thread thread;
    private boolean running = false;

    private final Game game;

    private final int FPS = 60;
    private final double nsPerFrame = 1000000000.0 / FPS;

    public GameLoop(Game game) {
        this.game = game;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "GameLoop-Thread");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;

        // ✅ buffer strategy (biar render halus)
        game.createBufferStrategy(3);
        BufferStrategy bs = game.getBufferStrategy();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                game.update();
                delta--;
            }

            game.renderToBuffer();

            Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();
            int screenW = game.getWidth();
            int screenH = game.getHeight();
            if (screenW <= 0) screenW = Game.WIDTH;
            if (screenH <= 0) screenH = Game.HEIGHT;

            // Clear full canvas with solid black
            g2.setColor(java.awt.Color.BLACK);
            g2.fillRect(0, 0, screenW, screenH);

            // Calculate aspect-ratio preserving scaling and centering
            double scale = Math.min((double) screenW / Game.WIDTH, (double) screenH / Game.HEIGHT);
            int scaledW = (int) Math.round(Game.WIDTH * scale);
            int scaledH = (int) Math.round(Game.HEIGHT * scale);
            int offsetX = (screenW - scaledW) / 2;
            int offsetY = (screenH - scaledH) / 2;

            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(game.getOffscreenBuffer(), offsetX, offsetY, scaledW, scaledH, null);

            g2.dispose();
            bs.show();

            // biar CPU ga 100%
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

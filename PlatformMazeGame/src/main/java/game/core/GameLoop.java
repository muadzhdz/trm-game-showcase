package game.core;

import java.awt.Graphics;
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

            Graphics g = bs.getDrawGraphics();
            g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

            game.render(g);

            g.dispose();
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

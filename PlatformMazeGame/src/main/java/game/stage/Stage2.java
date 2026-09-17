package game.stage;

import game.core.Camera;
import game.core.Game;
import game.core.Sound;
import game.entities.Diamond;
import game.entities.ExitDoor;
import game.entities.Player;
import game.map.CollisionManager;
import game.map.TileMap;
import game.ui.HUD;

import java.awt.Graphics;
import java.util.ArrayList;

public class Stage2 implements Stage {

    private TileMap tileMap;
    private Player player;
    private CollisionManager collision;
    private Camera camera;

    private ArrayList<Diamond> diamonds = new ArrayList<>();

    private ExitDoor startDoor1, startDoor2;
    private ExitDoor exitDoor1, exitDoor2;

    private int diamondCollected = 0;
    private final int diamondTarget = 4;

    private HUD hud = new HUD();

    private boolean stageClear = false;
    private boolean gameOver = false;

    private final int timeLimit = 300;
    private int timeLeft = timeLimit;
    private long lastTimerTick = System.currentTimeMillis();

    private boolean doorUnlockedSoundPlayed = false;

    public Stage2() {
        initStage();
    }

    private void initStage() {
        int tileSize = Game.TILE_SIZE * Game.SCALE;

        tileMap = new TileMap(
                "/maps/stage2.csv",
                "/tiles/stage2/floor.png",
                "/tiles/stage2/wall.png"
        );

        collision = new CollisionManager(tileMap.getMap(), tileSize);

        // spawn awal : (1,5)
        player = new Player(1 * tileSize, 5 * tileSize, tileSize);

        camera = new Camera();
        camera.setWorldSize(tileMap.getCols() * tileSize, tileMap.getRows() * tileSize);
        camera.snapTo(player.getX() - Game.WIDTH / 2, player.getY() - Game.HEIGHT / 2);

        diamonds.clear();
        addDiamondAtTile(29, 11);
        addDiamondAtTile(34, 23);
        addDiamondAtTile(32, 29);
        addDiamondAtTile(9, 23);
        diamondCollected = 0;

        // exit_open : (0,5) dan (0,6)
        startDoor1 = new ExitDoor(0 * tileSize, 5 * tileSize, tileSize, true);
        startDoor2 = new ExitDoor(0 * tileSize, 6 * tileSize, tileSize, true);

        // exit_locked : (5,35) dan (6,35)
        exitDoor1 = new ExitDoor(5 * tileSize, 35 * tileSize, tileSize, false);
        exitDoor2 = new ExitDoor(6 * tileSize, 35 * tileSize, tileSize, false);

        timeLeft = timeLimit;
        lastTimerTick = System.currentTimeMillis();

        stageClear = false;
        gameOver = false;
        doorUnlockedSoundPlayed = false;
    }

    private void addDiamondAtTile(int tileX, int tileY) {
        int tileSize = Game.TILE_SIZE * Game.SCALE;

        if (tileMap.getMap()[tileY][tileX] == 1) return;

        diamonds.add(new Diamond(tileX * tileSize, tileY * tileSize, tileSize));
    }

    private void updateTimer() {
        long now = System.currentTimeMillis();
        if (now - lastTimerTick >= 1000) {
            timeLeft--;
            lastTimerTick = now;

            if (timeLeft <= 0) {
                timeLeft = 0;
                gameOver = true;
            }
        }
    }

    @Override
    public void update() {
        if (stageClear) return;

        if (gameOver) {
            Sound.play("/audio/gameover.wav");
            initStage();
            return;
        }

        updateTimer();
        player.update(collision);

        camera.follow(
                player.getX() - Game.WIDTH / 2,
                player.getY() - Game.HEIGHT / 2
        );

        for (Diamond d : diamonds) {
            if (!d.isCollected() && player.getBounds().intersects(d.getBounds())) {
                d.collect();
                diamondCollected++;
                Sound.play("/audio/pickup.wav");
            }
        }

        if (diamondCollected >= diamondTarget && !doorUnlockedSoundPlayed) {
            exitDoor1.setUnlocked(true);
            exitDoor2.setUnlocked(true);
            doorUnlockedSoundPlayed = true;
            Sound.play("/audio/door_unlock.wav");
        }

        boolean hitExit =
                player.getBounds().intersects(exitDoor1.getBounds())
                        || player.getBounds().intersects(exitDoor2.getBounds());

        boolean unlocked = exitDoor1.isUnlocked() || exitDoor2.isUnlocked();

        if (unlocked && hitExit) stageClear = true;
    }

    @Override
    public void render(Graphics g) {
        int tileSize = Game.TILE_SIZE * Game.SCALE;

        g.translate(-camera.getX(), -camera.getY());

        tileMap.render(g, tileSize);

        for (Diamond d : diamonds) d.render(g);

        startDoor1.render(g);
        startDoor2.render(g);
        exitDoor1.render(g);
        exitDoor2.render(g);

        player.render(g);

        g.translate(camera.getX(), camera.getY());

        hud.render(g, diamondCollected, diamondTarget, timeLeft);

        if (stageClear) {
            g.setColor(java.awt.Color.YELLOW);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
            g.drawString("STAGE 2 CLEAR!", Game.WIDTH / 2 - 140, Game.HEIGHT / 2);
        }
    }

    @Override
    public boolean isClear() {
        return stageClear;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}

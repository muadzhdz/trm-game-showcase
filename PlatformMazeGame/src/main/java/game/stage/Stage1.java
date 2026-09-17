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

public class Stage1 implements Stage {

    private TileMap tileMap;
    private Player player;
    private CollisionManager collision;
    private Camera camera;

    private ArrayList<Diamond> diamonds = new ArrayList<>();

    private ExitDoor startDoor;
    private ExitDoor exitDoor;

    private int diamondCollected = 0;
    private final int diamondTarget = 3;

    private HUD hud = new HUD();

    private boolean stageClear = false;
    private boolean gameOver = false;

    // timer 5 menit
    private final int timeLimit = 300;
    private int timeLeft = timeLimit;
    private long lastTimerTick = System.currentTimeMillis();

    private boolean doorUnlockedSoundPlayed = false;

    public Stage1() {
        initStage();
    }

    private void initStage() {
        int tileSize = Game.TILE_SIZE * Game.SCALE;

        tileMap = new TileMap(
                "/maps/stage1.csv",
                "/tiles/stage1/floor.png",
                "/tiles/stage1/wall.png"
        );

        collision = new CollisionManager(tileMap.getMap(), tileSize);

        // ✅ spawn kiri bawah
        int startTileX = 1;
        int startTileY = tileMap.getRows() - 2;
        while (tileMap.getMap()[startTileY][startTileX] == 1) startTileX++;

        player = new Player(startTileX * tileSize, startTileY * tileSize, tileSize);

        // camera
        camera = new Camera();
        camera.setWorldSize(tileMap.getCols() * tileSize, tileMap.getRows() * tileSize);
        camera.snapTo(player.getX() - Game.WIDTH / 2, player.getY() - Game.HEIGHT / 2);

        // diamond
        diamonds.clear();
        addDiamondAtTile(19, 10);
        addDiamondAtTile(27, 24);
        addDiamondAtTile(49, 33);
        diamondCollected = 0;

        // doors
        startDoor = new ExitDoor(1 * tileSize, 35 * tileSize, tileSize, true);
        exitDoor = new ExitDoor(5 * tileSize, 0 * tileSize, tileSize, false);

        // timer reset
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

        // pickup diamond
        for (Diamond d : diamonds) {
            if (!d.isCollected() && player.getBounds().intersects(d.getBounds())) {
                d.collect();
                diamondCollected++;
                Sound.play("/audio/pickup.wav");
            }
        }

        // unlock exit
        if (diamondCollected >= diamondTarget && !doorUnlockedSoundPlayed) {
            exitDoor.setUnlocked(true);
            doorUnlockedSoundPlayed = true;
            Sound.play("/audio/door_unlock.wav");
        }

        // stage clear
        if (exitDoor.isUnlocked() && player.getBounds().intersects(exitDoor.getBounds())) {
            stageClear = true;
        }
    }

    @Override
    public void render(Graphics g) {
        int tileSize = Game.TILE_SIZE * Game.SCALE;

        g.translate(-camera.getX(), -camera.getY());

        tileMap.render(g, tileSize);
        for (Diamond d : diamonds) d.render(g);

        startDoor.render(g);
        exitDoor.render(g);

        player.render(g);

        g.translate(camera.getX(), camera.getY());

        hud.render(g, diamondCollected, diamondTarget, timeLeft);

        if (stageClear) {
            g.setColor(java.awt.Color.YELLOW);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
            g.drawString("STAGE CLEAR!", Game.WIDTH / 2 - 120, Game.HEIGHT / 2);
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

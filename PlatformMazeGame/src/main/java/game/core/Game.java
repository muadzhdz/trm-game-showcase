package game.core;

import game.input.Player1Input;
import game.stage.StageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends Canvas {

    public static final int TILE_SIZE = 16;
    public static final int SCALE = 3;

    public static final int WIDTH = 320 * SCALE;   // 960
    public static final int HEIGHT = 180 * SCALE;  // 540

    private JFrame frame;
    private GameLoop gameLoop;

    private StageManager stageManager;

    // ✅ pause state
    private GameState pauseState = GameState.PLAYING;

    // ✅ input
    private Player1Input player1Input;

    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        stageManager = new StageManager();

        player1Input = new Player1Input(stageManager.getPlayer());
        addKeyListener(player1Input);

        frame = new JFrame("Platform Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        setFocusable(true);
        requestFocusInWindow();

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                // ✅ WIN SCREEN CONTROL
                if (stageManager.isFinished()) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        stageManager.restart();
                        player1Input.setPlayer(stageManager.getPlayer());
                        pauseState = GameState.PLAYING;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.exit(0);
                    }
                    return;
                }

                // ✅ PAUSE
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    pauseState = (pauseState == GameState.PLAYING)
                            ? GameState.PAUSED
                            : GameState.PLAYING;
                }

                // ✅ Restart from pause
                if (pauseState == GameState.PAUSED && e.getKeyCode() == KeyEvent.VK_R) {
                    stageManager.restart();
                    player1Input.setPlayer(stageManager.getPlayer());
                    pauseState = GameState.PLAYING;
                }
            }
        });

        gameLoop = new GameLoop(this);
    }

    public void start() {
        gameLoop.start();
    }

    public void update() {
        // pause freeze
        if (pauseState == GameState.PAUSED) return;

        // finish freeze
        if (stageManager.isFinished()) return;

        stageManager.update();

        // stage pindah → update player input
        player1Input.setPlayer(stageManager.getPlayer());
    }

    public void render(Graphics g) {
        stageManager.render(g);

        // pause overlay
        if (pauseState == GameState.PAUSED && !stageManager.isFinished()) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PAUSED", WIDTH / 2 - 85, HEIGHT / 2 - 40);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("ESC = Resume", WIDTH / 2 - 80, HEIGHT / 2 + 10);
            g.drawString("R = Restart", WIDTH / 2 - 70, HEIGHT / 2 + 40);
        }
    }
}

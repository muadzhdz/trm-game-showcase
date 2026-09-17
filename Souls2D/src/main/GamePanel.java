package main;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

import entities.*;
import tile.TileManager;
import ui.MainMenu;
import ui.PauseMenu;
import ui.DialogueSystem;
import effects.DarknessEffect;
import states.GameProgress;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    final int originalTileSize = 64;
    final int scale = 1;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    
    // World settings (akan diatur oleh map)
    public int worldWidth;
    public int worldHeight;
    
    // Game thread
    private Thread gameThread;
    private final int FPS = 60;
    
    //camera
    private float cameraX, cameraY;
    private final float CAMERA_LERP = 0.1f;
    
    // Systems
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    
    // UI Systems
    public MainMenu mainMenu;
    public PauseMenu pauseMenu;
    public DialogueSystem dialogueSystem;
    
    // Effects
    public DarknessEffect darknessEffect;
    
    // Game State
    public GameStateManager gameState = GameStateManager.MAIN_MENU;
    public GameProgress gameProgress;
    
    // Entities
    public Player player;
    
    // Player camera
    public int screenX, screenY;
    
    // Font
    private Font gameFont;
	public Object soundManager;
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        
        setupGame();
        cameraX = 0;
        cameraY = 0;
    }
    
    private void setupGame() {
        // Load map
        tileM.loadMap("lantai1");
        
        // Set world dimensions based on map
        if (tileM.mapTileNum != null) {
            worldWidth = tileM.cols * tileSize;
            worldHeight = tileM.rows * tileSize;
            System.out.println("World size set to: " + worldWidth + "x" + worldHeight);
        } else {
            worldWidth = tileSize * 60;
            worldHeight = tileSize * 20;
            System.out.println("Using default world size: " + worldWidth + "x" + worldHeight);
        }
        
        // Initialize game progress
        gameProgress = new GameProgress(this);
        
        // Initialize player
        player = new Player(this, keyH);
        
        // Initialize UI systems
        mainMenu = new MainMenu(this);
        pauseMenu = new PauseMenu(this);
        dialogueSystem = new DialogueSystem(this);
        
        // Initialize effects
        darknessEffect = new DarknessEffect(this, player);
        
        // Set initial camera position
        screenX = Math.max(0, Math.min(player.worldX - screenWidth / 2, worldWidth - screenWidth));
        screenY = Math.max(0, Math.min(player.worldY - screenHeight / 2, worldHeight - screenHeight));
        
        // Place game objects
        aSetter.setObject();
        
        // Load font
        loadFont();
        
        System.out.println("Game setup complete");
        System.out.println("Current state: " + gameState);
    }
    
    public void startGame() {
        gameState = GameStateManager.CUTSCENE_OPENING;
        dialogueSystem.startOpeningCutscene();
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            
            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }
            
            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }
    
    public void update() {
        // Handle escape key for pause
        if (keyH.escapePressed) {
            if (gameState == GameStateManager.PLAYING) {
                gameState = GameStateManager.PAUSED;
                pauseMenu.resetSelection();
            } else if (gameState == GameStateManager.PAUSED) {
                gameState = GameStateManager.PLAYING;
            }
            keyH.escapePressed = false;
        }
        
        // Update based on current game state
        switch(gameState) {
            case MAIN_MENU:
                // Nothing to update in menu
                break;
                
            case PLAYING:
                updateGameplay();
                break;
                
            case PAUSED:
                // Pause menu handles its own updates via key listener
                break;
                
            case CUTSCENE_OPENING:
            case CUTSCENE_ENDING:
                // Dialogue system handles updates
                break;
                
            case GAME_OVER:
            case GAME_WIN:
                // Check for restart
                if (keyH.restartPressed) {
                    restartGame();
                    keyH.restartPressed = false;
                }
                break;
        }
    }
    
    private void updateGameplay() {
        player.update();
        
        // PERBAIKAN: Kamera mengikuti player dengan SMOOTHING
        int targetCameraX =
                player.worldX + tileSize / 2 - screenWidth / 2;
        int targetCameraY =
                player.worldY + tileSize / 2 - screenHeight / 2;
        
        // Smooth camera movement (optional - bisa dihapus jika ingin instant)
        cameraX += (targetCameraX - cameraX) * CAMERA_LERP;
        cameraY += (targetCameraY - cameraY) * CAMERA_LERP;
        
        // Update screen position (bisa pakai smoothing atau langsung)
        screenX = (int) cameraX; // Atau langsung: screenX = targetCameraX;
        screenY = (int) cameraY; // Atau langsung: screenY = targetCameraY;
        
        // Clamp camera to world boundaries
        screenX = Math.max(0, Math.min(screenX, worldWidth - screenWidth));
        screenY = Math.max(0, Math.min(screenY, worldHeight - screenHeight));
        
        // Update game progress
        gameProgress.update(player);
        
        // Update darkness effect
        darknessEffect.update();
        
        // Check win condition
        if (gameProgress.isGameWon()) {
            gameState = GameStateManager.GAME_WIN;
        }
        
        // Check game over condition
        if (gameProgress.isGameOver()) {
            gameState = GameStateManager.GAME_OVER;
        }
    }
    
    private java.awt.image.BufferedImage virtualBuffer;

    public Point getVirtualPoint(Point screenPoint) {
        if (screenPoint == null) return new Point(0, 0);
        int panelW = getWidth();
        int panelH = getHeight();
        if (panelW <= 0 || panelH <= 0) return screenPoint;
        
        double scale = Math.min((double) panelW / screenWidth, (double) panelH / screenHeight);
        int scaledW = (int) Math.round(screenWidth * scale);
        int scaledH = (int) Math.round(screenHeight * scale);
        int offsetX = (panelW - scaledW) / 2;
        int offsetY = (panelH - scaledH) / 2;
        
        int vx = (int) Math.round((screenPoint.x - offsetX) / scale);
        int vy = (int) Math.round((screenPoint.y - offsetY) / scale);
        return new Point(vx, vy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (virtualBuffer == null) {
            virtualBuffer = new java.awt.image.BufferedImage(screenWidth, screenHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }
        
        Graphics2D g2 = virtualBuffer.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw based on current game state
        switch(gameState) {
            case MAIN_MENU:
                mainMenu.draw(g2);
                break;
                
            case PLAYING:
                drawGameplay(g2);
                break;
                
            case PAUSED:
                drawGameplay(g2);
                pauseMenu.draw(g2);
                break;
                
            case CUTSCENE_OPENING:
            case CUTSCENE_ENDING:
                dialogueSystem.draw(g2);
                break;
                
            case GAME_OVER:
                drawGameplay(g2);
                drawGameOverScreen(g2);
                break;
                
            case GAME_WIN:
                drawGameplay(g2);
                drawWinScreen(g2);
                break;
        }
        
        g2.dispose();

        Graphics2D panelG2 = (Graphics2D) g;
        int panelW = getWidth();
        int panelH = getHeight();
        if (panelW <= 0) panelW = screenWidth;
        if (panelH <= 0) panelH = screenHeight;

        panelG2.setColor(Color.BLACK);
        panelG2.fillRect(0, 0, panelW, panelH);

        double scale = Math.min((double) panelW / screenWidth, (double) panelH / screenHeight);
        int scaledW = (int) Math.round(screenWidth * scale);
        int scaledH = (int) Math.round(screenHeight * scale);
        int offsetX = (panelW - scaledW) / 2;
        int offsetY = (panelH - scaledH) / 2;

        panelG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        panelG2.drawImage(virtualBuffer, offsetX, offsetY, scaledW, scaledH, null);
    }
    
    private void drawGameplay(Graphics2D g2) {
        // Draw tile map
        tileM.draw(g2);
        
        // Draw game progress (entities)
        gameProgress.draw(g2);
        
        // Draw player
        player.draw(g2);
        
        // Apply darkness effect
        darknessEffect.draw(g2);
        
        // Draw UI
        drawGameUI(g2);
    }
    
    private void drawGameUI(Graphics2D g2) {
        if (gameState != GameStateManager.PLAYING) return;
        
        // Draw stamina bar
        player.drawStaminaBar(g2);
        
        // Draw game progress info
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Background for text
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(5, 5, 120, 50);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Souls: " + gameProgress.getSoulsCollected() + "/3", 10, 25);
        g2.drawString("Delivered: " + gameProgress.getSoulsDelivered() + "/3", 10, 45);
        
        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("SHIFT: Sprint | ESC: Pause", 10, screenHeight - 30);
        
        // Sprint indicator
        if (player.isSprinting()) {
            g2.setColor(Color.YELLOW);
            g2.drawString("SPRINTING", screenWidth - 100, 30);
        }
    }
    
    private void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "GAME OVER";
        int x = getXForCenteredText(text, g2);
        g2.drawString(text, x, screenHeight / 2 - 50);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        text = "Press R to restart";
        x = getXForCenteredText(text, g2);
        g2.drawString(text, x, screenHeight / 2 + 50);
    }
    
    private void drawWinScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "ESCAPE SUCCESSFUL!";
        int x = getXForCenteredText(text, g2);
        g2.drawString(text, x, screenHeight / 2 - 50);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        text = "Press R to play again";
        x = getXForCenteredText(text, g2);
        g2.drawString(text, x, screenHeight / 2 + 50);
    }
    
    private int getXForCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }
    
    public void restartGame() {
        // Reset player
        player.setDefaultValues();
        
        // Reset game progress
        gameProgress.reset();
        
        // Reset camera
        screenX = Math.max(0, Math.min(player.worldX - screenWidth / 2, worldWidth - screenWidth));
        screenY = Math.max(0, Math.min(player.worldY - screenHeight / 2, worldHeight - screenHeight));
        
        // Replace objects
        aSetter.setObject();
        
        // Reset keys
        keyH.resetAll();
        
        // Start with opening cutscene
        gameState = GameStateManager.CUTSCENE_OPENING;
        dialogueSystem.startOpeningCutscene();
    }
    
    private void loadFont() {
        try {
            gameFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.PLAIN, 12f);
        } catch (Exception e) {
            gameFont = new Font("Arial", Font.PLAIN, 12);
        }
    }
 
}
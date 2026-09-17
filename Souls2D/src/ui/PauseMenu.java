package ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import main.GamePanel;
import main.GameStateManager;

public class PauseMenu {
    
    private GamePanel gp;
    private Font titleFont, optionFont;
    private String[] options = {"Resume", "Restart", "Main Menu", "Exit"};
    private int currentOption = 0;
    
    public PauseMenu(GamePanel gp) {
        this.gp = gp;
        loadFonts();
        setupKeyListener();
    }
    
    private void loadFonts() {
        try {
            titleFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.BOLD, 48f);
            
            optionFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.PLAIN, 32f);
        } catch (Exception e) {
            titleFont = new Font("Arial", Font.BOLD, 48);
            optionFont = new Font("Arial", Font.PLAIN, 32);
        }
    }
    
    private void setupKeyListener() {
        gp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gp.gameState == GameStateManager.PAUSED) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            currentOption--;
                            if (currentOption < 0) currentOption = options.length - 1;
                            gp.repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            currentOption = (currentOption + 1) % options.length;
                            gp.repaint();
                            break;
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_SPACE:
                            selectOption();
                            break;
                        case KeyEvent.VK_ESCAPE:
                            gp.gameState = GameStateManager.PLAYING;
                            gp.repaint();
                            break;
                    }
                }
            }
        });
    }
    
    private void selectOption() {
        switch(currentOption) {
            case 0: // Resume
                gp.gameState = GameStateManager.PLAYING;
                break;
            case 1: // Restart
                gp.restartGame();
                gp.gameState = GameStateManager.PLAYING;
                break;
            case 2: // Main Menu
                gp.gameState = GameStateManager.MAIN_MENU;
                break;
            case 3: // Exit
                System.exit(0);
                break;
        }
        gp.repaint();
    }
    
    public void draw(Graphics2D g2) {
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Title
        g2.setFont(titleFont);
        g2.setColor(new Color(200, 50, 50));
        
        String title = "PAUSED";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        int titleX = (gp.screenWidth - titleWidth) / 2;
        int titleY = 200;
        
        // Title shadow
        g2.setColor(Color.BLACK);
        g2.drawString(title, titleX + 3, titleY + 3);
        
        // Title main
        g2.setColor(new Color(200, 50, 50));
        g2.drawString(title, titleX, titleY);
        
        // Options
        g2.setFont(optionFont);
        
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int optionWidth = g2.getFontMetrics().stringWidth(option);
            int optionX = (gp.screenWidth - optionWidth) / 2;
            int optionY = 300 + i * 60;
            
            if (i == currentOption) {
                // Selected option
                g2.setColor(new Color(200, 50, 50));
                g2.drawString("> " + option + " <", optionX - 40, optionY);
            } else {
                // Normal option
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(option, optionX, optionY);
            }
        }
        
        // Instructions
        g2.setFont(optionFont.deriveFont(16f));
        g2.setColor(Color.LIGHT_GRAY);
        String instructions = "Use UP/DOWN to navigate, ENTER to select, ESC to resume";
        int instWidth = g2.getFontMetrics().stringWidth(instructions);
        g2.drawString(instructions, (gp.screenWidth - instWidth) / 2, 550);
    }
    
    public void resetSelection() {
        currentOption = 0;
    }
}
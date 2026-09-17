package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.GameStateManager;

public class MainMenu {
    
    private GamePanel gp;
    private BufferedImage background;
    private Font titleFont, buttonFont;
    private Rectangle playButton, exitButton;
    private boolean mouseOverPlay = false;
    private boolean mouseOverExit = false;
    
    public MainMenu(GamePanel gp) {
        this.gp = gp;
        loadImages();
        loadFonts();
        setupButtons();
        setupMouseListeners();
    }
    
    private void loadImages() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/ui/menu_background.png"));
        } catch (IOException | NullPointerException e) {
            // Create placeholder background
            background = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = background.createGraphics();
            
            // Dark gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 10, 30),
                gp.screenWidth, gp.screenHeight, new Color(30, 10, 50)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            
            // Add some spooky elements
            g2.setColor(new Color(100, 0, 0, 50));
            for (int i = 0; i < 20; i++) {
                int x = (int)(Math.random() * gp.screenWidth);
                int y = (int)(Math.random() * gp.screenHeight);
                int size = (int)(Math.random() * 30 + 10);
                g2.fillOval(x, y, size, size);
            }
            
            g2.dispose();
        }
    }
    
    private void loadFonts() {
        try {
            titleFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.BOLD, 72f);
            
            buttonFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.PLAIN, 36f);
        } catch (Exception e) {
            titleFont = new Font("Arial", Font.BOLD, 72);
            buttonFont = new Font("Arial", Font.PLAIN, 36);
        }
    }
    
    private void setupButtons() {
        int buttonWidth = 300;
        int buttonHeight = 80;
        int centerX = gp.screenWidth / 2 - buttonWidth / 2;
        
        playButton = new Rectangle(centerX, 350, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, 450, buttonWidth, buttonHeight);
    }
    
    private void setupMouseListeners() {
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gp.gameState == GameStateManager.MAIN_MENU) {
                    Point p = gp.getVirtualPoint(e.getPoint());
                    if (playButton.contains(p)) {
                        gp.startGame();
                    } else if (exitButton.contains(p)) {
                        System.exit(0);
                    }
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gp.gameState == GameStateManager.MAIN_MENU) {
                    Point p = gp.getVirtualPoint(e.getPoint());
                    mouseOverPlay = playButton.contains(p);
                    mouseOverExit = exitButton.contains(p);
                    gp.repaint();
                }
            }
        });
        
        gp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gp.gameState == GameStateManager.MAIN_MENU) {
                    Point p = gp.getVirtualPoint(e.getPoint());
                    mouseOverPlay = playButton.contains(p);
                    mouseOverExit = exitButton.contains(p);
                    gp.repaint();
                }
            }
        });
    }
    
    public void draw(Graphics2D g2) {
        // Draw background
        g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);
        
        // Title
        g2.setFont(titleFont);
        g2.setColor(new Color(200, 50, 50));
        
        // Draw play button
        drawButton(g2, playButton, "PLAY", mouseOverPlay);
        
        // Draw exit button
        drawButton(g2, exitButton, "EXIT", mouseOverExit);
        
        // Instructions
        g2.setFont(buttonFont.deriveFont(20f));
        g2.setColor(Color.LIGHT_GRAY);
        String instructions = "Collect souls and escape the darkness";
        int instWidth = g2.getFontMetrics().stringWidth(instructions);
        g2.drawString(instructions, (gp.screenWidth - instWidth) / 2, 300);
    }
    
    private void drawButton(Graphics2D g2, Rectangle button, String text, boolean hover) {
        // Button shadow
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(button.x + 3, button.y + 3, button.width, button.height, 20, 20);
        
        // Button background
        if (hover) {
            g2.setColor(new Color(200, 50, 50));
        } else {
            g2.setColor(new Color(100, 30, 30));
        }
        g2.fillRoundRect(button.x, button.y, button.width, button.height, 20, 20);
        
        // Button border
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(button.x, button.y, button.width, button.height, 20, 20);
        
        // Button text
        g2.setFont(buttonFont);
        g2.setColor(Color.WHITE);
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int textX = button.x + (button.width - textWidth) / 2;
        int textY = button.y + (button.height + g2.getFontMetrics().getAscent()) / 2 - 5;
        
        // Text shadow
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX + 2, textY + 2);
        
        // Text main
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }
}
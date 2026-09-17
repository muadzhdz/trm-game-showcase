package ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import main.GamePanel;
import main.GameStateManager;

public class DialogueSystem {
    
    private GamePanel gp;
    private Font dialogueFont;
    private List<String> currentDialogue;
    private int currentLine = 0;
    private boolean dialogueActive = false;
    private boolean skipRequested = false;
    
    // Opening dialogue
    private List<String> openingDialogue = new ArrayList<>();
    
    // Ending dialogue
    private List<String> endingDialogue = new ArrayList<>();
    
    public DialogueSystem(GamePanel gp) {
        this.gp = gp;
        loadFont();
        setupDialogues();
        setupKeyListener();
    }
    
    private void loadFont() {
        try {
            dialogueFont = Font.createFont(
                Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/font/slkscrb.ttf")
            ).deriveFont(Font.PLAIN, 24f);
        } catch (Exception e) {
            dialogueFont = new Font("Arial", Font.PLAIN, 24);
        }
    }
    
    private void setupDialogues() {
        // Opening dialogue
        openingDialogue.add("The abandoned mansion whispers with lost souls...");
        openingDialogue.add("Three tormented spirits wander these halls, trapped between worlds.");
        openingDialogue.add("Find their souls and guide them to their altars.");
        openingDialogue.add("Only then will the door of escape reveal itself...");
        openingDialogue.add("But beware... darkness hunts those who linger.");
        
        // Ending dialogue
        endingDialogue.add("The final soul finds peace...");
        endingDialogue.add("A spectral door materializes before you.");
        endingDialogue.add("The whispers fade, replaced by an eerie silence.");
        endingDialogue.add("You step through the threshold...");
        endingDialogue.add("...and escape the eternal darkness.");
    }
    
    private void setupKeyListener() {
        gp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (dialogueActive) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || 
                        e.getKeyCode() == KeyEvent.VK_SPACE ||
                        e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        
                        nextLine();
                    }
                }
            }
        });
    }
    
    public void startOpeningCutscene() {
        currentDialogue = openingDialogue;
        currentLine = 0;
        dialogueActive = true;
        gp.gameState = GameStateManager.CUTSCENE_OPENING;
    }
    
    public void startEndingCutscene() {
        currentDialogue = endingDialogue;
        currentLine = 0;
        dialogueActive = true;
        gp.gameState = GameStateManager.CUTSCENE_ENDING;
    }
    
    private void nextLine() {
        if (skipRequested) {
            skipRequested = false;
            endDialogue();
            return;
        }
        
        currentLine++;
        if (currentLine >= currentDialogue.size()) {
            endDialogue();
        }
    }
    
    private void endDialogue() {
        dialogueActive = false;
        
        if (gp.gameState == GameStateManager.CUTSCENE_OPENING) {
            gp.gameState = GameStateManager.PLAYING;
        } else if (gp.gameState == GameStateManager.CUTSCENE_ENDING) {
            gp.gameState = GameStateManager.GAME_WIN;
        }
        
        gp.repaint();
    }
    
    public void draw(Graphics2D g2) {
        if (!dialogueActive) return;
        
        // Black background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Dialogue box
        int boxWidth = gp.screenWidth - 100;
        int boxHeight = 200;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = gp.screenHeight - boxHeight - 50;
        
        // Box background
        g2.setColor(new Color(30, 30, 30, 220));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        
        // Box border
        g2.setColor(new Color(200, 50, 50));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        
        // Text
        g2.setFont(dialogueFont);
        g2.setColor(Color.WHITE);
        
        String line = currentDialogue.get(currentLine);
        drawWrappedText(g2, line, boxX + 40, boxY + 60, boxWidth - 80);
        
        // Continue prompt
        g2.setFont(dialogueFont.deriveFont(18f));
        g2.setColor(Color.LIGHT_GRAY);
        String prompt = "Press ENTER to continue...";
        int promptWidth = g2.getFontMetrics().stringWidth(prompt);
        g2.drawString(prompt, boxX + (boxWidth - promptWidth) / 2, boxY + boxHeight - 30);
        
        // Page indicator
        String page = (currentLine + 1) + "/" + currentDialogue.size();
        int pageWidth = g2.getFontMetrics().stringWidth(page);
        g2.drawString(page, boxX + boxWidth - pageWidth - 30, boxY + boxHeight - 30);
    }
    
    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        String currentLine = "";
        int lineHeight = fm.getHeight();
        int currentY = y;
        
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int testWidth = fm.stringWidth(testLine);
            
            if (testWidth > maxWidth && !currentLine.isEmpty()) {
                g2.drawString(currentLine, x, currentY);
                currentY += lineHeight;
                currentLine = word;
            } else {
                currentLine = testLine;
            }
        }
        
        if (!currentLine.isEmpty()) {
            g2.drawString(currentLine, x, currentY);
        }
    }
    
    public boolean isActive() {
        return dialogueActive;
    }
}
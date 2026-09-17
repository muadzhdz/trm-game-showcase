package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KeyHandler extends KeyAdapter {
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean shiftPressed, spacePressed, escapePressed, enterPressed;
    public boolean interactPressed, restartPressed; // <-- INTERACT untuk altar
    public boolean ctrlPressed;
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        switch(code) {
            // Movement
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
                
            // Actions
            case KeyEvent.VK_SHIFT:
                shiftPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = true;
                break;
            case KeyEvent.VK_E:
                interactPressed = true; // <-- TOMBOL E UNTUK INTERAKSI
                System.out.println("INTERACT KEY (E) PRESSED!");
                break;
            case KeyEvent.VK_R:
                restartPressed = true;
                break;
            case KeyEvent.VK_CONTROL:
                ctrlPressed = true;
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        switch(code) {
            // Movement
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
                
            // Actions
            case KeyEvent.VK_SHIFT:
                shiftPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = false;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = false;
                break;
            case KeyEvent.VK_E:
                interactPressed = false; // <-- RESET INTERACT
                break;
            case KeyEvent.VK_R:
                restartPressed = false;
                break;
            case KeyEvent.VK_CONTROL:
                ctrlPressed = false;
                break;
        }
    }
    
    public void resetAll() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        shiftPressed = false;
        spacePressed = false;
        escapePressed = false;
        enterPressed = false;
        interactPressed = false; // JANGAN LUPA!
        restartPressed = false;
        ctrlPressed = false;
    }
}
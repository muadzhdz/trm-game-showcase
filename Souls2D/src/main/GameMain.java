package main;

import javax.swing.JFrame;

public class GameMain {
    
    public static void main(String[] args) {
        JFrame window = new JFrame("Soul Escape - Horror Adventure");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(java.awt.Color.BLACK);
        window.setResizable(true);
        
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        gamePanel.startGameThread();
    }
}
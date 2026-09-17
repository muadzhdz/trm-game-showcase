/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.core;

/**
 *
 * @author nabildava
 */
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();

        JFrame frame = new JFrame("Platform Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }
}

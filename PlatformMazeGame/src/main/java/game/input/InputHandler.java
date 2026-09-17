/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.input;

/**
 *
 * @author nabildava
 */

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import game.entities.Player;

public class InputHandler extends KeyAdapter {

    private Player player;

    public InputHandler(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            // PLAYER 1: A & D
            case KeyEvent.VK_A -> player.setDirection(-1, 0);
            case KeyEvent.VK_D -> player.setDirection(1, 0);

            // PLAYER 2: UP & DOWN
            case KeyEvent.VK_UP -> player.setDirection(0, -1);
            case KeyEvent.VK_DOWN -> player.setDirection(0, 1);
        }
    }
}

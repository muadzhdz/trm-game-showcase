package game.input;

import game.entities.Player;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Player1Input extends KeyAdapter {

    private Player player;

    public Player1Input(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (player == null) return;

        int key = e.getKeyCode();

        // ✅ sesuai request kamu:
        // A & D = kiri kanan
        // Up & Down = atas bawah
        if (key == KeyEvent.VK_A) {
            player.setDirection(-1, 0);
        } else if (key == KeyEvent.VK_D) {
            player.setDirection(1, 0);
        } else if (key == KeyEvent.VK_UP) {
            player.setDirection(0, -1);
        } else if (key == KeyEvent.VK_DOWN) {
            player.setDirection(0, 1);
        }
    }
}

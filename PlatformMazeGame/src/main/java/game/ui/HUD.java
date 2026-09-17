package game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HUD {

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public void render(Graphics g, int collected, int total, int timeLeft) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        g.drawString("DIAMOND: " + collected + "/" + total, 20, 30);
        g.drawString("TIME: " + formatTime(timeLeft), 20, 55);
    }
}

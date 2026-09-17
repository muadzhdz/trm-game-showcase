package game.stage;

import game.core.Sound;
import java.awt.Graphics;

public class StageManager {

    private int stageIndex = 1;
    private Stage currentStage;

    private boolean winSoundPlayed = false;

    public StageManager() {
        loadStage(stageIndex);
    }

    private void loadStage(int index) {
        switch (index) {
            case 1 -> currentStage = new Stage1();
            case 2 -> currentStage = new Stage2();
            case 3 -> currentStage = new Stage3();
            default -> currentStage = null; // tamat
        }
    }

    public void update() {
        if (currentStage == null) return;

        currentStage.update();

        if (currentStage.isClear()) {
            stageIndex++;
            loadStage(stageIndex);

            if (currentStage == null && !winSoundPlayed) {
                Sound.play("/audio/win.wav");
                winSoundPlayed = true;
            }
        }
    }

    public void render(Graphics g) {
        if (currentStage == null) {
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, game.core.Game.WIDTH, game.core.Game.HEIGHT);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 44));
            g.setColor(java.awt.Color.YELLOW);
            g.drawString("YOU WIN!", 340, 230);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g.setColor(java.awt.Color.WHITE);
            g.drawString("ENTER = Restart", 380, 285);
            g.drawString("ESC = Exit", 400, 315);

            return;
        }

        currentStage.render(g);
    }

    public void restart() {
        stageIndex = 1;
        loadStage(stageIndex);
        winSoundPlayed = false;
    }

    public boolean isFinished() {
        return currentStage == null;
    }

    public game.entities.Player getPlayer() {
        if (currentStage == null) return null;
        return currentStage.getPlayer();
    }
}

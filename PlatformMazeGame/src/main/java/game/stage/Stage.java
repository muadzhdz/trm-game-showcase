package game.stage;

import java.awt.Graphics;
import game.entities.Player;

public interface Stage {
    void update();
    void render(Graphics g);

    boolean isClear();

    // ✅ supaya input bisa selalu ambil player stage aktif
    Player getPlayer();
}

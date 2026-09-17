package tile;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public boolean collision = false;
    public boolean isDoor = false;
    public int doorId = -1;
    public boolean isSpawnPoint = false;
    public int spawnId = -1;
    public boolean isSpawn = false;
    public Rectangle solidArea;
    
    // Constructor
    public Tile() {
        // Default: seluruh tile adalah solid area
        solidArea = new Rectangle(0, 0, 0, 0); // atau null jika tidak digunakan
    }
}

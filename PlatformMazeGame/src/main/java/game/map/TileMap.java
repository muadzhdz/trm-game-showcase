package game.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TileMap {

    private int[][] map;
    private int rows;
    private int cols;

    // ✅ tileset per stage
    private BufferedImage floorImg;
    private BufferedImage wallImg;

    /**
     * Constructor lama (kalau masih mau support default)
     */
    public TileMap(String csvPath) {
        this(csvPath, "/tiles/stage1/floor.png", "/tiles/stage1/wall.png");
    }

    /**
     * ✅ Constructor baru: CSV + floor/wall path
     */
    public TileMap(String csvPath, String floorPath, String wallPath) {
        loadCSV(csvPath);
        loadTiles(floorPath, wallPath);
    }

    private void loadTiles(String floorPath, String wallPath) {
        try {
            floorImg = ImageIO.read(getClass().getResourceAsStream(floorPath));
            wallImg = ImageIO.read(getClass().getResourceAsStream(wallPath));
            System.out.println("✅ Tiles loaded: " + floorPath + " | " + wallPath);
        } catch (Exception e) {
            System.out.println("❌ Gagal load tiles! Cek path tiles di resources.");
            e.printStackTrace();
        }
    }

    private void loadCSV(String csvPath) {
        try {
            InputStream is = getClass().getResourceAsStream(csvPath);
            if (is == null) throw new RuntimeException("CSV map tidak ditemukan: " + csvPath);

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // baca semua baris dulu
            java.util.List<String[]> lines = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.split(","));
            }

            rows = lines.size();
            cols = lines.get(0).length;

            map = new int[rows][cols];

            for (int y = 0; y < rows; y++) {
                if (lines.get(y).length != cols)
                    throw new RuntimeException("Panjang baris CSV tidak konsisten di baris: " + y);

                for (int x = 0; x < cols; x++) {
                    map[y][x] = Integer.parseInt(lines.get(y)[x].trim());
                }
            }

            br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tile value:
     * 0 = floor
     * 1 = wall
     */
    public void render(Graphics g, int tileSize) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                int tile = map[y][x];

                if (tile == 1) {
                    if (wallImg != null)
                        g.drawImage(wallImg, x * tileSize, y * tileSize, tileSize, tileSize, null);
                } else {
                    if (floorImg != null)
                        g.drawImage(floorImg, x * tileSize, y * tileSize, tileSize, tileSize, null);
                }
            }
        }
    }

    public int[][] getMap() {
        return map;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}

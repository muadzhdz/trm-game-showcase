package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {

    public Tile[] tile;
    public int[][] mapTileNum;
    public String currentMap = "";

    GamePanel gp;
    public int rows;
    public int cols;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[37];
        initializeTiles();
        getTileImage();
    }
    
    public static final int TILE_FLOOR = 1;
    public static final int MARKER_ALTAR_A = 7;
    public static final int MARKER_ALTAR_B = 8;
    public static final int MARKER_ALTAR_C = 9;


    private void initializeTiles() {
        for (int i = 0; i < tile.length; i++) {
            tile[i] = new Tile();
        }
    }

    public void getTileImage() {
        try {
            // ================= VOID =================
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Void.png"));
            tile[0].collision = true; // Void juga tidak bisa dilalui

            // ================= FLOOR =================
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Floor.png"));
            tile[1].isSpawn = true; // HANYA tile 1 yang boleh spawn

            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Floor2.png"));
            tile[2].isSpawn = true;
            
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorBT.png"));
            tile[3].isSpawn = true;
            
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorFT.png"));
            tile[4].isSpawn = true;
            
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorLT.png"));
            tile[5].isSpawn = true;
            
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorRT.png"));
            tile[6].isSpawn = true;
            
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorT4.png"));
            tile[10].isSpawn = true;
            
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorT5.png"));
            tile[11].isSpawn = true;
            
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FloorT6.png"));
            tile[12].isSpawn = true;

            // ================= WALL =================
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallFront.png"));
            tile[13].collision = true;
            
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallFrontDown.png"));
            tile[14].collision = true;
            
            tile[15].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallVoid.png"));
            tile[15].collision = true;
            
            tile[16].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallLEdgeUp.png"));
            tile[16].collision = true;
            
            tile[17].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallLEdgeDown.png"));
            tile[17].collision = true;
            
            tile[18].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallREdgeUp.png"));
            tile[18].collision = true;
            
            tile[19].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallREdgeDown.png"));
            tile[19].collision = true;
            
            tile[20].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallLSideBUp.png"));
            tile[20].collision = true;
            
            tile[21].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallLSideBDown.png"));
            tile[21].collision = true;
            
            tile[22].image = ImageIO.read(getClass().getResourceAsStream("/tiles/WallRSideBDown.png"));
            tile[22].collision = true;
            
            tile[35].image = ImageIO.read(getClass().getResourceAsStream("/tiles/RSideBUp.png"));
            tile[35].collision = true;
            
            tile[36].image = ImageIO.read(getClass().getResourceAsStream("/tiles/RSideBDown.png"));
            tile[36].collision = true;

            // ================= DOOR =================
            tile[23].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FrontDoor1.png"));
            tile[23].isDoor = true;
            tile[23].collision = true;
            
            tile[24].image = ImageIO.read(getClass().getResourceAsStream("/tiles/FrontDoor2.png"));
            tile[24].isDoor = true;
            tile[24].collision = true;
            
            tile[25].image = ImageIO.read(getClass().getResourceAsStream("/tiles/DoorDoubleL1.png"));
            tile[25].isDoor = true;
            tile[25].collision = true;
            
            tile[26].image = ImageIO.read(getClass().getResourceAsStream("/tiles/DoorDoubleL2.png"));
            tile[26].isDoor = true;
            tile[26].collision = true;
            
            tile[27].image = ImageIO.read(getClass().getResourceAsStream("/tiles/DoorDoubleR1.png"));
            tile[27].isDoor = true;
            tile[27].collision = true;
            
            tile[28].image = ImageIO.read(getClass().getResourceAsStream("/tiles/DoorDoubleR2.png"));
            tile[28].isDoor = true;
            tile[28].collision = true;
            
            tile[29].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BottomDoorLeft.png"));
            tile[29].isDoor = true;
            tile[29].collision = true;
            
            tile[30].image = ImageIO.read(getClass().getResourceAsStream("/tiles/BottomDoorRight.png"));
            tile[30].isDoor = true;
            tile[30].collision = true;
            
            tile[31].image = ImageIO.read(getClass().getResourceAsStream("/tiles/LSideBDoor.png"));
            tile[31].isDoor = true;
            tile[31].collision = true;
            
            tile[32].image = ImageIO.read(getClass().getResourceAsStream("/tiles/LSideBDoor1.png"));
            tile[32].isDoor = true;
            tile[32].collision = true;
            
            tile[33].image = ImageIO.read(getClass().getResourceAsStream("/tiles/RSideBDoor1.png"));
            tile[33].isDoor = true;
            tile[33].collision = true;
            
            tile[34].image = ImageIO.read(getClass().getResourceAsStream("/tiles/RSideBDoor2.png"));
            tile[34].isDoor = true;
            tile[34].collision = true;
            


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading tile images: " + e.getMessage());
        }
    }

    // ================= LOAD MAP =================
    public void loadMap(String mapName) {
        currentMap = mapName;
        List<int[]> rowsList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream("/maps/" + mapName + ".bg.txt");
            if (is == null) {
                System.err.println("Map file not found: /maps/" + mapName + ".bg.txt");
                createDefaultMap();
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            int row = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] nums = line.split("\\s+");
                int[] mapRow = new int[nums.length];

                for (int col = 0; col < nums.length; col++) {
                    int tileNum;

                    try {
                        tileNum = Integer.parseInt(nums[col]);
                    } catch (NumberFormatException e) {
                        tileNum = 0;
                    }

                    // ===== ALTAR MARKER PROCESS =====
                    if (tileNum == MARKER_ALTAR_A ||
                        tileNum == MARKER_ALTAR_B ||
                        tileNum == MARKER_ALTAR_C) {

                        int altarType = tileNum - MARKER_ALTAR_A;

                        int worldX = col * gp.tileSize;
                        int worldY = row * gp.tileSize;

                        gp.gameProgress.addAltar(
                            new entities.Altar(gp, worldX, worldY, altarType)
                        );

                        System.out.println(
                            "[MAP] ALTAR " + altarType +
                            " spawned at tile (" + col + "," + row + ")"
                        );

                        // Marker berubah jadi floor
                        tileNum = TILE_FLOOR;
                    }

                    // ===== TILE VALIDATION =====
                    if (tileNum < 0 || tileNum >= tile.length) {
                        tileNum = 0;
                    }

                    mapRow[col] = tileNum;
                }

                rowsList.add(mapRow);
                row++;
            }

            br.close();

            if (rowsList.isEmpty()) {
                System.err.println("Map file is empty, creating default map");
                createDefaultMap();
                return;
            }

            rows = rowsList.size();
            cols = rowsList.get(0).length;
            mapTileNum = new int[rows][cols];

            for (int r = 0; r < rows; r++) {
                int[] rowData = rowsList.get(r);
                for (int c = 0; c < cols; c++) {
                    mapTileNum[r][c] = (c < rowData.length) ? rowData[c] : 0;
                }
            }

            System.out.println("Map loaded: " + mapName + " (" + cols + "x" + rows + ")");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading map: " + mapName);
            createDefaultMap();
        }
    }

    
    private void createDefaultMap() {
        // Create a simple default map dengan beberapa tile 1 untuk spawn
        rows = 20;
        cols = 60;
        mapTileNum = new int[rows][cols];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    // Border walls
                    mapTileNum[r][c] = 13;
                } else {
                    // Floor area
                    mapTileNum[r][c] = 1;
                }
            }
        }
        
        System.out.println("Created default map with floor tiles for spawn");
    }

    // ================= DRAW =================
    public void draw(Graphics2D g2) {
        if (mapTileNum == null) {
            System.err.println("Map not loaded!");
            return;
        }
        
        // Calculate visible area based on camera position
        int startRow = Math.max(0, gp.screenY / gp.tileSize);
        int endRow = Math.min(rows, (gp.screenY + gp.screenHeight) / gp.tileSize + 1);
        int startCol = Math.max(0, gp.screenX / gp.tileSize);
        int endCol = Math.min(cols, (gp.screenX + gp.screenWidth) / gp.tileSize + 1);
        
        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                int tileNum = mapTileNum[r][c];
                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum].image != null) {
                    int worldX = c * gp.tileSize;
                    int worldY = r * gp.tileSize;
                    
                    // Convert world coordinates to screen coordinates
                    int screenX = worldX - gp.screenX;
                    int screenY = worldY - gp.screenY;
                    
                    // Only draw if on screen
                    if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                        screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
                        
                        g2.drawImage(
                            tile[tileNum].image,
                            screenX,
                            screenY,
                            gp.tileSize,
                            gp.tileSize,
                            null
                        );
                    }
                }
            }
        }
    }
    
    // ================= GET SPAWN POINT =================
    public int[] findSpawnPoint() {
        if (mapTileNum == null) {
            System.err.println("Map not loaded, using default spawn");
            return new int[]{gp.tileSize * 3, gp.tileSize * 3};
        }
        
        System.out.println("=== FINDING PLAYER SPAWN POINT ===");
        System.out.println("Map size: " + cols + "x" + rows);
        
        // PERBAIKAN: Spawn di dekat altar paling kiri (room kiri)
        // Cari tile 1 di room kiri (kolom 2-20, baris 2-16)
        
        // Area room kiri berdasarkan map Anda
        int leftRoomMinCol = 2;
        int leftRoomMaxCol = 20;
        int leftRoomMinRow = 2;
        int leftRoomMaxRow = 16;
        
        // Priority 1: Cari tile di sekitar altar kiri (jika ada)
        // Asumsikan altar kiri ada di sekitar tengah room kiri
        int altarLeftCol = (leftRoomMinCol + leftRoomMaxCol) / 2;
        int altarLeftRow = (leftRoomMinRow + leftRoomMaxRow) / 2;
        
        System.out.println("Searching near altar left at tile: (" + altarLeftCol + "," + altarLeftRow + ")");
        
        // Cari tile 1 di radius 3 tiles dari altar kiri
        for (int radius = 1; radius <= 5; radius++) {
            for (int dr = -radius; dr <= radius; dr++) {
                for (int dc = -radius; dc <= radius; dc++) {
                    int col = altarLeftCol + dc;
                    int row = altarLeftRow + dr;
                    
                    if (col >= leftRoomMinCol && col <= leftRoomMaxCol &&
                        row >= leftRoomMinRow && row <= leftRoomMaxRow &&
                        col >= 0 && col < cols && row >= 0 && row < rows) {
                        
                        int tileNum = mapTileNum[row][col];
                        if (tileNum == 1) { // HANYA tile 1
                            // Cek area 2x2 di sekitar spawn point bebas collision
                            if (isValidSpawnArea(col, row)) {
                                System.out.println("✓ Found spawn near altar left at tile: (" + col + "," + row + ")");
                                return new int[]{col * gp.tileSize + gp.tileSize/2, 
                                               row * gp.tileSize + gp.tileSize/2};
                            }
                        }
                    }
                }
            }
        }
        
        // Priority 2: Cari tile 1 mana saja di room kiri
        System.out.println("Searching any tile in left room...");
        for (int row = leftRoomMinRow; row <= leftRoomMaxRow; row++) {
            for (int col = leftRoomMinCol; col <= leftRoomMaxCol; col++) {
                if (row >= 0 && row < rows && col >= 0 && col < cols) {
                    int tileNum = mapTileNum[row][col];
                    if (tileNum == 1) {
                        if (isValidSpawnArea(col, row)) {
                            System.out.println("✓ Found spawn in left room at tile: (" + col + "," + row + ")");
                            return new int[]{col * gp.tileSize + gp.tileSize/2, 
                                           row * gp.tileSize + gp.tileSize/2};
                        }
                    }
                }
            }
        }
        
        // Priority 3: Cari tile 1 mana saja di map
        System.out.println("Searching any tile in entire map...");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int tileNum = mapTileNum[row][col];
                if (tileNum == 1) {
                    if (isValidSpawnArea(col, row)) {
                        System.out.println("✓ Found spawn at tile: (" + col + "," + row + ")");
                        return new int[]{col * gp.tileSize + gp.tileSize/2, 
                                       row * gp.tileSize + gp.tileSize/2};
                    }
                }
            }
        }
        
        // Default spawn
        System.out.println("✗ No valid spawn found, using default");
        return new int[]{gp.tileSize * 3, gp.tileSize * 3};
    }

    private boolean isValidSpawnArea(int col, int row) {
        // Cek area 2x2 di sekitar spawn point
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int checkCol = col + dc;
                int checkRow = row + dr;
                
                if (checkRow < 0 || checkRow >= rows || checkCol < 0 || checkCol >= cols) {
                    return false; // Out of bounds
                }
                
                int tileNum = mapTileNum[checkRow][checkCol];
                if (tileNum != 1) { // Bukan tile 1
                    return false;
                }
            }
        }
        return true;
    }
    
    // ================= CHECK COLLISION =================
    public boolean isCollisionAt(int worldX, int worldY) {
        if (mapTileNum == null) return true;
        
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            return true; // Out of bounds
        }
        
        int tileNum = mapTileNum[row][col];
        if (tileNum < 0 || tileNum >= tile.length) {
            return true; // Invalid tile
        }
        
        return tile[tileNum].collision;
    }
}
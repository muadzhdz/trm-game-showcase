package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.*;
import tile.TileManager;

public class AssetSetter {
    
    GamePanel gp;
    private List<Room> rooms;
    private Random random;
    
    private List<int[]> altarMarkers = new ArrayList<>();
    
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        this.rooms = new ArrayList<>();
        this.random = new Random();
    }
    
    public void setObject() {
        // 1. Deteksi rooms dari map
        detectRooms();
        
        // 2. Clear existing objects
        clearAllObjects();
        
        // 3. Place objects di setiap room
        placeObjectsInRooms();
        
        // 4. Place exit door di kanan atas (3 tile ke kiri)
        placeExitDoor();
        
        detectRooms();
        
        printPlacementSummary();
    }
   

    
    private void detectRooms() {
        rooms.clear();
        
        if (gp.tileM.mapTileNum == null) {
            System.out.println("ERROR: Map not loaded!");
            return;
        }
        
        int[][] map = gp.tileM.mapTileNum;
        int rows = gp.tileM.rows;
        int cols = gp.tileM.cols;
        
        // Identifikasi 3 big rooms berdasarkan map Anda
        Room[] detectedRooms = new Room[3];
        
        // Room 1: Kiri (kolom 2-20, baris 2-16)
        detectedRooms[0] = new Room(0, "Room Kiri", 2, 20, 2, 16);
        
        // Room 2: Tengah (kolom 22-40, baris 2-16)
        detectedRooms[1] = new Room(1, "Room Tengah", 22, 40, 2, 16);
        
        // Room 3: Kanan (kolom 42-60, baris 2-16)
        detectedRooms[2] = new Room(2, "Room Kanan", 42, 60, 2, 16);
        
        // Kumpulkan semua floor tiles di setiap room
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int tileNum = map[row][col];
                
                // PERBAIKAN: Hanya tile 1 yang bisa ditempati soul/altar
                boolean isFloor = (tileNum == 1); // HANYA tile 1 (Floor.png)
                
                if (isFloor) {
                    // Cek room mana yang mengandung tile ini
                    for (Room room : detectedRooms) {
                        if (room != null && room.contains(col, row)) {
                            room.addTile(col, row);
                            break;
                        }
                    }
                }
            }
        }
        
        // Tambahkan rooms yang memiliki cukup floor tiles
        for (Room room : detectedRooms) {
            if (room != null && room.getFloorTilesCount() >= 20) { // Minimal 20 tile floor
                rooms.add(room);
                System.out.println("Room " + room.id + " (" + room.name + "): " + 
                                 room.getFloorTilesCount() + " floor tiles");
            }
        }
        
        System.out.println("Detected " + rooms.size() + " big rooms");
    }
    
    private void clearAllObjects() {
        gp.gameProgress.getSouls().clear();
        gp.gameProgress.getAltars().clear();
        gp.gameProgress.getEnemies().clear();
        gp.gameProgress.getDoors().clear();
    }
    
    private void placeObjectsInRooms() {
        for (Room room : rooms) {
            System.out.println("\n=== Placing objects in " + room.name + " ===");
            
            // 1. Place Altar di tengah room
            int[] altarPos = placeAltarInRoom(room);
            
            // 2. Place Soul di random location dalam room
            placeSoulInRoom(room, altarPos);
            
            // 3. Place 3 ENEMIES per room (dari 2 menjadi 3)
            for (int i = 0; i < 3; i++) {
                placeEnemyInRoom(room, altarPos, i);
            }
            
            System.out.println("  Total: 1 Altar, 1 Soul, 3 Enemies placed");
        }
    }
    
    private Door spawnDoorWithOffset(int col, int row, int offsetCol, int offsetRow) {
        int worldX = (col + offsetCol) * gp.tileSize;
        int worldY = (row + offsetRow) * gp.tileSize;

        System.out.println(
            "DEBUG: Door spawned at tile (" +
            (col + offsetCol) + "," + (row + offsetRow) + ")"
        );

        return new Door(gp, worldX, worldY);
    }


    
    private int[] placeAltarInRoom(Room room) {
        int[] centerTile = room.getCenterTile();
        
        if (centerTile == null) {
            System.out.println("ERROR: Cannot find center tile for altar in " + room.name);
            return new int[]{0, 0};
        }
        
        int col = centerTile[0];
        int row = centerTile[1];
        
        // Verifikasi tile adalah tile 1
        if (isValidTileForObject(col, row)) {
            Altar altar = new Altar(gp, col * gp.tileSize, row * gp.tileSize, room.altarType);
            gp.gameProgress.addAltar(altar);
            System.out.println("  ✓ Altar type " + room.altarType + 
                             " placed at tile (" + col + "," + row + ")");
            return new int[]{col, row};
        } else {
            // Cari tile 1 terdekat
            for (int distance = 1; distance <= 5; distance++) {
                for (int dr = -distance; dr <= distance; dr++) {
                    for (int dc = -distance; dc <= distance; dc++) {
                        int testCol = col + dc;
                        int testRow = row + dr;
                        
                        if (isValidTileForObject(testCol, testRow)) {
                            Altar altar = new Altar(gp, testCol * gp.tileSize, testRow * gp.tileSize, room.altarType);
                            gp.gameProgress.addAltar(altar);
                            System.out.println("  ✓ Altar type " + room.altarType + 
                                             " placed at tile (" + testCol + "," + testRow + ")");
                            return new int[]{testCol, testRow};
                        }
                    }
                }
            }
            
            System.out.println("  ✗ ERROR: Cannot place altar in " + room.name + 
                             " - no valid tile found!");
            return new int[]{0, 0};
        }
    }
    
    private void placeSoulInRoom(Room room, int[] altarPos) {
        if (altarPos[0] == 0 && altarPos[1] == 0) return;
        
        // Cari posisi soul minimal 4 tiles dari altar
        int maxAttempts = 50;
        int[] soulTile = null;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int[] randomTile = room.getRandomTile();
            
            if (randomTile != null && isValidTileForObject(randomTile[0], randomTile[1])) {
                // Hitung jarak dari altar
                int distance = Math.abs(randomTile[0] - altarPos[0]) + 
                              Math.abs(randomTile[1] - altarPos[1]);
                
                if (distance >= 4) { // Minimal 4 tile jarak dari altar
                    soulTile = randomTile;
                    break;
                }
            }
        }
        
        // Jika tidak ketemu, ambil tile valid apapun
        if (soulTile == null) {
            soulTile = room.getAnyValidTile();
        }
        
        if (soulTile != null && isValidTileForObject(soulTile[0], soulTile[1])) {
            Soul soul = new Soul(gp, soulTile[0] * gp.tileSize, 
                                soulTile[1] * gp.tileSize, room.altarType);
            gp.gameProgress.addSoul(soul);
            System.out.println("  ✓ Soul type " + room.altarType + 
                             " placed at tile (" + soulTile[0] + "," + soulTile[1] + ")");
        } else {
            System.out.println("  ✗ ERROR: Cannot place soul in " + room.name);
        }
    }
    
    private void placeEnemyInRoom(Room room, int[] altarPos, int enemyIndex) {
        int maxAttempts = 100; // Tambah attempt untuk cari posisi yang aman
        int[] enemyTile = null;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int[] randomTile = room.getRandomTile();
            
            if (randomTile != null && isValidTileForEnemy(randomTile[0], randomTile[1])) {
                // Hitung jarak dari altar (minimal 3 tiles)
                int distanceToAltar = Math.abs(randomTile[0] - altarPos[0]) + 
                                     Math.abs(randomTile[1] - altarPos[1]);
                
                // Hitung jarak dari border room (hindari terlalu dekat dengan void)
                int distanceToLeftBorder = randomTile[0] - room.minCol;
                int distanceToRightBorder = room.maxCol - randomTile[0];
                int distanceToTopBorder = randomTile[1] - room.minRow;
                int distanceToBottomBorder = room.maxRow - randomTile[1];
                
                // Minimal 2 tiles dari border dan 3 tiles dari altar
                if (distanceToAltar >= 3 &&
                    distanceToLeftBorder >= 2 && distanceToRightBorder >= 2 &&
                    distanceToTopBorder >= 2 && distanceToBottomBorder >= 2) {
                    
                    // Cek area 3x3 di sekitar enemy bebas dari void
                    if (isSafeAreaForEnemy(randomTile[0], randomTile[1])) {
                        enemyTile = randomTile;
                        break;
                    }
                }
            }
        }
        
        // Jika tidak ketemu posisi ideal, cari yang aman saja
        if (enemyTile == null) {
            for (int attempt = 0; attempt < 50; attempt++) {
                int[] randomTile = room.getRandomTile();
                if (randomTile != null && isValidTileForEnemy(randomTile[0], randomTile[1])) {
                    enemyTile = randomTile;
                    break;
                }
            }
        }
        
        // Last resort: ambil tile valid pertama
        if (enemyTile == null) {
            enemyTile = room.getAnyValidTile();
        }
        
        if (enemyTile != null && isValidTileForEnemy(enemyTile[0], enemyTile[1])) {
            Enemy enemy = new Enemy(gp, enemyTile[0] * gp.tileSize, 
                                   enemyTile[1] * gp.tileSize);
            gp.gameProgress.addEnemy(enemy);
            System.out.println("  ✓ Enemy " + (enemyIndex + 1) + 
                             " placed at SAFE tile (" + enemyTile[0] + "," + enemyTile[1] + ")");
        } else {
            System.out.println("  ✗ ERROR: Cannot place enemy in " + room.name);
        }
    }
    
    private boolean isSafeAreaForEnemy(int centerCol, int centerRow) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int col = centerCol + dc;
                int row = centerRow + dr;
                
                if (col < 0 || col >= gp.tileM.cols || row < 0 || row >= gp.tileM.rows) {
                    return false;
                }
                
                int tileNum = gp.tileM.mapTileNum[row][col];
                if (tileNum != 1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValidTileForEnemy(int col, int row) {
        if (!isValidTileForObject(col, row)) return false;
        
        // Cek minimal 8 tetangga adalah tile 1 (untuk navigasi)
        int floorNeighbors = 0;
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
        
        for (int[] dir : directions) {
            int checkCol = col + dir[0];
            int checkRow = row + dir[1];
            
            if (checkCol >= 0 && checkCol < gp.tileM.cols && 
                checkRow >= 0 && checkRow < gp.tileM.rows) {
                int tileNum = gp.tileM.mapTileNum[checkRow][checkCol];
                if (tileNum == 1) {
                    floorNeighbors++;
                }
            }
        }
        
        return floorNeighbors >= 6; // Minimal 6 dari 8 tetangga adalah floor
    }	
    
    private void placeExitDoor() {
        System.out.println("\n=== Placing Exit Door ===");
        
        // PERBAIKAN: Door ditempatkan di KANAN ATAS MAP, tapi di tile 1
        // Cari tile 1 di area kanan atas (baris 2-6, kolom cols-10 sampai cols-3)
        
        int startRow = 2;
        int endRow = 6;
        int startCol = gp.tileM.cols - 10; // 10 tiles dari kanan
        int endCol = gp.tileM.cols - 3;   // 3 tiles dari kanan
        
        System.out.println("Searching for door in area: Col " + startCol + "-" + endCol + 
                          ", Row " + startRow + "-" + endRow);
        
        // Prioritas: Cari tile 1 di kanan atas
        for (int row = startRow; row <= endRow; row++) {
            for (int col = endCol; col >= startCol; col--) { // Cari dari kanan ke kiri
                if (isValidTileForDoor(col, row)) {
                	Door door = spawnDoorWithOffset(col, row, -2, -2);
                	gp.gameProgress.addDoor(door);
                    System.out.println("  ✓ Exit door placed at tile (" + col + "," + row + ")");
                    return;
                }
            }
        }
        
        // Alternatif: Cari tile 1 di border kanan manapun
        System.out.println("Searching for any tile on right border...");
        for (int row = 0; row < gp.tileM.rows; row++) {
            for (int col = gp.tileM.cols - 1; col >= gp.tileM.cols - 5; col--) {
                if (isValidTileForDoor(col, row)) {
                    Door door = new Door(gp, col * gp.tileSize, row * gp.tileSize);
                    gp.gameProgress.addDoor(door);
                    System.out.println("  ✓ Exit door placed at border tile (" + col + "," + row + ")");
                    return;
                }
            }
        }
        
        // Fallback: Tempatkan di posisi default
        Door door = new Door(gp, (gp.tileM.cols - 5) * gp.tileSize, 3 * gp.tileSize);
        gp.gameProgress.addDoor(door);
        System.out.println("  ✗ Exit door placed at fallback position");
    }
    
    private boolean isValidTileForDoor(int col, int row) {
        if (!isValidTileForObject(col, row)) return false;
        
        // Cek apakah ada space untuk door (2x3 tiles harus kosong)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int checkCol = col + dc;
                int checkRow = row + dr;
                
                if (checkRow < 0 || checkRow >= gp.tileM.rows || 
                    checkCol < 0 || checkCol >= gp.tileM.cols) {
                    return false;
                }
                
                int tileNum = gp.tileM.mapTileNum[checkRow][checkCol];
                if (tileNum != 1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private int[] findNearestValidTile(int startCol, int startRow) {
        if (gp.tileM.mapTileNum == null) return null;
        
        // BFS untuk mencari tile 1 terdekat
        boolean[][] visited = new boolean[gp.tileM.rows][gp.tileM.cols];
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startCol, startRow, 0}); // col, row, distance
        
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0}};
        
        while (!queue.isEmpty()) {
            int[] current = queue.remove(0);
            int col = current[0];
            int row = current[1];
            int dist = current[2];
            
            // Cek bounds
            if (col < 0 || col >= gp.tileM.cols || row < 0 || row >= gp.tileM.rows) {
                continue;
            }
            
            // Skip jika sudah visited
            if (visited[row][col]) continue;
            visited[row][col] = true;
            
            // Cek jika tile ini valid (tile 1)
            if (isValidTileForObject(col, row)) {
                return new int[]{col, row};
            }
            
            // Jika jarak sudah terlalu jauh, stop
            if (dist >= 10) continue;
            
            // Tambahkan tetangga
            for (int[] dir : directions) {
                int newCol = col + dir[0];
                int newRow = row + dir[1];
                queue.add(new int[]{newCol, newRow, dist + 1});
            }
        }
        
        return null;
    }
    
    // PERBAIKAN PENTING: Hanya tile 1 yang valid untuk object
    private boolean isValidTileForObject(int col, int row) {
        if (gp.tileM.mapTileNum == null) return false;
        
        if (row >= 0 && row < gp.tileM.rows && col >= 0 && col < gp.tileM.cols) {
            int tileNum = gp.tileM.mapTileNum[row][col];
            return (tileNum == 1); // HANYA tile 1 (Floor.png)
        }
        return false;
    }
    
    private void printPlacementSummary() {
        System.out.println("\n=== OBJECT PLACEMENT SUMMARY ===");
        System.out.println("Total Souls: " + gp.gameProgress.getSouls().size());
        System.out.println("Total Altars: " + gp.gameProgress.getAltars().size());
        System.out.println("Total Enemies: " + gp.gameProgress.getEnemies().size());
        System.out.println("Total Doors: " + gp.gameProgress.getDoors().size());
        
        // Print detail posisi
        for (int i = 0; i < gp.gameProgress.getAltars().size(); i++) {
            Altar altar = gp.gameProgress.getAltars().get(i);
            int tileX = altar.worldX / gp.tileSize;
            int tileY = altar.worldY / gp.tileSize;
            System.out.println("Altar " + i + ": Type " + altar.getType() + 
                             " at tile (" + tileX + "," + tileY + ")");
        }
        
        for (int i = 0; i < gp.gameProgress.getSouls().size(); i++) {
            Soul soul = gp.gameProgress.getSouls().get(i);
            int tileX = soul.worldX / gp.tileSize;
            int tileY = soul.worldY / gp.tileSize;
            System.out.println("Soul " + i + ": Type " + soul.getType() + 
                             " at tile (" + tileX + "," + tileY + ")");
        }
    }
    
    // Inner class Room
    private class Room {
        public int id;
        public String name;
        public int minCol, maxCol;
        public int minRow, maxRow;
        public int altarType;
        private List<int[]> floorTiles; // Hanya tile 1
        
        public Room(int id, String name, int minCol, int maxCol, int minRow, int maxRow) {
            this.id = id;
            this.name = name;
            this.minCol = minCol;
            this.maxCol = maxCol;
            this.minRow = minRow;
            this.maxRow = maxRow;
            this.altarType = id; // Altar type = room id (0, 1, 2)
            this.floorTiles = new ArrayList<>();
        }
        
        public void addTile(int col, int row) {
            floorTiles.add(new int[]{col, row});
        }
        
        public boolean contains(int col, int row) {
            return col >= minCol && col <= maxCol && row >= minRow && row <= maxRow;
        }
        
        public int getFloorTilesCount() {
            return floorTiles.size();
        }
        
        public int[] getCenterTile() {
            int centerCol = (minCol + maxCol) / 2;
            int centerRow = (minRow + maxRow) / 2;
            
            // Cari tile 1 terdekat ke center
            int[] closestTile = null;
            double minDistance = Double.MAX_VALUE;
            
            for (int[] tile : floorTiles) {
                int col = tile[0];
                int row = tile[1];
                double distance = Math.sqrt(Math.pow(col - centerCol, 2) + 
                                           Math.pow(row - centerRow, 2));
                
                if (distance < minDistance) {
                    minDistance = distance;
                    closestTile = tile;
                }
            }
            
            return closestTile;
        }
        
        public int[] getRandomTile() {
            if (floorTiles.isEmpty()) return null;
            return floorTiles.get(random.nextInt(floorTiles.size()));
        }
        
        public int[] getAnyValidTile() {
            if (!floorTiles.isEmpty()) {
                return floorTiles.get(0);
            }
            return null;
        }
    }
}
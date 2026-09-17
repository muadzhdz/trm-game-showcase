package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    public int roomId;
    public int minCol, maxCol;
    public int minRow, maxRow;
    public List<int[]> floorTiles; // Semua tile walkable (1) dalam room
    public int altarType; // 0, 1, atau 2
    
    public Room(int roomId) {
        this.roomId = roomId;
        this.floorTiles = new ArrayList<>();
        this.altarType = roomId % 3; // Altar type berdasarkan room ID
    }
    
    public void addTile(int col, int row) {
        floorTiles.add(new int[]{col, row});
    }
    
    public boolean contains(int col, int row) {
        return col >= minCol && col <= maxCol && row >= minRow && row <= maxRow;
    }
    
    public int[] getCenterTile() {
        int centerCol = (minCol + maxCol) / 2;
        int centerRow = (minRow + maxRow) / 2;
        
        // Cari tile floor terdekat ke center
        int[] closestTile = null;
        double minDistance = Double.MAX_VALUE;
        
        for (int[] tile : floorTiles) {
            int col = tile[0];
            int row = tile[1];
            double distance = Math.sqrt(Math.pow(col - centerCol, 2) + Math.pow(row - centerRow, 2));
            
            if (distance < minDistance) {
                minDistance = distance;
                closestTile = tile;
            }
        }
        
        return closestTile;
    }
    
    public int[] getRandomTile() {
        if (floorTiles.isEmpty()) return null;
        Random rand = new Random();
        return floorTiles.get(rand.nextInt(floorTiles.size()));
    }
    
    public int[] getRandomTileAwayFrom(int avoidCol, int avoidRow, int minDistance) {
        List<int[]> candidates = new ArrayList<>();
        
        for (int[] tile : floorTiles) {
            int col = tile[0];
            int row = tile[1];
            double distance = Math.sqrt(Math.pow(col - avoidCol, 2) + Math.pow(row - avoidRow, 2));
            
            if (distance >= minDistance) {
                candidates.add(tile);
            }
        }
        
        if (candidates.isEmpty()) {
            return getRandomTile(); // Fallback
        }
        
        Random rand = new Random();
        return candidates.get(rand.nextInt(candidates.size()));
    }
    
    public boolean isValidForPlacement(int col, int row) {
        // Pastikan tile adalah floor (1) dan tidak di border
        if (col <= minCol || col >= maxCol || row <= minRow || row >= maxRow) {
            return false;
        }
        
        // Cek di floorTiles
        for (int[] tile : floorTiles) {
            if (tile[0] == col && tile[1] == row) {
                return true;
            }
        }
        return false;
    }
    
    public void printInfo() {
        System.out.println("=== Room " + roomId + " ===");
        System.out.println("Bounds: Col " + minCol + "-" + maxCol + ", Row " + minRow + "-" + maxRow);
        System.out.println("Floor tiles: " + floorTiles.size());
        System.out.println("Altar type: " + altarType);
        System.out.println("Center tile: " + (getCenterTile() != null ? 
            "(" + getCenterTile()[0] + "," + getCenterTile()[1] + ")" : "None"));
    }
}
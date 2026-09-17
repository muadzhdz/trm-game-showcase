package main;

import entities.Altar;
import entities.Entity;
import entities.Player;

public class CollisionChecker {
    
    GamePanel gp;
    
    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }
    
    // ================= TILE COLLISION =================
    public void checkTile(Entity entity, int targetX, int targetY) {
        
        if (gp.tileM.mapTileNum == null) {
            entity.collisionOn = true;
            return;
        }
        
        int tileSize = gp.tileSize;
        int[][] map = gp.tileM.mapTileNum;
        
        // posisi solid area di world
        int left = targetX + entity.solidArea.x;
        int right = left + entity.solidArea.width - 1;
        int top = targetY + entity.solidArea.y;
        int bottom = top + entity.solidArea.height - 1;
        
        int colLeft = left / tileSize;
        int colRight = right / tileSize;
        int rowTop = top / tileSize;
        int rowBottom = bottom / tileSize;
        
        // batas map (anggap solid)
        if (colLeft < 0 || colRight >= gp.tileM.cols ||
            rowTop < 0 || rowBottom >= gp.tileM.rows) {
            
            entity.collisionOn = true;
            return;
        }
        
        // cek semua tile yang disentuh solidArea
        for (int r = rowTop; r <= rowBottom; r++) {
            for (int c = colLeft; c <= colRight; c++) {
                
                int tileNum = gp.tileM.mapTileNum[r][c];
                
                if (tileNum < 0 || tileNum >= gp.tileM.tile.length) {
                    entity.collisionOn = true;
                    return;
                }
                
                if (gp.tileM.tile[tileNum] != null && 
                    gp.tileM.tile[tileNum].collision) {
                    
                    entity.collisionOn = true;
                    return;
                }
            }
        }
        
        entity.collisionOn = false;
    }
    
    // ================= ENTITY COLLISION =================
    public void checkEntity(Entity entity, Entity target) {
        // Reset solidArea positions to current positions
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;
        
        target.solidArea.x = target.worldX + target.solidArea.x;
        target.solidArea.y = target.worldY + target.solidArea.y;
        
        // Check based on entity's intended movement direction
        switch(entity.direction) {
            case "up":
                entity.solidArea.y -= entity.speed;
                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entity.solidArea.y += entity.speed;
                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entity.solidArea.x -= entity.speed;
                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entity.solidArea.x += entity.speed;
                if (entity.solidArea.intersects(target.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
        }
        
        // Reset solidArea positions
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        target.solidArea.x = target.solidAreaDefaultX;
        target.solidArea.y = target.solidAreaDefaultY;
    }
    
    // Check collision between player and enemy
    public void checkPlayer(Entity enemy) {
        checkEntity(enemy, gp.player);
    }
    
 // Di CollisionChecker.checkTwoEntities():
    public boolean checkTwoEntities(Entity entity1, Entity entity2) {
        // Hitung world position dari solidArea
        int entity1Left = entity1.worldX + entity1.solidArea.x;
        int entity1Right = entity1Left + entity1.solidArea.width;
        int entity1Top = entity1.worldY + entity1.solidArea.y;
        int entity1Bottom = entity1Top + entity1.solidArea.height;
        
        int entity2Left = entity2.worldX + entity2.solidArea.x;
        int entity2Right = entity2Left + entity2.solidArea.width;
        int entity2Top = entity2.worldY + entity2.solidArea.y;
        int entity2Bottom = entity2Top + entity2.solidArea.height;
        
        // Check untuk intersection
        boolean intersects = entity1Left < entity2Right && 
                             entity1Right > entity2Left && 
                             entity1Top < entity2Bottom && 
                             entity1Bottom > entity2Top;
        
        // DEBUG: Print collision info
        if (gp.keyH.interactPressed && entity2 instanceof Altar) {
            System.out.println("=== COLLISION DEBUG ===");
            System.out.println("Entity1 (Player): " + entity1Left + "," + entity1Top + 
                             " to " + entity1Right + "," + entity1Bottom);
            System.out.println("Entity2 (Altar): " + entity2Left + "," + entity2Top + 
                             " to " + entity2Right + "," + entity2Bottom);
            System.out.println("Intersects: " + intersects);
        }
        
        return intersects;
    }
    // ================= OBJECT COLLISION =================
    public int checkObject(Entity entity, boolean player) {
        int index = -1;
        
        // Check collision with souls
        for (int i = 0; i < gp.gameProgress.getSouls().size(); i++) {
            Entity soul = gp.gameProgress.getSouls().get(i);
            if (checkTwoEntities(entity, soul)) {
                if (player) {
                    return i; // Return soul index
                }
            }
        }
        
        // Check collision with altars
        for (int i = 0; i < gp.gameProgress.getAltars().size(); i++) {
            Entity altar = gp.gameProgress.getAltars().get(i);
            if (checkTwoEntities(entity, altar)) {
                if (player) {
                    return i + 100; // Return altar index with offset
                }
            }
        }
        
        // Check collision with doors
        for (int i = 0; i < gp.gameProgress.getDoors().size(); i++) {
            Entity door = gp.gameProgress.getDoors().get(i);
            if (checkTwoEntities(entity, door)) {
                if (player) {
                    return i + 200; // Return door index with offset
                }
            }
        }
        
        return -1;
    }
    
    // ================= SIMPLE BOUNDARY CHECK =================
    public boolean isInsideMap(int worldX, int worldY) {
        return worldX >= 0 && worldX < gp.worldWidth && 
               worldY >= 0 && worldY < gp.worldHeight;
    }
    
    // ================= GET TILE AT POSITION =================
    public int getTileNumAt(int worldX, int worldY) {
        if (gp.tileM.mapTileNum == null) return 0;
        
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        
        if (col >= 0 && col < gp.tileM.cols && row >= 0 && row < gp.tileM.rows) {
            return gp.tileM.mapTileNum[row][col];
        }
        
        return 0; // Void tile
    }
    
    // ================= CHECK IF POSITION IS WALKABLE =================
    public boolean isWalkable(int worldX, int worldY) {
        if (!isInsideMap(worldX, worldY)) return false;
        
        int tileNum = getTileNumAt(worldX, worldY);
        
        if (tileNum >= 0 && tileNum < gp.tileM.tile.length) {
            return !gp.tileM.tile[tileNum].collision;
        }
        
        return false;
    }
}
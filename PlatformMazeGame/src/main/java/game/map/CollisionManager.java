/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

/**
 *
 * @author nabildava
 */

public class CollisionManager {

    private int[][] map;
    private int tileSize;

    public CollisionManager(int[][] map, int tileSize) {
        this.map = map;
        this.tileSize = tileSize;
    }

    public boolean isWall(int x, int y, int size) {
        int left = x / tileSize;
        int right = (x + size - 1) / tileSize;
        int top = y / tileSize;
        int bottom = (y + size - 1) / tileSize;

        // proteksi out of bounds (biar gak crash)
        if (top < 0 || left < 0 || bottom >= map.length || right >= map[0].length) {
            return true;
        }

        return map[top][left] == 1 ||
               map[top][right] == 1 ||
               map[bottom][left] == 1 ||
               map[bottom][right] == 1;
    }
}


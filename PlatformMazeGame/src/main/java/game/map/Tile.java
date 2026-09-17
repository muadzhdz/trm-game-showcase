/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

/**
 *
 * @author nabildava
 */
import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean collision;

    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }
}

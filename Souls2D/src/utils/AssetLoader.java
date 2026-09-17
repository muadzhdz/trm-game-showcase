package utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class AssetLoader {
    private static HashMap<String, BufferedImage[]> animations = new HashMap<>();
    
    public static void loadAssets() {
        try {
            // Load player sprites
            BufferedImage[] playerIdle = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                playerIdle[i] = ImageIO.read(
                    AssetLoader.class.getResource("/player/Player_Walk_Front" + i + ".png")
                );
            }
            animations.put("player_idle", playerIdle);
            
            // Load altar animations
            BufferedImage[] altarOff = new BufferedImage[1];
            altarOff[0] = ImageIO.read(
                AssetLoader.class.getResource("/altar/Altar.png")
            );
            animations.put("altar_off", altarOff);
            
            BufferedImage[] altarOn = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                altarOn[i] = ImageIO.read(
                    AssetLoader.class.getResource("/altar/AltarSoul" + i + ".png")
                );
            }
            animations.put("altar_on", altarOn);
            
            // Load door animations
            BufferedImage[][] doorStates = new BufferedImage[4][4];
            for (int state = 0; state < 4; state++) {
                for (int frame = 0; frame < 4; frame++) {
                    doorStates[state][frame] = ImageIO.read(
                        AssetLoader.class.getResource(
                            "/door/SoulDoorA_" + state + "_" + frame + ".png"
                        )
                    );
                }
                animations.put("door_" + state, doorStates[state]);
            }
            
            // Load soul sprite
            BufferedImage soul = ImageIO.read(
                AssetLoader.class.getResource("/items/soul.png")
            );
            BufferedImage[] soulArr = {soul};
            animations.put("soul", soulArr);
            
            // Load enemy sprites
            BufferedImage[] enemyIdle = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                enemyIdle[i] = ImageIO.read(
                    AssetLoader.class.getResource("/enemy/ShadowEnemy_" + i + ".png")
                );
            }
            animations.put("enemy_idle", enemyIdle);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading assets. Check if resources are in correct path.");
        }
    }
    
    public static BufferedImage[] getAnimation(String key) {
        return animations.get(key);
    }
    
    public static BufferedImage getFrame(String animation, int frame) {
        BufferedImage[] frames = animations.get(animation);
        if (frames != null && frame < frames.length) {
            return frames[frame];
        }
        return null;
    }
}
package game.core;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class Sound {

    public static void play(String path) {
        try {
            InputStream is = Sound.class.getResourceAsStream(path);
            if (is == null) {
                System.out.println("❌ Sound not found: " + path);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {
            System.out.println("❌ Failed to play sound: " + path);
            e.printStackTrace();
        }
    }
}

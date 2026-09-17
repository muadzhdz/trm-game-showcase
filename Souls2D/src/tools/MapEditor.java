package tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MapEditor {
    
    public static void createMapWithObjects(String filename, int width, int height) throws IOException {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        
        // Create walls around the edges
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    sb.append("13 "); // Wall
                } else if (y == 1 && x == width / 2) {
                    sb.append("1 "); // Spawn point
                } else if (rand.nextInt(10) == 0) {
                    sb.append("13 "); // Random walls
                } else {
                    sb.append("1 "); // Floor
                }
            }
            sb.append("\n");
        }
        
        FileWriter writer = new FileWriter(filename);
        writer.write(sb.toString());
        writer.close();
        
        System.out.println("Map created: " + filename);
        System.out.println("Player spawn at: row 1, column " + (width / 2));
        
        // Generate object placement file
        createObjectPlacementFile(filename.replace(".bg.txt", "_objects.txt"), width, height, rand);
    }
    
    private static void createObjectPlacementFile(String filename, int width, int height, Random rand) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# Object Placement for Map\n");
        sb.append("# Format: TYPE X Y [PARAMS]\n\n");
        
        // Place 3 souls
        for (int i = 0; i < 3; i++) {
            int x = rand.nextInt(width - 4) + 2;
            int y = rand.nextInt(height - 4) + 2;
            sb.append("SOUL ").append(x).append(" ").append(y).append(" ").append(i).append("\n");
        }
        
        // Place 3 altars
        String[] altarPositions = {
            "2 2 0",  // Top-left
            (width - 3) + " 2 1",  // Top-right
            (width / 2) + " " + (height - 3) + " 2"  // Bottom-center
        };
        
        for (String pos : altarPositions) {
            sb.append("ALTAR ").append(pos).append("\n");
        }
        
        // Place 2-3 enemies
        int enemyCount = rand.nextInt(2) + 2;
        for (int i = 0; i < enemyCount; i++) {
            int x = rand.nextInt(width - 4) + 2;
            int y = rand.nextInt(height - 4) + 2;
            sb.append("ENEMY ").append(x).append(" ").append(y).append("\n");
        }
        
        // Place door
        sb.append("DOOR ").append(width - 2).append(" ").append(height / 2).append("\n");
        
        FileWriter writer = new FileWriter(filename);
        writer.write(sb.toString());
        writer.close();
        
        System.out.println("Object placement created: " + filename);
    }
    
    public static void main(String[] args) {
        try {
            createMapWithObjects("resources/maps/custom_map.bg.txt", 40, 25);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
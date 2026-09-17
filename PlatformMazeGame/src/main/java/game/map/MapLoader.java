package game.map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapLoader {

    public static int[][] loadCSV(String resourcePath) {
        try {
            InputStream is = MapLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new RuntimeException("Map CSV tidak ditemukan: " + resourcePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // baca semua baris dulu
            java.util.List<int[]> rows = new java.util.ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                int[] row = new int[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    String p = parts[i].trim();

                    // Tiled kadang export kosong jadi ""
                    if (p.isEmpty()) p = "0";

                    int value = Integer.parseInt(p);

                    // ✅ konversi:
                    // 0  = kosong (floor)
                    // >0 = tile (anggap wall)
                    row[i] = (value > 0) ? 1 : 0;
                }

                rows.add(row);
            }

            br.close();

            // convert list -> array 2D
            int[][] map = new int[rows.size()][];
            for (int r = 0; r < rows.size(); r++) {
                map[r] = rows.get(r);
            }

            return map;

        } catch (Exception e) {
            throw new RuntimeException("Gagal load CSV map: " + resourcePath, e);
        }
    }
}

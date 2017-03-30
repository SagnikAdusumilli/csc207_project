package warehouse_system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WarehousePicking {
  /**
   * Based on the Integer SKUs in List 'skus', return a List of locations, where each location is a
   * String containing 5 pieces of information: the zone character (in the range ['A'..'B']), the
   * aisle number (an integer in the range [0..1]), the rack number (an integer in the range
   * ([0..2]), and the level on the rack (an integer in the range [0..3]), and the SKU number.
   * 
   * @param skus the list of SKUs to retrieve.
   * @return the List of locations.
   */
  public static List<String> optimize(List<String> skus) {
    List<String> path = new ArrayList<String>();

    try {
      BufferedReader bufferedReader =
          new BufferedReader(new FileReader("warehouse_config/traversal_table.csv"));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] values = line.split(",");
        int frequency = countFrequency(skus, values[4]);

        for (int i = 0; i < frequency; i++) {
          path.add(values[0] + " " + values[1] + " " + values[2] + " " + values[3]);
        }

      }

      bufferedReader.close();
    } catch (IOException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }

    return path;
  }

  private static int countFrequency(List<String> list, String element) {
    int count = 0;
    for (String item : list) {
      if (item.equals(element)) {
        count++;
      }
    }
    return count;
  }
}

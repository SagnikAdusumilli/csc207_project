package warehouse_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * A Warehouse represents the static state of a real warehouse system. In other words, the Warehouse
 * class models what physical workers and physical inventory are actually doing in real time, as
 * opposed to what they ought to be doing.
 */
public class Warehouse {

  /**
   * A map of the names of each initialized worker to a reference of its actual object.
   */
  private Map<String, Worker> workers;
  /**
   * A map of the IDs of each pick request to a reference of its the actual PickRequest object.
   */
  private Map<String, PickRequest> requests;
  /**
   * A map of the model and colour of minivans to the location of their fascia.
   */
  private Map<String, String[]> translationMap;

  /**
   * The distinct levels within the warehouse (there are 48) each containing a unique type of fascia
   * (unique SKU). The 4D array represents the warehouse's layout: 2 zones (A or B represented as
   * indices 0 and 1, respectively), 2 aisles per zone (0, 1), 3 racks per aisles (0..2), 4 levels
   * per rack (0..3).
   */
  private Level[][][][] levels;

  /** A map of the SKUs to a string representation of their locations. */
  private Map<String, String> locations;

  /** The inverse mapping of the above; maps locations to skus. */
  private Map<String, String> inverseLocations;

  /**
   * A string of IDs corresponding to the pick request orders in the marshalling area.
   */
  public Queue<String> marshallingIds;
  /**
   * An ArrayList of IDs corresponding to the pick request orders in the loading area.
   */
  public ArrayList<String> loadingIds;
  /** A representation of the next pick request ID that needs to be loaded. */
  public int loadingCounter;

  /** PickRequests that have been finally loaded are stored here. */
  private ArrayList<PickRequest> loadedRequests;

  /**
   * Initialize a warehouse with it's initial conditions.
   */
  public Warehouse() {

    this.requests = new HashMap<String, PickRequest>();
    this.workers = new HashMap<String, Worker>();
    this.levels = new Level[2][2][3][4];

    // The files must be read in this order, since they rely on each other.
    createLocationMap(new File("warehouse_config/traversal_table.csv"));
    createTranslationMap(new File("warehouse_config/translation.csv"));

    // Create the inversemap from <locations>
    this.inverseLocations = new HashMap<String, String>();
    for (Entry<String, String> entry : locations.entrySet()) {
      inverseLocations.put(entry.getValue(), entry.getKey());
    }

    this.marshallingIds = new LinkedList<String>();
    this.loadingIds = new ArrayList<String>();
    this.loadedRequests = new ArrayList<PickRequest>();
    this.loadingCounter = 1;

    adjustLevels(new File("warehouse_config/initial.csv"));

  }

  /**
   * Makes a map of SKUs to locations by reading the traversal map.
   * 
   * @param file the file this method will read
   */
  public void createLocationMap(File file) {

    locations = new HashMap<String, String>();

    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] chars = line.split(",");
        String location = chars[0] + " " + chars[1] + " " + chars[2] + " " + chars[3];
        locations.put(chars[4], location);
      }

      bufferedReader.close();

    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  /**
   * Makes a map of colours and models to SKUs by reading the translation map.
   * 
   * @param file the file this method will read
   */
  public void createTranslationMap(File file) {

    translationMap = new HashMap<String, String[]>();

    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

      String line = bufferedReader.readLine(); // Skip first line of
                                               // headings

      int counter = 0; // For now, we only want to read the first 24
                       // translation lines

      while ((line = bufferedReader.readLine()) != null && counter < 24) {

        String[] chars = line.split(",");
        String color = chars[0];
        String model = chars[1];
        String[] skus = {chars[2], chars[3]};
        String[] locationFront = getLocation(skus[0]).split(" ");
        String[] locationBack = getLocation(skus[1]).split(" ");

        // Create the level object for the front fascia.
        int aisle = Integer.valueOf(locationFront[1]);
        int rack = Integer.valueOf(locationFront[2]);
        int level = Integer.valueOf(locationFront[3]);

        if (locationFront[0].equals("A")) {
          levels[0][aisle][rack][level] = new Level(skus[0], color, model, 30);
        } else { // If chars[0].equals("B)
          levels[1][aisle][rack][level] = new Level(skus[0], color, model, 30);
        }

        // Create the level object for the front fascia.
        aisle = Integer.valueOf(locationBack[1]);
        rack = Integer.valueOf(locationBack[2]);
        level = Integer.valueOf(locationBack[3]);
        if (locationBack[0].equals("A")) {
          levels[0][aisle][rack][level] = new Level(skus[1], color, model, 30);
        } else { // If chars[0].equals("B)
          levels[1][aisle][rack][level] = new Level(skus[1], color, model, 30);
        }

        // Create the translation map.
        String order = color + " " + model;
        translationMap.put(order, skus);

        counter++;
      }

      bufferedReader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Adjusts the stock of the warehouse levels.
   * 
   * @param file the file this method will read
   */
  public void adjustLevels(File file) {

    try {

      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      String line;

      while ((line = bufferedReader.readLine()) != null) {

        String[] chars = line.split(",");
        String sku =
            getSkuFromLocation(chars[0] + " " + chars[1] + " " + chars[2] + " " + chars[3]);
        Level level = getLevel(sku);
        int amnt = Integer.valueOf(chars[4]);
        level.setStock(amnt);

      }

      bufferedReader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Return a reference to a Level object by specifying its unique SKU.
   * 
   * @param sku the SKU of the fascia the level stores
   * @return the level that stores the SKU
   */
  public Level getLevel(String sku) {

    int zone;

    String[] location = getLocation(sku).split(" ");
    if (location[0] == "A") {
      zone = 0;
    } else {
      zone = 1;
    }

    int aisle = Integer.parseInt(location[1]);
    int rack = Integer.parseInt(location[2]);
    int lev = Integer.parseInt(location[3]);

    Level level = levels[zone][aisle][rack][lev];

    return level;
  }

  public String[] getSkus(String model, String color) {
    return translationMap.get(color + " " + model);
  }

  public String getSkuFromLocation(String loc) {
    return inverseLocations.get(loc);
  }

  public PickRequest getPickRequest(String id) {
    return requests.get(id);
  }

  public String getLocation(String se) {
    return locations.get(se);
  }

  public Worker getWorker(String name) {
    return workers.get(name);
  }

  public void addWorker(Worker wo) {
    workers.put(wo.getName(), wo);
  }

  public void addPickRequest(PickRequest pr) {
    requests.put(pr.getId(), pr);
  }

  public void loadPickRequest(PickRequest pr) {
    loadedRequests.add(pr);
  }

  public void unloadPickRequest(PickRequest pr) {
    loadedRequests.remove(pr);
  }

  public ArrayList<PickRequest> getLoadedRequests() {
    return loadedRequests;
  }

  public void removePickRequest(PickRequest pr) {
    requests.remove(pr);
  }

}

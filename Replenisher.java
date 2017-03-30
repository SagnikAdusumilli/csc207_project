package warehouse_system;

/**
 * A Replenisher in the warehouse.
 */
public class Replenisher extends Worker {

  /** stores the location of the level that needs to be refilled. */
  private String targetLocation;

  /**
   * Instantiates a new replenisher.
   *
   * @param name of the worker
   * @param war the warehouse
   * @param isReady the is availability status
   */
  public Replenisher(String name, Warehouse war, boolean isReady) {
    super(name, war, isReady);
  }

  /**
   * Sets the target location. and returns a message to be printed
   *
   * @param targetLocation the location to be replenished
   * @return the string
   */
  public String setTargetLocation(String targetLocation) {
    this.targetLocation = targetLocation;
    return this.toString() + " go to " + targetLocation + ".";
  }

  /**
   * Returns the location that needs replenishing.
   * 
   * @return the location that needs replenishment.
   */

  public String getTargetLocation() {
    return targetLocation;
  }

  /**
   * Refills the rack target location and returns the appropriate message.
   *
   * @param location the location of the fascia that got replenished.
   * @return the message the organizer will print
   */
  public String replenishes(String location) {
    String sku = warehouse.getSkuFromLocation(location);
    Level level = warehouse.getLevel(sku);
    level.replenish();
    return this.toString() + " refilled " + targetLocation + ".";
  }

}

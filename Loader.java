package warehouse_system;

/**
 * A Loader is a subclass of ScanningWorker which itself is a subclass of Worker. A loader's
 * responsibility is to scan pallets it has been assigned in the loading area and to check if they
 * have been sequenced correctly (from ScanningWorking); if so, it must load them onto a truck.
 */
public class Loader extends ScanningWorker {

  /**
   * Instantiates a new loader by calling the parent constructor.
   *
   * @param name the name of the loader
   * @param war the warehouse the loader works in
   * @param isReady whether the loader is ready to work
   */
  public Loader(String name, Warehouse war, boolean isReady) {
    super(name, war, isReady);
  }

  /**
   * When a loader actually loads something, check if the right pick request was loaded and return
   * an appropriate message.
   *
   * @param id the id of the pick request loaded
   * @return the message to be printed by the Organizer
   */
  public String loads(String id) {

    // Print what the loader has done.
    String msg = toString() + " loaded " + id + ".";

    // Remove the loaded request from the loading area model.
    warehouse.loadingIds.remove(id);

    // This pick-request is now on the truck, so it is put in the list of
    // loaded PRs.
    warehouse.loadPickRequest(warehouse.getPickRequest(id));
    warehouse.loadingCounter += 1;

    // Turn String id into its int representation.
    int intId = Integer.parseInt(id.substring(0, id.length() - 2));

    // Tell the organizer that the loader loaded pallets that it was not
    // assigned to load, or if the truck has now departed.
    if (!this.getPrId().equals(id)) {
      msg = msg + "\nLoaded wrong pick request. " + toString() + " Unload pallets with ID " + id
          + ".";
    } else if (intId % 20 == 0) {
      msg = msg + "\nTruck has departed.";
    }

    return msg;
  }

  /**
   * When a loader unloads something, update the model and return an appropriate message for the
   * Organizer to print.
   * 
   * @param id id of the pick request
   * @return message to be printed
   */
  public String unloads(String id) {

    // Add the loaded request back into the warehouse system model.
    PickRequest currRequest = warehouse.getPickRequest(id);

    warehouse.loadingCounter -= 1;

    // Update warehouse model
    warehouse.loadingIds.add(id);
    warehouse.addPickRequest(currRequest);
    warehouse.unloadPickRequest(currRequest);

    // Tell the organizer what the loader did.
    return toString() + " unloaded " + id + " from truck.";
  }

}

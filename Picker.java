package warehouse_system;

import java.util.ArrayList;

/**
 * The Class Picker is a worker in the warehouse that picks fascia from the racks and drops them off
 * in the warehouse's marshalling area for the sequencers to sequence.
 */
public class Picker extends Worker {

  /** The current request the picker is processing. */
  private PickRequest currentRequest;

  /** An ArrayList containing all the SKUs that should be picked. */
  private ArrayList<String> needsPicking = new ArrayList<String>();

  /** An ArrayList of what the picker has put on the fork-lift. */
  private ArrayList<String> actuallyPicked = new ArrayList<String>();

  /** current location of the worker. **/

  /**
   * Instantiates a new picker, and calls the worker superclass constructor.
   *
   * @param name : the name of the picker
   * @param war the warehouse the picker is a part of
   * @param isReady : whether the Picker is ready to work (true) or not (false).
   */
  public Picker(String name, Warehouse war, boolean isReady) {
    super(name, war, isReady);
  }

  /**
   * Handle what happens when a Picker picks, by updating the warehouse model and informing the
   * organizer of what has occurred.
   *
   * @param sku is the SKU number of the fascia
   * @param location the location of the picker
   * @return the message to be printed
   */
  public String picks(String sku, String location) {
    String msg = this.toString() + " went to '" + location + "' and picked " + sku + ".";

    if (!(warehouse.getSkuFromLocation(currentRequest.getLocation()).equals(sku))) {
      return msg + "\nIncorrect sku picked. " + this.toString() + " return fascia with SKU " + sku
          + ".";
    } else {

      // Remove the fascia from the level and add it to the fork-lift.
      actuallyPicked.add(warehouse.getLevel(sku).takeFascia());

      needsPicking.remove(sku);
      currentRequest.next();
      return msg;
    }
  }

  /**
   * Return a reference to the pickRequest of the Picker.
   *
   * @return the current pick request
   */
  public PickRequest getCurrentRequest() {
    return currentRequest;
  }

  /**
   * Sets the currentRequest when a new PickRequest is assigned to the Picker.
   *
   * @param request the new pick request
   */
  public void setCurrentRequest(PickRequest request) {
    currentRequest = request;
    needsPicking.clear();
    ArrayList<String> xen = currentRequest.getFaxOrder();
    needsPicking.addAll(xen);

  }

}

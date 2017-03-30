package warehouse_system;

import java.util.ArrayList;
import java.util.List;

/**
 * PickRequest represents a request for the front and back fascia of 4 minivans.
 */
public class PickRequest {

  /**
   * globalID is a static variable used to assign an id to a PickRequest upon construction. It is
   * then incremented by 1 to ensure that all PickRequest ids are unique.
   */
  private static int globalID = 1;

  /** The unique id of a PickRequest instance. */
  private String id;

  /** The optimized traversal of this pick-request. */
  private List<String> locations;

  /**
   * Keep track of how many of the fascia from the request have been picked.
   */
  private Integer counter;

  /** An array of the original orders that came by fax. */
  private Order[] orders;

  /** Whether or not this PickRequest is done being picked. */
  private boolean donePicking = false;

  /** A list of SKUs in the order they came in by fax. */
  private ArrayList<String> faxOrder;

  /**
   * Instantiate a new pick request by extracting information from the 4 orders that comprise a pick
   * request.
   *
   * @param orders an Order array of length 4 that contains the 4 orders that this pick-request
   *        contains.
   */
  public PickRequest(Order[] orders) {

    this.orders = orders;

    faxOrder = new ArrayList<String>();
    for (int i = 0; i < orders.length; i++) {
      String[] skus = orders[i].getSkus();
      for (String sku : skus) {
        faxOrder.add(sku);
      }
    }

    locations = WarehousePicking.optimize(faxOrder);

    id = globalID + "pr";
    globalID += 1;

    counter = 0;
  }

  /**
   * Return the location that should be picked from the optimized order.
   *
   * @return the location
   */
  public String getLocation() {
    return locations.get(counter);
  }

  /**
   * Increment the counter by one, to simulate the Picker picking an SKU from the request. When all
   * fascia for this request have been picked (counter reaches 8), reset the counter.
   */
  public void next() {
    counter++;
    if (counter >= 8) {
      donePicking = true;
      counter = 0;
    }
  }

  /**
   * Return the id of this instantiated PickRequest object.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Return the orders associated with this request.
   *
   * @return the orders
   */
  public Order[] getOrders() {
    return orders;
  }

  /**
   * Check whether or not this request is done being picked.
   *
   * @return true, if all the fascia for this request have been picked
   */
  public boolean isDone() {
    return donePicking;
  }

  /**
   * Set the counter back to zero and donePicking to false; pick request needs to be re-picked.
   */
  public void reset() {
    counter = 0;
    donePicking = false;
  }

  /**
   * Return the original order that the minivan orders came in.
   *
   * @return the fax order
   */
  public ArrayList<String> getFaxOrder() {
    return faxOrder;
  }

}

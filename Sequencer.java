package warehouse_system;

/**
 * A sequencer in the the warehouse.
 */
public class Sequencer extends ScanningWorker {

  /**
   * Instantiates a new sequencer.
   *
   * @param name of the sequencer
   * @param war the warehouse the sequencer works in
   * @param isReady the is ready
   */
  public Sequencer(String name, Warehouse war, boolean isReady) {
    super(name, war, isReady);
  }

  /**
   * Tell the organizer that that a pair of pallets have been sent to the loading area. Update the
   * warehouse system model accordingly.
   * 
   * @return a message for printing by the system upon completion of the order
   */
  public String sendToLoading() {

    String id = this.getPrId();
    String name = this.getName();

    // Move the pallets' ID from the marshalling area to the loading area.
    // in the model.
    warehouse.loadingIds.add(id);
    warehouse.marshallingIds.remove(id);

    return "Pallets for request " + id + " have been processed by " + name
        + " and sent to the loading area.";
  }

}

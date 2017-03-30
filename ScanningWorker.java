package warehouse_system;

import java.util.ArrayList;

public abstract class ScanningWorker extends Worker {

  /** The current pick request the worker is scanning. */
  private PickRequest currentRequest;

  /**
   * Indicates whether or not the worker has already scanned a PR once and incorrectly.
   */
  private boolean triedOnce;

  /**
   * Instantiates a new ScanningWorker (either a Sequencer or a Loader).
   *
   * @param name of the sequencer.
   * @param war the warehouse the sequencer works in.
   * @param isReady whether the worker is ready for a new assignment.
   */
  public ScanningWorker(String name, Warehouse war, boolean isReady) {
    super(name, war, isReady);
  }

  /**
   * Sets the current pick request.
   *
   * @param pr the new current request
   */
  public void setCurrentRequest(PickRequest pr) {
    currentRequest = pr;
  }

  /**
   * Gets the pick request id.
   *
   * @return the pick request id
   */
  public String getPrId() {
    return currentRequest.getId();
  }

  /**
   * Check the order of fascia in the pallets.
   *
   * @param sequence the sequence of pallet sequenced.
   */
  public boolean scans(String[] sequence) {
    ArrayList<String> correctSeqeunce = currentRequest.getFaxOrder();
    for (int i = 0; i < sequence.length; i++) {
      if (!correctSeqeunce.get(i).equals(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Indicate whether or not this worker has tried to scan their current request once incorrectly.
   */

  public void setTriedOnce(boolean bool) {

    triedOnce = bool;

  }

  /** Get triedOnce. */
  public boolean getTriedOnce() {

    return triedOnce;
  }

}

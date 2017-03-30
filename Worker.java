package warehouse_system;

// TODO: Auto-generated Javadoc
/**
 * a worker in the Warehouse.
 *
 * @author Sagnik
 */
public abstract class Worker {

  /** The name of the worker. */
  protected String name;

  /** The wareHouse the worker works for. */
  protected Warehouse warehouse;

  /** the availability of the worker. */
  protected boolean isReady;

  /** The wants break. */
  protected boolean wantsBreak;

  /**
   * creates an instance of a worker.
   *
   * @param name is the name of worker
   * @param war is the warehouse
   * @param ready the availability of worker
   */
  public Worker(String name, Warehouse war, boolean ready) {

    this.name = name;
    this.warehouse = war;
    this.isReady = ready;
    this.wantsBreak = false;
  }

  /**
   * Checks if is worker is ready.
   * 
   * @return true, if is ready
   */
  public boolean isReady() {
    return isReady;
  }

  /**
   * Sets the ready. and prints appropriate message
   * 
   * @param isReady the new ready
   */
  public void setReady(boolean isReady) {
    this.isReady = isReady;
  }

  /**
   * Checks if worker wants break.
   *
   * @return true, if is wants break
   */
  public boolean isWantsBreak() {
    return wantsBreak;
  }

  /**
   * Sets the whether worker wants break break.
   *
   * @param wantsBreak new status of the woker
   */
  public void setWantsBreak(boolean wantsBreak) {
    this.wantsBreak = wantsBreak;
  }

  /**
   * Gets the name of the worker.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " " + this.name;
  }

}

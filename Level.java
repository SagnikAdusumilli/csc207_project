package warehouse_system;

/**
 * The Class Level.
 */
public class Level {

  /** The sku of the fascia held by this level. */
  private String sku;

  /** The color of the of the car to which this level's fascia belong. */
  private String color;

  /** The model of the car to which this level's fascia belong. */
  private String model;

  /** The current stock of this level. */
  private int currentStock;

  /** Whether or not this level needs to be restocked (currentStock <= 5). */
  private boolean needRestock;

  /**
   * Instantiate a Level.
   *
   * @param sku the SKU of the fascia
   * @param color the color of the car
   * @param model the model of the car
   * @param currentStock the original stock of the car
   */
  public Level(String sku, String color, String model, int currentStock) {

    this.sku = sku;
    this.color = color;
    this.model = model;
    this.currentStock = currentStock;
    this.needRestock = false;
  }

  /**
   * Sets the stock of the level - used to adjust levels based on initial.csv.
   *
   * @param currentStock the new stock
   */
  public void setStock(int currentStock) {
    this.currentStock = currentStock;
  }

  /**
   * Takes a fascia from this level (pickers would do this in the events file). If the stock is 5,
   * then change this.needRestock to True. Organizer should check this immediately after running
   * takeFascia, and if it's true, then add this level to a queue replenishQueue.
   *
   * @return the string
   */
  public String takeFascia() {
    currentStock -= 1;
    if (currentStock <= 5) {
      needRestock = true;
    }
    return sku;
  }

  /**
   * Replenish the level back to its original amount in this model: 30.
   */
  public void replenish() {
    currentStock = 30;
  }

  /**
   * Gets the color attribute.
   *
   * @return the color
   */
  public String getColor() {
    return color;
  }

  /**
   * Gets the SKU of the fascia at this level.
   *
   * @return the sku
   */
  public String getSku() {
    return sku;
  }

  /**
   * Gets the model attribute.
   *
   * @return the model
   */
  public String getModel() {
    return model;
  }

  /**
   * Gets the current stock.
   *
   * @return the current stock
   */
  public int getCurrentStock() {
    return currentStock;
  }

  /**
   * Checks if the level is in need restocking.
   *
   * @return true, if the level needs to be restocked
   */
  public boolean isNeedRestock() {
    return needRestock;
  }

}

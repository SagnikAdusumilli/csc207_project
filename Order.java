package warehouse_system;

/**
 * The Class Order.
 */
public class Order {

  /** An Array of 2 strings, the front SKU of this order and the back SKU. */
  private String[] skuDouble;

  /** The colour of the fascia of this order. */
  private String color;

  /** The model minivan this order is for. */
  private String model;

  /**
   * Boolean: true when this order has been finally loaded correctly on a truck for shipping.
   */
  private boolean loaded;

  /**
   * Instantiates a new order.
   *
   * @param model the model
   * @param color the color
   * @param skus the skus
   */
  public Order(String model, String color, String[] skus) {

    this.color = color;
    this.model = model;

    this.skuDouble = skus;
    this.loaded = false;
  }

  /**
   * Check whether or not this order has reached the final stage of the warehouse process.
   *
   * @return the loaded attribute
   */
  public boolean getLoaded() {

    return this.loaded;

  }

  /**
   * Gets the model attribute of the car.
   *
   * @return the model
   */
  public String getModel() {
    return this.model;
  }

  /**
   * Gets the color attribute of the car.
   *
   * @return the color
   */
  public String getColor() {
    return this.color;
  }

  /**
   * Gets the SKUs of the fascia.
   *
   * @return the skus, front then back
   */
  public String[] getSkus() {
    return this.skuDouble;
  }

  /**
   * Load this order onto a truck signifying that it's done being handled by the warehouse.
   */
  public void loadOrder() {
    this.loaded = true;
  }

  /**
   * Get the string representation of this Order.
   *
   * @return the string
   */
  public String toString() {
    return "Order" + " " + model + " " + color;
  }

}

package warehouse_system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;

import java.util.Queue;

/**
 * The Organizer handles the events (input/output) of the warehouse system. In other words, it
 * receives input about the real-life system (via a fax-machine and worker statements), modifies the
 * state of the warehouse, and finally, gives commands to the workers.
 */
public class Organizer {

  /** The warehouse models the real warehouse system. */
  private Warehouse warehouse = new Warehouse();

  /**
   * The readyPickers is a queue of the workers who are ready to process orders.
   */
  private Queue<Picker> readyPickers = new LinkedList<Picker>();
  /**
   * The readySequenceres is a queue of the workers who are ready to sequence orders and throw them
   * out if necessary.
   */
  private Queue<Sequencer> readySequencers = new LinkedList<Sequencer>();
  /**
   * The readyLoaders is a queue of the workers who are ready to load pallets in order.
   */
  private Queue<Loader> readyLoaders = new LinkedList<Loader>();
  /**
   * The readyReplenishers is a queue of the workers who are ready to refill levels in the warehouse
   * that are low in fascia.
   */
  private Queue<Replenisher> readyReplenishers = new LinkedList<Replenisher>();

  /**
   * The backlog of orders stored as a string array. Order needs to be preserved, so a Queue is
   * used. Once four orders come in, the orders are removed from the queue and a PickRequest is
   * created.
   */
  private Queue<String[]> ordersBacklog = new LinkedList<String[]>();
  /** The queue of the pickRequests that still need to be processed. */
  private Queue<PickRequest> pickRequests = new LinkedList<PickRequest>();

  /**
   * The lowLevels is a a queue of the SKUs of the fascia that need are low in stock and need to get
   * replenished.
   */
  private Queue<String> lowLevels = new LinkedList<String>();

  /**
   * Instantiate an Organizer for a warehouse w.
   * 
   * @param war the warehouse of the Organizer
   */
  public Organizer(Warehouse war) {

    this.warehouse = war;
  }

  /**
   * Link unique events from the input file to their event handlers. There are 4 possibilities: 1)
   * Blank line: do nothing. 2) "Order" as the first word: create a new order. 3) "ready" as the
   * third word: initialise a worker. 4) Otherwise: check if a worker did something.
   *
   * @param event : a line of input from the input file in the form of an array.
   */
  public void handle(String event) {

    // Events will be passed to event-handlers in the form of string arrays.
    String[] eventArray = event.split(" ");

    if (eventArray[0].equals("")) {
      return;

    } else if (eventArray[0].equals("Order")) {
      newOrder(eventArray);

    } else if (eventArray.length == 3 && eventArray[2].equals("ready")) {
      readyWorker(eventArray);

    } else {
      workerActs(eventArray);
    }

  }

  /**
   * Create a new minivan order. If there are 4 in the orderBacklog, create a new PickRequest by
   * invoking newRequest().
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void newOrder(String[] event) {

    ordersBacklog.add((String[]) event);
    System.out.println("New order for a " + event[2] + " minivan " + event[1] + " added to queue.");

    // Create a PickRequest if there are enough orders in the queue
    if (ordersBacklog.size() >= 4) {

      Order[] orders = new Order[4];

      // Go through the orders in the backlog and extract their front and
      // back fascia SKUs.
      String[] currOrder;
      String[] currSkus;
      for (int i = 0; i < 4; i++) {
        currOrder = ordersBacklog.poll();
        currSkus = warehouse.getSkus(currOrder[1], currOrder[2]);
        orders[i] = new Order(currOrder[1], currOrder[2], currSkus);
      }

      newRequest(orders);
    }

  }

  /**
   * Create a new PickRequest and add it to the warehouse system, and to the pickRequests queue.
   * Also, check if work can be assigned to pickers by invoking tryToAssignPicker().
   * 
   * @param orders a line of input from the input file in the form of an array.
   */
  private void newRequest(Order[] orders) {
    PickRequest pickRequest = new PickRequest(orders);
    warehouse.addPickRequest(pickRequest);
    pickRequests.add(pickRequest);
    System.out.println("A new request with ID '" + pickRequest.getId() + "' has been created.");
    tryToAssignPicker();
  }

  /**
   * Create a new worker and add it to the warehouse. Set the worker's status to ready and add it to
   * the appropriate ready-queues. Also, check if the worker can be assigned any work.
   * 
   * @param event a line of input from the input file in the form of an array.
   */
  private void readyWorker(String[] event) {

    // The first index of the event array specifies the type of the worker.
    if (event[0].equals("Picker")) {

      Picker worker;

      if (warehouse.getWorker(event[1]) == null) { // New worker.
        worker = new Picker(event[1], warehouse, true);
        warehouse.addWorker(worker);
        System.out.println("Welcome to the warehouse " + worker.getName() + ".");
      } else { // Worker off break.
        worker = (Picker) warehouse.getWorker(event[1]);
        worker.setReady(true);
        System.out.println("Hope you had a refreshing break " + worker.getName() + ".");
      }

      // Add the worker to the ready queue and see if work can be
      // assigned.
      readyPickers.add(worker);
      tryToAssignPicker();

    } else if (event[0].equals("Sequencer")) {

      Sequencer worker;

      if (warehouse.getWorker(event[1]) == null) { // New worker.
        worker = new Sequencer(event[1], warehouse, true);
        warehouse.addWorker(worker);
        System.out.println("Welcome to the warehouse " + worker.getName() + ".");
      } else { // Worker off break.
        worker = (Sequencer) warehouse.getWorker(event[1]);
        worker.setReady(true);
        System.out.println("Hope you had a refreshing break " + worker.getName() + ".");
      }

      // Add the worker to the ready queue and see if work can be
      // assigned.
      readySequencers.add(worker);
      tryToAssignSequencer();

    } else if (event[0].equals("Loader")) {

      Loader worker;

      if (warehouse.getWorker(event[1]) == null) { // New worker.
        worker = new Loader(event[1], warehouse, true);
        warehouse.addWorker(worker);
        System.out.println("Welcome to the warehouse " + worker.getName() + ".");
      } else { // Worker off break.
        worker = (Loader) warehouse.getWorker(event[1]);
        worker.setReady(true);
        System.out.println("Hope you had a refreshing break " + worker.getName() + ".");
      }

      // Add the worker to the ready queue and see if work can be
      // assigned.
      readyLoaders.add(worker);
      tryToAssignLoader();

    } else if (event[0].equals("Replenisher")) {

      Replenisher worker;

      if (warehouse.getWorker(event[1]) == null) { // New worker.
        worker = new Replenisher(event[1], warehouse, true);
        warehouse.addWorker(worker);
        System.out.println("Welcome to the warehouse " + worker.getName() + ".");
      } else { // Worker off break.
        worker = (Replenisher) warehouse.getWorker(event[1]);
        worker.setReady(true);
        System.out.println("Hope you had a refreshing break " + worker.getName() + ".");
      }

      // Add the worker to the ready queue and see if work can be
      // assigned.
      readyReplenishers.add(worker);
      tryToAssignReplenisher();

    } else {
      System.out.println("Incorrect input."); // Should not get called if
                                              // input format is correct.

    }

  }

  /**
   * Check to see if a picker is available to be assigned to a pick-request. If so, assign it.
   */
  private void tryToAssignPicker() {

    if (!(readyPickers.isEmpty()) && !(pickRequests.isEmpty())) {

      // Take a picker out of the queue and set its ready attribute to
      // false.
      Picker readyPicker = readyPickers.poll();
      readyPicker.setReady(false);

      // Take a pick request out of the queue and hand it to a picker.
      PickRequest pick = pickRequests.poll();
      readyPicker.setCurrentRequest(pick);

      // Tell the picker / system that the picker has been assigned.
      String id = pick.getId();
      System.out
          .println(readyPicker.toString() + " has been assigned pick request with ID " + id + ".");

      // Tell the picker to start picking the fascia of this request.
      commandPicker(readyPicker);
    }

  }

  /**
   * Command picker to get the next pick request or to go to marshalling.
   *
   * @param worker the picker to be ordered
   */
  private void commandPicker(Picker worker) {

    String name = worker.getName();
    PickRequest currRequest = worker.getCurrentRequest();
    String location = currRequest.getLocation();
    String sku = warehouse.getSkuFromLocation(location);
    Level level = warehouse.getLevel(sku);

    String toPrint;
    if (currRequest.isDone()) {
      toPrint = "Picker " + name + " go to Marshalling Area.";
    } else if (level.getCurrentStock() > 0) {
      toPrint = "Picker " + name + " go to '" + location + "' and pick " + sku + ".";
    } else {
      toPrint = "Picker " + name + " go to '" + location + "' and pick " + sku
          + " once it has been restocked.";
    }

    System.out.println(toPrint);

  }

  /**
   * Check if a sequencer can be assigned to a package in need of sequencing.
   */
  private void tryToAssignSequencer() {

    if (!(readySequencers.isEmpty()) && !(warehouse.marshallingIds.isEmpty())) {

      // Take a sequencer out of the queue and set its ready attribute to
      // false.
      Sequencer readySequencer = readySequencers.poll();
      readySequencer.setReady(false);

      // Take a pick request ID out of the queue and hand it to a
      // sequencer.
      String id = warehouse.marshallingIds.poll();
      PickRequest request = warehouse.getPickRequest(id);
      readySequencer.setCurrentRequest(request);

      // Tell the sequencer to sequence the fascia for that request.
      String name = readySequencer.getName();
      System.out.println("Sequencer " + name + " sequence pallets with ID " + id + ".");
    }

  }

  /**
   * Check if a loader can be assigned to pallets in need of loading.
   */
  private void tryToAssignLoader() {

    // Translate loadingCounter to its corresponding pick request ID string.
    String id = warehouse.loadingCounter + "pr";

    // Check if a loader is ready and if the next pallets to be loaded have
    // been sequenced (whether they're in the loading area).
    if (!(readyLoaders.isEmpty()) && warehouse.loadingIds.contains(id)) {

      // Take a loader out of the queue and set its ready attribute to
      // false.
      Loader readyLoader = readyLoaders.poll();
      readyLoader.setReady(false);

      // Take a pick request ID out of the queue and hand it to a loader.
      warehouse.loadingIds.remove(id);
      PickRequest request = warehouse.getPickRequest(id);
      readyLoader.setCurrentRequest(request);

      // Tell the loader to load the pallets with the given id.
      String name = readyLoader.getName();
      System.out.println("Loader " + name + " load pallets with ID " + id + ".");
    }
  }

  /**
   * Check if a replenisher can be assigned to a level in need of replenishment.
   */
  private void tryToAssignReplenisher() {

    if (!(readyReplenishers.isEmpty()) && !(lowLevels.isEmpty())) {

      // Get the SKU, the ready replenisher, and the location.
      String sku = lowLevels.poll();
      Replenisher readyReplenisher = readyReplenishers.poll();
      String location = warehouse.getLocation(sku);

      // Command the worker to replenish fascia at the given location.
      System.out.println(readyReplenisher.setTargetLocation(location));

    }

  }

  /**
   * Change the state of the system based on what the input says a particular worker picked. Through
   * this event, order the worker to do something if needed.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void workerActs(String[] event) {

    if (event.length < 3) {
      System.out.println("Incorrect input.");
    } else if (event[2].equals("picks")) { // Respond once a picker picks
                                           // something.

      pickingAct(event);
    } else if (event[2].equals("goes")) { // Respond once a picker goes to
                                          // the marshalling area.

      goesToMarshallingAct(event);
    } else if (event[2].equals("scans")) { // Respond once a sequencer
                                           // sequences
      scanningAct(event);
    } else if (event[2].equals("loads")) { // Respond once a loader loads
      loadingAct(event);
    } else if (event[2].equals("unloads")) { // Respond once a loader
                                             // unloads
      unloadingAct(event);
    } else if (event[2].equals("replenishes")) { // Respond once a
                                                 // replenisher
                                                 // replenishes

      replenishingAct(event);
    } else if (event[2].equals("takes")) { // Respond once a worker decides
                                           // to take a break.
      retiresAct(event);
    }

  }

  /**
   * Handle the event of a picker actually picking a fascia.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void pickingAct(String[] event) {

    // Update the warehouse system model and print to the console.
    Picker currPicker = (Picker) warehouse.getWorker(event[1]);
    String sku = event[3];
    String location = warehouse.getLocation(sku);
    System.out.println(currPicker.picks(sku, location));

    // Check if the level the picker picked from needs replenishing.
    // If so, try to assign a replenisher to replenish.
    Level level = warehouse.getLevel(event[3]);
    if (level.isNeedRestock()) {
      lowLevels.add(event[3]);
      tryToAssignReplenisher();
    }

    // Give the picker its next instructions.
    commandPicker(currPicker);

  }

  /**
   * Handle the event of a picker actually going to marshalling.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void goesToMarshallingAct(String[] event) {

    // Update the warehouse system model and print to the console.
    Picker currPicker = (Picker) warehouse.getWorker(event[1]);
    String id = currPicker.getCurrentRequest().getId();
    warehouse.marshallingIds.add(id);
    System.out.println("Picker " + currPicker.getName()
        + " went to the marshalling station with packet ID " + id + ".");
    currPicker.setReady(true);

    // Try to assign a Sequencer to the new package in the marshalling area.
    tryToAssignSequencer();

    // Free up the picker from its current request.
    currPicker.setReady(true);
    readyPickers.add(currPicker);
    // Try to assign the picker to another request.
    tryToAssignPicker();

  }

  /**
   * Handle the event of SKUs getting scanned by a ScanningWorker.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void scanningAct(String[] event) {

    // Only a sequencer or a loader can scan to the organizer.
    // So, break out of the function if another worker scanned.
    if (!event[0].equals("Sequencer") && !event[0].equals("Loader")) {
      System.out.print("Incorrect input.");
      return;
    }

    // Get an array of the scanned items and an array of what should have
    // been scanned.
    ScanningWorker currScanner = (ScanningWorker) warehouse.getWorker(event[1]);
    String[] scanned = Arrays.copyOfRange(event, 3, event.length);

    String id = currScanner.getPrId();
    String name = currScanner.getName();

    // Check to see if the scanned items were in the correct order.
    if (currScanner.scans(scanned)) { // Correct order.

      if (event[0].equals("Sequencer")) { // Worker is a sequencer.

        // Send pallets to the loading area.
        System.out.println(((Sequencer) currScanner).sendToLoading());

        // Try to assign a loader.
        tryToAssignLoader();

      } else if (event[0].equals("Loader")) { // Worker is a loader.

        // Command the loader to now load package onto truck.
        System.out.println(event[0] + event[1] + " load pallets with ID " + currScanner.getPrId()
            + " onto the truck.");

      }

    } else if (currScanner.getTriedOnce()) { // Incorrect order and already
                                             // double checked.

      // Command sequencer or loader to throw away the package.
      System.out.println("Pallet of ID " + id + " was picked incorrectly.\n" + name
          + " discard package with ID " + id);

      // Add the current request back to the pick request queue.
      PickRequest currRequest = warehouse.getPickRequest(id);
      currRequest.reset();
      pickRequests.add(currRequest);
      tryToAssignPicker(); // Check if any pickers are free to process
      // this new request.

      currScanner.setTriedOnce(false);

    } else { // Incorrect order but first scan

      currScanner.setTriedOnce(true);

      System.out.println("Scan of pallet of ID " + id + " gave a bad result.\n" + name
          + " rescan package of " + id + " to double check");
    }

    // Free up the sequencer in the model from its current PickRequest.
    if (event[0].equals("Sequencer")) {
      currScanner.setReady(true);
      readySequencers.add((Sequencer) currScanner);
      tryToAssignSequencer();
    }

    // The loader is not yet free, since it must still load after scanning.

  }

  /**
   * Handle the event of pallets being loaded onto a truck.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void loadingAct(String[] event) {

    // Get a reference to the loader.
    Loader currLoader = (Loader) warehouse.getWorker(event[1]);

    // Update the model to reflect the loading act.
    String id = event[3];
    String msg = currLoader.loads(id);
    System.out.println(msg);

    // Make the loader ready for work, and try to assign it some.
    currLoader.setReady(true);
    readyLoaders.add(currLoader);
    tryToAssignLoader();
  }

  /**
   * Handle the event of pallets actually being unloaded from a truck.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void unloadingAct(String[] event) {
    // Get a reference to the loader.
    Loader currLoader = (Loader) warehouse.getWorker(event[1]);
    // Update the model to reflect the unloading act.
    String id = event[3];
    System.out.println(currLoader.unloads(id));

    // An unloading act does not affect a loader's readiness;
    // it can unload anytime, even when assigned pallets to load.
    // Check if a loader needs to now load this unloaded package.
    tryToAssignLoader();

  }

  /**
   * Handle the event of a replenisher actually restocking a shelf.
   *
   * @param event a line of input from the input file in the form of an array.
   */
  private void replenishingAct(String[] event) {

    // Get a reference to the replenisher.
    Replenisher currReplenisher = (Replenisher) warehouse.getWorker(event[1]);

    // Update warehouse model to reflect replenishment and print to console.
    // event[3] is a location
    String location = event[3] + " " + event[4] + " " + event[5] + " " + event[6];
    System.out.println(currReplenisher.replenishes(location));

  }

  /**
   * Workers can only take breaks if they are idle.
   * 
   * @param event the input event
   */
  private void retiresAct(String[] event) {

    // Get a reference to the worker.
    Worker currWorker = (Worker) warehouse.getWorker(event[1]);

    // Set the worker's ready attribute to false.
    currWorker.setReady(false);

    // Remove the worker from its appropriate queue
    if (event[0].equals("Picker")) {
      readyPickers.remove(currWorker);
    } else if (event[0].equals("Sequencer")) {
      readySequencers.remove(currWorker);
    } else if (event[0].equals("Loader")) {
      readyLoaders.remove(currWorker);
    } else if (event[0].equals("Replenisher")) {
      readyReplenishers.remove(currWorker);
    }
  }

  /**
   * Creates a file that displays the stocks of all levels that have <30 fascia.
   */

  private void createInventoryLeftFile() {
    String newLineChar = System.getProperty("line.separator");
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream("final.csv"), "utf-8"))) {
      for (int zone = 0; zone < 2; zone++) {
        for (int aisle = 0; aisle < 2; aisle++) {
          for (int rack = 0; rack < 3; rack++) {
            for (int level = 0; level < 4; level++) {

              String zoneStr;
              if (zone == 0) {
                zoneStr = "A";
              } else {
                zoneStr = "B";
              }

              String currSku =
                  warehouse.getSkuFromLocation(zoneStr + " " + aisle + " " + rack + " " + level);
              Level currLevel = warehouse.getLevel(currSku);

              if (currLevel.getCurrentStock() < 30) {
                writer.write(zoneStr + "," + aisle + "," + rack + "," + level + ","
                    + currLevel.getCurrentStock() + newLineChar);
              }

            }
          }
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();

    }

  }

  /**
   * Create a file that displays all the orders that made their way onto a truck.
   */

  private void createOrdersTxt() throws IOException {
    String newLineChar = System.getProperty("line.separator");
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream("orders.csv"), "utf-8"))) {
      for (PickRequest pr : warehouse.getLoadedRequests()) {
        for (Order o : pr.getOrders()) {

          writer.write(o.toString() + newLineChar);

        }
      }
    }
  }

  /**
   * Entry point of program. Read the input file and call handle(event: String) method on each line.
   *
   * @param args args[0] is a the input file
   * @throws IOException end of file
   */
  public static void main(String[] args) throws IOException {

    // Instantiate a warehouse and it's organizer
    Warehouse warehouse = new Warehouse();
    Organizer organizer = new Organizer(warehouse);

    // Read the input from the provided text file, and pass them to the
    // handle method.
    String line = null;
    try {
      System.out.println(args[0]);
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      while ((line = reader.readLine()) != null) {
        organizer.handle(line);
      }
      reader.close();
    } catch (FileNotFoundException ex) {
      System.out.println("File not found.");
    } catch (IOException ex) {
      System.out.println("Finished reading input file.");
    }

    // Write the levels that are not full to a .csv file with their stock.
    organizer.createInventoryLeftFile();
    // Write the orders that were loaded into trucks to a .csv file.
    organizer.createOrdersTxt();

  }

}

/** @author Clara MCTC Java Programming Class */

import java.util.LinkedList;

public class InventoryController {
    

    static InventoryModel db ;


    public static void main(String[] args) {

        //Add a shutdown hook.
        //http://hellotojavaworld.blogspot.com/2010/11/runtimeaddshutdownhook.html
        AddShutdownHook closeDBConnection = new AddShutdownHook();
        closeDBConnection.attachShutdownHook();
        //Can put code in here to try to shut down the DB connection in a tidy manner if possible

        try {
            InventoryController controller = new InventoryController();


            db = new InventoryModel(controller);

            boolean setup = db.setupDatabase();
            if (setup == false) {
                System.out.println("Error setting up database, see error messages. Clean up database connections.... Quitting program ");

                db.cleanup();

                System.out.println("Quitting program ");

                System.exit(-1);   //Non-zero exit codes indicate errors.
            }

            new InventoryView(controller).launchUI();
        }

        finally {
            if (db != null) {
                db.cleanup();
            }
        }

    }

    public String requestAddLaptop(Laptop l) {

        //This message should arrive from the UI. Send a message to the db to request that this laptop is added.
        //Return error message, if any. Return null if transaction was successful.
        boolean success = db.addLaptop(l);
        if (success == true ) {
            return null;   //Null means all was well.
        }
        else {
            return "Unable to add laptop to database";
        }

    }

    public LinkedList<Laptop> requestAllInventory() {


        LinkedList<Laptop> allLaptops = db.displayAllLaptops();
        if (allLaptops == null ) {
            System.out.println("Controller detected error in fetching laptops from database");
            return null;   //Null means error. View can deal with how to display error to user.
        }
        else {
            return allLaptops;
        }


    }

}

class AddShutdownHook {
    public void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown hook: program closed, attempting to shut database connection");
                //Unfortunately this doesn't seem to be called when a program is restarted in eclipse.
                //Avoid restarting your programs. If you do, and you get an existing connection error you can either
                // 1. restart eclipse - Menu > Restart
                // 2. Delete your database folder. In this project it's a folder called laptopinventoryDB (or similar) in the root directory of your project.
                InventoryController.db.cleanup();
            }
        });
    }
}

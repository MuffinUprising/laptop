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

    //request edit laptop method
    public String requestEditLaptop(int idNumber, String staff) {

        boolean success = db.editLaptop(idNumber, staff);
        if(success == true) {
            return null;
        } else {
            return "Unable to edit laptop in database.";
        }
    }

    //request edit cellphone method
    public String requestEditCellphone(int idNumber, String staff) {

        boolean success = db.editCellphone(idNumber, staff);
        if(success == true) {
            return null;
        } else {
            return "Unable to edit cellphone in database.";
        }
    }

    //request retire laptop method
    public String requestRetireLaptop(int idNumber) {

        boolean success = db.retireLaptop(idNumber);
        if(success == true) {
            return null;
        } else {
            return "Unable to retire laptop from database.";
        }
    }

    //request retire cellphone method
    public String requestRetireCellphone(int idNumber) {

        boolean success = db.retireCellphone(idNumber);
        if(success == true) {
            return null;
        } else {
            return "Unable to retire laptop from database.";
        }
    }

    //request add laptop method
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

    //request add cellphone method
    public String requestAddCellphone(Cellphone c) {

        //This message should arrive from the UI. Send a message to the db to request that this laptop is added.
        //Return error message, if any. Return null if transaction was successful.
        boolean success = db.addCellphone(c);
        if (success == true ) {
            return null;   //Null means all was well.
        }
        else {
            return "Unable to add cellphone to database";
        }

    }

    //request all laptops method
    public LinkedList<Laptop> requestAllLaptopInventory() {

        //LinkedList for all laptops
        LinkedList<Laptop> allLaptops = db.displayAllLaptops();
        if (allLaptops == null ) {
            System.out.println("Controller detected error in fetching laptops from database");
            return null;   //Null means error. View can deal with how to display error to user.
        }
        else {
            return allLaptops;
        }
    }

    //request all cellphones method
    public LinkedList<Cellphone> requestAllCellphoneInventory() {

        //LinkedList for all cellphones
        LinkedList<Cellphone> allCellphones = db.displayAllCellphones();
        if (allCellphones == null ) {
            System.out.println("Controller detected error in fetching cellphones from database");
            return null;
        }
        else {
            return allCellphones;
        }
    }

    //rewuest all laptops for user method
    public LinkedList<Laptop> requestAllLaptopInvUser(String staff) {

        //LinkedList for laptops for user
        LinkedList<Laptop> allLaptopsForUser = db.displayAllLaptopsUser(staff);
        if (allLaptopsForUser == null ) {
            System.out.println("Controller detected error in fetching laptops from database");
            return null;   //Null means error. View can deal with how to display error to user.
        }
        else {
            return allLaptopsForUser;
        }
    }

    //request all cellphones for user method
    public LinkedList<Cellphone> requestAllCellphoneInvUser(String staff) {

        //LinkedList for cellphones for user
        LinkedList<Cellphone> allCellphonesForUser = db.displayAllCellphonesUser(staff);
        if (allCellphonesForUser == null ) {
            System.out.println("Controller detected error in fetching cellphones from database");
            return null;   //Null means error. View can deal with how to display error to user.
        }
        else {
            return allCellphonesForUser;
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

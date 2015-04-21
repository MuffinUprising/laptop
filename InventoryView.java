/** @author Clara MCTC Java Programming Class */

import javafx.scene.control.Cell;

import java.util.LinkedList;
import java.util.Scanner;

public class InventoryView {

    private final int QUIT = 9;   //Modify if you add more menu items.
    //Can you think of a more robust way of handling menu optons which would be easy to modify with a varying number of menu choices?

    InventoryController myController;
    Scanner s;

    InventoryView(InventoryController c) {
        myController = c;
        s = new Scanner(System.in);
    }


    public void launchUI() {
        //This is a text-based UI. Probably a GUI in a real program

        while (true) {

            int userChoice = displayMenuGetUserChoice();
            if (userChoice == QUIT ) {
                break;
            }

            doTask(userChoice);
        }

    }

    //switch cases
    private void doTask(int userChoice) {

        switch (userChoice) {

            case 1:  {
                displayAllInventory();
                break;
            }
            case 2: {
                addNewLaptop();
                break;
            }
            case 3 : {
                reassignLaptop();
                break;
            }
            case 4 : {
                retireLaptop();
                break;
            }
            case 5: {
                addNewCellphone();
            }
            case 6: {
                reassignCellphone();
            }
            case 7: {
                retireCellphone();
            }
            case 8: {
                displayAllForStaff();
            }
        }

    }

    // add new laptop method
    private void addNewLaptop() {

        //Get data about new laptop from user

        System.out.println("Please enter make of laptop (e.g. Toshiba, Sony) : ");
        String make = s.nextLine();

        System.out.println("Please enter model of laptop (e.g. MacBook, X-123) : ");
        String model = s.nextLine();

        System.out.println("Please enter name of staff member laptop is assigned to : ");
        String staff = s.nextLine();

        //new laptop
        Laptop l = new Laptop(make, model, staff);


        String errorMessage = myController.requestAddLaptop(l);

        if (errorMessage == null ) {
            System.out.println("New laptop added to database");
        } else {
            System.out.println("New laptop could not be added to database");
            System.out.println(errorMessage);
        }
        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //Add new cellphone method
    private void addNewCellphone() {

        //Get data about new laptop from user

        System.out.println("Please enter make of cellphone (e.g. LG, Apple) : ");
        String make = s.nextLine();

        System.out.println("Please enter model of cellphone (e.g. Galaxy S, iPhone 6) : ");
        String model = s.nextLine();

        System.out.println("Please enter name of staff member cellphone is assigned to : ");
        String staff = s.nextLine();

        //new cellphone
        Cellphone c = new Cellphone(make, model, staff);


        String errorMessage = myController.requestAddCellphone(c);

        if (errorMessage == null ) {
            System.out.println("New cellphone added to database");
        } else {
            System.out.println("New cellphone could not be added to database");
            System.out.println(errorMessage);
        }
        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //Display all inventory method
    private void displayAllInventory() {

        //LinkedLists for devices
        LinkedList<Laptop> allLaptops = myController.requestAllLaptopInventory();
        LinkedList<Cellphone> allCellphones = myController.requestAllCellphoneInventory();

        if (allLaptops == null && allCellphones == null) {
            System.out.println("Error fetching all laptops and cellphones from the database");
        } else if (allLaptops.isEmpty() && allCellphones.isEmpty()) {
            System.out.println("No laptops or cellphones found in database");
        } else {
            for (Laptop l : allLaptops) {
                System.out.println(l);   //Call the toString method in Laptop
            }
            for (Cellphone c : allCellphones) {
                System.out.println(c);
            }
        }
        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();
    }

    //reassign laptop method
    private void reassignLaptop() {

        // LinkedList for laptops
        LinkedList<Laptop> allLaptops = myController.requestAllLaptopInventory();
        if (allLaptops == null) {
            System.out.println("Error fetching all laptops from the database");
        } else if (allLaptops.isEmpty()) {
            System.out.println("No laptops found in database");
        } else {
            for (Laptop l : allLaptops) {
                System.out.println(l);   //Call the toString method in Laptop
            }
        }
        // fetch laptop ticket from user input
        System.out.println("Please enter the ID number of the laptop: ");
        int idNumber = s.nextInt();
        s.nextLine();

        // request new employee input
        System.out.println("Enter the staff member's name to reassign laptop to: ");
        String staff = s.nextLine();

        myController.requestEditLaptop(idNumber, staff);
        System.out.println("Laptop with ID " + idNumber + " reassigned to " + staff + ".");

        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //reassign cellphone method
    private void reassignCellphone() {

        //LinkedList for cellphones
        LinkedList<Cellphone> allCellphones = myController.requestAllCellphoneInventory();

        if (allCellphones == null) {
            System.out.println("Error fetching all cellphones from the database");
        } else if (allCellphones.isEmpty()) {
            System.out.println("No cellphones found in database");
        } else {
            for (Cellphone c : allCellphones) {
                System.out.println(c);   //Call the toString method in Cellphone
            }
        }
        // fetch cellphone ticket from user input
        System.out.println("Please enter the ID number of the cellphone: ");
        int idNumber = s.nextInt();

        // request new employee input
        System.out.println("Enter the staff member's name to reassign cellphone to: ");
        String staff = s.nextLine();

        myController.requestEditCellphone(idNumber, staff);
        System.out.println("Cellphone with ID " + idNumber + " reassigned to " + staff + ".");

        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //retire laptop method
    private void retireLaptop() {

        //LinkedList for laptops
        LinkedList<Laptop> allLaptops = myController.requestAllLaptopInventory();

        if (allLaptops == null) {
            System.out.println("Error fetching all laptops from the database");
        } else if (allLaptops.isEmpty()) {
            System.out.println("No laptops found in database");
        } else {
            for (Laptop l : allLaptops) {
                System.out.println(l);   //Call the toString method in Laptop
            }
        }
        // fetch laptop ticket from user input
        System.out.println("Please enter the ID number of the laptop to retire: ");
        int idNumber = s.nextInt();

        myController.requestRetireLaptop(idNumber);
        System.out.println("Laptop  with ID " + idNumber + " retired. ");

        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //retire cellphone method
    private void retireCellphone() {

        //LinkedList for cellphones
        LinkedList<Cellphone> allCellphones = myController.requestAllCellphoneInventory();

        if (allCellphones == null) {
            System.out.println("Error fetching all cellphones from the database");
        } else if (allCellphones.isEmpty()) {
            System.out.println("No cellphones found in database");
        } else {
            for (Cellphone c : allCellphones) {
                System.out.println(c);   //Call the toString method in Cellphone
            }
        }
        // fetch laptop ticket from user input
        System.out.println("Please enter the ID number of the cellphone to retire: ");
        int idNumber = s.nextInt();

        myController.requestRetireCellphone(idNumber);
        System.out.println("Cellphone  with ID " + idNumber + " retired. ");
        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();
        displayMenuGetUserChoice();

    }

    //display all devices for a staff member
    private void displayAllForStaff(){

        System.out.println("Enter the name of the staff member to display devices for: ");
        String staff = s.next();

        //LinkedLists for data
        LinkedList<Laptop> allLaptopsUser = myController.requestAllLaptopInvUser(staff);
        LinkedList<Cellphone> allCellphonesUser = myController.requestAllCellphoneInvUser(staff);

        if (allLaptopsUser == null && allCellphonesUser == null) {
            System.out.println("Error fetching all laptops and cellphones from the database");
        } else if (allLaptopsUser.isEmpty() && allCellphonesUser.isEmpty()) {
            System.out.println("No laptops or cellphones found in database");
        } else {
            for (Laptop l : allLaptopsUser) {
                System.out.println(l);   //Call the toString method in Laptop
            }
            for (Cellphone c : allCellphonesUser) {
                System.out.println(c);  //Call toString method in Cellphone
            }
        }
        //allow user to view data
        System.out.println("Hit enter to continue");
        s.nextLine();

        displayMenuGetUserChoice();

    }


    private int displayMenuGetUserChoice() {

        boolean inputOK = false;
        int userChoice = 0;

        while (!inputOK) {

            System.out.println("1. View all inventory");
            System.out.println("~~LAPTOPS~~");
            System.out.println("2. Add a new laptop");
            System.out.println("3. Reassign a laptop to another staff member");
            System.out.println("4. Retire a laptop");
            System.out.println("~~CELLPHONES~~");
            System.out.println("5. Add a new cellphone");
            System.out.println("6. Reassign a cellphone to another staff member");
            System.out.println("7. Retire a cellphone");
            System.out.println("8. View all results for a staff member");
            System.out.println(QUIT + ". Quit program");

            System.out.println();
            System.out.println("Please enter your selection: ");

            String userChoiceStr = s.nextLine();
            s.nextLine();
            try {
                userChoice = Integer.parseInt(userChoiceStr);
                if (userChoice < 1  ||  userChoice > 9) {
                    System.out.println("Please enter a number between 1 and 9");
                    continue;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a number");
                continue;
            }
            inputOK = true;

        }

        return userChoice;

    }
}
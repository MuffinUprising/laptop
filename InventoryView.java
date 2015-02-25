/** @author Clara MCTC Java Programming Class */

import java.util.LinkedList;
import java.util.Scanner;

public class InventoryView {

    private final int QUIT = 5;   //Modify if you add more menu items.
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
                System.out.println("Reassign laptop - Not yet implemented");
                break;
            }
            case 4 : {
                System.out.println("Retire laptop - Not yet implemented");
                break;
            }
        }

    }


    private void addNewLaptop() {

        //Get data about new laptop from user

        System.out.println("Please enter make of laptop (e.g. Toshiba, Sony) : ");
        String make = s.nextLine();

        System.out.println("Please enter make of laptop (e.g. Macbook, X-123) : ");
        String model = s.nextLine();

        System.out.println("Please enter name of staff member laptop is assigned to : ");
        String staff = s.nextLine();

        Laptop l = new Laptop(make, model, staff);


        String errorMessage = myController.requestAddLaptop(l);

        if (errorMessage == null ) {
            System.out.println("New laptop added to database");
        } else {
            System.out.println("New laptop could not be added to database");
            System.out.println(errorMessage);
        }

    }


    private void displayAllInventory() {

        LinkedList<Laptop> allLaptops = myController.requestAllInventory();
        if (allLaptops == null) {
            System.out.println("Error fetching all laptops from the database");
        } else if (allLaptops.isEmpty()) {
            System.out.println("No laptops found in database");
        } else {
            for (Laptop l : allLaptops) {
                System.out.println(l);   //Call the toString method in Laptop
            }
        }
    }



    private int displayMenuGetUserChoice() {

        boolean inputOK = false;
        int userChoice = -1;

        while (!inputOK) {

            System.out.println("1. View all inventory");
            System.out.println("2. Add a new laptop");
            System.out.println("3. To be added - reassign a laptop to another staff member");
            System.out.println("4. To be added - retire a laptop");
            System.out.println(QUIT + ". Quit program");

            System.out.println();
            System.out.println("Please enter your selection");

            String userChoiceStr = s.nextLine();
            try {
                userChoice = Integer.parseInt(userChoiceStr);
                if (userChoice < 1  ||  userChoice > 5) {
                    System.out.println("Please enter a number between 1 and 5");
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
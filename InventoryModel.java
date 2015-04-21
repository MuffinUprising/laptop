/** @author Clara MCTC Java Programming Class */

import com.sun.prism.impl.ps.CachingEllipseRep;
import sun.awt.image.ImageWatched;

import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.util.LinkedList;


public class InventoryModel {

    // JDBC driver name, protocol, used to create a connection to the DB
    private static String protocol = "jdbc:derby:";
    private static String dbName = "deviceInventoryDB";


    //  Database credentials - for embedded, usually defaults. A client-server DB would need to authenticate connections
    private static final String USER = "temp";
    private static final String PASS = "password";


    InventoryController myController;

    Statement statement = null;

    Connection conn = null;

    ResultSet rs = null;

    LinkedList<Statement> allStatements = new LinkedList<Statement>();

    PreparedStatement psAddLaptop = null;
    PreparedStatement psRetireLaptop = null;
    PreparedStatement psEditLaptop = null;

    PreparedStatement psAddCellphone = null;
    PreparedStatement psRetireCellphone = null;
    PreparedStatement psEditCellphone = null;

    PreparedStatement psDisplayAllLaptopsUser = null;
    PreparedStatement psDisplayAllCellsUser = null;


    public InventoryModel(InventoryController controller) {

        this.myController = controller;
    }


    public boolean setupDatabase() {
        return setupDatabase(false);
    }

    //Set up database method
    public boolean setupDatabase(boolean deleteAndRecreate) {
        // TODO Auto-generated method stub

        try {
            createConnection();
        } catch (Exception e) {

            System.err.println("Unable to connect to database. Error message and stack trace follow");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }


        try {
            createTables(deleteAndRecreate);

        } catch (SQLException sqle) {
            System.err.println("Unable to create database. Error message and stack trace follow");
            System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
            sqle.printStackTrace();
            return false;
        }


        //Remove the test data for real program
        try {
            //addTestData();
        }
        catch (Exception sqle) {

            System.err.println("Unable to add test data to database. Error message and stack trace follow");
            System.err.println(sqle.getMessage());
            sqle.printStackTrace();
            return false;
        }

        //At this point, it seems like everything worked.

        return true;
    }


    //Create tables method
    private void createTables(boolean deleteAndRecreate) throws SQLException {

        //create laptop table
        String createLaptopTableSQL = "CREATE TABLE laptops (id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1,INCREMENT BY 1), make varchar(30), model varchar(30), staff varchar(50))";
        String deleteLaptopTableSQL = "DROP TABLE laptops";

        //create cellphone table
        String createCellphoneTableSQL = "CREATE TABLE cellphones (id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), make varchar(30), model varchar(30), staff varchar(50))";
        String deleteCellTableSQL = "DROP TABLE cellphones";

        try {
            statement.executeUpdate(createLaptopTableSQL);
            System.out.println("Created laptop table");
            statement.executeUpdate(createCellphoneTableSQL);
            System.out.println("Created cellphone table");

        } catch (SQLException sqle) {
            //Seems the table already exists, or some other error has occurred.
            //Let's try to check if the DB exists already by checking the error code returned. If so, delete it and re-create it


            if (sqle.getSQLState().startsWith("X0") ) {    //Error code for table already existing starts with XO
                if (deleteAndRecreate == true) {

                    System.out.println("laptops table appears to exist already, delete and recreate");
                    try {
                        statement.executeUpdate(deleteLaptopTableSQL);
                        statement.executeUpdate(createLaptopTableSQL);
                        statement.executeUpdate(deleteCellTableSQL);
                        statement.executeUpdate(createCellphoneTableSQL);
                    } catch (SQLException e) {
                        //Still doesn't work. Throw the exception.
                        throw e;
                    }
                } else {
                    //do nothing - if the table exists, leave it be.
                }

            } else {
                //Something else went wrong. If we can't create the table, no point attempting
                //to run the rest of the code. Throw the exception again to be handled elsewhere. of the program.
                throw sqle;
            }
        }
    }



    private void createConnection() throws Exception {

        try {
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);
            statement = conn.createStatement();
            allStatements.add(statement);
        } catch (Exception e) {
            //There are a lot of things that could go wrong here. Should probably handle them all separately but have not done so here.
            //Should put something more helpful here...
            throw e;
        }

    }


    private void addTestData() throws Exception {
        // Test data.
        if (statement == null) {
            //This isn't going to work
            throw new Exception("Statement not initialized");
        }
        try {
            String addRecord1 = "INSERT INTO laptops (make, model, staff) VALUES ('Toshiba', 'XQ-45', 'Ryan')" ;
            statement.executeUpdate(addRecord1);
            String addRecord2 = "INSERT INTO laptops (make, model, staff) VALUES ('Sony', '1234', 'Jane')" ;
            statement.executeUpdate(addRecord2);
            String addRecord3 = "INSERT INTO laptops (make, model, staff) VALUES ('Apple', 'Air', 'Alex')" ;
            statement.executeUpdate(addRecord3);

            String addRecord4 = "INSERT INTO cellphones (make, model, staff) VALUES ('LG', 'GX4', 'Ryan')" ;
            statement.executeUpdate(addRecord4);
            String addRecord5 = "INSERT INTO cellphones (make, model, staff) VALUES ('Motorola', 'G2', 'Jane')" ;
            statement.executeUpdate(addRecord5);
            String addRecord6 = "INSERT INTO cellphones (make, model, staff) VALUES ('Apple', 'iPhone 6', 'Alex')" ;
            statement.executeUpdate(addRecord6);
        }
        catch (SQLException sqle) {
            System.err.println("Unable to add test data, check validity of SQL statements?");
            System.err.println("Unable to create database. Error message and stack trace follow");
            System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
            sqle.printStackTrace();

            throw sqle;
        }
    }

    //cleanup method
    public void cleanup() {
        try {
            if (rs != null) {
                rs.close();  //Close result set
                System.out.println("ResultSet closed");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        //Close all of the statements. Stored a reference to each statement in allStatements so we can loop over all of them and close them all.
        for (Statement s : allStatements) {

            if (s != null) {
                try {
                    s.close();
                    System.out.println("Statement closed");
                } catch (SQLException se) {
                    System.out.println("Error closing statement");
                    se.printStackTrace();
                }
            }
        }

        try {
            if (conn != null) {
                conn.close();  //Close connection to database
                System.out.println("Database connection closed");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

    }


    //Add laptop method
    public boolean addLaptop(Laptop laptop) {

        //Create SQL query to add this laptop info to DB

        String addLaptopSQLps = "INSERT INTO laptops (make, model, staff) VALUES ( ? , ? , ?)" ;
        try {
            psAddLaptop = conn.prepareStatement(addLaptopSQLps);
            allStatements.add(psAddLaptop);
            psAddLaptop.setString(1, laptop.getMake());
            psAddLaptop.setString(2, laptop.getModel());
            psAddLaptop.setString(3, laptop.getStaff());

            psAddLaptop.execute();
        }
        catch (SQLException sqle) {
            System.err.println("Error preparing statement or executing prepared statement to add laptop");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return false;
        }
        return true;
    }

    //Add cellphone method
    public boolean addCellphone(Cellphone cellphone) {
        //Create SQL query to add this cellphone info to DB
        String addCellphonesSQLps = "INSERT INTO cellphones (make, model, staff) VALUES ( ? , ? , ?)" ;
        try {
            psAddCellphone = conn.prepareStatement(addCellphonesSQLps);
            allStatements.add(psAddCellphone);
            psAddCellphone.setString(1, cellphone.getMake());
            psAddCellphone.setString(2, cellphone.getModel());
            psAddCellphone.setString(3, cellphone.getStaff());

            psAddCellphone.execute();
        }
        catch (SQLException sqle) {
            System.err.println("Error preparing statement or executing prepared statement to add cellphone");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return false;
        }
        return true;
    }

    //Display all laptops methods
    public LinkedList<Laptop> displayAllLaptops() {

        //LinkedList for laptops
        LinkedList<Laptop> allLaptops = new LinkedList<Laptop>();

        String displayAll = "SELECT * FROM laptops";
        try {
            rs = statement.executeQuery(displayAll);
        }
        catch (SQLException sqle) {
            System.err.println("Error fetching all laptops");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }

        try {
            while (rs.next()) {
                //add laptops to LinkedList
                int id = rs.getInt("id");
                String make = rs.getString("make");
                String model = rs.getString("model");
                String staff = rs.getString("staff");
                Laptop l = new Laptop(id, make, model, staff);
                allLaptops.add(l);

            }
        } catch (SQLException sqle) {
            System.err.println("Error reading from result set after fetching all laptop data");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;

        }

        //if we get here, everything should have worked...
        //Return the list of laptops, which will be empty if there is no data in the database
        return allLaptops;
    }

    //display all cellphones method
    public LinkedList<Cellphone> displayAllCellphones() {

        //LinkedList for cellphones
        LinkedList<Cellphone> allCellphones = new LinkedList<Cellphone>();

        String displayAll = "SELECT * FROM cellphones";
        try {
            rs = statement.executeQuery(displayAll);
        }
        catch (SQLException sqle) {
            System.err.println("Error fetching all cellphones");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }

        try {
            while (rs.next()) {

                //add cellphones to LinkedList
                int id = rs.getInt("id");
                String make = rs.getString("make");
                String model = rs.getString("model");
                String staff = rs.getString("staff");
                Cellphone c = new Cellphone(id, make, model, staff);
                allCellphones.add(c);

            }
        } catch (SQLException sqle) {
            System.err.println("Error reading from result set after fetching all laptop data");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }

        //if we get here, everything should have worked...
        //Return the list of laptops, which will be empty if there is no data in the database
        return allCellphones;
    }

    //reassign laptop method
    public boolean editLaptop(int idNumber, String staff) {

        String editLaptopSQLps = "UPDATE laptops SET staff = ? WHERE id = ?" ;


        try{
            psEditLaptop = conn.prepareStatement(editLaptopSQLps);
            allStatements.add(psEditLaptop);
            psEditLaptop.setString(1, staff);
            psEditLaptop.setInt(2, idNumber);

            psEditLaptop.executeUpdate();

        }catch(SQLException sqe) {
            System.out.println(sqe);
            return false;
        }

        return true;
    }

    //reassign cellphone method
    public boolean editCellphone(int idNumber, String staff) {

        String editCellphoneSQLps = "UPDATE cellphones SET staff = ? WHERE id = ?" ;


        try{
            psEditCellphone = conn.prepareStatement(editCellphoneSQLps);
            allStatements.add(psEditCellphone);
            psEditCellphone.setString(1, staff);
            psEditCellphone.setInt(2, idNumber);

            psEditCellphone.executeUpdate();

        }catch(SQLException sqe) {
            System.out.println(sqe);
            return false;
        }

        return true;
    }

    //retire laptop method
    public boolean retireLaptop(int idNumber) {

        String retire = "DELETE FROM laptops WHERE id =?";
        try{
            psRetireLaptop = conn.prepareStatement(retire);
            allStatements.add(psRetireLaptop);
            psRetireLaptop.setInt(1, idNumber);
            psRetireLaptop.executeUpdate();

        }catch (SQLException sqe) {
            System.out.println(sqe);
            return false;
        }
        return true;

    }

    //retire cellphone method
    public boolean retireCellphone(int idNumber) {

        String retire = "DELETE FROM cellphones WHERE id =?";
        try{
            psRetireCellphone = conn.prepareStatement(retire);
            allStatements.add(psRetireCellphone);
            psRetireCellphone.setInt(1, idNumber);
            psRetireCellphone.executeUpdate();

        }catch (SQLException sqe) {
            System.out.println(sqe);
            return false;
        }
        return true;

    }

    //Display all laptops for user method
    //Could not figure out how to se join statement because each method needs a respective LinkedList
    //<Cellphone> for cellphones and <Laptop> for laptops
    public LinkedList<Laptop> displayAllLaptopsUser(String staff) {

        LinkedList<Laptop> allLaptopsUser = new LinkedList<Laptop>();

        String displayAll = "SELECT * FROM laptops WHERE staff =?";



        try {
            psDisplayAllLaptopsUser = conn.prepareStatement(displayAll);
            psDisplayAllLaptopsUser.setString(1, staff);
            rs = psDisplayAllLaptopsUser.executeQuery();

        }
        catch (SQLException sqle) {
            System.err.println("Error fetching all laptops for staff member");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }


        try {
            while (rs.next()) {

                int id = rs.getInt("id");
                String make = rs.getString("make");
                String model = rs.getString("model");
                Laptop l = new Laptop(id, make, model, staff);
                allLaptopsUser.add(l);

            }
        } catch (SQLException sqle) {
            System.err.println("Error reading from result set after fetching all laptop data for staff member");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;

        }

        //if we get here, everything should have worked...
        //Return the list of laptops, which will be empty if there is no data in the database
        return allLaptopsUser;
    }

    //Display all cellphones per user
    //Could not figure out how to se join statement because each method needs a respective LinkedList
    //<Cellphone> for cellphones and <Laptop> for laptops
    public LinkedList<Cellphone> displayAllCellphonesUser(String staff) {

        LinkedList<Cellphone> allCellphonesUser = new LinkedList<Cellphone>();

        String displayAll = "SELECT * FROM cellphones WHERE staff =?";

        try {
            psDisplayAllCellsUser = conn.prepareStatement(displayAll);
            psDisplayAllCellsUser.setString(1, staff);
            rs = psDisplayAllCellsUser.executeQuery();
        }
        catch (SQLException sqle) {
            System.err.println("Error fetching all cellphones for staff member");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }

        try {
            while (rs.next()) {

                int id = rs.getInt("id");
                String make = rs.getString("make");
                String model = rs.getString("model");
                Cellphone c = new Cellphone(id, make, model, staff);
                allCellphonesUser.add(c);
            }
        } catch (SQLException sqle) {
            System.err.println("Error reading from result set after fetching all cellphone data for staff member");
            System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
            sqle.printStackTrace();
            return null;
        }

        //if we get here, everything should have worked...
        //Return the list of laptops, which will be empty if there is no data in the database
        return allCellphonesUser;
    }
}




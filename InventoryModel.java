/** @author Clara MCTC Java Programming Class */

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
    private static String dbName = "laptopInventoryDB";



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


    public InventoryModel(InventoryController controller) {

        this.myController = controller;

    }


    public boolean setupDatabase() {
        return setupDatabase(false);
    }

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
            createTable(deleteAndRecreate);
        } catch (SQLException sqle) {
            System.err.println("Unable to create database. Error message and stack trace follow");
            System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
            sqle.printStackTrace();
            return false;
        }


        //Remove the test data for real program
        try {
            addTestData();
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



    private void createTable(boolean deleteAndRecreate) throws SQLException {


        String createLaptopTableSQL = "CREATE TABLE laptops (id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, make varchar(30), model varchar(30), staff varchar(50))";
        String deleteTableSQL = "DROP TABLE laptops";

        try {
            statement.executeUpdate(createLaptopTableSQL);
            System.out.println("Created laptop table");

        } catch (SQLException sqle) {
            //Seems the table already exists, or some other error has occurred.
            //Let's try to check if the DB exists already by checking the error code returned. If so, delete it and re-create it


            if (sqle.getSQLState().startsWith("X0") ) {    //Error code for table already existing starts with XO
                if (deleteAndRecreate == true) {

                    System.out.println("laptops table appears to exist already, delete and recreate");
                    try {
                        statement.executeUpdate(deleteTableSQL);
                        statement.executeUpdate(createLaptopTableSQL);
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
            String addRecord1 = "INSERT INTO laptops (make, model, staff) VALUES ('Toshiba', 'XQ-45', 'Ryan' )" ;
            statement.executeUpdate(addRecord1);
            String addRecord2 = "INSERT INTO laptops (make, model, staff) VALUES ('Sony', '1234', 'Jane' )" ;
            statement.executeUpdate(addRecord2);
            String addRecord3 = "INSERT INTO laptops (make, model, staff) VALUES ('Apple', 'Air', 'Alex' )" ;
            statement.executeUpdate(addRecord3);
        }
        catch (SQLException sqle) {
            System.err.println("Unable to add test data, check validity of SQL statements?");
            System.err.println("Unable to create database. Error message and stack trace follow");
            System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
            sqle.printStackTrace();

            throw sqle;
        }
    }




    public void cleanup() {
        // TODO Auto-generated method stub
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


    /** Returns null if any errors in fetching laptops
     *  Returns empty list if no laptops in DB
     *
     */
    public LinkedList<Laptop> displayAllLaptops() {

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
}




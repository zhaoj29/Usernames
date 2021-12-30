import java.security.*;
import java.io.Console;
import java.math.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.io.*;
import java.util.*;

public class User {

    //connect to database file 
	private static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/[INSERT PATH]/[FILE NAME].db"; //should put this as param
            // create a connection to the database
       
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://[INSERT PATH]/[FILE NAME].db"; //should ask user where they want file
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	username varchar,\n"
                + "	password varchar\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(){
        //might put this in another function.. 
            //used many times 
        //ask user for credentials 
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String user = sc.nextLine();

        //hide pw typing 
        Console console = System.console();
        System.out.print("Enter password: ");
        char[] password = console.readPassword();

        //do the queries 
        String sql = "INSERT INTO users(username,password) VALUES(?,?)";
        String hashed = hash(Arrays.toString(password));
        //System.out.println("hashed pw: " + password);

        try (Connection conn = connect()){
            PreparedStatement pstmt = conn.prepareStatement(sql);

            //check if username exists in table
            String[] info = select(user);

            while(info!=null){ 
                //if it does, ask user to enter smthn else 
                System.out.println("Username already exists. Please choose another.");
                sc = new Scanner(System.in);
                System.out.print("Enter username: ");
                user = sc.nextLine();
                console = System.console();
                System.out.print("Enter password: ");
                password = console.readPassword();
                hashed = hash(Arrays.toString(password));
                pstmt = conn.prepareStatement(sql);
                info = select(user);
            }
            sc.close();
            
            pstmt.setString(1, user);
            pstmt.setString(2, hashed);
            pstmt.executeUpdate();

            //replace password arr w/ space to avoid any malicious code from accessing it 
            Arrays.fill(password, ' ');
        } 
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }


    private static String[] select(String user){
        String query ="SELECT * FROM users WHERE username = ?";
        try(Connection conn = connect();
            PreparedStatement checkpstmt = conn.prepareStatement(query)){

            checkpstmt.setString(1, user);
            ResultSet rs = checkpstmt.executeQuery();
            if(rs.next()){ //if username exists 
                String[] info = {rs.getString("username"), rs.getString("password")};
                return info;
            }
            else{
                return null;
            }
            
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void selectAll(){
        String sql = "SELECT username, password FROM users";
        
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("username") +  "\t" + 
                                   rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteRow(String user) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, user);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void login(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String user = sc.nextLine();

        //hide pw typing 
        Console console = System.console();
        System.out.print("Enter password: ");
        char[] password = console.readPassword();
        //hash pw 
        String hashed = hash(Arrays.toString(password));

        int tries = 0; //give 3 tries 
        String[] info = select(user);
        while(tries<3){
            if(info !=null){
                //System.out.println("username exists"); //this works
                //String storedUser = info[0];
                String storedPW = info[1];
                //System.out.println(storedPW);
                //System.out.println(storedUser);
                
                //if credentials incorrect
                //System.out.println("checking credentials");
                if(!hashed.equals(storedPW)){
                    //System.out.println("username and/or password typed incorrectly.");
                    System.out.println("Invalid username and/or password. Please try again.");
                    System.out.print("Enter username: ");
                    user = sc.nextLine();
                    console = System.console();
                    System.out.print("Enter password: ");
                    password = console.readPassword();
                    //hash pw 
                    hashed = hash(Arrays.toString(password));
                    tries++;
                }
                else{
                    System.out.println("Login successful. Welcome!");
                    break;
                }
                
            }
            //or if doesn't exist in database 
            else{
                //System.out.println("Username does not exist.");
                System.out.println("Invalid username and/or password. Please try again.");
                System.out.print("Enter username: ");
                user = sc.nextLine();
                console = System.console();
                System.out.print("Enter password: ");
                password = console.readPassword();
                //hash pw 
                hashed = hash(Arrays.toString(password));
                tries++;
            } 
            info = select(user);
        }
        sc.close();
        if(tries>=3){
            System.out.println("Unsuccessful login attempt too many times. Please try again later.");
        }
	Arrays.fill(password, ' ');
        
    }
    private static String hash(String input){
		try{
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] msgDigest = sha256.digest(input.getBytes());
			
			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, msgDigest);
	
			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}
   
    /*
	//kinda flawed 
	private static String MD5(String input){
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] msgDigest = md5.digest(input.getBytes());
			
			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, msgDigest);
	
			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}
	
	*/
}

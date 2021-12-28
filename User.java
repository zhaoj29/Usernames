import java.security.*;
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

	private static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/Jessica Zhao/Downloads/projects/cs/user_proj/test.db";
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
        String url = "jdbc:sqlite:C://Users/Jessica Zhao/Downloads/projects/cs/user_proj/test.db";
        
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

    public static void insert(String user, String password){

        String sql = "INSERT INTO users(username,password) VALUES(?,?)";
        String check ="SELECT * FROM users WHERE username = ?";
        String hashed = hash(password);
        //System.out.println("hashed pw: " + password);
        try (Connection conn = connect()){
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement checkpstmt = conn.prepareStatement(check);
            checkpstmt.setString(1, user);
            ResultSet rs = checkpstmt.executeQuery();

            //check if username exists in table 
            while(rs.next()){ 
                System.out.println("Username already exists. Please choose another.");
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter username: ");
                user = sc.nextLine();
                System.out.print("Enter password: ");
                password = sc.nextLine();
                check ="SELECT * FROM users WHERE username = ?";
                pstmt = conn.prepareStatement(sql);
                checkpstmt = conn.prepareStatement(check);
                checkpstmt.setString(1, user);
                rs = checkpstmt.executeQuery();
                
            }
            //sc.close();
            
            pstmt.setString(1, user);
            pstmt.setString(2, hashed);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
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

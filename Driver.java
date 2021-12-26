//import java.security.*;
//import java.math.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver {

	public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/Jessica Zhao/Downloads/projects/cs/user_proj/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
	public static void main(String[] args){
		//String practice = "steveslist";
		//String hashedMD5 = MD5(practice);
		//String hashedSHA256 = sha256(practice);
		
		//System.out.println("your MD5 password is: " + hashedMD5);
		//System.out.println("your sha-256 password is: " + hashedSHA256);
		
		//////////// login driver code /////////////////////
		App stuff = new App();
		stuff.add("robert", "password123");
		stuff.add("bob", "abddegh10");
		stuff.add("harris", "hello987");
		
		/*
		System.out.println(stuff.is_valid_creds("robert", "password123"));
		System.out.println(stuff.is_valid_creds("robert", "password"));
		System.out.println(stuff.is_valid_creds("hello", "password"));
		*/

		stuff.read("userFile.json");

		connect();
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
	
	private static String sha256(String input){
		try{
			MessageDigest md5 = MessageDigest.getInstance("SHA-256");
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


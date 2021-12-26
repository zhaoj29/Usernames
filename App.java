import java.util.*;
import java.io.*;
import java.security.*;
import java.math.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class App{
	private HashMap<String, String> logins; 
	
	private JSONArray info; 

	private final String  FILEPATH = "c:\\Users\\Jessica Zhao\\Downloads\\projects\\cs\\user_proj\\";
	
	public App(){
		logins = new HashMap<String, String>();
		info = new JSONArray(); 
	}
	
	public boolean is_valid_creds(String user, String password){
		//some sort of existing hash of credentials 
		String hashed = sha256(password);
		
		return logins.containsKey(user) && logins.get(user).equals(hashed);
	}
	
	public void add(String user, String password){
		//hash pw 
		String hashed = sha256(password);
		logins.put(user, hashed);
		//System.out.println(logins);
		
		//write to json
		write(user, password);
	}
	
	private void write(String user, String password){
		JSONObject newUser = new JSONObject();
		String hashed = sha256(password);
		newUser.put("username", user);
		newUser.put("password", hashed); 
		info.add(newUser);
		
		try{
			FileWriter file = new FileWriter("userFile.json");
			file.write(info.toJSONString()); 
            file.flush();
        } 
        catch (IOException e){
            e.printStackTrace();
        }
	
	}

	//read from json file
	public void read(String fname){
		JSONParser parser = new JSONParser();
		try{
			
			JSONArray JSONo = (JSONArray) parser.parse(new FileReader(FILEPATH + fname));

  			for (Object o : JSONo){
    			JSONObject user = (JSONObject) o;

    			String name = (String) user.get("username");
    			System.out.println(name);

    			String pw = (String) user.get("password");
    			System.out.println(pw);

  			}
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
		catch (IOException e) {
            e.printStackTrace();
        } 
		catch(org.json.simple.parser.ParseException e){
			System.out.println("Parsing failed");
		}
	}
	
	private static String sha256(String input){
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
}
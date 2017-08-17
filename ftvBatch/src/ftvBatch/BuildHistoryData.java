package ftvBatch;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class BuildHistoryData {

    static Cipher cipher;  

    public static void main(String[] args) throws Exception {
		
		String cfgFile = args[0];

		// get db configuration
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String db_url = prop.getProperty("db_url");
		String user = prop.getProperty("user");
		String encryptedPwd = prop.getProperty("password");
		String keyS = prop.getProperty("key");
		
        // The salt (probably) can be stored along with the encrypted data
        byte[] salt = new String("12345678").getBytes();

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        SecretKeySpec key = createSecretKey(keyS.toCharArray(),salt, iterationCount, keyLength);

        String decryptedPwd = decrypt(encryptedPwd, key);

		//create energy data
        Connection conn = null;
		Statement stmt = null;
		Statement stmt1 = null;
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection(db_url,user,decryptedPwd);
		    stmt = conn.createStatement();
		    stmt1 = conn.createStatement();

		    //clear henergy table
		    stmt.execute("truncate table henergy");
		    
		    //get data
		    ResultSet rs = stmt.executeQuery("select date,ftv_energy,con_energy from rs485data");
		    
	  		Float ftv1st = (float) 0;
			Float con1st = (float) 0;
			String actDate = null;
			Float ftvMemo = (float) 0;
			Float conMemo = (float) 0;

		    while(rs.next()){
		    	//Retrieve by column name
		        String date  = rs.getString("date").substring(0, 10);
		        Float ftv = rs.getFloat("ftv_energy");
		        Float con = rs.getFloat("con_energy");
		        
	  	  		if(!date.equals(actDate)){
	  	  	  		if(ftv1st != 0){
	  	  	  			Float ftvDay = ftvMemo - ftv1st;
	  	  	  			Float conDay = conMemo - con1st;
	  	  	  			stmt1.execute("insert into henergy set date=date('"+String.valueOf(actDate)+"'), ftv_energy="+ftvDay+", con_energy="+conDay);
	  	  	  		}
	  	  			actDate = date;
	  	  	  		ftv1st = ftv;
	  	  	  		con1st = con;
	  	  		}
	  	  		ftvMemo = ftv;
	  	  		conMemo = con;
		    }
		    
		    stmt.close();
		    stmt1.close();

		    conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
		    e.printStackTrace();
		}finally{
			//finally block used to close resources
		    try{
		    	if(stmt!=null)
		        	stmt.close();
		      }catch(SQLException se2){
		}// nothing we can do
		try{
			if(conn!=null)
		    	conn.close();
		    }catch(SQLException se){
		    	se.printStackTrace();
		    }//end finally try
		}
	}
    
    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }
    
    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
}

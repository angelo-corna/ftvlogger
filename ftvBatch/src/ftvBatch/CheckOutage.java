package ftvBatch;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.crypto.Cipher;

public class CheckOutage {

    static Cipher cipher;
	
    public static void main(String[] args) throws Exception {
		
		String cfgFile = args[0];
		String numLecturesString = args[1];
		String thresholdString = args[2];

		int numLectures = Integer.parseInt(numLecturesString);
		int threshold = Integer.parseInt(thresholdString);
		
		// get db configuration
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String db_url = prop.getProperty("db_url");
		String user = prop.getProperty("db_user");
		String encryptedPwd = prop.getProperty("db_password");
		String keyS = prop.getProperty("key");
		String telegramTokenValue = prop.getProperty("telegramTokenValue");
		String telegramChatId = prop.getProperty("telegramChatId");

        String decryptedPwd =  DecryptPwd.getDecryptedPwd(encryptedPwd, keyS);

	    ArrayList<Float> lectures = new ArrayList<Float>();

		//create energy data
        Connection conn = null;
		Statement stmt = null;
	
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection(db_url,user,decryptedPwd);
		    stmt = conn.createStatement();
		    
		    //get data
		    ResultSet rs = stmt.executeQuery("select con_power from rs485data order by date desc limit " + numLectures);
		    while(rs.next()){
		    	//Retrieve data
		    	lectures.add(rs.getFloat("con_power"));
		    }
		    stmt.close();

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
		
		boolean sendAlert = true;
		for(int i = 0 ; i < numLectures ; i++) {
			if(lectures.get(i) < threshold) {
				sendAlert = false;
			}
		}
		if(sendAlert) {
	    	String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

	    	String text = "Attenzione. Il consumo di energia elettrica è oltre la soglia consentita. Valori rilevati: ";
			for(int i = 0 ; i < numLectures ; i++) {
				text=text + lectures.get(i) + ", ";
			}
			
			String textFinal = text.substring(0, text.length() -2);
	    	urlString = String.format(urlString, telegramTokenValue, telegramChatId, textFinal);

	        try {
	            URL url = new URL(urlString);
	            URLConnection connWEB = url.openConnection();
	            InputStream is = new BufferedInputStream(connWEB.getInputStream());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}
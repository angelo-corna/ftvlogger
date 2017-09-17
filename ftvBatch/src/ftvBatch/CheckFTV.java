package ftvBatch;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;  

public class CheckFTV {
	 public static void main(String[] args) throws Exception {  
		 
		 String cfgFile = args[0];
		 
		// get db configuration
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String db_url = prop.getProperty("db_url");
		String user = prop.getProperty("db_user");
		String encryptedPwd = prop.getProperty("db_password");
		String keyS = prop.getProperty("key");
        String decryptedPwd =  DecryptPwd.getDecryptedPwd(encryptedPwd, keyS);
		 
        
		//check last data
        Connection conn = null;
		Statement stmt = null;
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection(db_url,user,decryptedPwd);
		    stmt = conn.createStatement();
			    
		    //get data
		    ResultSet rs = stmt.executeQuery("select date from rs485data order by date desc limit 1");
		    rs.next();
		    String lastDate  = rs.getString("date"); //2017-09-10 17:45:46
		    stmt.close();
		    conn.close();
		    
		    //String lastDate = "2017-09-17 10:31:30";
		    SimpleDateFormat fmtLastDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    fmtLastDate.setLenient(false);
		    Date dLastDate = fmtLastDate.parse(lastDate);
		    
		    Date actualDate = new Date();

		    if((actualDate.getTime() - dLastDate.getTime()) > 300000){
		    	sendMail(cfgFile, "Last insert date: "+lastDate, "FTV Logger - Data Collection Error");
		    }
		}catch(SQLException se){
			//Handle errors for JDBC
			sendMail(cfgFile, se.toString(), "FTV Logger - Error detected during SQL Statement submission");
			//se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			sendMail(cfgFile, e.toString(), "FTV Logger - Error detected during check execution");
		    //e.printStackTrace();
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
	 
	private static void sendMail(String cfgFile, String mailBody, String subject ) throws Exception{
		 ArrayList<String> toList = new ArrayList<String>();
		 toList.add("cornangelo@gmail.com");
		 SendMail.sendMailWithAuth(cfgFile, toList, mailBody, subject);
	}
}

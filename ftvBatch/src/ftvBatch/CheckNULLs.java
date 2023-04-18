package ftvBatch;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;  

public class CheckNULLs {
	 public static void main(String[] args) throws Exception {  
		 
		 String cfgFile = args[0];
		 int threashold = Integer.parseInt(args[1]);
		 
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
			    
		    //get FTV data
		    ResultSet rs = stmt.executeQuery("select DATE(date) mydate, count(date) '#NULLs' from rs485data where date > subdate(curdate(),1) AND date < curdate() AND (ftv_voltage is NULL or ftv_current is NULL or ftv_power is NULL or ftv_frequency is NULL or ftv_energy is NULL) group by mydate;");
		    if (rs.next() ) {
			    int numNulls  = rs.getInt("#NULLs"); 
		    	String chkDate  = rs.getString("mydate"); 
			    System.out.println("\t"+chkDate+" - FTV #NULLs: "+numNulls);
			    if(numNulls > threashold){
			    	//String[] mailArgs = {cfgFile,"cornangelo@gmail.com", chkDate+" - "+numNulls, "FTV Logger - FTV #NULLs Threashold Excideed"};
			    	//SendMail.main(mailArgs);
			    	String[] telegramArgs = { cfgFile, "FTV Logger - FTV #NULLs Threashold Excideed\n" + chkDate+" - "+numNulls };
			    	SendTelegramNotification.main(telegramArgs);
			    }
		    }else{
		    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		        Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.DATE, -1);
		        Date yesterday = calendar.getTime();
			    System.out.println("\t"+dateFormat.format(yesterday)+" - FTV #NULLs: 0");
		    }
		    rs.close();

		    //get CON data
		    rs = stmt.executeQuery("select DATE(date) mydate, count(date) '#NULLs' from rs485data where date > subdate(curdate(),1) AND date < curdate() AND (con_voltage is NULL or con_current is NULL or con_power is NULL or con_frequency is NULL or con_energy is NULL) group by mydate;");
		    if (rs.next() ) {
			    int numNulls  = rs.getInt("#NULLs"); 
		    	String chkDate  = rs.getString("mydate"); 
			    System.out.println("\t"+chkDate+" - CON #NULLs: "+numNulls);
			    if(numNulls > threashold){
			    	//String[] mailArgs = {cfgFile,"cornangelo@gmail.com", chkDate+" - "+numNulls, "FTV Logger - CON #NULLs Threashold Excideed"};
			    	//SendMail.main(mailArgs);
			    	String[] telegramArgs = { cfgFile, "FTV Logger - CON #NULLs Threashold Excideed\n" + chkDate+" - "+numNulls };
			    	SendTelegramNotification.main(telegramArgs);
			    }
		    }else{
		    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		        Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.DATE, -1);
		        Date yesterday = calendar.getTime();
			    System.out.println("\t"+dateFormat.format(yesterday)+" - CON #NULLs: 0");
		    }
		    rs.close();
		    stmt.close();
		    conn.close();
		
		}catch(SQLException se){
			//Handle errors for JDBC
	    	//String[] mailArgs = {cfgFile,"cornangelo@gmail.com", se.toString(), "FTV Logger - Error detected during SQL Statement submission"};
	    	//SendMail.main(mailArgs);
	    	String[] telegramArgs = { cfgFile, "FTV Logger - Error detected during SQL Statement submission\n" + se.toString() };
	    	SendTelegramNotification.main(telegramArgs);
			//se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
	    	//String[] mailArgs = {cfgFile,"cornangelo@gmail.com", e.toString(), "FTV Logger - Error detected during check execution"};
	    	//SendMail.main(mailArgs);
	    	String[] telegramArgs = { cfgFile, "FTV Logger - Error detected during check execution\n" + e.toString() };
	    	SendTelegramNotification.main(telegramArgs);
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
}

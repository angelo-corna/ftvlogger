package ftvBatch;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.crypto.Cipher;

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

        String decryptedPwd =  decryptPwd.getDecryptedPwd(encryptedPwd, keyS);
        
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
}

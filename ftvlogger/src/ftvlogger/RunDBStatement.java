package ftvlogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class RunDBStatement {
    
	public List<String> getResultSet(String DataSourceName, String Statement) throws NamingException, SQLException {
		DataSource ds = null;
	    Connection conn = null;
	    ResultSet result = null;
	    Statement stmt = null;
	    ResultSetMetaData rsmd = null;
		List<String> resultS = new ArrayList<>();

		try{
	    	Context context = new InitialContext();
	    	Context envCtx = (Context) context.lookup("java:comp/env");
	    	ds =  (DataSource)envCtx.lookup(DataSourceName);
	      
	    	if (ds != null) {
	    		conn = ds.getConnection();
	    		stmt = conn.createStatement();
	    		result = stmt.executeQuery(Statement);
	    	}
	    }
	    catch (SQLException e) {
	    	System.out.println("Error occurred " + e);
	    }
	    int columns=0;
	    try {
	    	rsmd = result.getMetaData();
	    	columns = rsmd.getColumnCount();
	    }
	    catch (SQLException e) {
	    	System.out.println("Error occurred " + e);
	    }

	    
	  	//get values
		while (result.next()) {
			String rowS = "";
			for (int i=1; i<=columns; i++) {
	        	rowS = rowS + "," + result.getString(i);
			}
			rowS = rowS.substring(1);
			resultS.add(rowS);
		}
		
	    
	    try {
	    	// close the connection, resultset, and the statement
	    	result.close();
	    	stmt.close();
	    	conn.close();
	    } // end of the try block
	    catch (SQLException e) {
	    	System.out.println("Error " + e);
	   	}
	    // ensure everything is closed
	    finally {
	    	try {
	    		if (stmt != null)
	    			stmt.close();
	       	}  catch (SQLException e) {}
	       	try {
	       		if (conn != null)
	       			conn.close();
	        } catch (SQLException e) {}
	    }
	    return resultS;
	}
	
	public StringBuilder getStringBuilder(List<String> rs, int position, String kind){
		
		StringBuilder sbOut = new StringBuilder();
		
        for(String rsRow : rs){
        	String[] fields = rsRow.split(",");
        	if(kind.equals("date")){
    	    	sbOut.append(fields[position].substring(11, 16)+",");
        	}else if(kind.equals("shortDate1")){
    	    	sbOut.append(fields[position].substring(8, 10)+" "+getMonth(fields[position])+",");
        	}else{
		    	sbOut.append(fields[position]+",");
	        }
		}
        return sbOut;
	}
	
	public String getMonth(String actDate){
  		String monthString;
  			switch (actDate.substring(5,7)) {
  	        case "01":  monthString = "Jan";
  	        	break;
  	        case "02":  monthString = "Feb";
  	        	break;
  	        case "03":  monthString = "Mar";
  	        	break;
  	       	case "04":  monthString = "Apr";
  	       		break;
  	       	case "05":  monthString = "May";
  	       		break;
  	       	case "06":  monthString = "Jun";
  	       		break;
  	        case "07":  monthString = "Jul";
  	        	break;
  	        case "08":  monthString = "Aug";
  	        	break;
  	        case "09":  monthString = "Sep";
  	        	break;
  	        case "10": monthString = "Oct";
  	        	break;
  	        case "11": monthString = "Nov";
  	        	break;
  	        case "12": monthString = "Dec";
  	        	break;
  	        default: monthString = "Invalid month";
  	        	break;
  	        }
		return  monthString;
	}

	
	public String getMonthNum(String month){
  		String monthN;
  			switch (month) {
  	        case "Jan":  monthN = "01";
  	        	break;
  	        case "Feb":  monthN = "02";
  	        	break;
  	        case "Mar":  monthN = "03";
  	        	break;
  	       	case "Apr":  monthN = "04";
  	       		break;
  	       	case "May":  monthN = "05";
  	       		break;
  	       	case "Jun":  monthN = "06";
  	       		break;
  	        case "Jul":  monthN = "07";
  	        	break;
  	        case "Aug":  monthN = "08";
  	        	break;
  	        case "Sep":  monthN = "09";
  	        	break;
  	        case "Oct": monthN = "10";
  	        	break;
  	        case "Nov": monthN = "11";
  	        	break;
  	        case "Dec": monthN = "12";
  	        	break;
  	        default: monthN = "Invalid month";
  	        	break;
  	        }
		return  monthN;
	}

	public String getMonthCom(String month){
  		String monthCom;
  			switch (month) {
  	        case "Jan":  monthCom = "January";
  	        	break;
  	        case "Feb":  monthCom = "February";
  	        	break;
  	        case "Mar":  monthCom = "March";
  	        	break;
  	       	case "Apr":  monthCom = "April";
  	       		break;
  	       	case "May":  monthCom = "May";
  	       		break;
  	       	case "Jun":  monthCom = "June";
  	       		break;
  	        case "Jul":  monthCom = "July";
  	        	break;
  	        case "Aug":  monthCom = "August";
  	        	break;
  	        case "Sep":  monthCom = "September";
  	        	break;
  	        case "Oct": monthCom = "October";
  	        	break;
  	        case "Nov": monthCom = "November";
  	        	break;
  	        case "Dec": monthCom = "December";
  	        	break;
  	        default: monthCom = "Invalid month";
  	        	break;
  	        }
		return  monthCom;
	}
	
}
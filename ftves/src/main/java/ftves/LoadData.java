package ftves;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class LoadData 
{
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
		
		//get last timestamp on es
		RestClient restClient = RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")).build();
		HttpEntity entity = new NStringEntity(
				"{\n" +
				"    \"query\" : {\"match_all\": {} },\n" +                                      
				"    \"size\" : 1,\n" +
				"    \"sort\" : [{\"timestamp\": {\"order\": \"desc\"}}]\n" +
				"}", ContentType.APPLICATION_JSON);
		Response lastHitResponse = restClient.performRequest("GET","/ftvlogger/rs485data/_search",Collections.<String, String>emptyMap(),entity);
		JSONObject obj = new JSONObject(EntityUtils.toString(lastHitResponse.getEntity()));
		int total = obj.getJSONObject("hits").getInt("total");
		String lastDate = null;
		Double lastFtvEnergy=null;
		Double lastConEnergy=null;
		if (total == 0){
			lastDate = "1970-01-01 00:00:00";
		}else{
			JSONObject jHits = obj.getJSONObject("hits");
			JSONArray aHits = jHits.getJSONArray("hits");
			lastDate = aHits.getJSONObject(0).getJSONObject("_source").getString("timestamp");
			lastFtvEnergy = aHits.getJSONObject(0).getJSONObject("_source").getDouble("ftvEnergy");
			lastConEnergy = aHits.getJSONObject(0).getJSONObject("_source").getDouble("conEnergy");
		}
		
		System.out.println("last insert timestamp is "+lastDate);
		System.out.println("last ftv energy is "+lastFtvEnergy);
		System.out.println("last con energy is "+lastConEnergy);
		
		//get mysql data and load es
		Connection conn = null;
		Statement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
	    conn = DriverManager.getConnection(db_url,user,decryptedPwd);
	    stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("select * from rs485data where date > '"+lastDate+"' order by date");
	    while (rs.next()) {
	    	String timestampI = rs.getString("date").substring(0,19);
	    	String ftvVoltageS = rs.getString("ftv_voltage");
	    	String ftvCurrentS = rs.getString("ftv_current");
	    	String ftvPowerS = rs.getString("ftv_power");
	    	String ftvFrequencyS = rs.getString("ftv_frequency");
	    	String ftvEnergyS = rs.getString("ftv_energy");
	    	String conVoltageS = rs.getString("con_voltage");
	    	String conCurrentS = rs.getString("con_current");
	    	String conPowerS = rs.getString("con_power");
	    	String conFrequencyS = rs.getString("con_frequency");
	    	String conEnergyS = rs.getString("con_energy");
	    	
	    	//insert record if no null values are detected
	    	if ( ftvVoltageS != null &&  ftvCurrentS != null && ftvPowerS != null && ftvFrequencyS != null && ftvEnergyS != null && conVoltageS != null  && conCurrentS != null && conPowerS != null && conFrequencyS != null && conEnergyS != null ){
		    	Double ftvEnergyI = rs.getDouble("ftv_energy");
		    	Double conEnergyI = rs.getDouble("con_energy");
	    		Double diffFtvEnergy = (double) 0;
	    		Double diffConEnergy = (double) 0;
	    		if(lastFtvEnergy != null){
			    	diffFtvEnergy = ftvEnergyI - lastFtvEnergy;
			    	diffConEnergy = conEnergyI - lastConEnergy;
	    		}
			    HttpEntity entityI = new NStringEntity(
					"{\n" +
					"    \"conCurrent\" : "+rs.getDouble("con_current")+",\n" +                                      
					"    \"conEnergy\" : "+conEnergyI+",\n" +                                      
					"    \"conFrequency\" : "+rs.getDouble("con_frequency")+",\n" +                                      
					"    \"conPower\" : "+rs.getDouble("con_power")+",\n" +                                      
					"    \"conVoltage\" : "+rs.getDouble("con_Voltage")+",\n" +                                      
					"    \"diffCONEnergy\" : "+diffConEnergy+",\n" +                                      
					"    \"ftvCurrent\" : "+rs.getDouble("ftv_current")+",\n" +                                      
					"    \"ftvEnergy\" : "+ftvEnergyI+",\n" +                                      
					"    \"ftvFrequency\" : "+rs.getDouble("ftv_frequency")+",\n" +                                      
					"    \"ftvPower\" : "+rs.getDouble("ftv_power")+",\n" +                                      
					"    \"ftvVoltage\" : "+rs.getDouble("ftv_Voltage")+",\n" +                                      
					"    \"diffFTVEnergy\" : "+diffFtvEnergy+",\n" +                                      
					"    \"timestamp\" : \""+timestampI+"\"\n" +
					"}", ContentType.APPLICATION_JSON);
				restClient.performRequest("POST","/ftvlogger/rs485data",Collections.<String, String>emptyMap(),entityI);
				System.out.println(timestampI+" measurement insert in elasticsearch index");
		    	lastFtvEnergy = ftvEnergyI;
		    	lastConEnergy = conEnergyI;
	    	}else{
				System.out.println(timestampI+" measurement NOT insert in elasticsearch index (null values detected)");
	    	}
	    }
	    restClient.close();
    }
}

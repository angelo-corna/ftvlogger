package ftves;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import org.json.*;

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
		BigDecimal lastFtvEnergy=null;
		BigDecimal lastConEnergy=null;
		if (total == 0){
			lastDate = "1970-01-01 00:00:00";
		}else{
			JSONObject jHits = obj.getJSONObject("hits");
			JSONArray aHits = jHits.getJSONArray("hits");
			lastDate = aHits.getJSONObject(0).getJSONObject("_source").getString("timestamp");
			lastFtvEnergy = aHits.getJSONObject(0).getJSONObject("_source").getBigDecimal("ftvEnergy");
			lastConEnergy = aHits.getJSONObject(0).getJSONObject("_source").getBigDecimal("conEnergy");
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
	    	BigDecimal ftvEnergyI = rs.getBigDecimal("ftv_energy");
	    	BigDecimal conEnergyI = rs.getBigDecimal("con_energy");
	    	if ( ftvEnergyI != null &&  conEnergyI != null){
	    		BigDecimal diffFtvEnergy = new BigDecimal("0.0");
	    		BigDecimal diffConEnergy = new BigDecimal("0.0");
		    	if (lastFtvEnergy != null){
			    	diffFtvEnergy = ftvEnergyI.subtract(lastFtvEnergy);
			    	diffConEnergy = conEnergyI.subtract(lastConEnergy);
		    	}
		    	HttpEntity entityI = new NStringEntity(
					"{\n" +
					"    \"conCurrent\" : "+rs.getFloat("con_current")+",\n" +                                      
					"    \"conEnergy\" : "+conEnergyI+",\n" +                                      
					"    \"conFrequency\" : "+rs.getFloat("con_frequency")+",\n" +                                      
					"    \"conPower\" : "+rs.getFloat("con_power")+",\n" +                                      
					"    \"conVoltage\" : "+rs.getFloat("con_Voltage")+",\n" +                                      
					"    \"diffCONEnergy\" : "+diffConEnergy+",\n" +                                      
					"    \"ftvCurrent\" : "+rs.getFloat("ftv_current")+",\n" +                                      
					"    \"ftvEnergy\" : "+ftvEnergyI+",\n" +                                      
					"    \"ftvFrequency\" : "+rs.getFloat("ftv_frequency")+",\n" +                                      
					"    \"ftvPower\" : "+rs.getFloat("ftv_power")+",\n" +                                      
					"    \"ftvVoltage\" : "+rs.getFloat("ftv_Voltage")+",\n" +                                      
					"    \"diffFTVEnergy\" : "+diffFtvEnergy+",\n" +                                      
					"    \"timestamp\" : \""+timestampI+"\"\n" +
					"}", ContentType.APPLICATION_JSON);
				Response indexResponse = restClient.performRequest("POST","/ftvlogger/rs485data",Collections.<String, String>emptyMap(),entityI);
				System.out.println(timestampI+" measurement insert to elasticsearch index");
	    	}else{
				System.out.println(timestampI+" measurement NOT insert to elasticsearch index (null values detected)");
	    	}
	    }
	    restClient.close();
    }
}

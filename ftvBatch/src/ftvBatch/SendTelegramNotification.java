package ftvBatch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;


public class SendTelegramNotification {
	
	 public static void main(String[] args) throws Exception {  
 
		//get arguments
		if (args.length != 2){
			System.out.println("usage SendTelegramNotification cfgfile message");
			System.exit(1);
		}
		String cfgFile = args[0];
		String message = args[1];
		
		
		// get telegram credentials
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String telegramTokenValue = prop.getProperty("telegramTokenValue");
		String telegramChatId = prop.getProperty("telegramChatId");
		
    	String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

    	urlString = String.format(urlString, telegramTokenValue, telegramChatId, message);

        try {
            URL url = new URL(urlString);
            URLConnection connWEB = url.openConnection();
            InputStream is = new BufferedInputStream(connWEB.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}

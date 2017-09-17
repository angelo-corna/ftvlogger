package ftvBatch;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;  


public class sendMail {
	 public static void sendMailWithAuth(String cfgFile, List<String> toList, String htmlBody, String subject) throws Exception {

		// get mail configuration
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String host = prop.getProperty("host");
		String user = prop.getProperty("user");
		String encryptedPwd = prop.getProperty("password");
		String keyS = prop.getProperty("key");
		String port = prop.getProperty("port");

        String decryptedPwd =  decryptPwd.getDecryptedPwd(encryptedPwd, keyS);
		 
		Properties props = System.getProperties();
		props.put("mail.smtp.user",user); 
		props.put("mail.smtp.password", decryptedPwd);
	    props.put("mail.smtp.host", host); 
	    props.put("mail.smtp.port", port); 
	    //props.put("mail.debug", "true"); 
	    props.put("mail.smtp.auth", "true"); 
	    props.put("mail.smtp.starttls.enable","true"); 
	    props.put("mail.smtp.EnableSSL.enable","true");

	    Session session = Session.getInstance(props, null);
	    //session.setDebug(true);

	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(user));

	    // To get the array of addresses
	    for (String to: toList) {
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	    }

	    message.setSubject(subject);
	    message.setContent(htmlBody, "text/html");

	    Transport transport = session.getTransport("smtp");
	    try {
	        transport.connect(host, user, decryptedPwd);
	        transport.sendMessage(message, message.getAllRecipients());
		} finally {
	        transport.close();
	    }
	}
}

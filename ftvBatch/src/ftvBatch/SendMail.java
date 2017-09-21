package ftvBatch;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;  


public class SendMail {
	
	 public static void main(String[] args) throws Exception {  
 
//	 public static void main(String cfgFile, List<String> toList, String htmlBody, String subject) throws Exception {
		 
		//get argumrnts
		if (args.length != 4){
			System.out.println("usage Send Mail cfgFile to htmlBody subject");
			System.exit(1);
		}
		 
		String cfgFile = args[0];
		String to = args[1];
		String htmlBody = args[2];
		String subject = args[3];
		 
		// get mail configuration
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(cfgFile);
		prop.load(input);
		String host = prop.getProperty("mail_host");
		String user = prop.getProperty("mail_user");
		String encryptedPwd = prop.getProperty("mail_password");
		String port = prop.getProperty("mail_port");
		String keyS = prop.getProperty("key");

        String decryptedPwd =  DecryptPwd.getDecryptedPwd(encryptedPwd, keyS);
		 
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

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

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

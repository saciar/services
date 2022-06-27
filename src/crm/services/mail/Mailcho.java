package crm.services.mail;

import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class Mailcho{
	  
	  String host = "mail.congressrental.com";
	    String port ="25";
	    boolean AUTH = true;

		String USERNAME = "saciar";

		String PASSWORD = "29438378"; 
	    String from = "saciar@congressrental.com";
	    String to = "saciar@congressrental.com";
	    //String filename = "C:/workspace/CongressCrmClient/src/004.jpg";
	    String filename = "http://200.80.201.51:8888/index_files/image007.jpg";
	    
	    private static Mailcho _instance = null;
	    public Mailcho()throws Exception{
	    // Get system properties
	    //Properties props = System.getProperties();

	    // Setup mail server
	   // props.put("mail.smtp.host", host);
	    
	    Authenticator authenticator = null;
		Properties properties = new Properties();

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);

		if (AUTH) {
			properties.put("mail.smtp.auth", "true");
			authenticator = new SMTPAuthenticator(USERNAME, PASSWORD);
		}
	    
	    // Get session
	    Session session = Session.getInstance(properties, authenticator);

	    // Define message
	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(from));
	    message.addRecipient(Message.RecipientType.TO, 
	      new InternetAddress(to));
	    message.setSubject("Hello JavaMail Attachment");

	    // Create the message part 
	    BodyPart messageBodyPart = new MimeBodyPart();

	    // Fill the message
	    messageBodyPart.setText("Here's the file");

	    // Create a Multipart
	    Multipart multipart = new MimeMultipart();

	    // Add part one
	    multipart.addBodyPart(messageBodyPart);

	    //
	    // Part two is attachment
	    //

	    // Create second body part
	    messageBodyPart = new MimeBodyPart();

	    // Get the attachment
	    DataSource source = new URLDataSource(new URL(filename));

	    // Set the data handler to the attachment
	    messageBodyPart.setDataHandler(new DataHandler(source));

	    // Set the filename
	    messageBodyPart.setFileName("choto");

	    // Add part two
	    multipart.addBodyPart(messageBodyPart);

	    // Put parts in message
	    message.setContent(multipart);

	    // Send the message
	    Transport.send(message);
	  }
	    
	    public static synchronized Mailcho getInstance() throws Exception{
			if (_instance == null)
				_instance = new Mailcho();

			return _instance;
		}
	    
	    private class SMTPAuthenticator extends javax.mail.Authenticator {
			private String username;

			private String password;

			public SMTPAuthenticator(String username, String password) {
				this.username = username;
				this.password = password;
			}

			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		}
}
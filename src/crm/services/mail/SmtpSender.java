package crm.services.mail;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

public class SmtpSender {
	private static final String HOST = "smtp.congressrental.com.ar";
	private static final String PORT = "587";
	private static final boolean AUTH = true;
	private static final String USERNAME = "crm@congressrental.com.ar";
	private static final String PASSWORD = "ArgentinA2012";
	
	
	
	private static SmtpSender _instance = null;

	private Session session = null;

	private SmtpSender() {
		Authenticator authenticator = null;

		Properties properties = new Properties();

		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", PORT);

		//MENDOZA---------------------------------------------------------------------------
		/*properties.put("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.socketFactory.port", "PORT");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");*/
		
		if (AUTH) {
			properties.put("mail.smtp.auth", "true");
			authenticator = new SMTPAuthenticator(USERNAME, PASSWORD);
		}

		session = Session.getInstance(properties, authenticator);
	}

	public void sendMail(MailMessage mail) throws SendMailException {
		try {
			Message message = mail.getMessage(session);
			Transport.send(message);
		} catch (Exception exception) {
			throw new SendMailException(exception);
		}
	}

	public static synchronized SmtpSender getInstance(){
		if (_instance == null)
			_instance = new SmtpSender();

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

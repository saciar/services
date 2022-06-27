package crm.services.mail;

//import com.oopreserch.util.SimpleException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

public final class MailMessage{
	private static final String X_MAILER = "Interlogical Webmail 0.1";

	private String charset;

	private String subject;

	private String toAddress[];

	private String toName[];

	private String ccAddress[];

	private String ccName[];

	private String bccAddress[];

	private String bccName[];

	private String fromAddress;

	private String fromName;

	private String replyToAddress;

	private String replyToName;

	private String body;

	private String htmlBody;
	
	private String filename;
	
	private String filePath;
		
	/**
	 * Constructor
	 */
	public MailMessage() {
		this.charset = "iso-8859-1";
		this.subject = "";
		this.toAddress = null;
		this.toName = null;
		this.ccAddress = null;
		this.ccName = null;
		this.bccAddress = null;
		this.bccName = null;
		this.fromAddress = null;
		this.fromName = "";
		this.replyToAddress = null;
		this.replyToName = "";
		this.body = "";
	}
	
	public String toString(){
		return toAddress+"\n"+toName+"\n"+ccAddress+"\n"+ccName+"\n"+bccAddress+"\n"+bccName+"\n"+fromAddress+"\n"+
			fromName+"\n"+replyToAddress;
	}
	
	/**
	 * Asigna el charset
	 * 
	 * @param s
	 */
	public void setCharset(String s) {
		if (StringUtils.isNotBlank(s)) {
			charset = s;
		}
	}

	/**
	 * Establece el motivo del mail
	 * 
	 * @param s
	 */
	public void setSubject(String s) {
		if (s == null)
			subject = "";
		else
			subject = s;
	}

	/**
	 * Asigna las direcciones de destino del mensaje
	 * 
	 * @param emailAddresses
	 * @param emailNames
	 * @throws EmailAddressException
	 * @throws EmailNameException
	 */
	public void setToAddress(String addresses[], String names[])
			throws EmailAddressException, EmailNameException {
		if (addresses == null)
			throw new EmailAddressException("Address is null");
		if (names == null)
			throw new EmailNameException("Name is null");

		if (addresses.length != names.length)
			throw new EmailAddressException(
					"The number of addresses does not match that of names");

		for (int i = 0; i < addresses.length; i++) {
			if (StringUtils.isBlank(addresses[i]))
				throw new EmailAddressException("Address[" + i
						+ "] is null or blank.");

			addresses[i] = addresses[i].trim();
			if (names[i] == null)
				names[i] = "";
		}

		this.toAddress = addresses;
		this.toName = names;
	}

	/**
	 * Agrega una direccion a la lista de direcciones de destino del mensaje.
	 * 
	 * @param address
	 * @param name
	 * @throws EmailAddressException
	 */
	public void addToAddress(String address, String name)
			throws EmailAddressException {
		if (StringUtils.isBlank(address))
			throw new EmailAddressException("Address is null");

		address = address.trim();

		if (this.toAddress == null) {
			this.toAddress = new String[1];
			this.toAddress[0] = address;
		} else {
			int i = this.toAddress.length;
			String as[] = new String[i + 1];
			for (int k = 0; k < i; k++)
				as[k] = this.toAddress[k];

			as[i] = address;
			this.toAddress = as;
		}
		if (this.toName == null) {
			this.toName = new String[1];
			if (name == null)
				this.toName[0] = "";
			else
				this.toName[0] = name;
		} else {
			int j = this.toName.length;
			String as1[] = new String[j + 1];
			for (int l = 0; l < j; l++)
				as1[l] = this.toName[l];

			if (name == null)
				as1[j] = "";
			else
				as1[j] = name;
			this.toName = as1;
		}
	}

	/**
	 * Asigna las direcciones de copia del mensaje
	 * 
	 * @param addresses
	 * @param names
	 * @throws SimpleException
	 */
	public void setCcAddress(String addresses[], String names[])
			throws EmailAddressException, EmailNameException {
		if (addresses == null)
			throw new EmailAddressException("Address is null");
		if (names == null)
			throw new EmailNameException("Name is null");

		if (addresses.length != names.length)
			throw new EmailAddressException(
					"The number of addresses does not match that of names");

		for (int i = 0; i < addresses.length; i++) {
			if (StringUtils.isBlank(addresses[i]))
				throw new EmailAddressException("Address[" + i + "] is null");

			addresses[i] = addresses[i].trim();
			if (names[i] == null)
				names[i] = "";
		}

		this.ccAddress = addresses;
		this.ccName = names;
	}

	/**
	 * Agrega una direccion de copia.
	 * 
	 * @param address
	 * @param name
	 * @throws EmailAddressException
	 */
	public void addCcAddress(String address, String name)
			throws EmailAddressException {
		if (StringUtils.isBlank(address))
			throw new EmailAddressException("Address is null");

		address = address.trim();

		if (this.ccAddress == null) {
			this.ccAddress = new String[1];
			this.ccAddress[0] = address;
		} else {
			int i = this.ccAddress.length;
			String as[] = new String[i + 1];
			for (int k = 0; k < i; k++)
				as[k] = this.ccAddress[k];

			as[i] = address;
			this.ccAddress = as;
		}
		if (this.ccName == null) {
			this.ccName = new String[1];
			if (name == null)
				this.ccName[0] = "";
			else
				this.ccName[0] = name;
		} else {
			int j = this.ccName.length;
			String as1[] = new String[j + 1];
			for (int l = 0; l < j; l++)
				as1[l] = this.ccName[l];

			if (name == null)
				as1[j] = "";
			else
				as1[j] = name;
			this.ccName = as1;
		}
	}

	/**
	 * Asigna las direcciones para la copia carbonica.
	 * 
	 * @param emailAddresses
	 * @param emailNames
	 * @throws EmailAddressException
	 * @throws EmailNameException
	 */
	public void setBccAddress(String emailAddresses[], String emailNames[])
			throws EmailAddressException, EmailNameException{

		if (emailAddresses == null)
			throw new EmailAddressException("Address is null");
		if (emailNames == null)
			throw new EmailNameException("Name is null");

		if (emailAddresses.length != emailNames.length)
			throw new EmailAddressException(
					"The number of addresses does not match that of names");

		for (int i = 0; i < emailAddresses.length; i++) {
			if (StringUtils.isBlank(emailAddresses[i]))
				throw new EmailAddressException("Address[" + i + "] is null");

			emailAddresses[i] = emailAddresses[i].trim();
			if (emailNames[i] == null)
				emailNames[i] = "";
		}

		this.bccAddress = emailAddresses;
		this.bccName = emailNames;
	}

	/**
	 * Agrega una direccion a la copia carbonica.
	 * 
	 * @param address
	 * @param name
	 * @throws EmailAddressException
	 */
	public void addBccAddress(String address, String name)
			throws EmailAddressException {
		if (StringUtils.isBlank(address))
			throw new EmailAddressException("Address is null");

		address = address.trim();

		if (this.bccAddress == null) {
			this.bccAddress = new String[1];
			this.bccAddress[0] = address;
		} else {
			int i = this.bccAddress.length;
			String as[] = new String[i + 1];
			for (int k = 0; k < i; k++)
				as[k] = this.bccAddress[k];

			as[i] = address;
			this.bccAddress = as;
		}
		if (this.bccName == null) {
			this.bccName = new String[1];
			if (name == null)
				this.bccName[0] = "";
			else
				this.bccName[0] = name;
		} else {
			int j = this.bccName.length;
			String as1[] = new String[j + 1];
			for (int l = 0; l < j; l++)
				as1[l] = this.bccName[l];

			if (name == null)
				as1[j] = "";
			else
				as1[j] = name;
			this.bccName = as1;
		}
	}

	/**
	 * Asigna la direcci�n desde la que se envia el correo.
	 * 
	 * @param address
	 * @param name
	 * @throws EmailAddressException
	 * @throws EmailNameException
	 */
	public void setFromAddress(String address, String name)
			throws EmailAddressException, EmailNameException {
		if (StringUtils.isBlank(address))
			throw new EmailAddressException("Address is null");

		this.fromAddress = address.trim();
		this.replyToAddress = address;
		if (name == null) {
			this.fromName = "";
			this.replyToName = "";
		} else {
			this.fromName = name;
			this.replyToName = name;
		}
	}

	/**
	 * Asigna la direccion de respuesta.
	 * 
	 * @param address
	 * @param name
	 * @throws EmailAddressException
	 */
	public void setReplyToAddress(String address, String name)
			throws EmailAddressException{
		address = address.trim();

		if (StringUtils.isBlank(address))
			throw new EmailAddressException("Address is null");

		this.replyToAddress = address;
		if (name == null)
			this.replyToName = "";
		else
			this.replyToName = name;
	}

	/**
	 * Asigna el cuerpo del email
	 * 
	 * @param s
	 */
	public void setBody(String s) {
		if (s == null)
			body = "";
		else
			body = s;
	}

	public void setHtmlBody(String s)  {
		if (s == null)
			htmlBody = "";
		else
			htmlBody = s;
	}
	
	public void setFileName(String s)  {
		if (s == null)
			filename = "";
		else
			filename = s;
	}
	
	public void setFilePath(String s)  {
		if (s == null)
			filePath = "";
		else
			filePath = s;
	}
	
	/**
	 * Genera el mail. TODO: Hacer mails HTML.
	 * 
	 * @param session
	 * @return
	 * @throws EmailAddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public Message getMessage(Session session) throws EmailAddressException,
			MessagingException, UnsupportedEncodingException, Exception {

		Message mimemessage = new MimeMessage(session);

		// ====================================== cargo las direcciones
		setMimeHeaders(mimemessage);
		// ====================================== cargo el subject
		mimemessage.setSubject(subject);
		// ====================================== cargo el body

		// Create a multi-part to combine the parts
		Multipart multiPart = new MimeMultipart("mixed");

		// cuerpo del mail
		BodyPart messageBodyPart;

		// Create your text message part
		
		/*messageBodyPart = new MimeBodyPart();
		messageBodyPart.setHeader("Content-Type", "text/plain; charset=\""
				+ charset + "\"");
		messageBodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		messageBodyPart.setText(body);
		multiPart.addBodyPart(messageBodyPart);*/

		// Create your html message part
		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlBody, "text/html");
		multiPart.addBodyPart(messageBodyPart);
		
		//Create your attachment
		if(!filePath.equals("") && !filename.equals("")){
		messageBodyPart = new MimeBodyPart();
	    DataSource source = new FileDataSource(filePath);
	    messageBodyPart.setDataHandler(new DataHandler(source));
	    messageBodyPart.setHeader("Content-ID",filePath);
	    messageBodyPart.setFileName(filename);
	    multiPart.addBodyPart(messageBodyPart);
		}
		// Associate multi-part with message
		mimemessage.setContent(multiPart);

		return mimemessage;
	}

	/**
	 * 
	 * @param mimemessage
	 * @throws EmailAddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private void setMimeHeaders(Message mimemessage)
			throws EmailAddressException, MessagingException,
			UnsupportedEncodingException {

		InternetAddress toAddress[] = null;
		InternetAddress ccAddress[] = null;
		InternetAddress bccAddress[] = null;
		InternetAddress fromAddress = null;
		InternetAddress replyAddress[] = null;

		if (this.toAddress == null)
			throw new EmailAddressException("TO address is null");

		toAddress = new InternetAddress[this.toAddress.length];
		for (int j = 0; j < this.toAddress.length; j++)
			toAddress[j] = new InternetAddress(this.toAddress[j],
					this.toName[j], charset);

		if (this.ccAddress != null) {
			ccAddress = new InternetAddress[this.ccAddress.length];
			for (int i1 = 0; i1 < this.ccAddress.length; i1++)
				ccAddress[i1] = new InternetAddress(this.ccAddress[i1],
						this.ccName[i1], charset);

		}
		if (this.bccAddress != null) {
			bccAddress = new InternetAddress[this.bccAddress.length];
			for (int j1 = 0; j1 < this.bccAddress.length; j1++)
				bccAddress[j1] = new InternetAddress(this.bccAddress[j1],
						this.bccName[j1], charset);

		}
		if (this.fromAddress != null)
			fromAddress = new InternetAddress(this.fromAddress , this.fromName,
					charset);
		if (this.replyToAddress != null) {
			replyAddress = new InternetAddress[1];
			replyAddress[0] = new InternetAddress(this.replyToAddress,
					this.replyToName, charset);
		}

		mimemessage.setHeader("X-Mailer", X_MAILER);
		mimemessage.setRecipients(javax.mail.Message.RecipientType.TO,
				toAddress);

		if (ccAddress != null)
			mimemessage.setRecipients(javax.mail.Message.RecipientType.CC,
					ccAddress);

		if (bccAddress != null)
			mimemessage.setRecipients(javax.mail.Message.RecipientType.BCC,
					bccAddress);

		if (fromAddress != null)
			mimemessage.setFrom(fromAddress);

		if (replyAddress != null)
			mimemessage.setReplyTo(replyAddress);

		Date date = new Date();
		mimemessage.setSentDate(date);
	}

	class FileAttachDataSource implements DataSource {

		private byte bin[];

		private String name;

		private String type;

		public FileAttachDataSource(byte bytes[], String fileName,
				String mimeType) {
			bin = null;
			name = null;
			type = null;
			bin = bytes;
			name = fileName;
			type = mimeType;
		}

		public String getContentType() {
			return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(bin);
		}

		public String getName() {
			return name;
		}

		public OutputStream getOutputStream() throws IOException {
			return null;
		}
	}

}
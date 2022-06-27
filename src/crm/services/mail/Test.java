package crm.services.mail;


public class Test {
	public static void main(String[] s){		
		try {
			MailMessage mailMessage = new MailMessage();
			mailMessage.setFromAddress("saciar@congressrental.com","Sergio Aciar");					
			mailMessage.setToAddress(new String[]{"saciar@congressrental.com"},new String[]{"Yo"});
			mailMessage.setSubject("test mail desde java");
			mailMessage.setFileName("http://192.168.1.11:8888/index_files/image007.jpg");
			mailMessage.setHtmlBody(getHTMLBody());
			SmtpSender.getInstance().sendMail(mailMessage);
		} catch (EmailAddressException e) {
			e.printStackTrace();
		} catch (EmailNameException e) {
			e.printStackTrace();
		} catch (SendMailException e) {
			e.printStackTrace();
		}
			
	}

	
	
	private static String getHTMLBody(){
		String html = new String();
		
		html += "<html>";
		html += "<body>";
		html += "<h1>";
		html += "Hola Mundo";
		html += "</h1>";
		//html += "<img src='http://tux.herac.com.ar/fotos043.jpg' />";		
		html += "<img src='http://192.168.1.11:8888/index_files/image007.jpg' />";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
}

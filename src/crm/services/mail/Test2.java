package crm.services.mail;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class Test2 {
  public static void main (String args[]) {
     try{
	    new Mailcho();
     }
     catch(Exception e){
    	 e.printStackTrace();
     }
  }
  
}
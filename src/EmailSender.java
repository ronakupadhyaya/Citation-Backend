

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage; 

/**
 * Servlet implementation class EmailSender
 */
@WebServlet("/EmailSender")
public class EmailSender extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public EmailSender() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");  
	    PrintWriter out = response.getWriter();  
	      
	    String to = "rdupadhy@usc.edu";
	    String subject = "Subject";
	    String msg = "Message";  
	          
	    try {
			Mailer.send(to, subject, msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}  
	    
	    out.print("message has been sent successfully");  
	    out.close();  
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");  
	    PrintWriter out = response.getWriter();  
	      
	    String to = "rdupadhy@usc.edu";
	    String subject = "Subject";
	    String msg = "Message";  
	          
	    try {
			Mailer.send(to, subject, msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}  
	    
	    out.print("message has been sent successfully");  
	    out.close();  
	}

}

class Mailer {  
	public static void send(String to,String subject,String msg) throws AddressException, MessagingException {  
	  
	final String user = "jsmscheduler@gmail.com";
	final String pass = "Jsmscheduler123!";  
	     
	Properties props = new Properties();  
	props.put("mail.smtp.user", "jsmscheduler@gmail.com");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.starttls.enable","true");
    props.put("mail.smtp.debug", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");  
	  
	Session session = Session.getDefaultInstance(props,  
	 new javax.mail.Authenticator() {  
		protected PasswordAuthentication getPasswordAuthentication() {  
			return new PasswordAuthentication(user, pass);  
	   }  
	});  

	MimeMessage message = new MimeMessage(session);  
	message.setFrom(new InternetAddress(user));  
	message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
	message.setSubject(subject);  
	message.setText(msg);  
		    
	Transport.send(message);  
		  
	System.out.println("Done");  
	
	}  
	
}  

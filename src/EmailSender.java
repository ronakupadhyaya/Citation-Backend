

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fortuna.ical4j.model.Calendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart; 

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
	    ArrayList<String> authors = new ArrayList<String>();

	    ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/files/JSM2019-Online-Program-New.htm");
	    File file = new File(fullPath);
		HTMLParser htmlparser = new HTMLParser(file);
		htmlparser.parse();

		HashMap<String, HashSet<Talk>> speakerMap = htmlparser.getSpeakerMap(authors);
		HashMap<String, HashSet<Talk>> authorMap = htmlparser.getAuthorMap(authors);
		cleanMaps(speakerMap, authorMap);
		
		response.setContentType("text/html");  
	    PrintWriter out = response.getWriter();  
	      
	    String name = "Ronak";
	    String to = "rdupadhy@usc.edu";
	    
	    String jacobBien = "<a href=\"http://faculty.marshall.usc.edu/Jacob-Bien/\">Jacob Bien</a>";
	    String ronakUpadhyaya = "<a href=\"http://www.linkedin.com/in/ronakupadhyaya/\">Ronak Upadhyaya</a>";
	    
	    String subject = "Test from JSM Scheduler";
	    String msg = "Hi " + name + ",<br/><br/>Results from the JSM Scheduler are attached below.  The .ics files can be imported into your iCloud or Google calendars.\n"
	    		+ "<br/><br/>Recall that on the website, there were several lists of people whose work we think you may be interested in.  The attachments include two calendar files and a text file:\n"
	    		+ "<br/><br/>1) speakercalendar.ics: talks where those people are the speakers"
	    		+ "<br/>2) authorcalendar.ics: talks where those people are authors but are not the speaker"
	    		+ "<br/>3) a simple text file with all those talks"
	    		+ "<br/><br/>Best, <br/>"
	    		+ jacobBien + " and " + ronakUpadhyaya; 
	          
	    try {
			Mailer.send(to, subject, msg, speakerMap, authorMap, context);
		} catch (MessagingException e) {
			e.printStackTrace();
		}  
	    
	    out.print("message has been sent successfully");  
	    out.close();  
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		StringBuilder inputBuffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        inputBuffer.append(line);
	    }
	    String data = inputBuffer.toString();
	    JsonParser jsonParser = new JsonParser();
	    JsonObject jsonObject = jsonParser.parse(data).getAsJsonObject();
	    JsonElement authorsJsonElement = jsonObject.get("authors");
	    JsonElement nameJsonElement = jsonObject.get("name");
	    JsonElement emailJsonElement = jsonObject.get("email");
	    ArrayList<String> authors = new Gson().fromJson(authorsJsonElement, ArrayList.class);
	    String name = new Gson().fromJson(nameJsonElement, String.class);
	    String to = new Gson().fromJson(emailJsonElement, String.class);

	    ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/files/JSM2019-Online-Program-New.htm");
	    File file = new File(fullPath);
		HTMLParser htmlparser = new HTMLParser(file);
		htmlparser.parse();

		HashMap<String, HashSet<Talk>> speakerMap = htmlparser.getSpeakerMap(authors);
		HashMap<String, HashSet<Talk>> authorMap = htmlparser.getAuthorMap(authors);
		cleanMaps(speakerMap, authorMap);
		
		response.setContentType("text/html");  
	    PrintWriter out = response.getWriter();  
	    
	    String jacobBien = "<a href=\"http://faculty.marshall.usc.edu/Jacob-Bien/\">Jacob Bien</a>";
	    String ronakUpadhyaya = "<a href=\"http://www.linkedin.com/in/ronakupadhyaya/\">Ronak Upadhyaya</a>";
	    
	    String subject = "Test from JSM Scheduler";
	    String msg = "Hi " + name + ",<br/><br/>Results from the JSM Scheduler are attached below.  The .ics files can be imported into your iCloud or Google calendars.\n"
	    		+ "<br/><br/>Recall that on the website, there were several lists of people whose work we think you may be interested in.  The attachments include two calendar files and a text file:\n"
	    		+ "<br/><br/>1) speakercalendar.ics: talks where those people are the speakers"
	    		+ "<br/>2) authorcalendar.ics: talks where those people are authors but are not the speaker"
	    		+ "<br/>3) a simple text file with all those talks"
	    		+ "<br/><br/>Best, <br/>"
	    		+ jacobBien + " and " + ronakUpadhyaya; 
	          
	    try {
			Mailer.send(to, subject, msg, speakerMap, authorMap, context);
		} catch (MessagingException e) {
			e.printStackTrace();
		}  
	    
	    out.print("message has been sent successfully");  
	    out.close();  
	}
	
	public static void cleanMaps(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap) {
		HashSet<Talk> allTalks = new HashSet<Talk>();
		for(String speaker: speakerMap.keySet()) {
			HashSet<Talk> talks = speakerMap.get(speaker);
			for(Talk talk: talks) {
				allTalks.add(talk);
			}
		}
		for(String author: authorMap.keySet()) {
			HashSet<Talk> talks = authorMap.get(author);
			HashSet<Talk> talksCopy = new HashSet<Talk>(talks);
			for(Talk talk: talksCopy) {
				if(allTalks.contains(talk)) {
					talks.remove(talk);
				}
				else {
					allTalks.add(talk);
				}
			}
		}
	}

}

class Mailer {  
	public static void send(String to, String subject, String msg, HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap, ServletContext context) throws AddressException, MessagingException, IOException {  
	  
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

	Multipart multipart = new MimeMultipart();
	
	BodyPart messageBodyPart = new MimeBodyPart();
	messageBodyPart.setContent(msg, "text/html");
//    messageBodyPart.setText(msg);
	multipart.addBodyPart(messageBodyPart);
	
	File schedule = createTextFile(speakerMap, authorMap, context);
	File speakerCalendar = createSpeakerCalendar(speakerMap, authorMap, context);
	File authorCalendar = createAuthorCalendar(speakerMap, authorMap, context);
	addAttachment(multipart, schedule);
	addAttachment(multipart, speakerCalendar);
	addAttachment(multipart, authorCalendar);
	
    message.setContent(multipart);
		    
	Transport.send(message);  
		  
	System.out.println("Done");  
	
	}
	
	private static void addAttachment(Multipart multipart, File file) throws MessagingException {
	    DataSource source = new FileDataSource(file);
	    BodyPart messageBodyPart = new MimeBodyPart();        
	    messageBodyPart.setDataHandler(new DataHandler(source));
	    messageBodyPart.setFileName(file.getName());
	    multipart.addBodyPart(messageBodyPart);
	}
	
	public static File createTextFile(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap, ServletContext context) throws IOException {		
		ArrayList<Talk> allTalks = mergeMaps(speakerMap, authorMap);
		Collections.sort(allTalks, new TalkComparator());
		
		StringBuilder sb = new StringBuilder();
		writeToStringBuilder(sb, allTalks);
		
		String fullPath = context.getRealPath("/WEB-INF/files/schedule.txt");
		File file = new File(fullPath);
		OutputStream outputStream = new FileOutputStream(file);
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0){
           outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
		
		return file;
	}
	
	public static File createAuthorCalendar(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap, ServletContext context) throws IOException {
		ArrayList<Event> authorEvents = CustomCalendar.getEvents(authorMap);

		Calendar authorCalendar = new Calendar();
		try {
			authorCalendar = CustomCalendar.getICal(authorEvents);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String fullPath = context.getRealPath("/WEB-INF/files/authorcalendar.ics");
		File file = new File(fullPath);
		OutputStream outputStream = new FileOutputStream(file);
		InputStream inputStream = new ByteArrayInputStream(authorCalendar.toString().getBytes());
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0){
           outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
		
		return file;
	}
	
	public static File createSpeakerCalendar(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap, ServletContext context) throws IOException {		
		ArrayList<Event> speakerEvents = CustomCalendar.getEvents(speakerMap);

		Calendar speakerCalendar = new Calendar();
		try {
			speakerCalendar = CustomCalendar.getICal(speakerEvents);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String fullPath = context.getRealPath("/WEB-INF/files/speakercalendar.ics");
		File file = new File(fullPath);
		OutputStream outputStream = new FileOutputStream(file);
		InputStream inputStream = new ByteArrayInputStream(speakerCalendar.toString().getBytes());
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0){
           outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
		
		return file;
	}
	
	public static ArrayList<Talk> mergeMaps(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap) {
		ArrayList<Talk> allTalks = new ArrayList<Talk>();
		for(String name: speakerMap.keySet()) {
			HashSet<Talk> talks = speakerMap.get(name);
			for(Talk talk: talks) {
				talk.name = name;
				talk.type = "Speaker";
				allTalks.add(talk);
			}
		}
		for(String name: authorMap.keySet()) {
			HashSet<Talk> talks = authorMap.get(name);
			for(Talk talk: talks) {
				talk.name = name;
				talk.type = "Author";
				allTalks.add(talk);
			}
		}
		return allTalks;
	}
	
	public static void writeToStringBuilder(StringBuilder sb, ArrayList<Talk> allTalks) {
		for(int i = 0; i < allTalks.size(); i++) {
			Talk talk = allTalks.get(i);
			sb.append(talk.date + "\n");
			if(!talk.name.equals(talk.speaker)) {
				sb.append(talk.name + " (Speaker: " + talk.speaker + ") - " + talk.topic + "\n");
			}
			else {
				sb.append(talk.name + " - " + talk.topic + "\n");
			}
			sb.append(talk.startTime + " - " + talk.endTime + "\n");
			sb.append("Location: " + talk.location + "\n");
			sb.append("\n\n");
		}		
	}
	
	public static void cleanMaps(HashMap<String, HashSet<Talk>> speakerMap, HashMap<String, HashSet<Talk>> authorMap) {
		HashSet<Talk> allTalks = new HashSet<Talk>();
		for(String speaker: speakerMap.keySet()) {
			HashSet<Talk> talks = speakerMap.get(speaker);
			for(Talk talk: talks) {
				allTalks.add(talk);
			}
		}
		for(String author: authorMap.keySet()) {
			HashSet<Talk> talks = authorMap.get(author);
			HashSet<Talk> talksCopy = new HashSet<Talk>(talks);
			for(Talk talk: talksCopy) {
				if(allTalks.contains(talk)) {
					talks.remove(talk);
				}
				else {
					allTalks.add(talk);
				}
			}
		}
	}
}  

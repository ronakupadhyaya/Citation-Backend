import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws IOException {
		ArrayList<String> citingAuthors = new ArrayList<String>();
		ArrayList<String> citedAuthors = new ArrayList<String>();
				
		
//		PrintWriter printWriter = new PrintWriter("attendees.txt", "UTF-8");
		File file = new File("JSM2019-Online-Program-New.htm");
		HTMLParser htmlparser = new HTMLParser(file);
		htmlparser.parse();
		HashMap<String, Speaker> speakersMap = htmlparser.speakersMap;
		HashMap<String, Speaker> authorsMap = htmlparser.authorsMap;
		HashMap<String, Speaker> chairsMap = htmlparser.chairsMap;
			
		HashSet<String> attendees = new HashSet<String>();
		attendees.addAll(speakersMap.keySet());
		attendees.addAll(chairsMap.keySet());
//		for(String attendee : attendees) {
//			printWriter.println(attendee);
//		}
//		printWriter.close();
		
//		HashMap<String, HashSet<Talk>> speakerMap = htmlparser.getSpeakerMap(citedAuthors);
//		HashMap<String, HashSet<Talk>> authorMap = htmlparser.getAuthorMap(citedAuthors);
//		GoogleCalendar googleCalendar = new GoogleCalendar(speakerMap, authorMap);
		
	}
	
	public static void writeInterestingTalks(HashMap<String, HashSet<Talk>> interestingTalks) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printWriter = new PrintWriter("interestingTalks.txt", "UTF-8");
		for(String author: interestingTalks.keySet()) {
			printWriter.println(author + "\n");
			HashSet<Talk> talks = interestingTalks.get(author);
			for(int i = 0; i < talks.size(); i++) {
			}
			for(Talk talk: talks) {
				printWriter.println(talk.toString());
			}
		}
		printWriter.close();
	}
	
	public static String parseLine(String line) {
		String[] result = line.split(",");
		String name = result[0].split(":")[1].trim();
		return name;
	}
}

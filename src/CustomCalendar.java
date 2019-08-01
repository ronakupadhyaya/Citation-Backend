import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.TzName;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

public class CustomCalendar {

	public static ArrayList<Event> getEvents(HashMap <String, HashSet<Talk>> map) {
		ArrayList<Event> events = new ArrayList<Event>();
		for(String author: map.keySet()) {
			HashSet<Talk> talks = map.get(author);
			for(Talk talk: talks) {
				Event event = getEvent(author, talk);
				events.add(event);
			}
		}
		return events;
	}

	public static String formatDate(String dateString, String timeString) {
		String[] dateArray = dateString.split("/");
		int month = Integer.parseInt(dateArray[0]);
		int day = Integer.parseInt(dateArray[1]);
		int year = Integer.parseInt(dateArray[2]);

		String time = timeString.split("\\s+")[0];
		String[] timeArray = time.split(":");
		int hours = Integer.parseInt(timeArray[0]);
		int minutes = Integer.parseInt(timeArray[1]);
		if (timeString.contains("PM") && hours != 12) {
			hours += 12;
		}

		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day, hours, minutes);
		Date date = calendar.getTime();

		DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		String dateAsISOString = df.format(date);
		return dateAsISOString;
	}
	
	private static Event getEvent(String author, Talk talk) {
		String date = talk.date;
		String startTime = talk.startTime;
		String endTime = talk.endTime;
		String location = talk.location;
		
		String title = author.equals(talk.speaker) ? 
				author + " - " + talk.topic : author + " (Speaker: " + talk.speaker + ") - " + talk.topic;		
		String start = formatDate(date, startTime);
		String end = formatDate(date, endTime);

		return new Event(title, start, end, location);
	}
	
	public static Calendar getICal(ArrayList<Event> events) throws ParseException, SocketException {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone tz = registry.getTimeZone("America/Denver");
		
		for(int i = 0; i < events.size(); i++) {
			Event event = events.get(i);

			DateTime start = new DateTime(event.start, tz);
			DateTime end = new DateTime(event.end, tz);
			String title = event.title;
			
			VEvent vEvent = new VEvent(start, end, title);
			vEvent.getProperties().add(new Location(event.location));
			calendar.getComponents().add(vEvent);
		}
		
		return calendar;
	}
	
	public static String getICalString(ArrayList<Event> events) {
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN:VCALENDAR \r\n");
		sb.append("VERSION:1.0 \r\n");
		sb.append("CALSCALE:GREGORIAN \r\n");
		for(int i = 0; i < events.size(); i++) {
			sb.append("BEGIN:VEVENT \r\n");
			Event event = events.get(i);
			System.out.println(i);
			sb.append("DTSTAMP:20190515T222247Z \r\n");
			sb.append("DTSTART:" + event.start + " \r\n");
			sb.append("DTEND:" + event.start + " \r\n");
			sb.append("SUMMARY:" + event.title + " \r\n");
			sb.append("END:VEVENT \r\n");
		}
		sb.append("END:VCALENDAR \r\n");
		return sb.toString();
	}

}

class Event {
	String title;
	String start;
	String end;
	String location;
	
	public Event(String title, String start, String end, String location) {
		this.title = title;
		this.start = start;
		this.end = end;
		this.location = location;
	}
}
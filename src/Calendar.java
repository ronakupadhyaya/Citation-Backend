import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;

public class Calendar {

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

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dateAsISOString = df.format(date);
		return dateAsISOString;
	}

	private static Event getEvent(String author, Talk talk) {
		String topic = talk.topic;
		String date = talk.date;
		String startTime = talk.startTime;
		String endTime = talk.endTime;

		String start = formatDate(date, startTime);
		String end = formatDate(date, endTime);

		return new Event(start, end, author + " - " + topic);
	}

}

class Event {
	String start;
	String end;
	String title;

	public Event(String start, String end, String title) {
		this.start = start;
		this.end = end;
		this.title = title;
	}
}
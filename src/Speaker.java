import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

public class Speaker {
	String name;
	HashSet<Talk> talks;
	
	public Speaker(String name) {
		this.name = name;
		talks = new HashSet<Talk>();
	}
	
	public String toString() {
		String result = name + "\n";
		for(Talk talk : talks) {
			result += talk.toString();
		}
		return result;
	}
}

class Talk {
	String location;
	String topic;
	String startTime;
	String endTime;
	String date;
	String name;
	String type;
	String speaker;
	
	public Talk(String location, String topic, String startTime, String date) {
		this.location = location;
		this.topic = topic;
		this.startTime = startTime;
		this.date = date;
	}
	
	public String toString() {
		return  "Date: " + date + "\n" +
				"Location: " + location + "\n" +
				"Topic: " + topic + "\n" + 
				"Start Time: " + startTime + "\n" +
				"End Time: " + endTime + "\n" +
				"Speaker:" + speaker + "\n";
	}
}

class TalkComparator implements Comparator<Talk> 
{ 
    public int compare(Talk a, Talk b) { 
    	String[] aDate = a.date.split("/");
    	int aMonth = Integer.parseInt(aDate[0]);
    	int aDay = Integer.parseInt(aDate[1]);
    	String[] aTime = a.startTime.split("\\s+");
    	String[] aNumbers = aTime[0].split(":");
    	String aPeriod = aTime[1];
    	int aHour = Integer.parseInt(aNumbers[0]);
    	int aMinute = Integer.parseInt(aNumbers[1]);
    	if(aPeriod.equals("PM") && aHour != 12) {
    		aHour += 12;
    	}
    	if(aPeriod.equals("AM") && aHour == 12) {
    		aHour -= 12;
    	}
    	
    	String[] bDate = b.date.split("/");
    	int bMonth = Integer.parseInt(bDate[0]);
    	int bDay = Integer.parseInt(bDate[1]);
    	String[] bTime = b.startTime.split("\\s+");
    	String[] bNumbers = bTime[0].split(":");
    	String bPeriod = bTime[1];
    	int bHour = Integer.parseInt(bNumbers[0]);
    	int bMinute = Integer.parseInt(bNumbers[1]);
    	if(bPeriod.equals("PM") && bHour != 12) {
    		bHour += 12;
    	}
    	if(bPeriod.equals("AM") && bHour == 12) {
    		bHour -= 12;
    	}
    	
    	if(!(aMonth == bMonth && aDay == bDay)) {
    		return aMonth == bMonth ? aDay - bDay : aMonth - bMonth;
    	}
    	
    	return aHour == bHour ? aMinute - bMinute : aHour - bHour;
    } 
} 
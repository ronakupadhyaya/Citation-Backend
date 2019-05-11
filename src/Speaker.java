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
				"End Time: " + endTime + "\n";
	}
}
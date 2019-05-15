import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;

public class GoogleCalendar {

  private final String APPLICATION_NAME = "";
  private final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");
  private FileDataStoreFactory dataStoreFactory;
  private HttpTransport httpTransport;
  private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private com.google.api.services.calendar.Calendar client;

  final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

  private Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(GoogleCalendar.class.getResourceAsStream("client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
          + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
        .build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }
  
  public GoogleCalendar() {
	  try {
	      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	      Credential credential = authorize();
	      client = new com.google.api.services.calendar.Calendar.Builder(
	          httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	    } catch (IOException e) {
	      System.err.println(e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
//	    System.exit(1);
  }

  private void showCalendars() throws IOException {
    CalendarList feed = client.calendarList().list().execute();
  }

  private void addCalendarsUsingBatch() throws IOException {
    BatchRequest batch = client.batch();

    // Create the callback.
    JsonBatchCallback<Calendar> callback = new JsonBatchCallback<Calendar>() {

      @Override
      public void onSuccess(Calendar calendar, HttpHeaders responseHeaders) {
        addedCalendarsUsingBatch.add(calendar);
      }

      @Override
      public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
        System.out.println("Error Message: " + e.getMessage());
      }
    };

    // Create 2 Calendar Entries to insert.
    Calendar entry1 = new Calendar().setSummary("Calendar for Testing 1");
    client.calendars().insert(entry1).queue(batch, callback);

    Calendar entry2 = new Calendar().setSummary("Calendar for Testing 2");
    client.calendars().insert(entry2).queue(batch, callback);

    batch.execute();
  }

  Calendar addCalendar(String calendarSummary) throws IOException {
    Calendar entry = new Calendar();
    entry.setSummary(calendarSummary);
    Calendar result = client.calendars().insert(entry).execute();
    return result;
  }

  private Calendar updateCalendar(Calendar calendar) throws IOException {
    Calendar entry = new Calendar();
    entry.setSummary("Updated Calendar for Testing");
    Calendar result = client.calendars().patch(calendar.getId(), entry).execute();
    return result;
  }

  void addSpeakerEvents(Calendar speakerCalendar, HashMap<String, HashSet<Talk>> speakerMap) throws IOException {
	  for(String speakerString: speakerMap.keySet()) {
		  HashSet<Talk> talks = speakerMap.get(speakerString);
		  for(Talk talk: talks) {
			  addEvent(speakerCalendar, speakerString, talk);
		  }
	  }
  }
  
  void addAuthorEvents(Calendar authorCalendar, HashMap<String, HashSet<Talk>> authorMap) throws IOException {
	  for(String authorString: authorMap.keySet()) {
		  HashSet<Talk> talks = authorMap.get(authorString);
		  for(Talk talk: talks) {
			  addEvent(authorCalendar, authorString, talk);
		  }
	  }
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
	  if(timeString.contains("PM") && hours != 12) {
		  hours += 12;
	  }
	  
	  GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day, hours + 6, minutes);
	  Date date = calendar.getTime();
	  
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      String dateAsISOString = df.format(date);
      return dateAsISOString;
  }
  
  private void addEvent(Calendar calendar, String author, Talk talk) throws IOException {
	  String topic = talk.topic;
	  String date = talk.date;
	  String startTime = talk.startTime;
	  String endTime = talk.endTime;
	  
	  Event event = new Event()
			  .setSummary(author + " - " + topic)
		      .setLocation("700 14th St, Denver, CO 80202")
			  .setDescription(topic);  
	  
	  DateTime startDateTime = new DateTime(formatDate(date, startTime));
	  EventDateTime start = new EventDateTime()
	      .setDateTime(startDateTime)
	      .setTimeZone("America/Denver");
	  event.setStart(start);

	  DateTime endDateTime = new DateTime(formatDate(date, endTime));
	  EventDateTime end = new EventDateTime()
	      .setDateTime(endDateTime)
	      .setTimeZone("America/Denver");
	  event.setEnd(end);
	  
	  Event result = client.events().insert(calendar.getId(), event).execute();
  }

  private Event newEvent() {
    Event event = new Event();
    event.setSummary("New Event");
    Date startDate = new Date();
    Date endDate = new Date(startDate.getTime() + 3600000);
    DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
    event.setStart(new EventDateTime().setDateTime(start));
    DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
    event.setEnd(new EventDateTime().setDateTime(end));
    return event;
  }

  private void showEvents(Calendar calendar) throws IOException {
    Events feed = client.events().list(calendar.getId()).execute();
  }

  private void deleteCalendarsUsingBatch() throws IOException {
    BatchRequest batch = client.batch();
    for (Calendar calendar : addedCalendarsUsingBatch) {
      client.calendars().delete(calendar.getId()).queue(batch, new JsonBatchCallback<Void>() {

        @Override
        public void onSuccess(Void content, HttpHeaders responseHeaders) {
          System.out.println("Delete is successful!");
        }

        @Override
        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
          System.out.println("Error Message: " + e.getMessage());
        }
      });
    }

    batch.execute();
  }

  private void deleteCalendar(Calendar calendar) throws IOException {
    client.calendars().delete(calendar.getId()).execute();
  }
}


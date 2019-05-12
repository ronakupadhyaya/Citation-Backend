import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

/**
 * Servlet implementation class getSchedule
 */
@WebServlet("/getSchedule")
public class getSchedule extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public getSchedule() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    JsonParser jsonParser = new JsonParser();
	    JsonObject jsonObject = jsonParser.parse(data).getAsJsonObject();
	    JsonElement jsonElement = jsonObject.get("authors");
	    ArrayList<String> authors = new Gson().fromJson(jsonElement, ArrayList.class);
	    
	    ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/files/JSM2019-Online-Program.htm");
	    File file = new File(fullPath);
		HTMLParser htmlparser = new HTMLParser(file);
		htmlparser.parse();
		
		HashMap<String, HashSet<Talk>> speakerMap = htmlparser.getSpeakerMap(authors);
		HashMap<String, HashSet<Talk>> authorMap = htmlparser.getAuthorMap(authors);
		ArrayList<Event> speakerEvents = Calendar.getEvents(speakerMap);
		ArrayList<Event> authorEvents = Calendar.getEvents(authorMap);
		
		HashMap<String, ArrayList<Event>> schedule = new HashMap<String, ArrayList<Event>>();
		schedule.put("Speaker", speakerEvents);
		schedule.put("Author", authorEvents);
		
		String json = new Gson().toJson(schedule);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}

}

class GetScheduleBody {
	ArrayList<String> authors;
}

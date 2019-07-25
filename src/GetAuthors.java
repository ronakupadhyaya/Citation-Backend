import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class getAuthors
 */
@WebServlet("/getAuthors")
public class GetAuthors extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ArrayList<Result> citedAuthors;
       
    public GetAuthors() throws FileNotFoundException {
        super();	
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		String name = request.getParameter("name");
		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/files/precomputed_results.csv");
	    File file = new File(fullPath);
		
		ArrayList<Result> citedAuthors = new ArrayList<Result>();
		ArrayList<Result> citingAuthors = new ArrayList<Result>();
		ArrayList<Result> coAuthors = new ArrayList<Result>();
		
		if(name != null) {
			String result = parseCSV(file, name);
			if(!result.equals("Not Found")) {
				String[] arr = result.split(",");
	            String coAuthorsString = "";
	            String citesString = "";
	            String isCitedByString = "";
	            for(int i = 0; i < arr.length; i++) {
	            	if(i == 1) coAuthorsString = arr[i];
	            	if(i == 2) citesString = arr[i];
	            	if(i == 3) isCitedByString = arr[i];
	            } 
	            coAuthors = parseCSVData(coAuthorsString);
	            citingAuthors = parseCSVData(isCitedByString);
	            citedAuthors = parseCSVData(citesString);
	            System.out.println("Here");
			}
			else {
				Author author = new Author(name);
				citedAuthors = (Author.mergeAuthors(author.citedAuthors));
				citingAuthors = (Author.mergeAuthors(author.citingAuthors));
			}
			
			HashMap<String, ArrayList<Result>> map = new HashMap<String, ArrayList<Result>>();
			map.put("Cited Authors", slice(citedAuthors));
			map.put("Citing Authors", slice(citingAuthors));
			map.put("Co Authors", slice(coAuthors));
			
			String json = new Gson().toJson(map);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
		}
	}

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
    public static Result parseLine(String line) {
		String[] result = line.split(",");
		String name = result[0].split(":")[1].trim();
		int count = Integer.parseInt(result[1].split(":")[1].trim());
		return new Result(name, count);
	}
	
	public static ArrayList<Result> removeSelf(ArrayList<Result> authors, String name) {
		ArrayList<Result> result = new ArrayList<Result>();
		for(int i = 0; i < authors.size(); i++) {
			Result author = authors.get(i);
			String authorName = author.name;
			if(!authorName.equals(name)) {
				result.add(author);
			}
		}
		return result;
	}
	
	public static ArrayList<Result> formatList(ArrayList<Result> authors) {
		ArrayList<Result> result = new ArrayList<Result>();
		for(int i = 0; i < authors.size(); i++) {
			Result author = authors.get(i);
			String authorName = author.name;
			String formattedName = formatString(authorName);
			author.name = formattedName;
		}
		return result;
	}
	
	public static String formatString(String string) {
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < string.length(); i++) {
			Character current = string.charAt(i);
			Character previous = string.charAt(i - 1);
			if(previous == ' ' || previous == '.' || previous == '-' || previous == '\'') {
				sb.append(current);
			}
			else {
				sb.append(Character.toLowerCase(current));
			}
		}
		return sb.toString();
	}
	
	public static String parseCSV(File file, String query) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            String name = arr[0];
            if(name.equals(query)) return line;
        }
		return "Not Found";
	}
	
	public static ArrayList<Result> parseCSVData(String string) {
		ArrayList<Result> authors = new ArrayList<Result>();
		String[] arr = string.split("::");
    	for(int i = 0; i < arr.length; i++) {
    		String authorName = arr[i];
    		Result author = new Result(authorName, 1);
    		authors.add(author);
    	}
    	return authors;
	}
	
	public static ArrayList<Result> slice(ArrayList<Result> authors) {
		ArrayList<Result> results = new ArrayList<Result>();
		int limit = Math.min(authors.size(), 30);
		for(int i = 0; i < limit; i++) {
			results.add(authors.get(i));
		}
		return results;
	}

}

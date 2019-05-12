import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetCitingAuthors
 */
@WebServlet("/getCitingAuthors")
public class GetCitingAuthors extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ArrayList<Result> citingAuthors;
       
    public GetCitingAuthors() throws FileNotFoundException {
        super();	
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String name = request.getParameter("name");
			
		citingAuthors = new ArrayList<Result>();
		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/files/citingauthors.txt");
		Scanner scanner = new Scanner(new File(fullPath));
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			citingAuthors.add(parseLine(line));
		}
		scanner.close();
		
		String json = new Gson().toJson(Author.mergeAuthors(citingAuthors));
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private static Result parseLine(String line) {
		String[] result = line.split(",");
		String name = result[0].split(":")[1].trim();
		int count = Integer.parseInt(result[1].split(":")[1].trim());
		return new Result(name, count);
	}

}

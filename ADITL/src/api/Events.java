package api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
/**
 * Servlet implementation class Events
 */
@WebServlet("/Events")
public class Events extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Events() {
        super();
      
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		
		String date = request.getParameter("date"); // format must be yyyy-mm-dd
		String city = request.getParameter("city");
		// use dbpedia query to get events
		
		String personBornQuery = "PREFIX ont: <http://dbpedia.org/ontology/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#> SELECT ?page ?place ?name ?date WHERE {" + 
				"?person ont:birthDate ?date; foaf:page ?page; ont:birthPlace ?place; foaf:name ?name ." + 
				"FILTER( ( ( datatype(?date) = xsd:date ) || ( datatype(?date) = xsd:dateTime ) )  && ( regex(str(?date), \"" + date + "\") )&& (regex(str(?place),\"Australia\") || regex(str(?place),\"" + city + "\")) ) }";
		URL url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&output=json&query=" + URLEncoder.encode(personBornQuery, "UTF-8")); 
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept", "application/json");
    
	    String line;
	    String result = "";
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = rd.readLine()) != null) {
	       result += line;
	    }
	    rd.close();
	    // results may be alternate display names for same person, so will need to filter these out in client
	    out.print(result);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

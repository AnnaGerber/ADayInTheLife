package api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Rainfall
 */
@WebServlet("/Rainfall")
public class Rainfall extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Rainfall() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    
    String date = request.getParameter("date"); // format must be yyyy-mm-dd
    String state = request.getParameter("state");
    
    MongoDBAPI mongo = new MongoDBAPI(Strings.MONGO_DB_NAME);
    
    String result = mongo.getRainfall(date, state);
    
    out.print(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
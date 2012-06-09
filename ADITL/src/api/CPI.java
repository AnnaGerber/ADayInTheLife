package api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CPI")
public class CPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	  PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    
    String date = request.getParameter("date"); // format must be yyyy-mm-dd
    String state = request.getParameter("state");

    String result = MongoDBAPI.getCPI(date, state);
    
    out.print(result);
	}
}

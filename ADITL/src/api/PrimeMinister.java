package api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PrimeMinister")
public class PrimeMinister extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	  PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    
    String date = request.getParameter("date"); // format must be yyyy-mm-dd
    
    String result = MongoDBAPI.getPrimeMinister(date);
    
    out.print(result);
	}
}

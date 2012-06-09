package api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Temperature")
public class Temperature extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  
  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    
    String date = request.getParameter("date"); // format must be yyyy-mm-dd
    String state = request.getParameter("state");
    String minormax = request.getParameter("minormax"); //must be 'min' or 'max'
    
    String temperature = "";
    if (minormax.equalsIgnoreCase("min")) {
      temperature = MongoDBAPI.getMinTemperature(date, state);
    } else if (minormax.equalsIgnoreCase("max")) {
      temperature = MongoDBAPI.getMaxTemperature(date, state);
    }
    
    out.print(temperature);
  }
}

package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Temperature
 */
@WebServlet("/Temperature")
public class Temperature extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public Temperature()
  {
    super();
    // TODO Auto-generated constructor stub
  }

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
    
    MongoDBAPI mongo = new MongoDBAPI(Strings.MONGO_DB_NAME);
    
    String temperature = "";
    if (minormax.equalsIgnoreCase("min")) {
      mongo.getMinTemperature(date, state);
    } else if (minormax.equalsIgnoreCase("max")) {
      mongo.getMaxTemperature(date, state);
    }
    
    out.print(temperature);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    // TODO Auto-generated method stub
  }

}

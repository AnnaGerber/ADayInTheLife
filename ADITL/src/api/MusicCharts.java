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
import java.util.Properties;
/**
 * Servlet implementation class MusicCharts
 */
@WebServlet("/MusicCharts")
public class MusicCharts extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String bbKey = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MusicCharts() {
        super();
      
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		if (this.bbKey == null){
			
			Properties props = new Properties();
			try{
				// properties file should be put here (not included in repo)
				props.load(getServletContext().getResourceAsStream("/WEB-INF/aditl.properties"));
			this.bbKey = props.getProperty("bbkey");
			} catch (Exception e){
				out.println(e.getMessage());
			}
		}
		String year = request.getParameter("year");
		// TODO get chart data and return
		

       // out.println("{year: '" + year + "'}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

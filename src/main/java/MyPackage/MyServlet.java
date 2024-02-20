package MyPackage;

import com.google.gson.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setting up API
		//API Key
		String apiKey = "a062b13f5a1c8e61bb80665e68e7faf0\r\n";	
		
		// Get the city from the form input
		String city = request.getParameter("city"); 

        // Create the URL for the OpenWeatherMap API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
		try {
			//api integration
			//now the apiUrl is a url otherwise earlier it was just a string
			URL url= new URL(apiUrl);
		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
        
			//gets input stream from the connection
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);
			//System.out.println(reader);
            
			Scanner scanner = new Scanner(reader);
			//used StringBuilder as String is immutable and we can use the append()
			StringBuilder responseContent = new StringBuilder();

			while (scanner.hasNext()) {
				responseContent.append(scanner.nextLine());
			}
            
			scanner.close();
			//System.out.println(responseContent);
        
			//(Type casting) Parse the JSON response to extract temperature, date, and humidity (from string to json)
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
        
			//Now getting particular data from the json object
			//Date & Time
			long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			String date = new Date(dateTimestamp).toString();
        
			//Temperature
			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble(); //first searching for the data then converting it into double
			int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
			//Humidity
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
			//Wind Speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
			//Weather Condition
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
			// Set the data as request attributes (for sending to the jsp page)
			request.setAttribute("date", date);
			request.setAttribute("city", city);
			request.setAttribute("temperature", temperatureCelsius);
			request.setAttribute("weatherCondition", weatherCondition); 
			request.setAttribute("humidity", humidity);    
			request.setAttribute("windSpeed", windSpeed);
			request.setAttribute("weatherData", responseContent.toString());
        
			connection.disconnect();
		}catch (IOException e) {
            e.printStackTrace();
        }
		// Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("Index.jsp").forward(request, response);
	}

}

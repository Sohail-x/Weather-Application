package weatherWeb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
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
		
		//Get the city name from the web
		String city = request.getParameter("city");
		
		//API Key
		String api_key = "573aa81bb933a41facb68bd949aeebd3";
		
		//URL Encoder...
		String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
		
		//Craete the URL for the OpenWeather Map API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + api_key;
		
		//API Integrate
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		//Reading the Data...
		int status = connection.getResponseCode();
		InputStream inputStream;
		if (status >= 200 && status < 300) {
		    inputStream = connection.getInputStream();
		} else {
		    inputStream = connection.getErrorStream();
		}
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		//Store Data in String
		StringBuilder responseContent = new StringBuilder();
		
		//To take input from the reader, will create sc scanner object...
		Scanner sc = new Scanner(reader);
		
		while(sc.hasNextLine()) {    //hasNext() = checks for next tokens
			responseContent.append(sc.nextLine());  //nextLine() = read next Lines 
		} 
		
		sc.close();
		
		//Type Casting = Parsing the data in JSON...
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		// System.out.println(jsonObject);
		
		//Date & Time
		long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new Date(dateTimestamp).toString();
		
		//Temperature...
		double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperature = (int) (temperatureKelvin - 273.15);
		
		//Humidity
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		
		//Wind Speed...
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//Waethet Condition...
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();	
		
		
		// Set the data as request attributes (for sending to the jsp page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperature);
		request.setAttribute("weatherCondition", weatherCondition); 
		request.setAttribute("humidity", humidity);    
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weatherData", responseContent.toString());
		
		connection.disconnect();
      
//      Forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}

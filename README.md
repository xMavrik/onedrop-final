# onedrop-final

As per requirement sheet 

 1. Accept a City or Zip code and provide the current weather and 7-day forecast
 2. Accept a list of cities or zip codes and provides aggregate weather data for those zip codes (like average, min/max for temperature, humidity etc.)
 3. Cache the data for 10 minutes.
 4. Provide any alerts for the city or zip code you are querying
 5. Both input and output should be in JSON format


This endpoint from the weatherService does it all, both current and 7 day weather, as well as any potential alerts, but it requires latitude and longitude coordinates 

https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}

![alt text](https://user-images.githubusercontent.com/26445751/142801901-e2097151-5205-4195-b660-1a02522fa861.png)


To get around this, must make a call to Current Weather data
 => https://api.openweathermap.org/data/2.5/weather?q=NewYorkCity
   this will give you ("lat":40.7143,"lon":-74.006) the same works for zip code, requirement 1 is complete
   
  For requirements (2, 3, 4, and 5) I implemented the following logic  
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142803412-3afbaa93-d128-44cb-bab5-b72a5ae41f5c.png)
  
  a single city or zip search can be supported with a simple get request 
  => GET     /weather/:input  controllers.HomeController.getWeather(input: String)
  
  
  for multi-city/zip I decided to expose a post request and use the body to hold the json
  could have potentially used GET, but it would not scale with many cities url would be to long
  ex: /weather/[New York City, Boston, Charlotte, Los Angeles]

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
  
  a single city or zip search can be supported with a simple get request 
  => GET     /weather/:input  controllers.HomeController.getWeather(input: String)
  
  
  for multi-city/zip I decided to expose a POST request and use the body to hold the json.
  could have potentially used GET, but it would not scale with many cities, url would be too long
  ex: /weather/[New York City, Boston, Charlotte, Los Angeles]
  
  POST Request looks like this
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142803947-7d9e4793-efce-4233-b23f-602f1f244347.png)
  
  I desinged the endpoint to accept both zip codes and cities in the same payload, seemed easier to use
  
  This is the overall code flow 
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142803412-3afbaa93-d128-44cb-bab5-b72a5ae41f5c.png)
  
  routes will begin at Homecontroller which will call for either city or zip code (or both)and begin to check the cache for each entry. Entries not in the cahce are sent to the apiEventHandler where the WeatherAPI will finally be checked, the entry will be cached here too
  
  if entry doesnt exist, it will be queried from the api and cached
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142804539-e5876057-8b84-4995-bddb-8a7840bc9e43.png)
  
  if entry already exists, it is pulled from cache, processed and sent back to controller to be Actioned out
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142804669-2c4f2121-452b-43d7-90f2-62c9a6ec15cd.png)
  
  
  
 Final payload is as follows, aggregate data is high and low temp and humidity as well as average day temperature across all submitted cities/zip codes
 Alerts are tacked on as well, looks like California is having issues with high winds and fires at the moment
 
 {"lowestHumidity":16,"highestHumidity":92,"lowestTemp":37.85,"highestTemp":82.06,"averageTemp":59.02250000000001,"Alerts":"Los Angeles...WIND ADVISORY REMAINS IN EFFECT FROM 3 AM SUNDAY TO NOON PST\nMONDAY...\n* WHAT...Northeast winds 15 to 30 mph with gusts up to between\n40 and 50 mph expected.\n* WHERE...Ventura County Coast and Los Angeles County Coast\nincluding Downtown Los Angeles.\n* WHEN...From 3 AM Sunday to noon PST Monday.\n* IMPACTS...Gusty winds will blow around unsecured objects and\nmake driving difficult, especially for high profile vehicles.\nTree limbs could be blown down and a few power outages may\nresult. Roadways may be affected by gusty cross winds. This\nincludes the Pacific Coast Highway, the 110, 405 and 710\nfreeways in Los Angeles County, and Highway 101 in Ventura\nCounty."}

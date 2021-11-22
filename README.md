# onedrop-final

<b>INTRO</b> 
--------------------------------------------------------------------------------------
As per the stated requirements: 

An API must be created that sends back weather details. The tool must also cache recently searched locations and provide alerts when available.
 
I decided to use Scala Play architecure during the creation process. This has libraires like playWebService (playWS) and scalaCache (Caffeine) that allowed me to quickly implement API querying and caching features.
 
This is the overall code flow: 
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142803412-3afbaa93-d128-44cb-bab5-b72a5ae41f5c.png)
  
Routes will begin at Homecontroller which will call for city, zip code, or both, as well as begin to check the cache for each entry. Entries not in the cache are sent to the apiEventHandler where the WeatherAPI will finally be checked. The entry will also be cached there.
  
  If entry doesnt exist, it will be queried from the API and cached.
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142804539-e5876057-8b84-4995-bddb-8a7840bc9e43.png)
  
  If entry already exists, it is pulled from cache, processed and sent back to controller to be Actioned out.
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142804669-2c4f2121-452b-43d7-90f2-62c9a6ec15cd.png)
   
   
   This becomes very important when we are dealing with multiple cities searched at once. Given this payload:
    => [New York City, Boston, Charlotte, Los Angeles]
    If NYC and Boston have been searched in the last 10 mintues, but Charlotte and LA have not, no problem. NYC and Boston will just be pulled from cache, while CLT and LA will be queried and added to the cache for 10 minutes. Also, if an incorrect city name is entered, it simlpy won't be included in the final payload, no need to reject the whole thing.
    ex: [N3w Y0rk C1t4, Boston, Charlotte, Los Angeles]
  
   
  <b>Input/Output</b> 
  --------------------------------------------------------------------------------------------- 
  
  A single city or zip search can be supported with a simple get request 
  => GET     /weather/:input  controllers.HomeController.getWeather(input: String)
  
  This endpoint from the weatherService does it all, both current and 7 day weather, as well as any potential alerts, but it requires latitude and longitude coordinates. 

  https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}

![alt text](https://user-images.githubusercontent.com/26445751/142801901-e2097151-5205-4195-b660-1a02522fa861.png)


To get around this, it must make a call to Current Weather data
 => https://api.openweathermap.org/data/2.5/weather?q=NewYorkCity
   this will give you ("lat":40.7143,"lon":-74.006) the same works for zip code.
  
  For multi-city/zip I decided to expose a POST request and use the body to hold the json.
  I could have potentially used GET, but it would not scale with many cities, the URL would be too long.
  ex: /weather/[New York City, Boston, Charlotte, Los Angeles]
  
  POST Request looks like this:
  
  ![alt text](https://user-images.githubusercontent.com/26445751/142803947-7d9e4793-efce-4233-b23f-602f1f244347.png)
  
  I designed the endpoint to accept both zip codes and cities in the same payload, seemed easier to use.
  
  
  
 Final payload is as follows, aggregate data is high and low temp and humidity as well as average day temperature across all submitted cities/zip codes.
 Alerts are tacked on as well, looks like California is having issues with high winds and fires at the moment.
 
 {"lowestHumidity":16,"highestHumidity":92,"lowestTemp":37.85,"highestTemp":82.06,"averageTemp":59.02250000000001,"Alerts":"Los Angeles...WIND ADVISORY REMAINS IN EFFECT FROM 3 AM SUNDAY TO NOON PST\nMONDAY...\n* WHAT...Northeast winds 15 to 30 mph with gusts up to between\n40 and 50 mph expected.\n* WHERE...Ventura County Coast and Los Angeles County Coast\nincluding Downtown Los Angeles.\n* WHEN...From 3 AM Sunday to noon PST Monday.\n* IMPACTS...Gusty winds will blow around unsecured objects and\nmake driving difficult, especially for high profile vehicles.\nTree limbs could be blown down and a few power outages may\nresult. Roadways may be affected by gusty cross winds. This\nincludes the Pacific Coast Highway, the 110, 405 and 710\nfreeways in Los Angeles County, and Highway 101 in Ventura\nCounty."}
 
    
  <b>Testing</b>   
  --------------------------------------------------------------------------------------------- 
 
 
 Testing was done as well, single endpoint and multi-city POST end point were tested. Confirmed failures should failed and the successes look correct.
 
![alt text](https://user-images.githubusercontent.com/26445751/142805160-62965166-be07-4ca4-a5d3-17ede11ceaa3.png)


 
  <b>Future Work</b> 
  --------------------------------------------------------------------------------------------- 
  
  If I had more time on this I would implement the following:
  
  -an optional UI page to display the weather data, maybe some graphs too.
  
  -historical data support. (ex: its currently July and you want to check historical temperatures for August)
  
  -better error handling. (ex: if you accidentally submit N3w York City, try and find closest comparable city by string match percentage and return instead of error)
  
  

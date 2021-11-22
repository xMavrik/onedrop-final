package controllers

import play.api.{Configuration, cache}

import javax.inject._
import play.api.mvc._
import play.api.libs.ws._

import scala.language.postfixOps
import play.api.cache._
import play.api.libs.json.{JsObject, JsValue}
import models.CacheSearchService
import play.api.libs.json.JsPath.\
import play.api.libs.json._

import scala.Console.println
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.xml.NodeSeq.Empty.\

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cacheSearchService: CacheSearchService, ws: WSClient, cache: AsyncCacheApi, val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   */

  def multipleGetWeather = Action { request =>
    val json = request.body.asJson.get

    val cityList = (json \\ "city")

    // TODO: will need to handle multiple zip codes
    val zipList = (json \\ "zip")

    //only 1 record provided from json, error out here
    if (cityList.length < 2 && zipList.length < 2) {
      BadRequest("Need at least two cities/zip codes to call this api")
    }
    //handle multiple cities only
    else if (zipList.length == 0) {


      var futureWeather: JsValue = null

      var finalString = "["

      //loop to handle each city in list, will check cache for every city
      // in case they have been searched in the past 10 minutes
      cityList.foreach(eachCity =>
        // for whatever reason, string coming in shows as liter "Houston", must trim first and last char so api call is clean
        if (eachCity.toString().substring(1, eachCity.toString().length - 1).matches("[a-z A-Z]+")) {
          finalString += handleCity(eachCity.toString().substring(1, eachCity.toString().length - 1)) + ","
        }
      )

      finalString = finalString.substring(0, finalString.length - 1)
      finalString += "]"

      val finalJson: JsValue = Json.parse(finalString)

      val maxTemp = (finalJson \\ "dayMax").maxBy(_.toString)
      val minTemp = (finalJson \\ "dayMin").minBy(_.toString)
      val minHumidity = (finalJson \\ "humidity").minBy(_.toString)
      val maxHumidity = (finalJson \\ "humidity").maxBy(_.toString)
      var tempAvg = 0.0

      (finalJson \\ "dayTemp").foreach(u =>
        tempAvg += u.toString().toDouble
      )
      tempAvg = (tempAvg / (finalJson \\ "dayTemp").length)

      if ((finalJson \\ "Alerts").isEmpty) {
        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg  }"""
        Ok(Json.parse(outputString))
      }
      else {
        var alertString = ""
        (finalJson \\ "Alerts").foreach(u =>
          alertString += u.toString().replaceAll("\"", "")
        )

        alertString = '"' + alertString + '"'
        print(alertString)

        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg, "Alerts": $alertString  }"""
        Ok(Json.parse(outputString))
      }
    }
    //handle multiple zip codes only
    else if (cityList.length == 0) {
      var futureWeather: JsValue = null

      var finalString = "["

      //loop to handle each city in list, will check cache for every city
      // in case they have been searched in the past 10 minutes
      zipList.foreach(eachZip =>
        // for whatever reason, string coming in shows as literal "Houston", must trim first and last char so api call is clean
        if (eachZip.toString().substring(1, eachZip.toString().length - 1).matches("^\\d{5}(?:[-\\s]\\d{4})?$")) {
          finalString += handleZipCode(eachZip.toString().substring(1, eachZip.toString().length - 1)) + ","
        }
        //futureWeather = cacheSearchService.recentSearch(eachCity.toString())
      )

      finalString = finalString.substring(0, finalString.length - 1)
      finalString += "]"
      val finalJson: JsValue = Json.parse(finalString)

      val maxTemp = (finalJson \\ "dayMax").maxBy(_.toString)
      val minTemp = (finalJson \\ "dayMin").minBy(_.toString)
      val minHumidity = (finalJson \\ "humidity").minBy(_.toString)
      val maxHumidity = (finalJson \\ "humidity").maxBy(_.toString)
      var tempAvg = 0.0

      (finalJson \\ "dayTemp").foreach(u =>
        tempAvg += u.toString().toDouble
      )
      tempAvg = (tempAvg / (finalJson \\ "dayTemp").length)

      if ((finalJson \\ "Alerts").isEmpty) {
        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg  }"""
        Ok(Json.parse(outputString))
      }
      else {
        var alertString = ""
        (finalJson \\ "Alerts").foreach(u =>
          alertString += u.toString().replaceAll("\"", "")
        )

        alertString = '"' + alertString + '"'
        print(alertString)

        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg, "Alerts": $alertString  }"""
        Ok(Json.parse(outputString))
      }
    }
    //handle multiple cities and zip codes
    else {
      var futureWeather: JsValue = null

      var finalString = "["

      //loop to handle each city in list, will check cache for every city
      // in case they have been searched in the past 10 minutes
      zipList.foreach(eachZip =>
        // for whatever reason, string coming in shows as literal "Houston", must trim first and last char so api call is clean
        if (eachZip.toString().substring(1, eachZip.toString().length - 1).matches("^[0-9]{5}$")) {
          finalString += handleZipCode(eachZip.toString().substring(1, eachZip.toString().length - 1)) + ","
        }
      )

      //loop to handle each city in list, will check cache for every city
      // in case they have been searched in the past 10 minutes
      cityList.foreach(eachCity =>
        // for whatever reason, string coming in shows as liter "Houston", must trim first and last char so api call is clean
        if (eachCity.toString().substring(1, eachCity.toString().length - 1).matches("[a-z A-Z]+")) {
          finalString += handleCity(eachCity.toString().substring(1, eachCity.toString().length - 1)) + ","
        }
      )

      finalString = finalString.substring(0, finalString.length - 1)
      finalString += "]"

      //print(finalString)
      val finalJson: JsValue = Json.parse(finalString)

      val maxTemp = (finalJson \\ "dayMax").maxBy(_.toString)
      val minTemp = (finalJson \\ "dayMin").minBy(_.toString)
      val minHumidity = (finalJson \\ "humidity").minBy(_.toString)
      val maxHumidity = (finalJson \\ "humidity").maxBy(_.toString)
      var tempAvg = 0.0

      (finalJson \\ "dayTemp").foreach(u =>
        tempAvg += u.toString().toDouble
      )
      tempAvg = (tempAvg / (finalJson \\ "dayTemp").length)

      if ((finalJson \\ "Alerts").isEmpty) {
        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg  }"""
        Ok(Json.parse(outputString))
      }
      else {
        var alertString = ""
        (finalJson \\ "Alerts").foreach(u =>
          alertString += u.toString().replaceAll("\"", "")
        )

        alertString = '"' + alertString + '"'

        print(alertString)

        val outputString = s"""{ "lowestHumidity": $minHumidity, "highestHumidity": $maxHumidity, "lowestTemp": $minTemp, "highestTemp" : $maxTemp, "averageTemp": $tempAvg, "Alerts": $alertString  }"""
        Ok(Json.parse(outputString))
      }
    }
  }

  //handler function for the loop on multiple zip codes
  //will aggregate the basic weather info for all provided zip codes
  //will also provide average temp across all zip codes
  def handleZipCode(inputZip: String): String = {
    val futureWeather: JsValue = cacheSearchService.recentZipSearch(inputZip)
    //val json = Await.result(futureWeather, 2 seconds)
    val humidity = (futureWeather \ "daily" \ 0 \ "humidity").get.toString().toDouble
    val dayTemp = (futureWeather \ "daily" \ 0 \ "temp" \ "day").get.toString().toDouble
    val dayMin = (futureWeather \ "daily" \ 0 \ "temp" \ "min").get.toString().toDouble
    val dayMax = (futureWeather \ "daily" \ 0 \ "temp" \ "max").get.toString().toDouble

    if ((futureWeather \ "alerts").isEmpty) {
      val myString = s"""{ "humidity": $humidity, "dayTemp" : $dayTemp, "dayMin": $dayMin, "dayMax": $dayMax  }"""
      myString
    }
    else {
      val alertTitle = s"$inputZip Alerts"
      val alerts: String = '"' + inputZip + (futureWeather \ "alerts" \ 0 \ "description").get.toString().substring(1, (futureWeather \ "alerts" \ 0 \ "description").get.toString().length)
      val myString = s"""{ "humidity": $humidity, "dayTemp" : $dayTemp, "dayMin": $dayMin, "dayMax": $dayMax, "Alerts": $alerts  }"""
      myString
    }
  }

  //handler function for the loop on multiple cities
  //will aggregate the basic weather info for all provided cities
  //will also provide average temp across all cities
  def handleCity(inputCity: String): String = {
    if (inputCity.matches("[a-z A-Z]+")) {
      val futureWeather: JsValue = cacheSearchService.recentCitySearch(inputCity)
      //val json = Await.result(futureWeather, 2 seconds)
      val humidity = (futureWeather \ "daily" \ 0 \ "humidity").get.toString().toDouble
      val dayTemp = (futureWeather \ "daily" \ 0 \ "temp" \ "day").get.toString().toDouble
      val dayMin = (futureWeather \ "daily" \ 0 \ "temp" \ "min").get.toString().toDouble
      val dayMax = (futureWeather \ "daily" \ 0 \ "temp" \ "max").get.toString().toDouble

      if ((futureWeather \ "alerts").isEmpty) {
        val myString = s"""{ "humidity": $humidity, "dayTemp" : $dayTemp, "dayMin": $dayMin, "dayMax": $dayMax  }"""
        myString
      }
      else {
        val alerts: String = '"' + inputCity + (futureWeather \ "alerts" \ 0 \ "description").get.toString().substring(1, (futureWeather \ "alerts" \ 0 \ "description").get.toString().length)
        //print((futureWeather \ "alerts" \ 0 \ "description").get.toString())
        val myString = s"""{ "humidity": $humidity, "dayTemp" : $dayTemp, "dayMin": $dayMin, "dayMax": $dayMax, "Alerts": $alerts  }"""

        //println(myString)
        myString
      }
    }
    else {
      ""
    }
  }

  //handle single calls for city or zip code
  def getWeather(inputCity: String)= Action { implicit request: Request[AnyContent] =>

    //handle just the zip code
    if (inputCity.matches("^\\d{5}(?:[-\\s]\\d{4})?$")) {
      val futureWeather: JsValue = cacheSearchService.recentZipSearch(inputCity)
      //val json = Await.result(futureWeather, 2 seconds)
      Ok(futureWeather)
    }
    //handle just the city
    else if (inputCity.matches("[a-z A-Z]+")) {
      val futureWeather: JsValue = cacheSearchService.recentCitySearch(inputCity)
      //val json = Await.result(futureWeather, 2 seconds)
      Ok(futureWeather)
    }
    else {
      BadRequest("Invalid Input")
    }
  }
}

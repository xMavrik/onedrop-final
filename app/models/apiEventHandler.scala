package models

import akka.Done
import play.api.{Configuration, cache}

import javax.inject._
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.language.postfixOps
import play.api.cache._
import play.api.libs.json
import play.api.libs.json.{JsValue, Json}
import play.libs.Json
import play.libs.Json._

import java.util.NoSuchElementException

class apiEventHandler @Inject()(ws: WSClient, configuration: Configuration, cityName: String){

  def getWeatherByCityName(cityName: String): JsValue = {

    val apiPass: String = configuration.get[String]("play.http.secret.key")
    val request: WSRequest = ws.url(s"https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=imperial&appid=$apiPass")
    val futureResult: Future[JsValue] = request.get().map { response =>
      response.json
    }

    val json = Await.result(futureResult, 5 seconds)

    if((json \ "coord" \ "lat").isEmpty) {
      throw new RuntimeException("Invalid City/Zip")
    }

    val lat = (json \ "coord" \ "lat").get
    val long = (json \ "coord" \ "lon").get

    val requestFinal: WSRequest = ws.url(s"https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&exclude=minutely,hourly&units=imperial&appid=$apiPass")
    val futureResultFinal: Future[JsValue] = requestFinal.get().map { response =>
      response.json
    }

    val jsonFinal: JsValue = Await.result(futureResultFinal, 2 seconds)
    jsonFinal
  }


  def getWeatherByZipCode(zipCode: String): JsValue = {

    val apiPass: String = configuration.get[String]("play.http.secret.key")
    val request: WSRequest = ws.url(s"https://api.openweathermap.org/data/2.5/weather?zip=$zipCode&units=imperial&appid=$apiPass")
    val futureResult: Future[JsValue] = request.get().map { response =>
      response.json
    }
    val json = Await.result(futureResult, 5 seconds)

    if((json \ "coord" \ "lat").isEmpty) {
      throw new RuntimeException("Invalid City/Zip")
    }

    val lat = (json \ "coord" \ "lat").get
    val long = (json \ "coord" \ "lon").get
    val requestFinal: WSRequest = ws.url(s"https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&exclude=minutely,hourly&units=imperial&appid=$apiPass")
    val futureResultFinal: Future[JsValue] = requestFinal.get().map { response =>
      response.json
    }

    val jsonFinal = Await.result(futureResultFinal, 2 seconds)

    jsonFinal
  }
}

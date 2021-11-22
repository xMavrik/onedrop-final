package models

import akka.Done
import play.api.{Configuration, cache}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent._
import scala.language.postfixOps
import play.api.cache.AsyncCacheApi
import play.api.libs.json.JsValue

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


class CacheSearchService @Inject()(apiE: apiEventHandler, cache: AsyncCacheApi, configuration: Configuration, implicit val executionContext: ExecutionContext) {

  val cacheTime: Duration = configuration.get[Duration]("weatherCache.expiry")

  def recentCitySearch(cityName: String): JsValue = {

    val futureWeather: Future[Option[JsValue]] = cache.get[JsValue](cityName)

    println(cityName)
    println("-------------------------------")

    val result = Await.result(futureWeather, 3 seconds)

    result match{
      case None =>
        println(s"This is a new search, adding $cityName to cache")
        println()
        val output: JsValue = apiE.getWeatherByCityName(cityName)
        //apiE.getWeatherByCityName(cityName)
        cache.set(cityName, output, cacheTime)
        output
      case _ =>
        println("The API was not hit")
        println()
        result.get
    }
  }

  def recentZipSearch(zipCode: String): JsValue = {

    val futureWeather: Future[Option[JsValue]] = cache.get[JsValue](zipCode)

    println(zipCode)
    println("-------------------------------")

    val result = Await.result(futureWeather, 3 seconds)
    result match{
      case None =>
        println(s"This is a new search, adding $zipCode to cache")
        println()
        val output: JsValue = apiE.getWeatherByZipCode(zipCode)
        if (output == None) {
          cache.set(zipCode, output, cacheTime)
          output
        }
        else {
          cache.set(zipCode, output, cacheTime)
          output
        }
      case _ =>
        println("The API was not hit")
        println()
        result.get
    }
  }
}

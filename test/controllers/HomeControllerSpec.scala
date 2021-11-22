package controllers

import akka.Done
import com.typesafe.config.{Config, ConfigList, ConfigMemorySize, ConfigMergeable, ConfigObject, ConfigOrigin, ConfigResolveOptions, ConfigValue}
import com.typesafe.play.cachecontrol.Seconds.ZERO.seconds
import models.{CacheSearchService, apiEventHandler}
import org.joda.time.DurationFieldType.seconds
import org.joda.time.PeriodType.{millis, seconds}
import play.api.{Configuration, cache}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc.ControllerHelpers.TODO.executionContext
import play.api.test._
import play.api.test.Helpers._

import java.{lang, time, util}
import java.time.{Duration, Period}
import java.time.temporal.TemporalAmount
import java.util.Map
import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future, duration}
import scala.reflect.ClassTag

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {



  val configuration: Configuration = new Configuration(new Config {
    override def root(): ConfigObject = ???

    override def origin(): ConfigOrigin = ???

    override def withFallback(other: ConfigMergeable): Config = ???

    override def resolve(): Config = ???

    override def resolve(options: ConfigResolveOptions): Config = ???

    override def isResolved: Boolean = ???

    override def resolveWith(source: Config): Config = ???

    override def resolveWith(source: Config, options: ConfigResolveOptions): Config = ???

    override def checkValid(reference: Config, restrictToPaths: String*): Unit = ???

    override def hasPath(path: String): Boolean = ???

    override def hasPathOrNull(path: String): Boolean = ???

    override def isEmpty: Boolean = ???

    override def entrySet(): util.Set[Map.Entry[String, ConfigValue]] = ???

    override def getIsNull(path: String): Boolean = false

    override def getBoolean(path: String): Boolean = ???

    override def getNumber(path: String): Number = ???

    override def getInt(path: String): Port = ???

    override def getLong(path: String): Long = ???

    override def getDouble(path: String): Double = ???

    override def getString(path: String): String = path

    override def getEnum[T <: Enum[T]](enumClass: Class[T], path: String): T = ???

    override def getObject(path: String): ConfigObject = ???

    override def getConfig(path: String): Config = ???

    override def getAnyRef(path: String): AnyRef = ???

    override def getValue(path: String): ConfigValue = ???

    override def getBytes(path: String): lang.Long = ???

    override def getMemorySize(path: String): ConfigMemorySize = ???

    override def getMilliseconds(path: String): lang.Long = ???

    override def getNanoseconds(path: String): lang.Long = ???

    override def getDuration(path: String, unit: TimeUnit): Long = ???

    override def getDuration(path: String): time.Duration = time.Duration.ofMillis(1000)

    override def getPeriod(path: String): Period = ???

    override def getTemporal(path: String): TemporalAmount = ???

    override def getList(path: String): ConfigList = ???

    override def getBooleanList(path: String): util.List[lang.Boolean] = ???

    override def getNumberList(path: String): util.List[Number] = ???

    override def getIntList(path: String): util.List[Integer] = ???

    override def getLongList(path: String): util.List[lang.Long] = ???

    override def getDoubleList(path: String): util.List[lang.Double] = ???

    override def getStringList(path: String): util.List[String] = ???

    override def getEnumList[T <: Enum[T]](enumClass: Class[T], path: String): util.List[T] = ???

    override def getObjectList(path: String): util.List[_ <: ConfigObject] = ???

    override def getConfigList(path: String): util.List[_ <: Config] = ???

    override def getAnyRefList(path: String): util.List[_] = ???

    override def getBytesList(path: String): util.List[lang.Long] = ???

    override def getMemorySizeList(path: String): util.List[ConfigMemorySize] = ???

    override def getMillisecondsList(path: String): util.List[lang.Long] = ???

    override def getNanosecondsList(path: String): util.List[lang.Long] = ???

    override def getDurationList(path: String, unit: TimeUnit): util.List[lang.Long] = ???

    override def getDurationList(path: String): util.List[time.Duration] = ???

    override def withOnlyPath(path: String): Config = ???

    override def withoutPath(path: String): Config = ???

    override def atPath(path: String): Config = ???

    override def atKey(key: String): Config = ???

    override def withValue(path: String, value: ConfigValue): Config = ???
  })

  val apiE: apiEventHandler = new apiEventHandler(ws: WSClient, configuration: play.api.Configuration, "Charlotte")

  val cacheSearchService: CacheSearchService = new CacheSearchService(apiE: apiEventHandler, cache: AsyncCacheApi, configuration: Configuration,
  executionContext: ExecutionContext)

  val ws: WSClient = new WSClient {
    override def underlying[T]: T = ???

    override def url(url: String): WSRequest = ???

    override def close(): Unit = ???
  }

  val cache: AsyncCacheApi = new AsyncCacheApi {
     def set(key: String, value: Any, expiration: Duration): Future[Done] = ???

    override def remove(key: String): Future[Done] = ???

     def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: => Future[A])(implicit evidence$1: ClassTag[A]): Future[A] = ???

    override def get[T](key: String)(implicit evidence$2: ClassTag[T]): Future[Option[T]] = ???

    override def removeAll(): Future[Done] = ???

    override def set(key: String, value: Any, expiration: duration.Duration): Future[Done] = ???

    override def getOrElseUpdate[A](key: String, expiration: duration.Duration)(orElse: => Future[A])(implicit evidence$1: ClassTag[A]): Future[A] = ???
  }

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(cacheSearchService: CacheSearchService, ws: WSClient, cache: AsyncCacheApi, stubControllerComponents())
      val home = controller.getWeather("Charlotte")

      //status(home)
      //status(home) mustBe OK
      //contentType(home) mustBe Some("text/html")
      //contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      //val home = controller.index().apply(FakeRequest(GET, "/"))
      //val home = controller.getWeather("Charlotte").apply()

      //status(home) mustBe OK
      //contentType(home) mustBe Some("text/html")
      //contentAsString(home) must include ("Welcome to Play")
    }

    "run the basic test for getting weather on one city" in {
      val request = FakeRequest(GET, "/weather/Charlotte")
      val home = route(app, request).get

      //status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("\"lat\":35.2271,\"lon\":-80.8431,\"timezone\":\"America/New_York\",\"timezone_offset\":-18000,\"current\"")
    }

    "run the basic test for getting weather on one zip" in {
      val request = FakeRequest(GET, "/weather/28105")
      val home = route(app, request).get

      //status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("{\"lat\":35.1149,\"lon\":-80.705,\"timezone\":\"America/New_York\",\"timezone_offset\":-18000,\"current\":")
    }

    "run the basic fail test for getting weather on one zip" in {
      val request = FakeRequest(GET, "/weather/2810")
      val home = route(app, request).get

      //status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include ("Invalid Input")
    }

    "run the basic fail test for getting weather on one city" in {
      val request = FakeRequest(GET, "/weather/Charlot4e")
      val home = route(app, request).get

      //status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include ("Invalid Input")
    }

    "run the basic test for getting weather multiple cities and zip codes" in {

      var json = """ {
                   |	"List": [
                   |		{"zip": "28105"},
                   |		{"zip": "28277"},
                   |		{"city": "Charlotte"}
                   |	]


                   |} """.stripMargin


      println(Json.parse(json))
      println()

      val fakeRequest = FakeRequest(POST, "/weather", headers = FakeHeaders(
        Seq("Content-type"->"application/json")
      ), Json.parse(json))

      //println()
      val home = route(app, fakeRequest).get
      println(contentAsString(home))
      //status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("{\"lowestHumidity\":46,\"highestHumidity\":48,\"lowestTemp\":38.37,\"highestTemp\":61.61,\"averageTemp\":56.29333333333333}")
    }

    "run the basic fail test for getting weather multiple cities and zip codes" in {

      var json = """ {
                   |	"List": [
                   |		{"zip": "28105"},
                   |		{"zip": "28277"},
                   |		{"city": "Charl4tte"}
                   |	]


                   |} """.stripMargin


      println(Json.parse(json))
      println()

      val fakeRequest = FakeRequest(POST, "/weather", headers = FakeHeaders(
        Seq("Content-type"->"application/json")
      ), Json.parse(json))

      val home = route(app, fakeRequest).get
      println(contentAsString(home))
      //status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("{\"lowestHumidity\":47,\"highestHumidity\":48,\"lowestTemp\":38.37,\"highestTemp\":61.61,\"averageTemp\":56.85}")
    }
  }
}

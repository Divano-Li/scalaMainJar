package httpClient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.ActorMaterializer

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait HttpClient {
  implicit val system: ActorSystem = ActorSystem("HttpClientSystem")
  val poolSettings = ConnectionPoolSettings(system).withIdleTimeout(8, TimeUnit.MILLISECONDS)

  def getWithHeader(url: String, headers: Map[String, String], params: Map[String, String]): Future[HttpResponse] = {
    val uriWithParams = if (params.nonEmpty) Uri(url).withQuery(Uri.Query(params)) else Uri(url)
    var request = HttpRequest(
      method = HttpMethods.GET,
      uri = uriWithParams
    ).addHeader(RawHeader("Accept", "application/json"))

    headers.foreach(each => {
      request = request.addHeader(RawHeader(each._1, each._2))
    })

    Http().singleRequest(request, settings = poolSettings)

  }

  def postWithForm(url: String, form: Map[String, String]): Future[HttpResponse] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = url,
      entity = FormData(form).toEntity
    )
    Http().singleRequest(request, settings = poolSettings)
  }

  def postWithJson(url: String, body: String, headers: Map[String, String], params: Map[String, String]): Future[HttpResponse] = {
    val uriWithParams = if (params.nonEmpty) Uri(url).withQuery(Uri.Query(params)) else Uri(url)
    var request = HttpRequest(
      method = HttpMethods.POST,
      uri = uriWithParams,
      entity = HttpEntity(ContentTypes.`application/json`, body)
    ).addHeader(RawHeader("Accept", "application/json"))

    headers.foreach(each => {
      request = request.addHeader(RawHeader(each._1, each._2))
    })

    Http().singleRequest(request, settings = poolSettings)
  }

  def terminate(): Unit = {
    system.terminate()
  }


}

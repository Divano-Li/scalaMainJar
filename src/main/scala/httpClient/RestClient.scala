package httpClient

import akka.http.scaladsl.model.HttpResponse

import java.io.{PrintWriter, StringWriter}
import java.util.UUID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait RestClient extends ClientLog {
  def get(url: String, headers: Map[String, String] = Map.empty, params: Map[String, String] = Map.empty): HttpResponseAction = {
    val tId = preRequest("GET", url, "")
    processHttpResponse(getWithHeader(url, headers, params), tId)
  }

  def postByUrl(url: String, body: String = "", headers: Map[String, String] = Map.empty, params: Map[String, String] = Map.empty): HttpResponseAction = {
    val tId = preRequest("POST", url, body)
    processHttpResponse(postWithJson(url, body, headers, params), tId)
  }

  def postWithForm(url: String, body: String, form: Map[String, String]): HttpResponseAction = {
    val tId = preRequest("POST", url, body)
    processHttpResponse(postWithForm(url, form), tId)
  }

}

//object  aaa extends App with RestClient {
//  val url = "https://portal.xunyou.mobi/api/v7/android/games?only_accel=true"
//  for (i <- 1 to 10) {
//    val resp = get(url)
//    val rsp = JsonMapper.fromGzipEncrypt[ClientGameResponse](resp.body)
//    //println(JsonMapper.to(rsp.gameList))
//    val lushi = rsp.gameList.filter(_.uid == "58fa3aac8956ca39bc837b6c").head
//    if (lushi.serverLocation.isEmpty) {
//      println("lushi.serverLocation.isEmpty")
//      println(JsonMapper.to(rsp.gameList.filter(_.uid == "58fa3aac8956ca39bc837b6c").head.serverLocation))
//      println(resp)
//    } else {
//      println(JsonMapper.to(rsp.gameList.filter(_.uid == "58fa3aac8956ca39bc837b6c").head.serverLocation))
//    }
//    println(i)
//  }
//
//}

trait ClientLog  extends HttpClient {
  def logName: String = "ClientLog"

  private val internalError = 500
  private val ok = 200
  private val notFound = 404
  private val multiple = 300


  def preRequest(method: String, url: String, requestBody: String): String = {
    val tId: String = UUID.randomUUID().toString
    println("%s %s %s %s %s".format("[" + logName + " -- begin]", tId, method, url, requestBody))
    tId
  }

  def processHttpResponse(responseFuture: Future[HttpResponse], tId: String): HttpResponseAction = {
    try {
      val response = Await.result(responseFuture, Duration.create(8000, "ms"))
      var rspBody: String = null
      if (response.entity != null) rspBody = HttpClientUtil.getEntityString(response.entity)
      val rspStatus = response.status.intValue()
      if (rspStatus > multiple) {
        response.headers.foreach(println)
        println("%s %s %d %s".format("[" + logName + " -- end]", tId, rspStatus, rspBody))
      } else {
        //response.headers.foreach(println)
        println("%s %s %d %s".format("[" + logName + " -- end]", tId, rspStatus, ""))
      }
      buildResponse(rspStatus, rspBody)
    } catch {
      case e: Throwable =>
        println(logName + " " + tId + " " + e.getClass + " " + e.getMessage + " " + e.getCause + "\n" + getStackTraceAsString(e))
        new HttpResponseAction(e.getMessage, internalError)
    }
  }

  def buildResponse(statusCode: Int, rspBody: String): HttpResponseAction = {
    statusCode match {
      case x if x < multiple => new HttpResponseAction(rspBody, ok)
      case x if x == notFound => new HttpResponseAction(rspBody, notFound)
      case _ => new HttpResponseAction(rspBody, statusCode)
    }
  }

  protected def getStackTraceAsString(t: Throwable): String = {
    val sw = new StringWriter
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
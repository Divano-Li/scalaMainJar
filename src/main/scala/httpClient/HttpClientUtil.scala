package httpClient

import akka.http.scaladsl.model.ResponseEntity
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Await
import scala.concurrent.duration.{FiniteDuration, _}

object HttpClientUtil extends HttpClient {
  val EntityTimeOut: FiniteDuration = 3.seconds

  def getEntityString(entity: ResponseEntity): String = {
    val entityFuture = Unmarshal(entity).to[String]
    Await.result(entityFuture, EntityTimeOut)
  }
}
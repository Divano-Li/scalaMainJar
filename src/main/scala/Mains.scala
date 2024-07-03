import httpClient.RestClient
import org.slf4j.LoggerFactory

object Mains extends RestClient{
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger("Launcher")
    logger.info("%s %s".format("Launcher:", "portal is starting..."))
    val url = "https://portal.littlesix.mobi/api/v6/littlesix_ios/nodes"
    for (i <- 1 to 1) {
      //val url = "https://test-portal.xunyou.mobi/api/v7/ios/games?only_recommend=true"
      //val resp = get(url, headers = Map("content-type" -> "application/json"))
      //val resp = get(url, headers = Map("Accept-Encoding" -> "gzip"))
      val resp = get(url)
      if (resp.statusCode == 405) {
        println(s"请求失败: ${resp.statusCode}")
        sys.exit(1)
      } else {
        println("continue" + i)
      }
    }


  }
}

/*
 * Copyright (C) 2020-2023 Lightbend Inc. <https://www.lightbend.com>
 */


//import actor.PingClass
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Route.seal
//import akka.pattern.ask
//
//import scala.util.Random
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.io.StdIn
//import scala.concurrent.{Await, Awaitable}
//import scala.concurrent.duration._
//import akka.util.Timeout
//import SingleExecutionContext1.SingleEC
//object HttpServerRoutingMinimal {
//
//  def main(args: Array[String]): Unit = {
//
//    implicit val system = ActorSystem( "main-system", defaultExecutionContext = Some(SingleEC))
//    // needed for the future flatMap/onComplete in the end
//
//    val route =
//      path("hello") {
//        get {
//          val i =  Random.nextInt(4)
//          ResourceActor.pingActor(i) ! PingClass(i)
//          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http pong </h1>" + i))
//        }
//      }
//
//    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
//
//    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
//    StdIn.readLine() // let it run until user presses return
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done
//  }
//}
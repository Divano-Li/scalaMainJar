import actor.PingActor
import akka.actor.{ActorRef, ActorSystem, Props}
import SingleExecutionContext.SingleEC
import HttpClientExecutionContext1.HttpClientEC
import scala.collection.mutable

object ResourceActor {
  val system: ActorSystem = ActorSystem("ResourceActorSystem", defaultExecutionContext = Some(HttpClientEC))
  var pingActor: mutable.Map[Int, ActorRef] = mutable.Map()
  for {i <- 0 until 4} {
    pingActor(i) = system.actorOf(Props(new PingActor()), name = "pingActor" + i.toString)
  }
}

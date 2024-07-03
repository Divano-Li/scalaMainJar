package actor

import akka.actor.Actor

object PingActor {

}

case class PingClass(flag: Int)

class PingActor extends Actor {
  override def receive: Receive = {
    case PingClass(flag) =>
      println("Pong" + flag)
      "Pong" + flag
    case _ =>
  }
}

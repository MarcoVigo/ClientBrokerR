package ClientBroker

import akka.actor.{ActorSelection}

case object ERROR1
case object ERROR
case object ExitT
case object ExitP
case class Topic(i:Int)
case object SottoscrizioneT
case object SottoscrizioneP
case class DatoT(i: Int)
case class DatoP(i: Int)
case class START(other: ActorSelection)

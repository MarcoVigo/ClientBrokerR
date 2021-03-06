package ClientBroker

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Broker extends Actor{
  private val Topic_Temperatura= collection.mutable.Buffer[ActorRef]()
  private val Topic_Pressione= collection.mutable.Buffer[ActorRef]()

  def receive: Receive ={
    case Topic(i) =>
      println("SOTTOSCRIZIONE AL TOPIC RICEVUTA DA  "+sender() )
      if(i==1 && !Topic_Temperatura.contains(sender())) {
        Topic_Temperatura += sender()
        sender() ! SottoscrizioneT
      }
      else if(i==2 && !Topic_Pressione.contains(sender())){
        Topic_Pressione+=sender()
        sender() ! SottoscrizioneP
      }
      else if(i==3){
        if(!Topic_Temperatura.contains(sender()))Topic_Temperatura+=sender()
        sender() ! SottoscrizioneT
        if(!Topic_Pressione.contains(sender()))Topic_Pressione+=sender()
        sender() ! SottoscrizioneP
      }
      else sender() ! ERROR

    case DatoT(i) =>
      Topic_Temperatura.foreach(_ ! DatoT(i))

    case DatoP(i) =>
      Topic_Pressione.foreach(_ ! DatoP(i))

    case ExitT =>
      if(Topic_Temperatura.contains(sender())) Topic_Temperatura-=sender()
      else sender() ! ERROR1

    case ExitP =>
      if(Topic_Pressione.contains(sender())) Topic_Pressione-=sender()
      else sender() ! ERROR1

}
}


object Broker{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val server = ActorSystem("server", config)
    server.actorOf(Props[Broker], "Broker")
    println("Broker Connesso")

  }


  val conf =
    """
      |akka {
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
      |  }
      |
      |  remote {
      |    enabled-transports = ["akka.remote.netty.tcp"]
      |    netty.tcp {
      |      hostname = "127.0.0.1"
      |      port = 2552
      |    }
      |  }
      |}
    """.stripMargin
}


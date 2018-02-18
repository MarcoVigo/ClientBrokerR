package ClientBroker

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Client extends Actor{
  val R = org.ddahl.rscala.RClient()
  val S = org.ddahl.rscala.RClient()
  var countT=0
  var countP=0

  def receive: Receive = {
    case START(other) =>
      Future {
        while (true) {
          println("1) Sottoscrizione Temperatura")
          println("2) Sottoscrizione Pressione ")
          println("3) Sottoscrizione a Entrambi ")
          println("4) Lascia Topic Temperatura")
          println("5) Lascia Topic Pressione")
          val y = scala.io.StdIn.readInt()
          if (y == 1 || y == 2 || y == 3)
            other ! Topic(y)
          if (y == 4) other ! ExitT
          if (y == 5) other ! ExitP
          else
            other ! ERROR
          Thread.sleep(2500)
        }
      }
    case ERROR =>
      println("ERRORE TOPIC INESISTENTE")
    case ERROR1 =>
      println("ERRORE NON SEI ISCRITTO AL TOPIC SELEZIONATO")
    case SottoscrizioneT =>
      R eval
        """
         Tempo=c()
         Temperatura=c()
         ContatoreT=0
         ValoreT=0

      """
    case SottoscrizioneP =>
      S eval
        """
         Tempo=c()
         Pressione=c()
         ContatoreP=0
         ValoreP=0
      """
    case DatoT(i) =>
      R.ContatoreT=countT
      R.ValoreT=i
      countT+=1
      R eval
        """
          Tempo= append(Tempo,ContatoreT)
          Temperatura= append(Temperatura,ValoreT)
          plot(Tempo,Temperatura, type= "b", col = "red", pch= 20)

      """
    case DatoP(i) =>
      S.ContatoreP=countP
      S.ValoreP=i
      countP+=1
      S eval
        """
          Tempo= append(Tempo,ContatoreP)
          Pressione= append(Pressione,ValoreP)
          plot(Tempo,Pressione, type="b", col = "blue", pch= 20)

      """



  }

}

object Client{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("RemoteClient", config)
    val actor  = client.actorOf(Props[Client], "Client")

    val path =  "akka.tcp://server@127.0.0.1:2552/user/Broker"
    val Broker = client.actorSelection(path)


    actor ! START(Broker)
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
      |      hostname = "0.0.0.0"
      |      port = 0
      |    }
      |  }
      |}
    """.stripMargin
}

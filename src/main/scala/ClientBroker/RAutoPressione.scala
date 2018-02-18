package ClientBroker

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RAutoPressione extends Actor {
  val R = org.ddahl.rscala.RClient()

  def receive: Receive={
    case START(other) =>
      while(true){

          R eval
            """
          x <- sample(0:1500,1)
          """
          var Pressione=R.x._1.toString.toInt
          println(Pressione)
          other ! DatoP(Pressione)
          Thread.sleep(1000)
      }

  }

}

object RAutoPressione{
  def main(args: Array[String]): Unit = {
    
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("AutoPress", config)
    val actor = client.actorOf(Props[RAutoPressione], "AutoValGenPress")

    val path = "akka.tcp://server@127.0.0.1:2552/user/Broker"
    val Broker = client.actorSelection(path)


    println("Generatore automatico di valori, premere un tasto per l'avvio")

    scala.io.StdIn.readLine()

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

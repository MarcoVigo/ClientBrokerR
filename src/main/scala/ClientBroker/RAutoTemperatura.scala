package ClientBroker

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class RAutoTemperatura extends Actor {
  val R = org.ddahl.rscala.RClient()

  def receive: Receive = {
    case START(other) =>
      while (true) {

        R eval
          """
        x <- sample(-3000:3000,1)

        """
        var Temperatura = R.x._1.toString.toInt
        println(Temperatura)
        other ! DatoT(Temperatura)
        Thread.sleep(1000)

      }
  }
}

  object RAutoTemperatura {
    def main(args: Array[String]): Unit = {

      val config = ConfigFactory.parseString(conf)
      val client = ActorSystem("AutoTemp", config)
      val actor = client.actorOf(Props[RAutoTemperatura], "AutoValGenTemp")

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



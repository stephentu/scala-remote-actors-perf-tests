package perftest
package akka

import java.util.concurrent.CountDownLatch

import se.scalablesolutions.akka.actor.{Actor, ActorRef}
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote._
import se.scalablesolutions.akka.util.Logging

import Utils._

case object STOP_RUNNING
case class START_RUNNING(runId: Int)

class EchoActor extends Actor {
  def receive = {
    case m: TestMessage => 
      //println("Got TestMessage: " + m)
      self.reply(m)
  }
}

class TestExecutor(settings: TestSetting) extends Logging {
  def execute() {


    val server = new RemoteServer
    server.start("localhost", settings.serverport)
    log.info("Remote node started")
    server.register("server", actorOf[EchoActor])
    log.info("Remote actor registered and started")

    val remoteLog = net.lag.logging.Logger.get(classOf[RemoteServer].getName)
    remoteLog.setLevel(java.util.logging.Level.WARNING)

    val a = actorOf(new ClientActor).start
    (1 to settings.numruns).foreach(runNum => {
      a ! START_RUNNING(runNum)
      Thread.sleep(settings.runtime)
      a ! STOP_RUNNING
    })
    println("Execute is done")
  }
  class ClientActor extends Actor {
    val nodes = settings.nodes.toArray
    val random = new scala.util.Random

    def nextNode() = {
      val idx = random.nextInt(nodes.length)
      nodes(idx)
    }

    private val serverMap = new scala.collection.mutable.HashMap[String, ActorRef]

    def sendNextMessage() {
      val nextN = nextNode()
      val server = serverMap.getOrElseUpdate(nextN, { 
          println("Made new actor ref")
          RemoteClient.actorFor("server", nextN, settings.serverport)})
      //println("server ! msg")
      server ! TestMessage(runId, System.currentTimeMillis, settings.payload)
      msgsSent += 1
    }

    private var msgsSent = 0
    private var msgsRecv = 0
    private var startTime: Long = _
    private var runId: Int = _

    def receive = {
      case m @ TestMessage(id, _, _) if (id == runId) =>
        //println("Received response to test Message: " + m)
        msgsRecv += 1
        sendNextMessage()
      case TestMessage(_, _, _) =>
        // dropped message from prev round
      case START_RUNNING(id) =>
        println("Starting run " + id)
        runId = id
        startTime = System.currentTimeMillis
        (1 to settings.windowsize).foreach(i => sendNextMessage())
      case STOP_RUNNING =>
        reportResult(TestResult(settings.testName, runId, startTime, System.currentTimeMillis, msgsSent, msgsRecv))
        msgsSent = 0
        msgsRecv = 0
    }
  }
}

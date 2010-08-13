package perftest
package akka

import java.util.concurrent.CountDownLatch

import se.scalablesolutions.akka.actor.{Actor, ActorRef}
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote._
import se.scalablesolutions.akka.util.Logging

import Utils._

case object STOP_RUNNING
case object START_RUNNING

class EchoActor extends Actor {
  def receive = {
    case m: TestMessage => 
      println("Got TestMessage: " + m)
      self.reply(m)
  }
}

class TestExecutor(settings: TestSetting) extends Logging {
  def execute() {
    val server = new RemoteServer
    //server.start("localhost", settings.serverport)
    server.start("localhost", 9999)
    log.info("Remote node started")
    server.register("server", actorOf[EchoActor])
    log.info("Remote actor registered and started")
    (1 to settings.numruns).foreach(runNum => {
      val latch = new CountDownLatch(1)
      val a = actorOf(new ClientActor(runNum, latch)).start
      a ! START_RUNNING
      Thread.sleep(settings.runtime)
      a ! STOP_RUNNING
      latch.await()
    })
  }
  class ClientActor(runId: Int, latch: CountDownLatch) extends Actor {
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
          RemoteClient.actorFor("server", nextN, 9999 /*settings.serverport */)})
      println("server ! msg")
      server ! TestMessage(runId, System.currentTimeMillis, settings.payload)
      msgsSent += 1
    }

    private var msgsSent = 0
    private var msgsRecv = 0
    private var startTime: Long = _

    def receive = {
      case m @ TestMessage(id, _, _) if (id == runId) =>
        println("Received response to test Message: " + m)
        msgsRecv += 1
        sendNextMessage()
      case START_RUNNING =>
        println("Starting run " + runId)
        startTime = System.currentTimeMillis
        val windowSize = settings.windowsize
        (1 to windowSize).foreach(i => sendNextMessage())
      case STOP_RUNNING =>
        reportResult(TestResult(settings.testName, runId, startTime, System.currentTimeMillis, msgsSent, msgsRecv))
        latch.countDown()
        exit()
    }
  }
}

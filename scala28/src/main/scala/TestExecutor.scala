package perftest
package stock

import scala.actors._
import Actor._
import remote._
import RemoteActor._

import perftest.common._
import Utils._

import java.util.concurrent._

case object STOP_RUNNING

class TestExecutor(settings: TestSetting) {
  def execute() {
    actor {
      alive(settings.serverport)
      register('server, self)
      loop {
        react {
          case e => sender ! e
        }
      }
    }
    Thread.sleep(settings.delaytime)
    (1 to settings.numruns).foreach(runNum => {
      val latch = new CountDownLatch(1)
      val a = new ClientActor(runNum, latch)
      a.start()
      Thread.sleep(settings.runtime)
      a ! STOP_RUNNING
      latch.await()
    })
  }
  class ClientActor(runId: Int, latch: CountDownLatch) extends Actor {
    val nodes = settings.nodes.map(host => Node(host, settings.serverport)).toArray
    val random = new scala.util.Random
    def nextNode() = {
      val idx = random.nextInt(nodes.length)
      nodes(idx)
    }
    def sendNextMessage() {
      val nextN = nextNode()
      val server = select(nextN, 'server) 
      server ! TestMessage(runId, System.currentTimeMillis, settings.payload)
      msgsSent += 1
    }
    var msgsSent = 0
    var msgsRecv = 0

    override def act() {
      println("Starting run " + runId)
      val startTime = System.currentTimeMillis
      val windowSize = settings.windowsize
      (1 to windowSize).foreach(i => sendNextMessage())
      loop {
        react {
          case TestMessage(id, _, _) if (id == runId) =>
            msgsRecv += 1
            sendNextMessage()
          case STOP_RUNNING =>
            reportResult(TestResult(settings.testName, runId, startTime, System.currentTimeMillis, msgsSent, msgsRecv))
            latch.countDown()
            exit()
        }
      }
    }
  }
}



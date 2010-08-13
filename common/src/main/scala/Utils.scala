package perftest
package common

import java.net.InetAddress

import scala.actors.Debug

object Utils {
  def containsOpt(args: Array[String], opt: String) = args.filter(_.startsWith(opt)).size > 0
  def parseOptList(args: Array[String], opt: String): List[String] = 
    parseOptString(args, opt).split(",").toList
  def parseOptListDefault(args: Array[String], opt: String, default: List[String]): List[String] = 
    if (containsOpt(args, opt))
      parseOptList(args, opt)
    else
      default
  def parseOptString(args: Array[String], opt: String) = args.filter(_.startsWith(opt)).head.substring(opt.size)
  def parseOptStringDefault(args: Array[String], opt: String, default: String) = 
    if (containsOpt(args, opt))
      parseOptString(args, opt)
    else
      default
  def parseOptInt(args: Array[String], opt: String) = parseOptString(args, opt).toInt
  def parseOptIntDefault(args: Array[String], opt: String, default: Int) =
    if (containsOpt(args, opt))
      parseOptInt(args, opt)
    else
      default
 
  def makeSettings(args: Array[String], testName: String): TestSetting = {
    val level = parseOptIntDefault(args, "--debug=", 1) 
    scala.actors.Debug.level = level
    val windowSize = parseOptIntDefault(args, "--windowsize=", 1024)
    val runTime = parseOptIntDefault(args,"--runtime=", 5) // 5 minutes per run instance
    val numRuns = parseOptIntDefault(args,"--numruns=", 5)
    val nodes = parseOptList(args, "--nodes=")
    val delayTime = parseOptIntDefault(args, "--delaytime=", 10000) // wait 10 seconds before starting
    val payloadSize = parseOptIntDefault(args, "--payloadsize=", 1024) // 1024 bytes

    // sanity checks
    require(windowSize >= 0)
    require(runTime >= 0)
    require(numRuns >= 0)
    require(delayTime >= 0)
    require(payloadSize >= 0)

    val LocalHostName = InetAddress.getLocalHost.getCanonicalHostName

    println("---------------------------------------------------------------------")
    println("Starting new performance test: " + testName)
    println("---------------------------------------------------------------------")
    println("Localhost name: " + LocalHostName)
    println("RunTime      = " + runTime)
    println("NumRuns      = " + numRuns)
    println("WindowSize   = " + windowSize)
    println("Nodes to RR  = " + nodes)
    println("Payload size = " + payloadSize)
    println("---------------------------------------------------------------------")

    TestSetting(testName,
                windowSize, 
                runTime * 60 * 1000,
                numRuns,
                nodes,
                delayTime,
                16780,
                (1 to payloadSize).map(_.toByte).toArray,
                LocalHostName)

  }

  def reportResult(result: TestResult) {
    println("Result for test: " + result.testName)
    println("Run " + result.runId + " completed")
    val elasped = result.endTime - result.startTime
    println("Elasped time (ms): " + elasped)
    println("Num msgs recv: " + result.msgsRecv)
    println("Num msgs sent: " + result.msgsSent)
  }
}

case class TestResult(testName: String,
                      runId: Int,
                      startTime: Long,
                      endTime: Long,
                      msgsSent: Int,
                      msgsRecv: Int)

case class TestSetting(testName: String,
                       windowsize: Int,
                       runtime: Long, /* in ms */
                       numruns: Int,
                       nodes: List[String],
                       delaytime: Long, /* in ms */
                       serverport: Int,
                       payload: Array[Byte],
                       localhost: String)

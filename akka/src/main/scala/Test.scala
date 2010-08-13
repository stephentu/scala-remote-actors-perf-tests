package perftest
package akka

import Utils._

object Test {
  def main(args: Array[String]) {
    val testName = "Akka Test"
    val settings = makeSettings(args, testName)
    (new TestExecutor(settings)).execute()
  }
}

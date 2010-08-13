package perftest
package stock

import perftest.common._
import Utils._

object Test {
  def main(args: Array[String]) {
    val testName = "Stock Scala 2.8.0 Test"
    val settings = makeSettings(args, testName)
    (new TestExecutor(settings)).execute()
  }
}

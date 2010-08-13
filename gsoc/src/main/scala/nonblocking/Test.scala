package perftest.gsoc
package nonblocking

import perftest.gsoc._

import perftest.common._
import Utils._

import scala.actors.remote._

object Test {
  def main(args: Array[String]) {
    val isVarInt = containsOpt(args, "--enhanced")
    val name = 
      if (isVarInt)
        "NonBlocking GSoC Test (VarInt Java Serializer)"
      else
        "NonBlocking GSoC Test"
    val settings = makeSettings(args, name)
    implicit val config = 
      new DefaultNonBlockingConfiguration {
        override def newSerializer() = {
          if (isVarInt)
            new remote_actors.javaserializer.EnhancedJavaSerializer
          else
            new JavaSerializer
        }
      }
    (new TestExecutor(settings)).execute() 
  }
}

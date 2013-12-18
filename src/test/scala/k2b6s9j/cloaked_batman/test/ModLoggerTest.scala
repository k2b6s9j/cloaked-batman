package k2b6s9j.cloaked_batman.test

import k2b6s9j.cloaked_batman.util.log.ModLogger

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ModLoggerTest extends FlatSpec {
  
   var msg: String = "Test Log Message"
  
  "An information log message" should "be logged if given an message" in {
    ModLogger.info(msg) 
  }
  
  "A warning message" should "be logged if given a message" in {
    ModLogger.warning(msg)
  }
  
  "A severe message" should "be logged if given a message" in {
    ModLogger.severe(msg)
  }
  
  "The logger" should "return itself if getLogger is called" in {
    ModLogger.getLogger shouldBe a ModLogger
  }
  
}

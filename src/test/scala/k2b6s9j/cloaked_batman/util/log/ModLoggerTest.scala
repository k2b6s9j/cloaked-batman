package k2b6s9j.cloaked_batman.util.log

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.util.logging.Logger

@RunWith(classOf[JUnitRunner])
class ModLoggerTest extends FlatSpec with Matchers with BeforeAndAfter {
  
   var msg: String = "Test Log Message"
   //var logger =  new ModLogger() 
  
  "An information log message" should "be logged if given an message" in {
	//ModLogger.info(msg) 
  }
  
  "A warning message" should "be logged if given a message" in {
    //ModLogger.warning(msg)
  }
  
  "A severe message" should "be logged if given a message" in {
    //ModLogger.severe(msg)
  }
  
  "The logger" should "return itself if getLogger is called" in {
    //logger.getLogger shouldBe ModLogger
  }
  
}
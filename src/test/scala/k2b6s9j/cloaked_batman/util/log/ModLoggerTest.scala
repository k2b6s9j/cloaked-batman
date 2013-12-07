package k2b6s9j.cloaked_batman.util.log

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ModLoggerTest extends FlatSpec with Matchers {
  
  var msg: String = "Test Log Message"
  
  "A log message" should "log an information message if given an information message" in {
     
  }
  
  it should "return a NullPointerException if no information message is given" in {
    
  } 
  
  it should "log a warning message if given a warning message" in {
    
  }
  
  it should "return a NullPointerException if no warning message is given" in {
    
  }
  
  it should "log a severe message if given a severe message" in {
    
  }
  
  it should "return a NullPointerException if no severe message is given" in {
    
  }
  
  "The logger" should "return itself if getLogger is called" in {
    
  }
  
}
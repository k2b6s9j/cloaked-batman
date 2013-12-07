package k2b6s9j.cloaked_batman.util.log

import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
object ModLoggerTest {
  var msg: String = "Test Message"
  
  def testInfo() {
    Assert.assertNotNull(ModLogger.info(msg))
  }
  
  def testWarning() {
    Assert.assertNotNull(ModLogger.warning(msg))
  }
  
  def testSevere() {
    Assert.assertNotNull(ModLogger.severe(msg))
  }
  
  def testGetLogger() {
    
  }
}
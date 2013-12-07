package k2b6s9j.cloaked_batman.util.log

import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
object ModLoggerTest {
  var msg: String = "Test Message"
  
  @Test
  def testInfo() {
    Assert.assertNotNull(ModLogger.info(msg))
  }
  
  @Test
  def testWarning() {
    Assert.assertNotNull(ModLogger.warning(msg))
  }
  
  @Test
  def testSevere() {
    Assert.assertNotNull(ModLogger.severe(msg))
  }
  
  @Test
  def testGetLogger() {
    
  }
}
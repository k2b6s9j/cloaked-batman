package k2b6s9j.cloaked_batman.util.log

import org.junit.Test
import java.util.logging.Logger
import cpw.mods.fml.common.FMLLog
import org.junit.Assert
import java.util.logging.Level

class ModLoggerTest {
  def log: Logger = Logger.getLogger("cloaked batman")
  var msg: String = "Test Message"
  
  @Test
  def testInfo() {
    Assert.assertNotNull("Logging information messages works", log.log(Level.INFO, msg))
  }
  
  @Test
  def testWarning() {
    Assert.assertNotNull("Logging warning messages works", log.log(Level.WARNING, msg))
  }
  
  @Test
  def testSevere() {
    Assert.assertNotNull("Logging severe messages works", log.log(Level.SEVERE, msg))
  }
  
  @Test
  def testGetLogger() {
    
  }

}
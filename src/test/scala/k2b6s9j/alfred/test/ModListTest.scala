package k2b6s9j.alfred.test

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import cpw.mods.fml.common.Loader

@RunWith(classOf[JUnitRunner])
class ModListTest extends FlatSpec with Matchers {
  
  "The active mod list" should "be returned." in {
    //val modList = Loader.instance().getActiveModList
    //modList shouldBe a [List[_]]
  }

}
import ColorConsoleContext.Companion.colorConsole
import Colors
import TestFile
import TestUtils.Companion.computeBasePath
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import urlshortenservice.ShortenUrlService
import java.awt.datatransfer.DataFlavor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** This is a simple test just for demonstration purposes. */
class SimpleTest: BasePlatformTestCase() {

  @Test
  fun testA() {
    colorConsole {
      printDebugHeader()
      printLine {
        span(Colors.Red, "testA() running")
        assertThat("AssertJ").isEqualTo("AssertJ")
      }
    }
  }

}

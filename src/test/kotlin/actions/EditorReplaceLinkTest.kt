package actions

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

val mockShortenUrlService = object : ShortenUrlService {
  override fun shorten(longUrl: String) = "http://shorturl.com"
}

class EditorReplaceLinkTest : BasePlatformTestCase() {

  @Before
  public override fun setUp() {
    super.setUp()
    assertThat(testDataPath).isNotNull()
  }

  override fun getTestDataPath(): String = computeBasePath

  @Test
  fun testEditorReplaceLink() {
    colorConsole {
      printDebugHeader()
    }
    myFixture.configureByFile(TestFile.Input(getTestName(false)))

    val action = EditorReplaceLink(mockShortenUrlService)
    val presentation = myFixture.testAction(action)
    assertThat(presentation.isEnabledAndVisible).isTrue()

    val textInClipboard = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor)
    assertThat(textInClipboard).isSameAs(mockShortenUrlService.shorten())

    myFixture.checkResultByFile(TestFile.Output(getTestName(false)))
  }

  @Test
  fun testUnderlyingFunctionUsedByTestEditorReplaceLinkAction() {
    colorConsole {
      printDebugHeader()
    }

    val psiFile = myFixture.configureByFile(TestFile.Input(getTestName(false)))
    val project = myFixture.project
    val editor = myFixture.editor

    val action = EditorReplaceLink(mockShortenUrlService)
    val result = action.doWorkInBackground(editor, psiFile, project)
    assertThat(result).isTrue()

    val presentation = myFixture.testAction(action)
    assertThat(presentation.isEnabledAndVisible).isTrue()

    myFixture.checkResultByFile(TestFile.Output(getTestName(false)))
  }

  /**
   * [Further reading on Future and Executor](https://www.callicoder.com/java-callable-and-future-tutorial/)
   */
  @Test
  fun testTheActionByConnectingWithTinyUrlServiceLive() {
    colorConsole {
      printDebugHeader()
    }

    myFixture.configureByFile(TestFile.Input(getTestName(false)))

    val action = EditorReplaceLink()

    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit {
      while (true) {
        colorConsole {
          printLine {
            span(Colors.Blue, "executor: isRunning: ${action.isRunning()}, isCancelled: ${action.isCanceled()}")
          }
        }
        if (action.isRunning() == EditorReplaceLink.RunningState.NOT_STARTED) {
          shortSleep()
          continue
        }
        if (action.isRunning() == EditorReplaceLink.RunningState.IS_CANCELLED || action.isRunning() == EditorReplaceLink.RunningState.HAS_STOPPED) {
          executor.shutdown()
          break
        }
        else shortSleep()
      }
    }

    val presentation = myFixture.testAction(action)
    assertThat(presentation.isEnabledAndVisible).isTrue()

    val textInClipboard = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor)
    assertThat(textInClipboard).isEqualTo("https://tinyurl.com/mbq3m")

    myFixture.checkResultByFile(TestFile.Output(getTestName(false)))

    colorConsole {
      printLine {
        span(Colors.Blue, "executor: future.isDone: ${future.isDone}")
      }
    }
    executor.awaitTermination(30, TimeUnit.SECONDS)
    colorConsole {
      printLine {
        span(Colors.Blue, "executor: future.isDone: ${future.isDone}")
      }
    }
    executor.shutdown()
  }

  private fun sleep(durationMs: Long = 100) {
    val formattedDuration = "%.3f sec".format(durationMs / 1000f)

    colorConsole {
      printWhichThread()
      printLine {
        span(Colors.Yellow, " sleeping for $formattedDuration 😴")
      }
    }

    Thread.sleep(durationMs)

    colorConsole {
      printWhichThread()
      printLine {
        span(Colors.Purple, " awake 😳")
      }
    }
  }

  private fun longSleep() {
    sleep(100 * 20)
  }

  private fun shortSleep() {
    sleep(20)
  }
}


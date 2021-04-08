import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.*
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationManager.getApplication

const val GROUP_DISPAY_ID = "Shorty.Group"

/**
 * Generate a [Notification] in IDEA.
 * @param first title of the notification
 * @param second content of the notification
 */
fun notify(first: String, second: String) = com.intellij.notification
  .Notifications.Bus
  .notify(
    Notification(
      GROUP_DISPAY_ID,
      first,
      second,
      NotificationType.INFORMATION,
      NotificationListener.URL_OPENING_LISTENER
    )
  )

fun isPluginInTestIDE(): Boolean = System.getProperty("idea.is.internal")?.toBoolean() ?: false

fun isPluginInUnitTestMode(): Boolean = ApplicationManager.getApplication().isUnitTestMode

fun printWhichThread() {
  colorConsole {
    printLine {
      span(whichThread())
    }
  }
}

/**
 * [ApplicationManager.getApplication#isDispatchThread] is equivalent to calling [java.awt.EventQueue.isDispatchThread].
 */
private fun whichThread() = buildString {
  append(
    when {
      getApplication().isDispatchThread -> Red("Running on EDT")
      else -> Green("Running on BGT")
    }
  )
  append(
    " - ${Thread.currentThread().name.take(50)}..."
  )
}

fun printDebugHeader() {
  val stackTrace = Thread.currentThread().stackTrace[2]
  colorConsole {
    printLine {
      span(Purple, "${stackTrace.className}.${stackTrace.methodName}()")
    }
  }
}

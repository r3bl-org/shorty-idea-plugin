package ui

import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.Blue
import color_console_log.Colors.Green
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel

/**
 * This application level configurable shows up the in IDE Preferences UI.
 */
class SettingsUIConfigurable : BoundConfigurable("Shorty - URL Shortener") {
  override fun apply() {
    colorConsole {
      printLine {
        span(Green, "KotlinDSLUISampleConfigurable apply() called")
      }
    }
    super.apply()
  }

  override fun cancel() {
    colorConsole {
      printLine {
        span(Green, "KotlinDSLUISampleConfigurable cancel() called")
      }
    }
    super.cancel()
  }

  /** When the form is changed by the user, this returns `true` and enables the "Apply" button. */
  override fun isModified(): Boolean {
    colorConsole {
      printLine {
        span(Green, "KotlinDSLUISampleConfigurable isModified() called")
        span(Blue, "return ${super.isModified()}")
      }
    }
    return super.isModified()
  }

  override fun reset() {
    colorConsole {
      printLine {
        span(Green, "KotlinDSLUISampleConfigurable reset() called")
      }
    }
    super.reset()
  }

  override fun createPanel(): DialogPanel = createDialogPanel()
}
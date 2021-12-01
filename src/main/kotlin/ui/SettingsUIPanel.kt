package ui

import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.*
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun createDialogPanel(): DialogPanel {
  return panel {
    row {
      cell {
        checkBox("", SettingsUIData.instance.myState::myFlag)
        label("Convert all URLs to short links when Markdown files are saved")
      }
    }
    noteRow("""This plugin is <a href="https://github.com/r3bl-org/shorty-idea-plugin">open source️</a> 💙️""") {
      colorConsole {
        printLine {
          span(Purple, "link url: '$it' clicked")
        }
      }
      BrowserUtil.browse(it)
    }
  }
}

@Service
@State(name = "SettingsUIData", storages = [Storage("settingsUIData.xml")])
class SettingsUIData : PersistentStateComponent<SettingsUIData.State> {
  companion object {
    val instance: SettingsUIData
      get() = ApplicationManager.getApplication().getService(SettingsUIData::class.java)
  }

  var myState = State()

  // PersistentStateComponent methods.
  override fun getState(): State {
    colorConsole {
      printLine {
        span(Purple, "SettingsUIData.getState(): $myState")
      }
    }
    return myState
  }

  override fun loadState(stateLoadedFromPersistence: State) {
    colorConsole {
      printLine {
        span(Purple, "SettingsUIData.loadState(): $stateLoadedFromPersistence")
      }
    }
    myState = stateLoadedFromPersistence
  }

  // Properties in this class are bound to the Kotlin DSL UI.
  class State {
    var myFlag: Boolean by object : LoggingProperty<State, Boolean>(false) {}

    override fun toString(): String =
      "State{ myFlag: '$myFlag' }"

    /** Factory class to generate synthetic properties, that log every access and mutation to each property. */
    open class LoggingProperty<R, T>(initValue: T) : ReadWriteProperty<R, T> {
      var backingField: T = initValue

      override fun getValue(thisRef: R, property: KProperty<*>): T {
        colorConsole {
          printLine {
            span(Blue, "State::${property.name}.getValue()")
            span(Green, "value: '$backingField'")
          }
        }
        return backingField
      }

      override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        backingField = value
        colorConsole {
          printLine {
            span(Blue, "State::${property.name}.setValue()")
            span(Green, "value: '$backingField'")
          }
        }
      }
    }
  }

}
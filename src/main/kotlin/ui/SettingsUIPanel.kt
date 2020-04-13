package ui

import ColorConsoleContext.Companion.colorConsole
import Colors
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.components.*
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
          span(Colors.Purple, "link url: '$it' clicked")
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
      get() = ServiceManager.getService(SettingsUIData::class.java)
  }

  var myState = State()

  // PersistentStateComponent methods.
  override fun getState(): State {
    colorConsole {
      printLine {
        span(Colors.Purple, "SettingsUIData.getState(): $myState")
      }
    }
    return myState
  }

  override fun loadState(stateLoadedFromPersistence: State) {
    colorConsole {
      printLine {
        span(Colors.Purple, "SettingsUIData.loadState(): $stateLoadedFromPersistence")
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
            span(Colors.Blue, "State::${property.name}.getValue()")
            span(Colors.Green, "value: '$backingField'")
          }
        }
        return backingField
      }

      override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        backingField = value
        colorConsole {
          printLine {
            span(Colors.Blue, "State::${property.name}.setValue()")
            span(Colors.Green, "value: '$backingField'")
          }
        }
      }
    }
  }

}
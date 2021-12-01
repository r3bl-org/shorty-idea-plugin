package vfs

import color_console_log.ColorConsoleContext.Companion.colorConsole
import color_console_log.Colors.*
import com.intellij.AppTopics
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.intellij.plugins.markdown.lang.MarkdownTokenTypeSets
import printDebugHeader
import printWhichThread
import psi.*
import ui.SettingsUIData
import urlshortenservice.ShortenUrlService
import urlshortenservice.TinyUrl

class VFSListenerStartupActivity : StartupActivity {
  /**
   * This runs every time a project is opened in IDEA. [More info](https://tinyurl.com/ufd64mk).
   */
  override fun runActivity(project: Project) {
    colorConsole {
      printLine {
        span(Purple, "postStartupActivity running")
      }
    }
    VFSListenerLightService.instance(project).registerListener()
  }
}

@Service
class VFSListenerLightService(
  /** One instance of this service is created per project. */
  private val project: Project
) {

  companion object {
    /**
     * This is used by IDEA to get a reference to the single instance of this service (used by [ServiceManager]).
     */
    fun instance(project: Project): VFSListenerLightService = project.getService(VFSListenerLightService::class.java)
  }

  fun registerListener() {
    colorConsole {
      printLine {
        span(Purple, "registerListener() running")
      }
    }
    val connection = project.messageBus.connect(/*parentDisposable=*/ project)
    connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, VFSDocumentSyncListener(project))
  }
}

class VFSDocumentSyncListener(
  private val project: Project,
  private val shortenUrlService: ShortenUrlService = TinyUrl()
) : FileDocumentManagerListener {
  override fun beforeDocumentSaving(document: Document) =
    ReplaceLongLinksInMarkdownFileOnSave(project, shortenUrlService).execute(document)
}

class ReplaceLongLinksInMarkdownFileOnSave(
  private val project: Project,
  private val shortenUrlService: ShortenUrlService
) {
  fun execute(document: Document) {
    val vFile = FileDocumentManager.getInstance().getFile(document)

    colorConsole {
      printDebugHeader()
      printWhichThread()
      printLine {
        span(Purple, "project: $project")
      }
      printLine {
        span(Blue, "A VirtualFile is about to be saved\n")
        span("\tvFile: $vFile\n")
        span("\tdocument: $document\n")
      }
    }

    if (SettingsUIData.instance.myState.myFlag) {
      object : Task.Backgroundable(project, "🔥 Run background task on save Markdown file 🔥") {
        override fun run(indicator: ProgressIndicator) = doWorkInBackground(document, indicator)
      }.queue()
    } else {
      colorConsole {
        printLine {
          span(Red, "⚠️ myFlag is false -> do nothing ⚠️")
        }
      }
    }
  }

  private fun doWorkInBackground(document: Document, indicator: ProgressIndicator) {
    colorConsole {
      printDebugHeader()
      printWhichThread()
    }

    val checkCancelled = CheckCancelled(indicator, project)
    val markdownPsiFile = runReadAction { getMarkdownPsiFile(project, document) }

    markdownPsiFile?.apply {
      val linkNodes = runReadAction { getAllLongLinks(markdownPsiFile, checkCancelled) }

      colorConsole {
        printLine {
          span(Red, "️⚠️ Shorten links ⚠️")
          span(Green, "size: ${linkNodes.size}")
          span(Green, linkNodes.toString())
        }
      }

      // Do this in background thread: make blocking calls that perform network IO.
      linkNodes.forEach { linkNode: LinkNode ->
        linkNode.linkDestination = shortenUrlService.shorten(linkNode.linkDestination)
        checkCancelled.invoke()
      }

      // Mutate the PSI in this write command action.
      // - The write command action enables undo.
      // - The lambda inside of this call runs in the EDT.
      WriteCommandAction.runWriteCommandAction(project) {
        if (!markdownPsiFile.isValid) return@runWriteCommandAction
        colorConsole {
          printWhichThread()
          printLine {
            span(Green, "🔥 Running write action to replace links 🔥")
          }
        }
        linkNodes.forEach { replaceExistingLinkWith(project, it, checkCancelled) }
      }
    }
  }

  private fun getAllLongLinks(psiFile: PsiFile, checkCancelled: CheckCancelled): List<LinkNode> {
    val links = mutableListOf<LinkNode>()

    colorConsole {
      printLine {
        span(Red, "🔥 Process Markdown file 🔥")
      }
    }

    val linkElements = findAllChildElements(psiFile, MarkdownTokenTypeSets.LINKS, checkCancelled)
    // The following line does the same thing as above:
    // val collectedLinks = PsiTreeUtil.collectElementsOfType(psiFile, MarkdownLinkDestinationImpl::class.java)

    colorConsole {
      printLine {
        span(Purple, "size of collected link elements")
        span(Green, linkElements.size.toString())
      }
    }

    linkElements.forEach { element ->
      val linkNode = findLink(element, psiFile, checkCancelled)
      colorConsole {
        printLine {
          span(Purple, "linkNode")
          span(Green, "${linkNode ?: "null"}")
        }
      }
      if (shouldAccept(linkNode)) links.add(linkNode!!)
    }

    return links
  }

  private fun shouldAccept(linkNode: LinkNode?): Boolean = when {
    linkNode == null -> false
    linkNode.linkDestination.startsWith("https://tinyurl.com") -> false
    linkNode.linkDestination.startsWith("http") -> true
    else -> false
  }
}

fun getMarkdownPsiFile(project: Project, document: Document): PsiFile? {
  colorConsole {
    printWhichThread()
  }

  val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)

  psiFile?.apply {
    val viewProvider = psiFile.viewProvider
    val langs = viewProvider.languages
    if (langSetContains(langs, "Markdown")) return psiFile
  }

  return null
}

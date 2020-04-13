package actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import psi.findLink
import vfs.getMarkdownPsiFile

/**
 * There are other ways to access an [Editor] instance:
 * - If a `DataContext` object is available: `CommonDataKeys.EDITOR.getData(context)`
 * - If only a `Project` object is available: `FileEditorManager.getInstance(project).getSelectedTextEditor()`
 */
fun mustHaveProjectAndEditorAndTextSelection(e: AnActionEvent) {
  val project: Project? = e.project
  val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
  // Action visible only if the editor in the open project has text selected.
  e.presentation.isEnabledAndVisible =
      project != null
      && editor != null
      && editor.selectionModel.hasSelection()
}

fun mustHaveProjectAndEditor(e: AnActionEvent) {
  val project: Project? = e.project
  val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
  e.presentation.isEnabledAndVisible =
      project != null
      && editor != null
}

fun mustBeMarkdownDocument(e: AnActionEvent) {
  val project = e.getRequiredData(CommonDataKeys.PROJECT)
  val document = e.getRequiredData(PlatformDataKeys.EDITOR).document
  val markdownFile = runReadAction { getMarkdownPsiFile(project, document) }
  e.presentation.isEnabledAndVisible = markdownFile != null
}

fun mustHaveLinkSelected(e: AnActionEvent) {
  val editor = e.getRequiredData(PlatformDataKeys.EDITOR)
  val psiFile = e.getRequiredData(CommonDataKeys.PSI_FILE)

  // Acquire a read lock in order to find the link information.
  val linkNode = runReadAction {
    val offset = editor.caretModel.offset
    val elementAtCaret: PsiElement? = psiFile.findElementAt(offset)
    findLink(elementAtCaret, psiFile, null)
  }

  e.presentation.isEnabledAndVisible = linkNode != null
}
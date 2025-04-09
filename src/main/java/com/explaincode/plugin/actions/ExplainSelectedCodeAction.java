package com.explaincode.plugin.actions;

import com.explaincode.plugin.services.CodeAnalyzerService;
import com.explaincode.plugin.ui.CodeExplanationDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

/**
 * Action that analyzes and explains the currently selected code in the editor.
 */
public class ExplainSelectedCodeAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable the action only when there's a selection in the editor
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null && 
                editor.getSelectionModel().hasSelection());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);

        if (psiFile == null) {
            Messages.showErrorDialog(project, "Cannot find PSI file for the current editor", "Code Explanation Error");
            return;
        }

        // Get the selected text
        SelectionModel selectionModel = editor.getSelectionModel();
        if (!selectionModel.hasSelection()) {
            Messages.showInfoMessage(project, "Please select some code to explain", "No Selection");
            return;
        }

        String selectedText = selectionModel.getSelectedText();
        int startOffset = selectionModel.getSelectionStart();
        int endOffset = selectionModel.getSelectionEnd();

        // Find the PSI element at the selection
        PsiElement element = psiFile.findElementAt(startOffset);
        if (element == null) {
            Messages.showErrorDialog(project, "Cannot find PSI element at the current position", "Code Explanation Error");
            return;
        }

        // Analyze the selected code and display information
        analyzeAndExplainCode(project, psiFile, element, selectedText, startOffset, endOffset);
    }

    private void analyzeAndExplainCode(Project project, PsiFile psiFile, PsiElement element, 
                                      String selectedText, int startOffset, int endOffset) {
        // Use the CodeAnalyzerService to analyze the selected code
        CodeAnalyzerService analyzerService = new CodeAnalyzerService();
        String explanation = analyzerService.analyzeCode(element, selectedText);

        // Add file information
        StringBuilder fullExplanation = new StringBuilder(explanation);
        fullExplanation.append("\n\nFile: ").append(psiFile.getName());
        fullExplanation.append("\nSelection Range: ").append(startOffset).append(" - ").append(endOffset);

        // Show the explanation in a custom dialog
        CodeExplanationDialog dialog = new CodeExplanationDialog(project, fullExplanation.toString(), selectedText);
        dialog.show();
    }
}

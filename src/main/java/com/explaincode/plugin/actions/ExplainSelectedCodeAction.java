package com.explaincode.plugin.actions;

import com.explaincode.plugin.config.PluginConfig;
import com.explaincode.plugin.services.CodeAnalyzerService;
import com.explaincode.plugin.ui.CodeExplanationDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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

    // Static initializer to set debug mode from system property or environment variable
    static {
        // Check for system property first
        String debugProperty = System.getProperty("explaincode.debug");
        if (debugProperty != null && (debugProperty.equalsIgnoreCase("true") || debugProperty.equals("1"))) {
            PluginConfig.getInstance().setDebugMode(true);
        } else {
            // Check for environment variable if system property is not set
            String debugEnv = System.getenv("EXPLAINCODE_DEBUG");
            if (debugEnv != null && (debugEnv.equalsIgnoreCase("true") || debugEnv.equals("1"))) {
                PluginConfig.getInstance().setDebugMode(true);
            }
        }
    }

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
        // Get the CodeAnalyzerService from the application service registry
        CodeAnalyzerService analyzerService = com.intellij.openapi.application.ApplicationManager.getApplication()
                .getService(CodeAnalyzerService.class);

        // Show loading indicator in the background and make the API call without blocking the UI
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Analyzing Code", true) {
            private String explanation;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Analyzing your code...");
                indicator.setIndeterminate(true);

                try {
                    // Make the API call and wait for the result
                    explanation = analyzerService.analyzeCodeAsync(element, selectedText).get();
                } catch (Exception e) {
                    explanation = "Error: Failed to get explanation from API. Exception: " + e.getMessage();
                }
            }

            @Override
            public void onSuccess() {
                // Show dialog with the explanation
                CodeExplanationDialog dialog = new CodeExplanationDialog(project, explanation, selectedText);
                dialog.show();
            }
        });
    }
}

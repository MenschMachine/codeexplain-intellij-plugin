package xzy.codeexplain.plugin.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import xzy.codeexplain.plugin.services.CodeAnalyzerService;
import xzy.codeexplain.plugin.ui.CodeExplanationDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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

        // Find the PSI element at the selection
        PsiElement element = psiFile.findElementAt(startOffset);
        if (element == null) {
            Messages.showErrorDialog(project, "Cannot find PSI element at the current position", "Code Explanation Error");
            return;
        }

        // Analyze the selected code and display information
        analyzeAndExplainCode(project, element, selectedText, editor);
    }

    private void analyzeAndExplainCode(Project project, PsiElement element,
                                       String selectedText, Editor editor) {
        String context = getSurroundingContext(element);
        CodeAnalyzerService analyzerService = com.intellij.openapi.application.ApplicationManager.getApplication()
                .getService(CodeAnalyzerService.class);

        // Create a loading hint near the cursor
        JComponent loadingHint = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loadingHint.setOpaque(false);
        JBLabel loadingLabel = new JBLabel("Analyzing...", IconLoader.getIcon("/icons/explain_code.svg", ExplainSelectedCodeAction.class), SwingConstants.LEFT);
        loadingHint.add(loadingLabel);

        // Show the hint near the editor caret
        HintManager.getInstance().showInformationHint(
                editor,
                loadingHint
        );

        // Show loading indicator in the background and make the API call without blocking the UI
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Analyzing Code", true) {
            private String explanation;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Analyzing your code...");
                indicator.setIndeterminate(true);

                try {
                    explanation = analyzerService.analyzeCodeAsync(element, selectedText, context).get();
                } catch (Exception e) {
                    explanation = "Error: Failed to get explanation from API. Exception: " + e.getMessage();
                } finally {
                    // Hide the hint when done
                    ApplicationManager.getApplication().invokeLater(() -> HintManager.getInstance().hideAllHints());
                }
            }

            @Override
            public void onSuccess() {
                CodeExplanationDialog dialog = new CodeExplanationDialog(project, explanation, selectedText);
                dialog.show();
            }
        });
    }


    /**
     * Gets the surrounding context of the selected code.
     * This extracts a larger portion of code around the selected element.
     */
    private String getSurroundingContext(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        // Get the entire file content as context
        // In a more sophisticated implementation, you might want to get just
        // the surrounding function/method/class
        return Objects.requireNonNullElse(containingFile, element).getText();

        // If we can't get the file, just use the element's text
    }
}

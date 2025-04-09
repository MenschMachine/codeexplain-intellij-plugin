package com.explaincode.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying code explanation in a more user-friendly way.
 */
public class CodeExplanationDialog extends DialogWrapper {
    private final String explanation;
    private final String selectedCode;

    public CodeExplanationDialog(@Nullable Project project, String explanation, String selectedCode) {
        super(project);
        this.explanation = explanation;
        this.selectedCode = selectedCode;
        setTitle("Code Explanation");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBPanel<JBPanel<?>> panel = new JBPanel<>(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 400));
        panel.setBorder(JBUI.Borders.empty(10));

        // Create a tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab for the explanation
        JBPanel<JBPanel<?>> explanationPanel = new JBPanel<>(new BorderLayout());
        JTextArea explanationText = new JTextArea(explanation);
        explanationText.setEditable(false);
        explanationText.setLineWrap(true);
        explanationText.setWrapStyleWord(true);
        explanationText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JBScrollPane explanationScrollPane = new JBScrollPane(explanationText);
        explanationPanel.add(explanationScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Explanation", explanationPanel);
        
        // Tab for the selected code
        JBPanel<JBPanel<?>> codePanel = new JBPanel<>(new BorderLayout());
        JTextArea codeText = new JTextArea(selectedCode);
        codeText.setEditable(false);
        codeText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JBScrollPane codeScrollPane = new JBScrollPane(codeText);
        codePanel.add(codeScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Selected Code", codePanel);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
}
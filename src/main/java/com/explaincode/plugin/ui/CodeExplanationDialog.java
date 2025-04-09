package com.explaincode.plugin.ui;

import com.explaincode.plugin.config.PluginConfig;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dialog for displaying code explanation in a more user-friendly way.
 */
public class CodeExplanationDialog extends DialogWrapper {
    private String explanation;
    private final String selectedCode;
    private String htmlSource; // Store the HTML source for debug mode
    private JEditorPane explanationText;
    private JPanel loadingPanel;
    private JTabbedPane tabbedPane;
    private JBPanel<JBPanel<?>> explanationPanel;
    private boolean isDarkTheme;

    /**
     * Constructor for showing the dialog with a loading indicator.
     */
    public CodeExplanationDialog(@Nullable Project project, String selectedCode) {
        super(project);
        this.explanation = "Loading explanation...";
        this.selectedCode = selectedCode;
        this.htmlSource = ""; // Initialize HTML source
        setTitle("Code Explanation");
        init();
    }

    /**
     * Constructor for showing the dialog with an explanation already available.
     */
    public CodeExplanationDialog(@Nullable Project project, String explanation, String selectedCode) {
        super(project);
        this.explanation = explanation;
        this.selectedCode = selectedCode;
        this.htmlSource = ""; // Initialize HTML source
        setTitle("Code Explanation");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBPanel<JBPanel<?>> panel = new JBPanel<>(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 400));
        panel.setBorder(JBUI.Borders.empty(10));

        // Create a tabbed pane for different views
        tabbedPane = new JTabbedPane();

        // Tab for the explanation
        explanationPanel = new JBPanel<>(new BorderLayout());

        // Create loading panel with spinner
        loadingPanel = createLoadingPanel();

        // Create explanation editor pane with HTML support
        explanationText = new JEditorPane();
        explanationText.setEditable(false);
        explanationText.setContentType("text/html");

        // Set up HTML styling
        HTMLEditorKit kit = new HTMLEditorKit();
        explanationText.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();

        // Check if the IDE is using a dark theme
        this.isDarkTheme = UIUtil.isUnderDarcula();

        // Apply common styles
        styleSheet.addRule("body { font-family: sans-serif; font-size: 12pt; margin: 10px; }");
        styleSheet.addRule("h1, h2, h3, h4, h5, h6 { margin: 8px; }");
        styleSheet.addRule("p { margin: 8px; }");
        styleSheet.addRule("ul, ol { margin: 4px; }");

        // Apply theme-specific styles
        if (isDarkTheme) {
            // Dark theme styles
            styleSheet.addRule("body { background-color: #2b2b2b; color: #a9b7c6; }");
            styleSheet.addRule("pre { background-color: #2d2d2d; color: #f8f8f2; padding: 10px; font-family: monospace; }");
            styleSheet.addRule("code { background-color: #2d2d2d; color: #f8f8f2; padding: 2px 4px; font-family: monospace; }");
            styleSheet.addRule("a { color: #589df6; }");
            styleSheet.addRule("h1, h2, h3, h4, h5, h6 { color: #d0d0ff; }");
        } else {
            // Light theme styles
            styleSheet.addRule("body { background-color: #ffffff; color: #000000; }");
            styleSheet.addRule("pre { background-color: #f5f5f5; color: #000000; padding: 10px; font-family: monospace; }");
            styleSheet.addRule("code { background-color: #f5f5f5; color: #000000; padding: 2px 4px; font-family: monospace; }");
            styleSheet.addRule("a { color: #0366d6; }");
            styleSheet.addRule("h1, h2, h3, h4, h5, h6 { color: #000000; }");
        }

        // Set the content
        String htmlContent;
        if (explanation.equals("Loading explanation...")) {
            String bodyStyle = isDarkTheme
                    ? "<html><body style=\"background-color: #2b2b2b; color: #a9b7c6;\">Loading explanation...</body></html>"
                    : "<html><body>Loading explanation...</body></html>";
            htmlContent = bodyStyle;
            this.htmlSource = htmlContent;
        } else {
            htmlContent = markdownToHtml(explanation);
            this.htmlSource = htmlContent;
        }
        explanationText.setText(htmlContent);

        JBScrollPane explanationScrollPane = new JBScrollPane(explanationText);

        // Add the appropriate component based on whether we're loading or not
        if (explanation.equals("Loading explanation...")) {
            explanationPanel.add(loadingPanel, BorderLayout.CENTER);
        } else {
            explanationPanel.add(explanationScrollPane, BorderLayout.CENTER);
        }

        tabbedPane.addTab("Explanation", explanationPanel);

        // Tab for the selected code
        JBPanel<JBPanel<?>> codePanel = new JBPanel<>(new BorderLayout());
        JTextArea codeText = new JTextArea(selectedCode);
        codeText.setEditable(false);
        codeText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Apply theme-specific styling to the code text area
        if (isDarkTheme) {
            // Dark theme colors
            codeText.setBackground(new Color(0x2d2d2d));
            codeText.setForeground(new Color(0xf8f8f2));
            codeText.setCaretColor(new Color(0xf8f8f2));
        } else {
            // Light theme colors
            codeText.setBackground(new Color(0xf5f5f5));
            codeText.setForeground(new Color(0x000000));
            codeText.setCaretColor(new Color(0x000000));
        }

        JBScrollPane codeScrollPane = new JBScrollPane(codeText);
        codePanel.add(codeScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Selected Code", codePanel);

        // Add HTML Source tab if debug mode is enabled
        PluginConfig config = ApplicationManager.getApplication().getService(PluginConfig.class);
        if (config.isDebugMode()) {
            JBPanel<JBPanel<?>> htmlSourcePanel = new JBPanel<>(new BorderLayout());
            JTextArea htmlSourceText = new JTextArea(htmlSource);
            htmlSourceText.setEditable(false);
            htmlSourceText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            // Apply theme-specific styling to the HTML source text area
            if (isDarkTheme) {
                // Dark theme colors
                htmlSourceText.setBackground(new Color(0x2d2d2d));
                htmlSourceText.setForeground(new Color(0xf8f8f2));
                htmlSourceText.setCaretColor(new Color(0xf8f8f2));
            } else {
                // Light theme colors
                htmlSourceText.setBackground(new Color(0xf5f5f5));
                htmlSourceText.setForeground(new Color(0x000000));
                htmlSourceText.setCaretColor(new Color(0x000000));
            }

            JBScrollPane htmlSourceScrollPane = new JBScrollPane(htmlSourceText);
            htmlSourcePanel.add(htmlSourceScrollPane, BorderLayout.CENTER);
            tabbedPane.addTab("HTML Source", htmlSourcePanel);
        }

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a panel with a loading spinner and message.
     */
    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.empty(20));

        // Create a panel for the spinner and text with BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Add spinner
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel loadingLabel = new JLabel("Loading...");
        spinnerPanel.add(loadingLabel);
        JProgressBar spinner = new JProgressBar();
        spinner.setIndeterminate(true);
        spinnerPanel.add(spinner);
        centerPanel.add(spinnerPanel);

        // Add message
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel messageLabel = new JLabel("Analyzing your code. This may take a few seconds.");
        messagePanel.add(messageLabel);
        centerPanel.add(messagePanel);

        // Apply theme-specific styling
        if (isDarkTheme) {
            // Dark theme colors
            Color darkBackground = new Color(0x2b2b2b);
            Color darkForeground = new Color(0xa9b7c6);

            panel.setBackground(darkBackground);
            centerPanel.setBackground(darkBackground);
            spinnerPanel.setBackground(darkBackground);
            messagePanel.setBackground(darkBackground);

            loadingLabel.setForeground(darkForeground);
            messageLabel.setForeground(darkForeground);
        }

        // Center the content
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Updates the dialog with the explanation received from the API.
     *
     * @param newExplanation The explanation text to display
     */
    public void updateExplanation(String newExplanation) {
        this.explanation = newExplanation;

        // Convert markdown to HTML and set the text
        String htmlContent = markdownToHtml(newExplanation);
        this.htmlSource = htmlContent; // Store the HTML source
        explanationText.setText(htmlContent);

        // Replace loading panel with explanation text
        explanationPanel.removeAll();
        JBScrollPane explanationScrollPane = new JBScrollPane(explanationText);
        explanationPanel.add(explanationScrollPane, BorderLayout.CENTER);

        // Update HTML Source tab if debug mode is enabled
        PluginConfig config = ApplicationManager.getApplication().getService(PluginConfig.class);
        if (config.isDebugMode()) {
            // Check if the HTML Source tab exists
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if ("HTML Source".equals(tabbedPane.getTitleAt(i))) {
                    // Get the component at this tab
                    Component component = tabbedPane.getComponentAt(i);
                    if (component instanceof JBPanel) {
                        JBPanel<?> panel = (JBPanel<?>) component;
                        // Find the JTextArea inside the panel
                        for (Component c : panel.getComponents()) {
                            if (c instanceof JBScrollPane) {
                                JBScrollPane scrollPane = (JBScrollPane) c;
                                Component view = scrollPane.getViewport().getView();
                                if (view instanceof JTextArea) {
                                    JTextArea textArea = (JTextArea) view;
                                    textArea.setText(htmlSource);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        // Refresh the UI
        explanationPanel.revalidate();
        explanationPanel.repaint();
    }

    /**
     * Converts markdown text to HTML for display in the JEditorPane.
     * This is a simple implementation that handles common markdown elements.
     *
     * @param markdown The markdown text to convert
     * @return HTML representation of the markdown
     */
    private String markdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            String bodyStyle = isDarkTheme
                    ? "<html><body style=\"background-color: #2b2b2b; color: #a9b7c6;\"></body></html>"
                    : "<html><body></body></html>";
            return bodyStyle;
        }

        // Escape HTML special characters
        String html = markdown.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        // Convert markdown to HTML

        // Headers
        html = html.replaceAll("(?m)^# (.*?)$", "<h1>$1</h1>");
        html = html.replaceAll("(?m)^## (.*?)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^### (.*?)$", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^#### (.*?)$", "<h4>$1</h4>");
        html = html.replaceAll("(?m)^##### (.*?)$", "<h5>$1</h5>");
        html = html.replaceAll("(?m)^###### (.*?)$", "<h6>$1</h6>");

        // Bold
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        html = html.replaceAll("__(.*?)__", "<strong>$1</strong>");

        // Italic
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        html = html.replaceAll("_(.*?)_", "<em>$1</em>");

        // Code blocks
        Pattern codeBlockPattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
        Matcher codeBlockMatcher = codeBlockPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (codeBlockMatcher.find()) {
            String codeContent = codeBlockMatcher.group(1).trim();
            codeBlockMatcher.appendReplacement(sb, "<pre><code>" + codeContent + "</code></pre>");
        }
        codeBlockMatcher.appendTail(sb);
        html = sb.toString();

        // Inline code
        html = html.replaceAll("`([^`]*?)`", "<code>$1</code>");

        // Lists
        html = html.replaceAll("(?m)^- (.*?)$", "<li>$1</li>");
        html = html.replaceAll("(?m)^\\* (.*?)$", "<li>$1</li>");
        html = html.replaceAll("(?m)^\\d+\\. (.*?)$", "<li>$1</li>");

        // Wrap lists in <ul> or <ol> tags
        // This is a simplified approach and might not handle nested lists correctly
        html = html.replaceAll("(<li>.*?</li>)+", "<ul>$0</ul>");

        // Links
        html = html.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

        // Paragraphs
        html = html.replaceAll("(?m)^([^<].*?)$", "<p>$1</p>");

        // Clean up any empty paragraphs or duplicate tags
        html = html.replaceAll("<p>\\s*</p>", "");
        html = html.replaceAll("<p><li>", "<li>");
        html = html.replaceAll("</li></p>", "</li>");

        // Add theme-specific body styling
        String bodyStyle = isDarkTheme
                ? "<html><body style=\"background-color: #2b2b2b; color: #a9b7c6;\">"
                : "<html><body>";
        return bodyStyle + html + "</body></html>";
    }
}

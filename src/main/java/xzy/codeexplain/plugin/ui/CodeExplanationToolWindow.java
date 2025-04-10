package xzy.codeexplain.plugin.ui;

import xzy.codeexplain.plugin.config.PluginConfig;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tool window for displaying code explanation in a more user-friendly way.
 */
public class CodeExplanationToolWindow {
    private String explanation;
    private String selectedCode;
    private String htmlSource; // Store the HTML source for debug mode
    private JEditorPane explanationText;
    private JPanel loadingPanel;
    private JTabbedPane tabbedPane;
    private JBPanel<JBPanel<?>> explanationPanel;
    private boolean isDarkTheme;
    private JPanel mainPanel;
    private Project project;

    /**
     * Constructor for creating the tool window with a loading indicator.
     */
    public CodeExplanationToolWindow(@Nullable Project project) {
        this.project = project;
        this.explanation = "No explanation available yet. Select code and use 'Explain Selected Code' action.";
        this.selectedCode = "";
        this.htmlSource = ""; // Initialize HTML source
        createUI();
    }

    /**
     * Creates the UI components for the tool window.
     */
    private void createUI() {
        mainPanel = new JPanel(new BorderLayout());

        // Create a tabbed pane for different views
        tabbedPane = new JTabbedPane();

        // Tab for the explanation
        explanationPanel = new JBPanel<>(new BorderLayout());

        // Create loading panel with spinner
        loadingPanel = createLoadingPanel();

        // Check if the IDE is using a dark theme
        this.isDarkTheme = UIUtil.isUnderDarcula();

        // Create explanation editor pane with HTML support
        explanationText = new JEditorPane();
        explanationText.setEditable(false);
        explanationText.setContentType("text/html");

        // Set up HTML styling
        HTMLEditorKit kit = new HTMLEditorKit();
        explanationText.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();

        // Apply common styles
        styleSheet.addRule("body { font-family: sans-serif; font-size: 12pt; margin: 8px; }");
        styleSheet.addRule("h1, h2, h3, h4, h5, h6 { margin: 8px; }");
        styleSheet.addRule("p { margin: 4px; }");
        styleSheet.addRule("ul, ol { margin: 2px; }");

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

        // Set the initial content
        String htmlContent = markdownToHtml(explanation);
        this.htmlSource = htmlContent;
        explanationText.setText(htmlContent);

        // Initially show the loading panel instead of the explanation text
        explanationPanel.add(loadingPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Explanation", explanationPanel);

        // Tab for the selected code (initially empty)
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
        if (PluginConfig.isDebugMode()) {
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

            // Add Original Markdown tab
            JBPanel<JBPanel<?>> markdownPanel = new JBPanel<>(new BorderLayout());
            JTextArea markdownText = new JTextArea(explanation);
            markdownText.setEditable(false);
            markdownText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            // Apply theme-specific styling to the markdown text area
            if (isDarkTheme) {
                // Dark theme colors
                markdownText.setBackground(new Color(0x2d2d2d));
                markdownText.setForeground(new Color(0xf8f8f2));
                markdownText.setCaretColor(new Color(0xf8f8f2));
            } else {
                // Light theme colors
                markdownText.setBackground(new Color(0xf5f5f5));
                markdownText.setForeground(new Color(0x000000));
                markdownText.setCaretColor(new Color(0x000000));
            }

            JBScrollPane markdownScrollPane = new JBScrollPane(markdownText);
            markdownPanel.add(markdownScrollPane, BorderLayout.CENTER);
            tabbedPane.addTab("Original Markdown", markdownPanel);
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
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
        JLabel messageLabel = new JLabel("Select code and use 'Explain Selected Code' action.");
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
     * Updates the tool window with the explanation received from the API.
     *
     * @param newExplanation The explanation text to display
     * @param newSelectedCode The selected code to display
     */
    public void updateContent(String newExplanation, String newSelectedCode) {
        this.explanation = newExplanation;
        this.selectedCode = newSelectedCode;

        // Convert markdown to HTML and set the text
        String htmlContent = markdownToHtml(newExplanation);
        this.htmlSource = htmlContent; // Store the HTML source
        explanationText.setText(htmlContent);

        // Replace loading panel with explanation text if it's currently showing
        explanationPanel.removeAll();
        JBScrollPane explanationScrollPane = new JBScrollPane(explanationText);
        explanationPanel.add(explanationScrollPane, BorderLayout.CENTER);

        // Update the selected code tab
        updateSelectedCodeTab(newSelectedCode);

        // Update HTML Source tab if debug mode is enabled
        if (PluginConfig.isDebugMode()) {
            updateDebugTabs();
        }

        // Refresh the UI
        explanationPanel.revalidate();
        explanationPanel.repaint();
    }

    /**
     * Updates the selected code tab with new code.
     */
    private void updateSelectedCodeTab(String newCode) {
        // Find the Selected Code tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if ("Selected Code".equals(tabbedPane.getTitleAt(i))) {
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
                                textArea.setText(newCode);
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * Updates the debug tabs (HTML Source and Original Markdown) if they exist.
     */
    private void updateDebugTabs() {
        // Update HTML Source tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if ("HTML Source".equals(tabbedPane.getTitleAt(i))) {
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

        // Update Original Markdown tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if ("Original Markdown".equals(tabbedPane.getTitleAt(i))) {
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
                                textArea.setText(explanation);
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * Converts markdown text to HTML for display in the JEditorPane.
     *
     * @param markdownInput The markdown text to convert
     * @return HTML representation of the markdown
     */
    private String markdownToHtml(String markdownInput) {
        Parser parser = Parser.builder().build();
        Document document = parser.parse(markdownInput);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Returns the main panel of the tool window.
     */
    public JComponent getContent() {
        return mainPanel;
    }

    /**
     * Shows the loading panel in the explanation tab with the initial message.
     */
    public void showLoading() {
        explanationPanel.removeAll();
        explanationPanel.add(loadingPanel, BorderLayout.CENTER);
        explanationPanel.revalidate();
        explanationPanel.repaint();
    }

    /**
     * Shows the loading panel with an analyzing message when processing code.
     */
    public void showAnalyzing() {
        // First update the message in the loading panel
        for (Component c : loadingPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel centerPanel = (JPanel) c;
                for (Component innerC : centerPanel.getComponents()) {
                    if (innerC instanceof JPanel) {
                        JPanel messagePanel = (JPanel) innerC;
                        // Check if this is the message panel (second panel)
                        if (messagePanel.getComponents().length > 0 && messagePanel.getComponents()[0] instanceof JLabel) {
                            JLabel messageLabel = (JLabel) messagePanel.getComponents()[0];
                            messageLabel.setText("Analyzing your code. This may take a few seconds.");
                            break;
                        }
                    }
                }
            }
        }

        // Then show the loading panel
        explanationPanel.removeAll();
        explanationPanel.add(loadingPanel, BorderLayout.CENTER);
        explanationPanel.revalidate();
        explanationPanel.repaint();
    }
}

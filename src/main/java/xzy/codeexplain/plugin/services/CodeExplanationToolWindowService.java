package xzy.codeexplain.plugin.services;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xzy.codeexplain.plugin.ui.CodeExplanationToolWindow;

/**
 * Project service that manages the Code Explanation tool window.
 * This service provides access to the tool window instance from different parts of the plugin.
 */
public final class CodeExplanationToolWindowService {
    private CodeExplanationToolWindow toolWindow;
    private final Project project;

    public CodeExplanationToolWindowService(@NotNull Project project) {
        this.project = project;
    }

    /**
     * Sets the tool window instance.
     *
     * @param toolWindow The tool window instance
     */
    public void setToolWindow(@NotNull CodeExplanationToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    /**
     * Gets the tool window instance.
     *
     * @return The tool window instance, or null if not initialized
     */
    @Nullable
    public CodeExplanationToolWindow getToolWindow() {
        return toolWindow;
    }

    /**
     * Gets the service instance for the given project.
     *
     * @param project The project
     * @return The service instance
     */
    @NotNull
    public static CodeExplanationToolWindowService getInstance(@NotNull Project project) {
        return project.getService(CodeExplanationToolWindowService.class);
    }

    /**
     * Updates the tool window content with new explanation and code.
     *
     * @param explanation The explanation text
     * @param selectedCode The selected code
     */
    public void updateContent(@NotNull String explanation, @NotNull String selectedCode) {
        if (toolWindow != null) {
            toolWindow.updateContent(explanation, selectedCode);
        }
    }

    /**
     * Shows the loading indicator in the tool window.
     */
    public void showLoading() {
        if (toolWindow != null) {
            toolWindow.showLoading();
        }
    }
}

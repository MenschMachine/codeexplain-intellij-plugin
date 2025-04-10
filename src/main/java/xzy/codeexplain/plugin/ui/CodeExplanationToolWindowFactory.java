package xzy.codeexplain.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import xzy.codeexplain.plugin.services.CodeExplanationToolWindowService;

/**
 * Factory class for creating the Code Explanation tool window.
 */
public class CodeExplanationToolWindowFactory implements ToolWindowFactory {

    /**
     * Creates the tool window content.
     *
     * @param project    current project
     * @param toolWindow the tool window to which content will be added
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodeExplanationToolWindow codeExplanationToolWindow = new CodeExplanationToolWindow(project);
        Content content = ContentFactory.getInstance().createContent(
                codeExplanationToolWindow.getContent(),
                "",
                false
        );
        toolWindow.getContentManager().addContent(content);

        // Set the tool window type to floating
        toolWindow.setType(ToolWindowType.FLOATING, null);

        // Store the tool window instance in the project service for later access
        CodeExplanationToolWindowService service = project.getService(CodeExplanationToolWindowService.class);
        service.setToolWindow(codeExplanationToolWindow);
    }
}

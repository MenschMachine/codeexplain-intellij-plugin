package com.explaincode.plugin.startup;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Executes tasks after the project has been opened and initialized.
 */
public class PluginStartupActivity implements StartupActivity {

    private static final Logger LOG = Logger.getInstance(PluginStartupActivity.class);

    @Override
    public void runActivity(@NotNull Project project) {
        // This code runs once the project is opened and initialized.
        // We can confirm the plugin is active here.
        // Note: PluginConfig initialization now happens in its constructor.
        LOG.info("ExplainCode plugin is active for project: " + project.getName());
    }
}

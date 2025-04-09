package xzy.codeexplain.plugin.services;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for plugin lifecycle events.
 * This listener is notified when plugins are loaded or unloaded.
 * It properly cleans up resources when the plugin is unloaded.
 */
public class PluginLifecycleListener implements DynamicPluginListener {
    private static final Logger LOG = Logger.getInstance(PluginLifecycleListener.class);

    @Override
    public void beforePluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        LOG.info("Plugin is about to be loaded: " + pluginDescriptor.getName());
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        LOG.info("Plugin has been loaded: " + pluginDescriptor.getName());
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin is about to be unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);

        // Get the CodeAnalyzerService from the application service registry
        CodeAnalyzerService analyzerService = ApplicationManager.getApplication()
                .getService(CodeAnalyzerService.class);

        // Clean up resources if it's our plugin
        if (pluginDescriptor.getPluginId().getIdString().equals("xyz.codeexplain.plugin")) {
            // Dispose of any resources that need to be cleaned up
            if (analyzerService != null) {
                try {
                    analyzerService.close();
                } catch (Exception e) {
                    LOG.error("Error closing CodeAnalyzerService", e);
                }
            }

            // Remove any listeners or references that might prevent unloading
            LOG.info("Cleaned up resources for plugin: " + pluginDescriptor.getName());
        }
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin has been unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);
    }
}
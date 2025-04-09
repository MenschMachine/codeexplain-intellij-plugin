package com.explaincode.plugin.services;

import com.explaincode.plugin.config.PluginConfig;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for plugin lifecycle events.
 * This listener is notified when plugins are loaded or unloaded.
 * It handles initialization and cleanup of plugin resources.
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

        // Initialization logic moved to PluginConfig constructor
        if ("com.explaincode.plugin".equals(pluginDescriptor.getPluginId().getIdString())) {
            LOG.info("ExplainCode plugin loaded.");
            // You could add other non-config initialization here if needed
        }
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin is about to be unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin has been unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);

        // Only clean up our plugin
        if ("com.explaincode.plugin".equals(pluginDescriptor.getPluginId().getIdString())) {
            cleanupPlugin();
        }
    }

    /**
     * Clean up plugin resources
     * This is important for dynamic plugins to prevent memory leaks
     */
    private void cleanupPlugin() {
        // The services annotated with @Service will be automatically disposed
        // by the platform when the plugin is unloaded
        LOG.info("Cleaning up plugin resources");
    }
}

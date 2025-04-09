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

        // Only initialize our plugin
        if ("com.explaincode.plugin".equals(pluginDescriptor.getPluginId().getIdString())) {
            initializePlugin();
        }
    }

    /**
     * Initialize plugin resources and configuration
     */
    private void initializePlugin() {
        // Get the PluginConfig service
        PluginConfig config = ApplicationManager.getApplication().getService(PluginConfig.class);

        // Check for system property first
        String debugProperty = System.getProperty("explaincode.debug");
        if (debugProperty != null && (debugProperty.equalsIgnoreCase("true") || debugProperty.equals("1"))) {
            config.setDebugMode(true);
        } else {
            // Check for environment variable if system property is not set
            String debugEnv = System.getenv("EXPLAINCODE_DEBUG");
            if (debugEnv != null && (debugEnv.equalsIgnoreCase("true") || debugEnv.equals("1"))) {
                config.setDebugMode(true);
            }
        }
    }

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin is about to be unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin has been unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);
    }
}

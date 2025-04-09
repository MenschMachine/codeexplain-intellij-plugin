package com.explaincode.plugin.services;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for plugin lifecycle events.
 * This listener is notified when plugins are loaded or unloaded.
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
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOG.info("Plugin has been unloaded: " + pluginDescriptor.getName() + ", isUpdate: " + isUpdate);
    }
}
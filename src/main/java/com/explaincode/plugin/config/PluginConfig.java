package com.explaincode.plugin.config;

import com.intellij.openapi.components.Service;

/**
 * Configuration class for the Explain Code plugin.
 * Contains settings that can be used throughout the plugin.
 * Implemented as an application service for proper lifecycle management.
 */
@Service
public final class PluginConfig {

    // Configuration properties
    private boolean debugMode = false;

    /**
     * Constructor for the service
     */
    public PluginConfig() {
        // Initialize with default values
    }

    /**
     * Check if debug mode is enabled
     * 
     * @return true if debug mode is enabled, false otherwise
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Set the debug mode
     * 
     * @param debugMode true to enable debug mode, false to disable
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}

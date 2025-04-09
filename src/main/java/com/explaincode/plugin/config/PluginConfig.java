package com.explaincode.plugin.config;

/**
 * Configuration class for the Explain Code plugin.
 * Contains settings that can be used throughout the plugin.
 */
public class PluginConfig {
    // Singleton instance
    private static PluginConfig instance;
    
    // Configuration properties
    private boolean debugMode = false;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private PluginConfig() {
        // Initialize with default values
    }
    
    /**
     * Get the singleton instance of the configuration
     * 
     * @return The PluginConfig instance
     */
    public static synchronized PluginConfig getInstance() {
        if (instance == null) {
            instance = new PluginConfig();
        }
        return instance;
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
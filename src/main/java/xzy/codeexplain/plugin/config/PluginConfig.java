package xzy.codeexplain.plugin.config;

/**
 * Configuration class for the Explain Code plugin.
 * Contains settings that can be used throughout the plugin.
 */
public class PluginConfig {
    /**
     * Check if debug mode is enabled
     *
     * @return true if debug mode is enabled, false otherwise
     */
    public static boolean isDebugMode() {
        String debugProperty = System.getProperty("explaincode.debug");
        if (debugProperty != null && (debugProperty.trim().equalsIgnoreCase("true") || debugProperty.trim().equals("1"))) {
            return true;
        } else {
            // Check for environment variable if system property is not set
            String debugEnv = System.getenv("EXPLAINCODE_DEBUG");
            if (debugEnv != null && (debugEnv.trim().equalsIgnoreCase("true") || debugEnv.trim().equals("1"))) {
                return true;
            }
        }
        return false;
    }

}
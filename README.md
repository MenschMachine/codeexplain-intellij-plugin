# ExplainCode Plugin

This guide provides detailed instructions on how to install, publish, set up your development environment, build, run, test, and debug the ExplainCode plugin.

## Table of Contents
- [Installation Guide](#installation-guide)
- [Publishing Guide](#publishing-guide)
- [Prerequisites](#prerequisites)
- [Setting Up the Development Environment](#setting-up-the-development-environment)
- [Building the Plugin](#building-the-plugin)
- [Running the Plugin](#running-the-plugin)
- [Loading the Plugin Locally for Quick Testing](#loading-the-plugin-locally-for-quick-testing)
- [Debugging the Plugin](#debugging-the-plugin)
- [Testing Strategies](#testing-strategies)
- [Troubleshooting](#troubleshooting)

## Installation Guide

### Installing from JetBrains Marketplace

1. Open IntelliJ IDEA
2. Go to Settings/Preferences > Plugins
3. Click on "Marketplace" tab
4. Search for "Explain Selected Code"
5. Click "Install"
6. Restart IntelliJ IDEA when prompted

### Installing from Disk

1. Download the plugin ZIP file from the [releases page](https://github.com/yourusername/codeexplain-intellij-plugin/releases) or build it yourself (see [Building the Plugin](#building-the-plugin))
2. Open IntelliJ IDEA
3. Go to Settings/Preferences > Plugins
4. Click the gear icon and select "Install Plugin from Disk..."
5. Navigate to the downloaded ZIP file and select it
6. Click "OK"
7. Restart IntelliJ IDEA when prompted

### Using the Plugin

1. Open any code file in your JetBrains IDE
2. Select a piece of code
3. Right-click and select "Explain Selected Code" from the context menu (or use Alt+Shift+E shortcut)
4. A dialog will appear with an explanation of the selected code

The plugin works with any programming language supported by your JetBrains IDE.

### Debug Mode

The plugin includes a DEBUG mode that can be enabled to show the HTML source code of the explanation in a third tab of the dialog.

#### Enabling Debug Mode

Debug mode can be enabled in one of two ways:

1. **System Property**:
   ```
   -Dexplaincode.debug=true
   ```
   Add this to your IDE's VM options.

2. **Environment Variable**:
   ```
   EXPLAINCODE_DEBUG=true
   ```
   Set this environment variable before starting your IDE.

When DEBUG mode is enabled, the explanation dialog will show a third tab labeled "HTML Source" that displays the raw HTML code used to render the explanation.

## Publishing Guide

### Prerequisites for Publishing

1. JetBrains Marketplace account - [Sign up here](https://plugins.jetbrains.com/author/me)
2. Plugin ZIP file (see [Building the Plugin](#building-the-plugin))

### Configuring for Publication

1. Update the `build.gradle` file to include publishing configuration:

```gradle
intellij {
    // Existing configuration...
}

// Add this section for publishing
publishPlugin {
    token = System.getenv("INTELLIJ_MARKETPLACE_TOKEN") // Use environment variable for security
    channels = ['default']
}
```

2. Ensure your plugin metadata in `plugin.xml` is complete and accurate:
   - Plugin ID: `xyz.codeexplain.plugin`
   - Name: `Explain Selected Code`
   - Vendor information
   - Description
   - Change notes for each version

### Publishing to JetBrains Marketplace

1. Generate a permanent token in your JetBrains Marketplace profile
2. Set the token as an environment variable:
   ```bash
   export INTELLIJ_MARKETPLACE_TOKEN="your-token-here"
   ```
3. Build and publish the plugin:
   ```bash
   ./gradlew publishPlugin
   ```
4. Verify the plugin appears in your JetBrains Marketplace profile
5. Submit the plugin for review by JetBrains (required for first-time publishing)

### Updating the Plugin

1. Update the plugin version in `build.gradle`:
   ```gradle
   version '1.0.1' // Increment version number
   ```
2. Update the change notes in `patchPluginXml` section of `build.gradle`
3. Build and publish as described above

## Prerequisites

Before you begin, ensure you have the following installed:
- Java Development Kit (JDK) 11 or later
- IntelliJ IDEA (Community or Ultimate edition)
- Git (for version control)

## Setting Up the Development Environment

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/codeexplain-intellij-plugin.git
cd codeexplain-intellij-plugin
```

### 2. Open the Project in IntelliJ IDEA

1. Launch IntelliJ IDEA
2. Select "Open" and navigate to the cloned repository
3. Choose "Open as Project"
4. Wait for the Gradle sync to complete

### 3. Configure Gradle JVM

1. Go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle
2. Ensure that the Gradle JVM is set to Java 11 or later

## Building the Plugin

### Building from IntelliJ IDEA

1. Open the Gradle tool window (View > Tool Windows > Gradle)
2. Navigate to Tasks > intellij > buildPlugin
3. Double-click on buildPlugin to execute it

### Building from Command Line

```bash
./gradlew buildPlugin
```

The built plugin will be located in `build/distributions/` as a ZIP file.

## Running the Plugin

### Running in a Development Instance

1. Open the Gradle tool window (View > Tool Windows > Gradle)
2. Navigate to Tasks > intellij > runIde
3. Double-click on runIde to execute it

This will launch a new IntelliJ IDEA instance with the plugin installed.

### Running from Command Line

```bash
./gradlew runIde
```

### Testing the Plugin Functionality

1. In the development instance, create or open a Java project
2. Open a Java file
3. Select a piece of code (e.g., a method, class, or statement)
4. Right-click and select "Explain Selected Code" from the context menu (or use Alt+Shift+E shortcut)
5. A dialog should appear with an explanation of the selected code

## Loading the Plugin Locally for Quick Testing

If you want to quickly test changes to the plugin without setting up a full development environment, you can build the plugin and load it into your existing IntelliJ IDEA installation.

### Quick Build and Install

1. Build the plugin from the command line:
   ```bash
   ./gradlew buildPlugin
   ```

2. The built plugin will be located in `build/distributions/` as a ZIP file (e.g., `codeexplain-intellij-plugin-1.0-SNAPSHOT.zip`)

3. Open your regular IntelliJ IDEA installation (not the development instance)

4. Go to Settings/Preferences > Plugins

5. Click the gear icon and select "Install Plugin from Disk..."

6. Navigate to the `build/distributions/` directory in your project and select the ZIP file

7. Click "OK" and restart IntelliJ IDEA when prompted

### Testing Changes

After installing the plugin:

1. Open a Java file in IntelliJ IDEA
2. Select a piece of code
3. Right-click and select "Explain Selected Code" from the context menu (or use Alt+Shift+E shortcut)
4. Verify that your changes are working as expected

### Updating After Changes

When you make changes to the plugin code:

1. Uninstall the previous version from IntelliJ IDEA:
   - Go to Settings/Preferences > Plugins
   - Find "Explain Selected Code" in the "Installed" tab
   - Click the gear icon next to it and select "Uninstall"
   - Restart IntelliJ IDEA when prompted

2. Rebuild the plugin:
   ```bash
   ./gradlew clean buildPlugin
   ```

3. Install the updated version following the steps in "Quick Build and Install"

### Enabling Plugin Development Mode

For even faster testing cycles, you can enable Plugin Development mode in IntelliJ IDEA:

1. Go to Settings/Preferences > Advanced Settings
2. Check "Allow loading plugins from disk in development mode"
3. Restart IntelliJ IDEA

With this setting enabled, you can update plugins without restarting the IDE:
1. After rebuilding the plugin, go to Settings/Preferences > Plugins
2. Click the "Reload All Plugins" button (circular arrow icon)

## Debugging the Plugin

### Setting Up a Debug Configuration

1. Go to Run > Edit Configurations
2. Click the "+" button and select "Gradle"
3. Set the Name to "Debug Plugin"
4. Set the Gradle project to your project
5. Set the Tasks to "runIde"
6. Apply and close

### Starting a Debug Session

1. Set breakpoints in your code where you want to pause execution
2. Select the "Debug Plugin" configuration from the dropdown in the toolbar
3. Click the debug button (or press Shift+F9)

### Key Places to Set Breakpoints

- `ExplainSelectedCodeAction.actionPerformed()`: Entry point when the action is triggered
- `CodeAnalyzerService.analyzeCode()`: Where code analysis begins
- `CodeExplanationDialog.createCenterPanel()`: Where the UI is created

### Debugging PSI Elements

When debugging PSI (Program Structure Interface) elements:

1. Use the "Evaluate Expression" feature (Alt+F8) to inspect PSI elements
2. Try expressions like `element.getText()`, `element.getClass().getSimpleName()`, or `PsiTreeUtil.getParentOfType(element, PsiMethod.class)`
3. Use the debugger's "Watches" window to monitor PSI elements as you step through the code

### Logging for Debugging

Add logging statements to help debug issues:

```java
import com.intellij.openapi.diagnostic.Logger;

// In your class
private static final Logger LOG = Logger.getInstance(YourClass.class);

// Usage examples
LOG.info("Processing element: " + element.getText());
LOG.debug("Debug information: " + someValue);
LOG.error("Error occurred", exception);
```

View logs in the development instance at Help > Show Log in Explorer/Finder.

## Testing Strategies

### Manual Testing

1. Test with different types of code selections:
   - Methods with various signatures
   - Classes with different modifiers and inheritance
   - Different statement types (if, for, while, etc.)
   - Expressions and literals
2. Test with edge cases:
   - Empty selection
   - Very large selections
   - Selections that span multiple elements
   - Incomplete code or code with errors

### Automated Testing

The project uses JUnit 5 for testing. To run tests:

1. From IntelliJ IDEA: Right-click on the test directory and select "Run Tests"
2. From Gradle: Run `./gradlew test`

### Writing Tests

Create tests in the `src/test/java` directory. Example test structure:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodeAnalyzerServiceTest {
    @Test
    void testAnalyzeMethod() {
        // Setup test data
        // Call the method
        // Assert results
    }
}
```

For testing IntelliJ Platform components, use the `com.intellij.testFramework` classes:

```java
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class ExplainSelectedCodeActionTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Test setup
    }

    public void testActionPerformed() {
        // Test code
    }
}
```

## Troubleshooting

### Common Issues and Solutions

1. **Gradle Sync Failed**
   - Check your JDK version (should be 11 or later)
   - Ensure Gradle is using the correct JVM
   - Try refreshing Gradle dependencies

2. **Plugin Not Appearing in Development Instance**
   - Check the plugin.xml for correct configuration
   - Ensure the plugin is being built correctly
   - Look for errors in the build output

3. **PSI-related Exceptions**
   - PSI operations must be performed in a read action
   - Ensure you're handling null values properly
   - Check if you're working with valid PSI elements

4. **UI Issues**
   - Make sure you're creating UI components on the EDT (Event Dispatch Thread)
   - Use IntelliJ's UI components (JB* classes) for better integration

### Getting Help

- Check the [IntelliJ Platform SDK Documentation](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- Search the [IntelliJ Platform Plugin SDK Forum](https://intellij-support.jetbrains.com/hc/en-us/community/topics/200366979-IntelliJ-IDEA-Open-API-and-Plugin-Development)
- Look for similar issues on [GitHub](https://github.com/JetBrains/intellij-community)

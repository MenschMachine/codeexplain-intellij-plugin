# Best Practices for IntelliJ Plugin Development

This guide outlines best practices for developing plugins for IntelliJ-based IDEs. Following these guidelines will help you create high-quality, maintainable, and user-friendly plugins.

> For setup, building, running, debugging, and troubleshooting information, see the [main documentation](README.md).

## Table of Contents
- [Architecture and Design](#architecture-and-design)
- [Working with PSI](#working-with-psi)
- [UI/UX Guidelines](#uiux-guidelines)
- [Performance Optimization](#performance-optimization)
- [Testing Strategies](#testing-strategies)
- [Deployment and Distribution](#deployment-and-distribution)
- [Compatibility Considerations](#compatibility-considerations)

## Architecture and Design

### Component Organization
- **Follow the MVC pattern**: Separate your plugin into model (data), view (UI), and controller (actions) components.
- **Use services for shared functionality**: Register services using the IntelliJ Platform's service framework for components that need to be shared across the plugin.

In your plugin.xml, register services using the extensions tag with applicationService or projectService elements. For example:

    <extensions defaultExtensionNs="com.intellij">
        <applicationService serviceImplementation="com.example.MyApplicationService"/>
        <projectService serviceImplementation="com.example.MyProjectService"/>
    </extensions>

To retrieve these services in your code, use:

    MyApplicationService service = ApplicationManager.getApplication().getService(MyApplicationService.class);
    MyProjectService projectService = project.getService(MyProjectService.class);

### Dependency Injection
- **Avoid direct instantiation**: Instead of creating service instances directly (e.g., `new MyService()`), retrieve them from the platform.
- **Use constructor injection**: Pass dependencies through constructors rather than creating them inside methods.

Good practice:
- Initialize services in the constructor of your action or component
- Store them as final fields
- Use them in methods as needed

Avoid:
- Creating new instances of services inside methods
- Using static methods instead of instance methods on services

### Extension Points
- **Use extension points for extensibility**: Allow other plugins to extend your plugin's functionality by defining extension points.
- **Implement existing extension points**: Extend the IDE's functionality by implementing existing extension points.

## Working with PSI

### PSI Operations
- **Use read actions**: Wrap PSI read operations in read actions to ensure thread safety.

When reading PSI elements, use:

    ApplicationManager.getApplication().runReadAction(() -> {
        // PSI read operations here
    });

- **Use write actions**: Wrap PSI modifications in write actions.

When modifying PSI elements, use:

    ApplicationManager.getApplication().runWriteAction(() -> {
        // PSI write operations here
    });

### PSI Navigation
- **Use PsiTreeUtil**: Leverage `PsiTreeUtil` for navigating the PSI tree efficiently.

For example, to find a containing method:

    PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

- **Check for null**: Always check for null when working with PSI elements, as they might not exist in all contexts.

### PSI Performance
- **Cache PSI results**: Avoid repeated PSI tree traversals by caching results when appropriate.
- **Use light elements**: For large-scale operations, consider using light PSI elements which are more memory-efficient.

## UI/UX Guidelines

### UI Components
- **Use IntelliJ UI components**: Use JetBrains UI components (JB* classes) instead of standard Swing components for better integration.

For example, use JBPanel instead of JPanel, JBLabel instead of JLabel, etc.

- **Follow IntelliJ UI guidelines**: Maintain consistent spacing, font sizes, and colors with the rest of the IDE.

### Dialog Design
- **Extend DialogWrapper**: Use `DialogWrapper` as the base class for dialogs to ensure consistent behavior.
- **Provide context-sensitive help**: Include help buttons that link to documentation when appropriate.

### Notifications
- **Use the notification system**: Display messages using IntelliJ's notification system instead of modal dialogs when possible.

Use NotificationGroupManager to create and show notifications:

    NotificationGroupManager.getInstance()
        .getNotificationGroup("MyPlugin.Notifications")
        .createNotification("Message", NotificationType.INFORMATION)
        .notify(project);

### Progress Indication
- **Show progress for long operations**: Use `ProgressManager` for operations that might take time.

Use Task.Backgroundable for background tasks with progress indication:

    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Processing", true) {
        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            indicator.setText("Initializing...");
            // Long-running operation here
        }
    });

## Performance Optimization

### Background Processing
- **Run heavy operations in the background**: Avoid freezing the UI by running intensive operations in background threads.
- **Use smart caching**: Cache results of expensive computations, but invalidate the cache when necessary.

### Memory Management
- **Avoid memory leaks**: Be careful with listeners and references to project components.
- **Dispose resources properly**: Implement the `Disposable` interface and clean up resources in the `dispose()` method.

For example, disconnect message bus connections in the dispose method:

    public class MyComponent implements Disposable {
        private final MessageBusConnection connection;

        public MyComponent(Project project) {
            connection = project.getMessageBus().connect();
            connection.subscribe(TopicName.TOPIC, this::handleEvent);
        }

        @Override
        public void dispose() {
            connection.disconnect();
        }
    }

### Startup Performance
- **Lazy initialization**: Initialize components only when they're needed, not at startup.
- **Use lightweight services**: Mark services as lightweight when they don't need to be created at application startup.

## Testing Strategies

### Unit Testing
- **Test PSI operations**: Use `LightCodeInsightFixtureTestCase` for testing PSI-related functionality.
- **Mock dependencies**: Use mocking frameworks like Mockito to isolate the code being tested.

### UI Testing
- **Test UI components**: Use `GuiTestCase` for testing UI components.
- **Automate common scenarios**: Create tests for common user workflows.

### Test Data
- **Use test data files**: Store test inputs and expected outputs in the `testData` directory.
- **Create test fixtures**: Reuse test setup code across multiple tests.

## Deployment and Distribution

### Plugin Configuration
- **Provide comprehensive plugin.xml**: Include all necessary information in your plugin.xml file.
- **Version carefully**: Follow semantic versioning and update the version number in plugin.xml for each release.

### Documentation
- **Write clear documentation**: Provide comprehensive documentation for your plugin, including installation, usage, and troubleshooting.
- **Include screenshots**: Visual aids help users understand how to use your plugin.

### Distribution
- **Publish to JetBrains Marketplace**: Make your plugin available through the official JetBrains Marketplace.
- **Set up CI/CD**: Automate building, testing, and releasing your plugin.

## Compatibility Considerations

### API Compatibility
- **Use @ApiStatus annotations**: Mark APIs with appropriate stability annotations.
- **Check API compatibility**: Use the Plugin Verifier to ensure compatibility with different IDE versions.

### Platform Versions
- **Specify compatibility range**: Define the range of IDE versions your plugin supports in plugin.xml.

For example:

    <idea-plugin>
        <idea-version since-build="203" until-build="231.*"/>
    </idea-plugin>

- **Use API version checks**: Add runtime checks for features that are only available in certain IDE versions.

For example:

    if (PlatformUtils.isIdeaUltimate()) {
        // Use Ultimate-only APIs
    }

### Language Support
- **Handle multiple languages**: If your plugin works with code, consider supporting multiple languages.
- **Use language-specific extensions**: Register extensions for specific languages when appropriate.

For example:

    <extensions defaultExtensionNs="com.intellij">
        <lang.inspectionSuppressor language="JAVA" implementationClass="com.example.MySuppressor"/>
    </extensions>

---

By following these best practices, you'll create plugins that integrate seamlessly with the IntelliJ Platform, provide a great user experience, and maintain compatibility across IDE versions.

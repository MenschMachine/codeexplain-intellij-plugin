# Release Process

This document outlines the process for creating new releases of the ExplainCode IntelliJ plugin.

## Automated Release Process

We use GitHub Actions to automate the release process. The workflow handles:

1. Automatic version bumping in build.gradle (increments patch version)
2. Building the plugin
3. Creating a GitHub release
4. Attaching the built plugin to the release
5. Optionally publishing to the JetBrains Marketplace (for non-prerelease versions)

### Automatic Releases

A new release is automatically created whenever code is pushed to the main branch. The workflow will:

1. Extract the current version from build.gradle
2. Increment the patch version (e.g., 1.0.0 → 1.0.1)
3. Update build.gradle with the new version
4. Build the plugin
5. Create a GitHub release with the new version
6. Publish to JetBrains Marketplace if configured

### Manual Release Trigger

You can also manually trigger a release:

1. Go to the GitHub repository
2. Navigate to the "Actions" tab
3. Select the "Release Plugin" workflow
4. Click "Run workflow"
5. Select whether this is a pre-release
6. Click "Run workflow" to start the process

### Version Numbering

We follow semantic versioning (SemVer) for our releases:

- **Major version** (X.0.0): Significant changes, potentially breaking compatibility
- **Minor version** (0.X.0): New features, maintaining backward compatibility
- **Patch version** (0.0.X): Bug fixes and minor improvements

### Pre-releases

Pre-releases (alpha, beta, release candidates) should be marked with appropriate suffixes:

- Alpha: `1.0.0-alpha.1`
- Beta: `1.0.0-beta.1`
- Release Candidate: `1.0.0-rc.1`

When manually triggering a release, check the "Is this a pre-release?" option to create a pre-release with a -SNAPSHOT suffix.

### JetBrains Marketplace Publishing

For non-prerelease versions, the workflow will attempt to publish the plugin to the JetBrains Marketplace if the `INTELLIJ_MARKETPLACE_TOKEN` secret is configured in the repository.

To set up marketplace publishing:

1. Generate a permanent token in your JetBrains Marketplace profile
2. Add the token as a repository secret named `INTELLIJ_MARKETPLACE_TOKEN`

## Manual Release Process (Fallback)

If you need to create a release manually:

1. Update the version in `build.gradle`
2. Build the plugin: `./gradlew buildPlugin`
3. Create a new release on GitHub and upload the ZIP file from `build/distributions/`
4. Publish to JetBrains Marketplace: `./gradlew publishPlugin` (requires `INTELLIJ_MARKETPLACE_TOKEN` environment variable)

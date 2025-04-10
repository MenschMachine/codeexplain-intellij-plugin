#!/bin/bash
# Script to perform a release by bumping the version and building the plugin
# Usage: ./release.sh [major|minor|patch] [--snapshot]

# Make scripts executable
chmod +x get-current-version.sh
chmod +x bump-version.sh

# Get the current version
CURRENT_VERSION=$(./get-current-version.sh)
echo "Current version: $CURRENT_VERSION"

# Bump the version
BUMP_TYPE=${1:-"patch"}
SNAPSHOT_FLAG=$2
NEW_VERSION=$(./bump-version.sh build.gradle "$BUMP_TYPE" "$SNAPSHOT_FLAG")
echo "New version: $NEW_VERSION"

# Build the plugin
echo "Building plugin..."
./gradlew buildPlugin

# Create a tag
echo "Creating git tag v$NEW_VERSION..."
git tag -a "v$NEW_VERSION" -m "Release v$NEW_VERSION"

echo "Release process completed!"
echo "To push the tag: git push origin v$NEW_VERSION"
echo "To create a GitHub release, use: gh release create v$NEW_VERSION build/distributions/*.zip --title \"Release v$NEW_VERSION\""

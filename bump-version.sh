#!/bin/bash
# Script to bump the version number in build.gradle
# Usage: ./bump-version.sh [path/to/build.gradle] [major|minor|patch] [--snapshot] [--help]

# Parse arguments
BUILD_GRADLE="build.gradle"
BUMP_TYPE="patch"
SNAPSHOT=false

# Display help information if --help is present anywhere in the arguments
for arg in "$@"; do
  if [[ "$arg" == "--help" ]]; then
    echo "Usage: ./bump-version.sh [path/to/build.gradle] [major|minor|patch] [--snapshot] [--help]"
    echo ""
    echo "Options:"
    echo "  path/to/build.gradle  Path to the build.gradle file (default: ./build.gradle)"
    echo "  major|minor|patch     Version component to bump (default: patch)"
    echo "  --snapshot            Add -SNAPSHOT suffix to the new version"
    echo "  --help                Display this help message"
    echo ""
    echo "Examples:"
    echo "  ./bump-version.sh                     # Bump patch version in ./build.gradle"
    echo "  ./bump-version.sh minor              # Bump minor version in ./build.gradle"
    echo "  ./bump-version.sh build.gradle major # Bump major version in specified file"
    echo "  ./bump-version.sh --snapshot         # Bump patch version and add -SNAPSHOT suffix"
    exit 0
  fi
done

# Process arguments
for arg in "$@"; do
  if [[ "$arg" == "--snapshot" ]]; then
    SNAPSHOT=true
  elif [[ "$arg" == "major" || "$arg" == "minor" || "$arg" == "patch" ]]; then
    BUMP_TYPE="$arg"
  elif [[ "$arg" != "--help" && ! "$arg" =~ ^-- ]]; then
    # If it's not a flag and not a bump type, assume it's the build.gradle path
    if [[ -f "$arg" ]]; then
      BUILD_GRADLE="$arg"
    else
      echo "Warning: '$arg' is not a valid file. Using default build.gradle."
    fi
  fi
done

if [ ! -f "$BUILD_GRADLE" ]; then
  echo "Error: $BUILD_GRADLE not found" >&2
  exit 1
fi

# Get the current version
CURRENT_VERSION=$(./get-current-version.sh "$BUILD_GRADLE")
echo "Current version: $CURRENT_VERSION"

# Remove -SNAPSHOT suffix if present
BASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
echo "Base version: $BASE_VERSION"

# Check if version follows semver pattern
if [[ ! $BASE_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Warning: Current version doesn't follow semantic versioning pattern (X.Y.Z). Using 0.0.1 as base."
  BASE_VERSION="0.0.1"
fi

# Split version into components
IFS='.' read -r -a VERSION_PARTS <<< "$BASE_VERSION"
MAJOR=${VERSION_PARTS[0]:-0}
MINOR=${VERSION_PARTS[1]:-0}
PATCH=${VERSION_PARTS[2]:-0}

echo "Parsed version: Major=$MAJOR, Minor=$MINOR, Patch=$PATCH"

# Bump the version according to the specified type
case "$BUMP_TYPE" in
  major)
    NEW_MAJOR=$((MAJOR + 1))
    NEW_VERSION="$NEW_MAJOR.0.0"
    ;;
  minor)
    NEW_MINOR=$((MINOR + 1))
    NEW_VERSION="$MAJOR.$NEW_MINOR.0"
    ;;
  patch|*)
    NEW_PATCH=$((PATCH + 1))
    NEW_VERSION="$MAJOR.$MINOR.$NEW_PATCH"
    ;;
esac

# Add -SNAPSHOT suffix if requested
if [ "$SNAPSHOT" = true ]; then
  NEW_VERSION="${NEW_VERSION}-SNAPSHOT"
fi

echo "New version: $NEW_VERSION"

# Update the version in build.gradle
# First, try to find the group line and update the version on the next line
GROUP_LINE_NUM=$(grep -n "group 'xyz.codeexplain'" "$BUILD_GRADLE" | cut -d ':' -f1)

if [ -n "$GROUP_LINE_NUM" ]; then
  echo "Found 'group 'xyz.codeexplain'' at line $GROUP_LINE_NUM"
  # Get the next line after the group line
  NEXT_LINE_NUM=$((GROUP_LINE_NUM + 1))
  VERSION_LINE=$(sed -n "${NEXT_LINE_NUM}p" "$BUILD_GRADLE")
  echo "Version line: $VERSION_LINE"

  # Check if the next line contains a version declaration
  if [[ $VERSION_LINE =~ version ]]; then
    echo "Updating version at line $NEXT_LINE_NUM"

    # Create a temporary file
    TEMP_FILE=$(mktemp)

    # Process the file line by line
    LINE_NUM=1
    while IFS= read -r LINE || [ -n "$LINE" ]; do
      if [ $LINE_NUM -eq $NEXT_LINE_NUM ]; then
        # Replace the version line
        if [[ $LINE =~ "'" ]]; then
          # Version is defined with single quotes
          NEW_LINE=$(echo "$LINE" | sed "s/version '[^']*'/version '$NEW_VERSION'/")
        else
          # Version is defined with double quotes
          NEW_LINE=$(echo "$LINE" | sed "s/version \"[^\"]*\"/version \"$NEW_VERSION\"/")
        fi
        echo "$NEW_LINE" >> "$TEMP_FILE"
      else
        # Keep the line unchanged
        echo "$LINE" >> "$TEMP_FILE"
      fi
      LINE_NUM=$((LINE_NUM + 1))
    done < "$BUILD_GRADLE"

    # Ensure the file ends with a newline
    if [ "$(tail -c 1 "$TEMP_FILE" | wc -l)" -eq 0 ]; then
      echo "" >> "$TEMP_FILE"
    fi

    # Replace the original file with the modified one
    mv "$TEMP_FILE" "$BUILD_GRADLE"

    echo "Successfully updated version at line $NEXT_LINE_NUM"
  else
    echo "Warning: Line after 'group 'xyz.codeexplain'' does not contain a version declaration" >&2
    echo "Falling back to global search and replace" >&2
    # Fall back to the global search and replace
    if grep -q "version '[^']*'" "$BUILD_GRADLE"; then
      # Version is defined with single quotes
      sed -i.bak "s/version '[^']*'/version '$NEW_VERSION'/" "$BUILD_GRADLE"
    elif grep -q 'version "[^"]*"' "$BUILD_GRADLE"; then
      # Version is defined with double quotes
      sed -i.bak "s/version \"[^\"]*\"/version \"$NEW_VERSION\"/" "$BUILD_GRADLE"
    else
      echo "Error: Could not find version pattern in $BUILD_GRADLE" >&2
      exit 1
    fi

    # Remove backup file
    rm -f "${BUILD_GRADLE}.bak"
  fi
else
  echo "Warning: Could not find 'group 'xyz.codeexplain'' in $BUILD_GRADLE" >&2
  echo "Falling back to global search and replace" >&2
  # Fall back to the global search and replace
  if grep -q "version '[^']*'" "$BUILD_GRADLE"; then
    # Version is defined with single quotes
    sed -i.bak "s/version '[^']*'/version '$NEW_VERSION'/" "$BUILD_GRADLE"
  elif grep -q 'version "[^"]*"' "$BUILD_GRADLE"; then
    # Version is defined with double quotes
    sed -i.bak "s/version \"[^\"]*\"/version \"$NEW_VERSION\"/" "$BUILD_GRADLE"
  else
    echo "Error: Could not find version pattern in $BUILD_GRADLE" >&2
    exit 1
  fi

  # Remove backup file
  rm -f "${BUILD_GRADLE}.bak"
fi

echo "Updated $BUILD_GRADLE with new version: $NEW_VERSION"

# Commit the version change
git commit -m "build: Bump version to $NEW_VERSION" "$BUILD_GRADLE"

# Output the new version (for use in scripts)
echo "$NEW_VERSION"

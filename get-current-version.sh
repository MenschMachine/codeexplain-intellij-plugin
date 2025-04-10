#!/bin/bash
# Script to extract the current version from build.gradle
# Usage: ./get-current-version.sh [path/to/build.gradle]

# Set the build.gradle path (default to build.gradle in current directory)
BUILD_GRADLE=${1:-"build.gradle"}

if [ ! -f "$BUILD_GRADLE" ]; then
  echo "Error: $BUILD_GRADLE not found" >&2
  exit 1
fi

# Debug: Print the build.gradle content
echo "Analyzing $BUILD_GRADLE..." >&2

# Try to find the group line and the version on the next line
GROUP_LINE_NUM=$(grep -n "group 'xyz.codeexplain'" "$BUILD_GRADLE" | cut -d ':' -f1)

if [ -n "$GROUP_LINE_NUM" ]; then
  # Get the next line after the group line
  NEXT_LINE_NUM=$((GROUP_LINE_NUM + 1))
  VERSION_LINE=$(sed -n "${NEXT_LINE_NUM}p" "$BUILD_GRADLE")
  echo "Found version line: $VERSION_LINE" >&2
  
  # Extract version from the line, handling potential whitespace
  if [[ $VERSION_LINE =~ [[:space:]]*version[[:space:]]*\'([^\']*)\'|[[:space:]]*version[[:space:]]*\"([^\"]*)\" ]]; then
    if [ -n "${BASH_REMATCH[1]}" ]; then
      CURRENT_VERSION="${BASH_REMATCH[1]}"
    else
      CURRENT_VERSION="${BASH_REMATCH[2]}"
    fi
  else
    # Try a simpler approach with grep
    if [[ $VERSION_LINE =~ version ]]; then
      if [[ $VERSION_LINE =~ "'" ]]; then
        CURRENT_VERSION=$(echo "$VERSION_LINE" | grep -oP "version\s*'\K[^']+")
      else
        CURRENT_VERSION=$(echo "$VERSION_LINE" | grep -oP 'version\s*"\K[^"]+')  
      fi
    fi
    
    if [ -z "$CURRENT_VERSION" ]; then
      echo "Could not parse version from line: $VERSION_LINE" >&2
      # Fall back to searching the whole file
      echo "Falling back to whole file search..." >&2
    fi
  fi
fi

# If we couldn't find the version after the group line, try searching the whole file
if [ -z "$CURRENT_VERSION" ]; then
  echo "Searching entire file for version..." >&2
  if grep -q "version '[^']*'" "$BUILD_GRADLE"; then
    CURRENT_VERSION=$(grep -oP "version '\K[^']+" "$BUILD_GRADLE")
  elif grep -q 'version "[^"]*"' "$BUILD_GRADLE"; then
    CURRENT_VERSION=$(grep -oP 'version "\K[^"]+' "$BUILD_GRADLE")
  else
    echo "Could not find version in $BUILD_GRADLE, defaulting to 0.0.1" >&2
    CURRENT_VERSION="0.0.1"
  fi
fi

echo "Current version: $CURRENT_VERSION" >&2

# Output only the version number (for use in scripts)
echo "$CURRENT_VERSION"

#!/bin/bash

# Exit on error
set -e

# Check if a tag was provided as a parameter
if [ -n "$1" ]; then
    LATEST_TAG="$1"
    echo "Using provided tag: $LATEST_TAG"
else
    # Get the latest tag that starts with 'v' followed by a number
    LATEST_TAG=$(git tag -l 'v[0-9]*' --sort=-v:refname | head -n 1)

    if [ -z "$LATEST_TAG" ]; then
        echo "No version tags found starting with 'v' followed by a number"
        exit 1
    fi

    echo "Latest tag: $LATEST_TAG"
fi

# Generate changelog between latest tag and HEAD
# Filter out commit messages starting with chore:, build:, docs:, or refactor:
CHANGELOG=$(git log --pretty=format:"%s (%h)" $LATEST_TAG..HEAD | grep -v -E "^(chore|build|docs|refactor):" | grep -v -E "^Merge " | sed 's/^/- /')

if [ -z "$CHANGELOG" ]; then
    echo "No significant changes found between $LATEST_TAG and HEAD"
    echo "Consider adding a meaningful commit message that doesn't start with chore:, build:, docs:, or refactor:"
    exit 0
fi

echo "Generated changelog (filtered):"
echo "$CHANGELOG"

# Create a backup of the original file
cp build.gradle build.gradle.bak

# Use perl to replace the changeNotes section
# This handles multiline replacements better than awk
perl -i -0pe 's/(changeNotes = """).*?(""")/$1\n      Changes since '"$LATEST_TAG"':\n'"$(echo "$CHANGELOG" | sed 's/\//\\\//g')"'\n    $2/s' build.gradle

echo "Changelog updated in build.gradle"

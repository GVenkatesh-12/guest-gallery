#!/bin/bash
# Script to download Inter font files for the Guest Gallery project
# Run this script from the project root directory

FONT_DIR="core/src/main/res/font"
mkdir -p "$FONT_DIR"

INTER_VERSION="4.1"
BASE_URL="https://github.com/rsms/inter/releases/download/v${INTER_VERSION}"

echo "Downloading Inter font files..."

# Download the Inter font package
curl -sL "${BASE_URL}/Inter-${INTER_VERSION}.zip" -o /tmp/inter.zip

# Extract specific weights
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/InterVariable.ttf" -d /tmp/inter/ 2>/dev/null || true
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/extras/ttf/Inter-Regular.ttf" -d /tmp/inter/ 2>/dev/null || true
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/extras/ttf/Inter-Medium.ttf" -d /tmp/inter/ 2>/dev/null || true
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/extras/ttf/Inter-SemiBold.ttf" -d /tmp/inter/ 2>/dev/null || true
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/extras/ttf/Inter-Bold.ttf" -d /tmp/inter/ 2>/dev/null || true
unzip -jo /tmp/inter.zip "Inter-${INTER_VERSION}/extras/ttf/Inter-Light.ttf" -d /tmp/inter/ 2>/dev/null || true

# Copy and rename to Android-compatible names (lowercase, underscores)
cp /tmp/inter/Inter-Regular.ttf "$FONT_DIR/inter_regular.ttf" 2>/dev/null || echo "Warning: Inter-Regular.ttf not found"
cp /tmp/inter/Inter-Medium.ttf "$FONT_DIR/inter_medium.ttf" 2>/dev/null || echo "Warning: Inter-Medium.ttf not found"
cp /tmp/inter/Inter-SemiBold.ttf "$FONT_DIR/inter_semibold.ttf" 2>/dev/null || echo "Warning: Inter-SemiBold.ttf not found"
cp /tmp/inter/Inter-Bold.ttf "$FONT_DIR/inter_bold.ttf" 2>/dev/null || echo "Warning: Inter-Bold.ttf not found"
cp /tmp/inter/Inter-Light.ttf "$FONT_DIR/inter_light.ttf" 2>/dev/null || echo "Warning: Inter-Light.ttf not found"

# Cleanup
rm -rf /tmp/inter /tmp/inter.zip

echo "Done! Font files downloaded to $FONT_DIR"
ls -la "$FONT_DIR"

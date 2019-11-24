#!/usr/bin/env bash

echo "Building ObjectiveRocks macOS ..."
cd "ObjectiveRocks" || exit
OUTPUT=$(/usr/bin/xcodebuild build -scheme objectiveRocks-macOS-lib -configuration Release -derivedDataPath ../xcodeBuild)
RESULT=$(echo "$OUTPUT" | grep "\\*\\* BUILD ")
if [ "$RESULT" != "** BUILD SUCCEEDED **" ]
then
  echo "$OUTPUT"
  exit 1
fi
echo "${RESULT}"

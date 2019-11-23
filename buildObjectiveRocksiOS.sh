#!/usr/bin/env bash

echo "Building ObjectiveRocks iOS ..."
cd "ObjectiveRocks" || exit
OUTPUT=$(/usr/bin/xcodebuild build -scheme objectiveRocks-iOS-lib -configuration Release -derivedDataPath ../xcodeBuild)
RESULT=$(echo "$OUTPUT" | grep "\\*\\* BUILD ")
if [ "$RESULT" != "** BUILD SUCCEEDED **" ]
then
  echo "$OUTPUT"
  exit 1
fi
echo "${RESULT}"

#!/usr/bin/env bash

SDK=$1

echo "Building ObjectiveRocks iOS for $SDK ..."
cd "ObjectiveRocks" || exit
OUTPUT=$(/usr/bin/xcodebuild build -scheme objectiveRocks-iOS-lib -destination generic/platform=iOS -configuration Release -sdk "$SDK" -derivedDataPath ../xcodeBuild)
RESULT=$(echo "$OUTPUT" | grep "\\*\\* BUILD ")
if [ "$RESULT" != "** BUILD SUCCEEDED **" ]
then
  echo "$OUTPUT"
  exit 1
fi
echo "${RESULT}"

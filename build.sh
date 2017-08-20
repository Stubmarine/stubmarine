#!/usr/bin/env bash

# 1. Build front end
pushd frontend
yarn build
popd
cp frontend/dist/* backend/src/main/resources/static

# 2. Build back end
pushd backend
./gradlew clean build jar
popd

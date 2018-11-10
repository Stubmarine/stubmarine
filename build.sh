#!/usr/bin/env sh

pushd frontend
./build.sh
popd

pushd backend
./build.sh
popd


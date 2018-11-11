#!/usr/bin/env sh
set -e

rm -rf dist
mkdir dist

cp static/* dist/

elm make --optimize src/Main.elm --output dist/app.js

pushd ..
dotnet run --project build
popd

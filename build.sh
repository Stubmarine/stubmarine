#!/usr/bin/env sh

pushd frontend
./build.sh
popd

rm -rf backend/static
cp -R frontend/dist backend/static

pushd backend
./build.sh
rm -rf bin/Release/netcoreapp2.1/publish/static
mkdir bin/Release/netcoreapp2.1/publish/static
popd

cp -R frontend/dist/* backend/bin/Release/netcoreapp2.1/publish/static


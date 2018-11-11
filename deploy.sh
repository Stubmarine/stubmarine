#!/usr/bin/env sh
set -e

cf push stubmarine -m 256M -p backend/bin/Release/netcoreapp2.1/publish

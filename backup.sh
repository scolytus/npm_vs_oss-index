#!/bin/bash

set -e
set -x

TIMESTAMP="$(date +%Y%m%d-%H%M%S)"

mkdir -p "./backup/${TIMESTAMP}"

cp *.json "./backup/${TIMESTAMP}"

cd "./backup/${TIMESTAMP}"

gzip -9 *


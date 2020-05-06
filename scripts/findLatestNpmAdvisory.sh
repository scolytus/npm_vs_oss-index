#!/bin/bash

# set -x
# set -e

# this is more an experiment than an actually useful script...
# just look at https://www.npmjs.com/advisories?page=0&perPage=2000 and find the highest number on top...

for i in $(seq 1700 -1 1500); do
    if curl -s -f "https://www.npmjs.com/advisories/$i/versions" > /dev/null; then
        echo "$i FOUND!"
        break
    fi
done

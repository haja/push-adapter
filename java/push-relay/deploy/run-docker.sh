#!/bin/bash

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DOCKERFILE="$BASE/docker-compose.yml"

docker-compose --file "$DOCKERFILE" up


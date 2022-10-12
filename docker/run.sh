#!/usr/bin/env bash

export CLIENT_SERVICE_VERSION=client-service:latest
export PORT=8001
export APP_NAME=client-service
export ADDRESS=localhost
export ORDERING_ADDRESS=localhost
export ORDERING_PORT=8002

function noArgumentSupplied() {

    echo ""
    echo "========================================================================="
    echo ""

    exit 1
}

args=("$@")

echo "========================================================================="
echo ""
echo "  Client Service Docker"
echo ""
echo "  Number of arguments: $#"

if [[ $# -eq 0 ]] ; then
    echo '  No arguments supplied'
    noArgumentSupplied
    exit 1
fi

if [[ -z ${args[0]} ]] ; then
    echo '  no example instance name supplied'
    noArgumentSupplied
    exit 1
fi

export CLIENT_SERVICE_INSTANCE_NAME=${args[0]}

echo "  Client service instance name: ${CLIENT_SERVICE_INSTANCE_NAME}"
echo ""
echo "========================================================================="
echo ""

docker run -it --name ${CLIENT_SERVICE_INSTANCE_NAME} --net host --log-driver none \
-e ADDRESS=${ADDRESS} \
-e ORDERING_ADDRESS=${ORDERING_ADDRESS} \
-e ORDERING_PORT=${ORDERING_PORT} \
-e PORT=${PORT} \
-e APP_NAME=${APP_NAME} \
-e JAVA_OPTS="${JAVA_OPTS}" \
--rm ${CLIENT_SERVICE_VERSION}
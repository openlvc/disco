#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DISCO_HOME=$DIR/..
java -jar $DISCO_HOME/lib/disco.jar --app:distributor $*


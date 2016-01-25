#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LM_HOME=$DIR/..
java -jar $LM_HOME/lib/loadmaster.jar $*


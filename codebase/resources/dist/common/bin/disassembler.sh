#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DISCO_HOME=$DIR/..
#PATH=$DISCO_HOME/jre/bin:$PATH
java -jar $DISCO_HOME/lib/disco.jar --app:disassembler $*


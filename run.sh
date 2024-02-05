#!/bin/sh
java \
-javaagent:/newrelic/newrelic.jar $JVM_OPTS \
-jar /app/frontservice.jar
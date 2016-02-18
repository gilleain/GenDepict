#!/bin/sh
CDK_DIR=../../chemistry/cdk
CLASSPATH=bin:lib/*
for p in ${CDK_DIR}/*/*/target/classes; do
	CLASSPATH="${CLASSPATH}:${p}"
done
CLASSPATH=${DIST}/*:$CLASSPATH:/Users/maclean/.m2/repository/java3d/vecmath/1.3.1/vecmath-1.3.1.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-core/0.8/beam-core-0.8.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/uk/ac/ebi/beam/beam-func/0.8/beam-func-0.8.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository//com/github/gilleain/signatures/signatures/1.1/signatures-1.1.jar
CLASSPATH=$CLASSPATH:/Users/maclean/development/chemistry/moleculegen/target/classes
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar
CLASSPATH=$CLASSPATH:/Users/maclean/.m2/repository/com/google/guava/guava/17.0/guava-17.0.jar
java -cp ${CLASSPATH} app.DuplicateChecker $*

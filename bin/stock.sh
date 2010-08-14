#!/bin/sh
CLASSPATH=/work/sltu/scala-remote-actors-perf-tests/scala28/target/test-classes:/work/sltu/scala-remote-actors-perf-tests/scala28/target/classes:/work/sltu/repository/org/scala-lang/scala-library/2.8.0/scala-library-2.8.0.jar:/work/sltu/repository/org/scala-lang/scala-compiler/2.8.0/scala-compiler-2.8.0.jar:/work/sltu/repository/com/googlecode/remote-actors-perf-tests-common/1.0-SNAPSHOT/remote-actors-perf-tests-common-1.0-SNAPSHOT.jar:/work/sltu/repository/com/googlecode/avro-scala-compiler-plugin/1.1-SNAPSHOT/avro-scala-compiler-plugin-1.1-SNAPSHOT.jar:/work/sltu/repository/org/apache/hadoop/avro/1.3.3/avro-1.3.3.jar:/work/sltu/repository/org/codehaus/jackson/jackson-mapper-asl/1.4.2/jackson-mapper-asl-1.4.2.jar:/work/sltu/repository/org/codehaus/jackson/jackson-core-asl/1.4.2/jackson-core-asl-1.4.2.jar:/work/sltu/repository/org/slf4j/slf4j-api/1.5.11/slf4j-api-1.5.11.jar:/work/sltu/repository/com/thoughtworks/paranamer/paranamer/2.2/paranamer-2.2.jar:/work/sltu/repository/com/thoughtworks/paranamer/paranamer-ant/2.2/paranamer-ant-2.2.jar:/work/sltu/repository/com/thoughtworks/paranamer/paranamer-generator/2.2/paranamer-generator-2.2.jar:/work/sltu/repository/com/thoughtworks/qdox/qdox/1.10.1/qdox-1.10.1.jar:/work/sltu/repository/asm/asm/3.2/asm-3.2.jar:/work/sltu/repository/org/apache/ant/ant/1.7.1/ant-1.7.1.jar:/work/sltu/repository/org/apache/ant/ant-launcher/1.7.1/ant-launcher-1.7.1.jar:/work/sltu/repository/org/mortbay/jetty/jetty/6.1.22/jetty-6.1.22.jar:/work/sltu/repository/org/mortbay/jetty/jetty-util/6.1.22/jetty-util-6.1.22.jar:/work/sltu/repository/org/mortbay/jetty/servlet-api/2.5-20081211/servlet-api-2.5-20081211.jar:/work/sltu/repository/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:/work/sltu/repository/com/googlecode/remote-actors-util/1.0-SNAPSHOT/remote-actors-util-1.0-SNAPSHOT.jar:/work/sltu/repository/com/google/protobuf/protobuf-java/2.3.0/protobuf-java-2.3.0.jar:/work/sltu/repository/org/apache/commons/commons-math/2.1/commons-math-2.1.jar:/work/sltu/repository/org/scala-tools/maven-scala-plugin/2.14.1/maven-scala-plugin-2.14.1.jar 
JVMARGS="-server -Xms2048m -Xmx2048m -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -XX:+HeapDumpOnOutOfMemoryError"
/usr/lib/jvm/java-6-sun-1.6.0.12/jre/bin/java $JVMARGS -classpath $CLASSPATH perftest.stock.Test $@

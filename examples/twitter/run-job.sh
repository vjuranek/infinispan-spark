#!/bin/bash

set -e

if [ $# -lt 5 ]
  then
    echo "Usage:run-job.sh <MainClass> <twitter.consumerKey> <twitter.consumerSecret> <twitter.accessToken> <twitter.accessTokenSecret>"
    exit 0
fi

SPARK_VERSION=$(cat ../../project/Versions.scala | grep sparkVersion | awk '{print $4}' | sed 's/\"//g')
HADOOP_VERSION="2.6"

echo "Getting Spark..."

wget -nc "http://mirror.vorboss.net/apache/spark/spark-$SPARK_VERSION/spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz" && tar -xzf spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz

SPARK_HOME=spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION

echo "Obtaining Spark master"
SPARK_MASTER_NAME="twitter_sparkMaster_1"
INFINISPAN_NAME="twitter_infinispan1_1"

STATE=$(docker inspect --format="{{ .State.Running  }}" $SPARK_MASTER_NAME || exit 1;)
if [ "$STATE" == "false" ]
then
  echo "Docker containers not started, exiting..."
  exit 1
fi

SPARK_MASTER="$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' $SPARK_MASTER_NAME)"
INFINISPAN_MASTER="$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' $INFINISPAN_NAME)"

echo "Submitting the job"
$SPARK_HOME/bin/spark-submit  --class $1 --master spark://$SPARK_MASTER:7077 target/scala-2.10/infinispan-spark-twitter.jar $2 $3 $4 $5 $INFINISPAN_MASTER


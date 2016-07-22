package org.infinispan.spark.examples.twitter

import java.util.Properties

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.infinispan.spark.examples.twitter.Sample.{getSparkConf, runAndExit}
import org.infinispan.spark.stream._

/**
  * @author gustavonalle
  */
object StreamProducerScala {
   def main(args: Array[String]) {
      if (args.length < 5) {
         System.out.println("Usage: StreamProducerScala  <infinispan_host> <twitter4j.oauth.consumerKey> <twitter4j.oauth.consumerSecret> <twitter4j.oauth.accessToken> <twitter4j.oauth.accessTokenSecret>")
         System.exit(1)
      }

      Logger.getLogger("org").setLevel(Level.WARN)
      val infinispanHost = args(0)
      System.setProperty("twitter4j.oauth.consumerKey", args(1))
      System.setProperty("twitter4j.oauth.consumerSecret", args(2))
      System.setProperty("twitter4j.oauth.accessToken", args(3))
      System.setProperty("twitter4j.oauth.accessTokenSecret", args(4))
      val durationOptional: Long = if(args.length > 5) args(5).toLong * 1000 else -1

      val conf = getSparkConf("spark-infinispan-stream-producer-scala")
      val sparkContext = new SparkContext(conf)

      val streamingContext = new StreamingContext(sparkContext, Seconds(1))

      val infinispanProperties = new Properties
      infinispanProperties.put("infinispan.client.hotrod.server_list", infinispanHost)

      val twitterDStream = TwitterUtils.createStream(streamingContext, None)

      val keyValueTweetStream = twitterDStream.map {
         s => (s.getId, new Tweet(s.getId, s.getUser.getScreenName, Option(s.getPlace).map(_.getCountry).getOrElse("N/A"), s.getRetweetCount, s.getText))
      }

      keyValueTweetStream.writeToInfinispan(infinispanProperties)

      val infinispanStream = new InfinispanInputDStream[Long, Tweet](streamingContext, StorageLevel.MEMORY_ONLY, infinispanProperties)

      val countryDStream = infinispanStream.transform(rdd => rdd.collect { case (k, v, _) => (v.getCountry, 1) }.reduceByKey(_ + _))

      val lastMinuteDStream = countryDStream.reduceByKeyAndWindow(_ + _, Seconds(60))

      lastMinuteDStream.foreachRDD { (rdd, time) => println(s"--------- $time -------"); rdd.sortBy(_._2, ascending = false).collect().foreach(println) }

      runAndExit(streamingContext, durationOptional)

   }
}

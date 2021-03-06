package org.infinispan.spark.suites

import org.infinispan.spark.JavaStreamApiTest
import org.infinispan.spark.test.{JavaSparkStream, SingleServer}
import org.scalatest.{DoNotDiscover, FunSuite, Matchers}

/**
 * @author gustavonalle
 */
@DoNotDiscover
class JavaStreamApiSuite extends FunSuite with JavaSparkStream with SingleServer with Matchers {

   lazy val javaTest: JavaStreamApiTest = new JavaStreamApiTest

   test("Stream consumer from Java") {
      javaTest.testStreamConsumer(jssc, getConfiguration, getRemoteCache)
   }

   test("Stream producer from Java") {
      javaTest.testStreamProducer(jssc, getConfiguration, getRemoteCache)
   }
}

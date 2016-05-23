package org.infinispan.spark

import org.infinispan.spark.suites._
import org.infinispan.spark.test.Cluster
import org.scalatest.{BeforeAndAfterAll, Suites}

/**
  * @author vjuranek
  */
class CompatibilitySuites extends Suites(new RDDCompatibilitySuite, new StreamingCompatibilitySuite) with BeforeAndAfterAll {

   override protected def beforeAll(): Unit = {
      Cluster.start()
      super.beforeAll()
   }

   override protected def afterAll(): Unit = {
      Cluster.shutDown()
      super.afterAll()
   }
}

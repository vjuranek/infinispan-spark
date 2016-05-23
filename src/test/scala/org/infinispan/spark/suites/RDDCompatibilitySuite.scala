package org.infinispan.spark.suites

import org.infinispan.spark.test._
import org.jboss.dmr.scala.ModelNode
import org.scalatest.DoNotDiscover

/**
  * @author vjuranek
  */
@DoNotDiscover
class RDDCompatibilitySuite extends RDDRetrievalTest with WordCache with Spark with MultipleServers {
   override protected def getNumEntries: Int = 100

   override def getCacheType = CacheType.DISTRIBUTED

   override def getCacheConfig: Option[ModelNode] = Some(ModelNode(
         "compatibility" -> ModelNode(
            "COMPATIBILITY" -> ModelNode(
               "enabled" -> true
            )
         )
      )
   )
}
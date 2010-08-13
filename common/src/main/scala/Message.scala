package perftest
package common

import com.googlecode.avro.marker.AvroRecord

case class TestMessage(var round: Int, 
                       var created: Long,
                       var payload: Array[Byte]) extends AvroRecord

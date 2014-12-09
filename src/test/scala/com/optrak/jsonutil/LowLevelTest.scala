/*
package com.optrak.scalautil.jsonutil

import grizzled.slf4j.Logging
import JsonUtils._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.specs2.mutable._
import com.optrak.scalautil._
import ScalazBits._
import org.joda.time._
import scalaz.{Failure,Success}


class LowLevelTest extends Specification with Logging {

  implicit val lowLevelContext = HeadContext("Low level getters")

  "getString" in {
    getString("string",jBasic) mustEqual Success("2 and a half")
    getString("sint",jBasic \ "ints") mustEqual Success("2")

    getString("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
    getString("array",jBasic) must beAnInstanceOf[Failure[_]]
  }

  //getOptionalString just wraps optionalGetString into Success, so we are testing the latter
  "getOptionalString" in {
    getOptionalString("string",jBasic) mustEqual Success(Some("2 and a half"))
    getOptionalString("sint",jBasic \ "ints") mustEqual Success(Some("2"))

    getOptionalString("nonExistentField",jBasic) mustEqual Success(None)
    getOptionalString("array",jBasic) mustEqual Success(None)
  }

  "getInt" in {
    getInt("int",jBasic \ "ints") mustEqual Success(2)
    getInt("intf",jBasic \ "ints") mustEqual Success(2)
    getInt("sint",jBasic \ "ints") mustEqual Success(2)
    //getInt("sintf") mustEqual Success(2) My implementation of getInt seems to be an overkill, since
    //the original parser already pukes at "2.0"

    getInt("string",jBasic) must beAnInstanceOf[Failure[_]]
    getInt("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
    getInt("array",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalInt" in {
    getOptionalInt("int",jBasic \ "ints") mustEqual Success(Some(2))
    getOptionalInt("intf",jBasic \ "ints") mustEqual Success(Some(2))
    getOptionalInt("sint",jBasic \ "ints") mustEqual Success(Some(2))

    getOptionalInt("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalInt("nonExistentField",jBasic) mustEqual Success(None)
    getOptionalInt("array",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalInt("double",jBasic \ "doubles") must beAnInstanceOf[Failure[_]]
  }

  "getBoolean" in {
    getBoolean("one",jBasic \ "booleans") mustEqual Success(true)
    getBoolean("sone",jBasic \ "booleans") mustEqual Success(true)
    getBoolean("true",jBasic \ "booleans") mustEqual Success(true)
    getBoolean("strue",jBasic \ "booleans") mustEqual Success(true)
    getBoolean("syes",jBasic \ "booleans") mustEqual Success(true)

    getBoolean("zero",jBasic \ "booleans") mustEqual Success(false)
    getBoolean("sno",jBasic \ "booleans") mustEqual Success(false)
    getBoolean("false",jBasic \ "booleans") mustEqual Success(false)

    getBoolean("string",jBasic) must beAnInstanceOf[Failure[_]]
    getBoolean("int",jBasic \ "ints") must beAnInstanceOf[Failure[_]]
    getBoolean("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalBoolean" in {
    getOptionalBoolean("one",jBasic \ "booleans") mustEqual Success(Some(true))
    getOptionalBoolean("sone",jBasic \ "booleans") mustEqual Success(Some(true))
    getOptionalBoolean("true",jBasic \ "booleans") mustEqual Success(Some(true))
    getOptionalBoolean("strue",jBasic \ "booleans") mustEqual Success(Some(true))
    getOptionalBoolean("syes",jBasic \ "booleans") mustEqual Success(Some(true))

    getOptionalBoolean("zero",jBasic \ "booleans") mustEqual Success(Some(false))
    getOptionalBoolean("sno",jBasic \ "booleans") mustEqual Success(Some(false))
    getOptionalBoolean("false",jBasic \ "booleans") mustEqual Success(Some(false))

    getOptionalBoolean("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalBoolean("int",jBasic \ "ints") must beAnInstanceOf[Failure[_]]
    getOptionalBoolean("nonExistentField",jBasic) mustEqual Success(None)
  }

  "getLong" in {
    getLong("long",jBasic \ "longs") mustEqual Success(4444444444L)
    getLong("slong",jBasic \ "longs") mustEqual Success(40000000000L)
    getLong("int",jBasic \ "ints") mustEqual Success(2L)

    getLong("string",jBasic) must beAnInstanceOf[Failure[_]]
    getLong("double",jBasic \ "doubles") must beAnInstanceOf[Failure[_]]
    getLong("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalLong" in {
    getOptionalLong("long",jBasic \ "longs") mustEqual Success(Some(4444444444L))
    getOptionalLong("slong",jBasic \ "longs") mustEqual Success(Some(40000000000L))
    getOptionalLong("int",jBasic \ "ints") mustEqual Success(Some(2L))

    getOptionalLong("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalLong("double",jBasic \ "doubles") must beAnInstanceOf[Failure[_]]
    getOptionalLong("nonExistentField",jBasic) mustEqual Success(None)
  }

  "getDouble" in {
    getDouble("double",jBasic \ "doubles") mustEqual Success(3.1)
    getDouble("sdouble",jBasic \ "doubles") mustEqual Success(8.12393E-6)
    getDouble("int",jBasic \ "ints") mustEqual Success(2)
    getDouble("slong",jBasic \ "longs") mustEqual Success(40000000000.0)
    getDouble("dollars",jBasic \ "doubles") mustEqual Success(300.98)

    getDouble("string",jBasic) must beAnInstanceOf[Failure[_]]
    getDouble("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalDouble" in {
    getOptionalDouble("double",jBasic \ "doubles") mustEqual Success(Some(3.1))
    getOptionalDouble("sdouble",jBasic \ "doubles") mustEqual Success(Some(8.12393E-6))
    getOptionalDouble("int",jBasic \ "ints") mustEqual Success(Some(2))
    getOptionalDouble("slong",jBasic \ "longs") mustEqual Success(Some(40000000000.0))
    getOptionalDouble("dollars",jBasic \ "doubles") mustEqual Success(Some(300.98))

    getOptionalDouble("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalDouble("nonExistentField",jBasic) mustEqual Success(None)
  }

  "getMoney" in {
    getMoney("pounds",jBasic \ "doubles") mustEqual Success(300.98)
    getMoney("dollars",jBasic \ "doubles") mustEqual Success(300.98)
    getMoney("sdouble",jBasic \ "doubles") mustEqual Success(8.12393E-6)
    getMoney("int",jBasic \ "ints") mustEqual Success(2)
    getMoney("slong",jBasic \ "longs") mustEqual Success(40000000000.0)

    getMoney("string",jBasic) must beAnInstanceOf[Failure[_]]
    getMoney("nonExistentField",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalMoney" in {
    getOptionalMoney("pounds",jBasic \ "doubles") mustEqual Success(Some(300.98))
    getOptionalMoney("dollars",jBasic \ "doubles") mustEqual Success(Some(300.98))
    getOptionalMoney("sdouble",jBasic \ "doubles") mustEqual Success(Some(8.12393E-6))
    getOptionalMoney("int",jBasic \ "ints") mustEqual Success(Some(2))
    getOptionalMoney("slong",jBasic \ "longs") mustEqual Success(Some(40000000000.0))

    getOptionalMoney("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalMoney("nonExistentField",jBasic) mustEqual Success(None)
  }

  "get date and time" in {
    val date = new LocalDate(2013,11,29)
//    getLocalDate("sbtDate",jBasic \ "date&time") mustEqual Success(date)
    getLocalDate("jsonDate",jBasic \ "date&time") mustEqual Success(date)
    //getLocalTime("sbtTime",jBasic \ "date&time") mustEqual Success(300.98)
    getLocalTime("jsonTime",jBasic \ "date&time") mustEqual Success(new LocalTime(14,49,49,380))
    //getTime("sbtDate",jBasic \ "date&time") mustEqual Success(300.98)
//    getTime("jsonDate",jBasic \ "date&time") mustEqual Success(new DateTime(2013,11,29,14,49,49,380))


    getLocalDate("string",jBasic) must beAnInstanceOf[Failure[_]]
    getLocalTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getLocalDate("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
    getTime("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
  }

  "get optional date and time" in {
    val date = new LocalDate(2013,11,29)
//    getOptionalLocalDate("sbtDate",jBasic \ "date&time") mustEqual Success(Some(date))
    getOptionalLocalDate("jsonDate",jBasic \ "date&time") mustEqual Success(Some(date))
    //getLocalTime("sbtTime",jBasic \ "date&time") mustEqual Success(300.98)
    getOptionalLocalTime("jsonTime",jBasic \ "date&time") mustEqual Success(Some(new LocalTime(14,49,49,380)))
    //getTime("sbtDate",jBasic \ "date&time") mustEqual Success(300.98)
//    getOptionalTime("jsonDate",jBasic \ "date&time") mustEqual Success(Some(new DateTime(2013,11,29,14,49,49,380)))

    getOptionalLocalDate("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalLocalTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalLocalDate("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
    getOptionalTime("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
    getOptionalLocalDate("Non-existent field",jBasic) mustEqual Success(None)
    getOptionalLocalTime("Non-existent field",jBasic) mustEqual Success(None)
    getOptionalTime("Non-existent field",jBasic) mustEqual Success(None)
  }

  "get optional date and time" in {
    val date = new LocalDate(2013,11,29)
//    getOptionalLocalDate("sbtDate",jBasic \ "date&time") mustEqual Success(Some(date))
    getOptionalLocalDate("jsonDate",jBasic \ "date&time") mustEqual Success(Some(date))
    //getLocalTime("sbtTime",jBasic \ "date&time") mustEqual Success(300.98)
    getOptionalLocalTime("jsonTime",jBasic \ "date&time") mustEqual Success(Some(new LocalTime(14,49,49,380)))
    //getTime("sbtDate",jBasic \ "date&time") mustEqual Success(300.98)
//    getOptionalTime("jsonDate",jBasic \ "date&time") mustEqual Success(Some(new DateTime(2013,11,29,14,49,49,380)))

    getOptionalLocalDate("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalLocalTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalTime("string",jBasic) must beAnInstanceOf[Failure[_]]
    getOptionalLocalDate("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
    getOptionalTime("long",jBasic \ "longs") must beAnInstanceOf[Failure[_]]
    getOptionalLocalDate("Non-existent field",jBasic) mustEqual Success(None)
    getOptionalLocalTime("Non-existent field",jBasic) mustEqual Success(None)
    getOptionalTime("Non-existent field",jBasic) mustEqual Success(None)
  }

  "getDuration" in {
    getDuration("stdW-S",jBasic \ "durations") mustEqual Success(new Duration(1389722L*1000))
    getDuration("stdS",jBasic \ "durations") mustEqual Success(new Duration(47000L))

    getDuration("alteww",jBasic \ "durations") must beAnInstanceOf[Failure[_]]
    getDuration("Non-existent field",jBasic) must beAnInstanceOf[Failure[_]]
  }

  "getOptionalDuration" in {
    getOptionalDuration("stdW-S",jBasic \ "durations") mustEqual Success(Some(new Duration(1389722L*1000)))
    getOptionalDuration("stdS",jBasic \ "durations") mustEqual Success(Some(new Duration(47000L)))

    getOptionalDuration("alteww",jBasic \ "durations") must beAnInstanceOf[Failure[_]]
    getOptionalDuration("Non-existent field",jBasic) mustEqual Success(None)
  }

  "getPointValue" in {
    import com.optrak.scalautil.Geo._
    import shapeless._
    import JsonExtraction._
    import ExtractAux._

    val thePoint = new Point(555.55e+5, 5)
    val points = jBasic \ "Points"

    getPointValue(points \ "goodpoint") mustEqual Success(thePoint)

    val pointExtractor = extractor[Point](pointFields)
    pointExtractor.extract(points \ "goodpoint") mustEqual Success(thePoint)

    getPointValue(jBasic \ "string") must beAnInstanceOf[Failure[_]]
    getPointValue(points \ "badpoint") must beAnInstanceOf[Failure[_]]
    pointExtractor.extract(points \ "badpoint") must beAnInstanceOf[Failure[_]]
  }

  val jBasic = parse(
    """{
      |  "string": "2 and a half",
      |  "ints": {
      |     "int": 2,
      |     "intf": 2.0,
      |     "sint": "2",
      |     "sintf": "2.0"
      |  },
      |  "booleans": {
      |     "one": 1,
      |     "sone": "1",
      |     "true": true,
      |     "strue": "true",
      |     "syes": "yes"
      |     "zero": 0,
      |     "false": false,
      |     "sno": "no"
      |  },
      |  "doubles": {
      |     "double": 3.1,
      |     "sdouble": "8.12393E-6",
      |     "dollars": "$300.98",
      |     "pounds": "£300.98"
      |  },
      |  "longs": {
      |     "slong": "40000000000",
      |     "long": 4444444444
      |  },
      |  "date&time": {
      |     "sbtDate": "Nov 29, 2013 2:41:47 PM",
      |     "jsonDate": "2013-11-29T14:49:49.380+02:00"
      |     "sbtTime": "2:41:47.25 PM",
      |     "jsonTime": "T14:49:49.380+02:00"
      |  }
      |  "durations": {
      |     "stdW-S": "P2W2DT2H2M2S",
      |     "stdS": "PT47S",
      |     "alteww": "P2013-W11-29T16:47:47",
      |  }
      |  "Points": {
      |     "goodpoint": {
      |       "x": 555.55e+5,
      |       "y": "5"
      |     },
      |     "badpoint": {
      |       "x": "string",
      |       "y": "5",
      |       "z": "7"
      |     }
      |  }
      |  "array": [
      |    {"name": "George"},
      |    {"name": "Harris"}
      |  ]
      |}""".stripMargin
  )
}
*/

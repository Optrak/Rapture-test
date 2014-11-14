/*
package com.optrak.scalautil.jsonutil

import org.specs2.mutable.Specification
import com.optrak.scalautil.ScalazBits._
import com.optrak.scalautil.Geo._
import JsonGeo._
import JsonExtraction._
import org.json4s.JValue
import scalaz.syntax.applicative._

/**
 * Created by timpigden on 16/12/13.
 * (c) Tim Pigden and Optrak Distribution Software Ltd, Ware, UK, 2013
 */
class JsonGeoSpec extends Specification {
  "point" should {
    "parse and rewrite" in {
      val pNoSrid :JValue =
        """{
          |"x": 10,
          |"y": 11
          |}
        """.stripMargin
      val pSrid: JValue =
        """{
          |"x": 10,
          |"y": 11,
          |"srid": 212
          |}
        """.stripMargin

      import JsonGeo._
      import ExtractAux._
      implicit val context = HeadContext("parse point")

      wrapErrorAsString(
        ( extractor[Point](pointFields).extract(pNoSrid) |@|
          extractor[Point](pointFields).extract(pSrid)) {(pn, ps) =>
          pn mustEqual(Point(10, 11, None))
          ps mustEqual(Point(10, 11, Some(212)))
        }
      ) mustEqual("OK")

    }

  }
}
*/

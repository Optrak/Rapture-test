/*
package com.optrak.scalautil.jsonutil

import org.json4s._
import org.specs2.mutable.Specification
import com.optrak.scalautil.Dimensions._
import shapeless._
import JsonExtraction._
import ExtractAux._
import com.optrak.scalautil._
import ScalazBits._
import JsonDimensions._
import IOTestData._

/**
 * Created with IntelliJ IDEA.
 * User: timpigden
 * Date: 02/09/13
 * Time: 18:41
 * Copyright Optrak Distribution Software Ltd, Hertford, UK, 2013
 */

class ParseXYZFieldsTest extends Specification {
  val stuff: JValue = """{
    "Box": {
      "name": "Jack",
      "dimensions": {
        "x": 10.2,
        "y": "1.1",
        "z": "3.0"
      }
    },
    "Parcel": [
      {
        "name": "Joe",
        "dimensions": null
      },
      { "name": "Joe2" },
      {
        "name": "Joey",
        "dimensions": { "y": "1.0" }
      }
    ],
    "Bundle": {
      "name": "jane",
      "dimensions": { "y": "1.0" }
    }
  }"""

  "box and parcel" should {

    "parse box" in {
      implicit val context = HeadContext("parse box")
      wrapErrorAsString( {
        val boxExtractor = extractor[Box](boxFields)
        val vBox = boxExtractor.extract(stuff \ "Box")
        for {
          box <- vBox
        } yield box.dimensions.x mustEqual(10.2)
      }) mustEqual("OK")
    }

    "parse parcels" in {
      implicit val context = HeadContext("parse parcels")
      wrapErrorAsString( {
        val parcelExtractor = extractor[Parcel](parcelFields)
        val vParcels = (stuff \ "Parcel").children map parcelExtractor.extract
        for {
          parcels <- stackSafeSequence(vParcels)
        } yield {
          parcels.size mustEqual(3)
          parcels(0).dimensions.get.x mustEqual(None)
          parcels(1).dimensions mustEqual(None)
          parcels(2).dimensions.get.y mustEqual(Some(1.0))
        }
      }) mustEqual("OK")
    }

    "parse bundle " in {
      implicit val context = HeadContext("parse bundles")
      wrapErrorAsString( {
        val bundleExtractor = extractor[Bundle](bundleFields)
        val vBundle = bundleExtractor.extract(stuff \ "Bundle")
        for {
          bundle <- vBundle
        } yield {
          bundle.dimensions.y mustEqual(Some(1.0))
        }
      }) mustEqual("OK")
    }

  }

}

*/

package com.optrak

import grizzled.slf4j.Logging
import org.specs2.mutable.Specification
import rapture.core._
import rapture.data.BasicExtractor
import rapture.json._
import jsonBackends.json4s._
import com.optrak.scalautil.Dimensions
import com.optrak.jsonutil.CompanyTestData._
import IOTestData._

/**
 * Created by timpi_000 on 24/10/2014.
 */
class RaptureTest extends Specification with Logging {

  case class Customer(id: String, name: String)
  type CustomerLibrary = Map[String, Customer]

  val libraryOne : CustomerLibrary = Map("tim" -> Customer("tim", "Tim Pigden"))
  val libraryTwo : CustomerLibrary = Map("tim" -> Customer("tim", "Tim Smith"))

  case class Letter(from: String, to: Customer)

  val jsonIn =
    json"""{"from":"Jon","to":"tim"}"""

  "rapture" should {
    "parse this to Tim Pigden" in {
      val myLibrary = libraryOne
      implicit val myExtractor = BasicExtractor[Customer, Json](x => myLibrary(x.as[String]))
      // stuff
      val myLetter = jsonIn.as[Letter]
      myLetter.to.name === "Tim Pigden"
    }
    
    "format this with reference" in {
      val myLibrary = libraryOne
      val myLetter = Letter("Jon", myLibrary.values.head)

      // stuff
      implicit def custSer[Ast <: JsonAst, JsonType <: JsonDataType[JsonType, _ <: Ast]](implicit ast: Ast) = 
		new rapture.data.Serializer[Customer, Json] {
		  def serialize(c: Customer) = ast.fromString(c.id)
		}
      val myNewJson = Json(myLetter)
      myNewJson === jsonIn
    }
  }
}
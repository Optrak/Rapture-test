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

/*class ParseCompanyTest extends Specification with Logging {
  case class Person(name: String, age: Option[Int], years: Int)
  //val personFields = "name" :: Some("age") :: "years" :: HNil

  "parse people" in {
    val people1 = jWidgetCo.Person.as[Set[Person]]
    //peopleMap = people.map(p => (p.name -> p)).toMap
    people1 mustEqual Set(joe, jane, fred)
  }

  "parse team" in {
    implicit val context= newCompanyContext

    val teams = for {
      people <- parsePeople(jWidgetCo \ "Person").disjunction
      peopleMap = people.map(p => (p.name -> p)).toMap
      teams <- parseTeams(jWidgetCo \ "Team", peopleMap.get).disjunction
    } yield teams
    teams mustEqual \/-(Set(aTeam,bTeam))
  }

  "company" should {
    "parse two companies ok" in {
      implicit val context= HeadContext("company")
      val rightNow = new DateTime
      wrapErrorAsString(
        for {
          companies <- parseCompanies(j2Companies \ "Company")
        } yield {
          companies.size mustEqual(2)
          logger.debug(companies)
          companies.head.dateProcessed.getMillis > rightNow.getMillis mustEqual(true)
          companies.head.copy(dateProcessed = thisTime) mustEqual(widgets)
          companies.last.copy(dateProcessed = thisTime) mustEqual(holdings)
        }
      ) mustEqual("OK")
    }

    "parse error ceo" in {
      implicit val context = HeadContext("company")
      val errList = listErrorReports(parseCompanies(jBadRefCo))
      errList.size mustEqual(1)
      val noFred = errList.head
      //val sampleError = NotFoundErrorReport("fred", , None) TODO: figure out where the validation context comes from
      noFred must beAnInstanceOf[NotFoundErrorReport]
      noFred.subject mustEqual("fred")
      noFred.validationContext.toString mustEqual("company >> ceo")
    }

    "parse error bad years" in {
      implicit val context = HeadContext("company")
      val errList = listErrorReports(parseCompanies(jBadIntsCo))
      //logger.debug(s"Errors from2: \n$errList")
      errList.size mustEqual 2
      val badYears = errList.tail.head
      badYears must beAnInstanceOf[ParseErrorReport]
      badYears.subject mustEqual("5x")
      badYears.validationContext.toString mustEqual("company >> parse people in company >> years >> parseInt")
      val badAge = errList.head
      badAge must beAnInstanceOf[ParseErrorReport]
      badAge.subject mustEqual("55y")
      badAge.validationContext.toString mustEqual("company >> parse people in company >> age >> parseInt")
    }
  }
}*/

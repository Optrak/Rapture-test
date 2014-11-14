package com.optrak

import grizzled.slf4j.Logging
import org.specs2.mutable.Specification
import com.optrak.scalautil.Dimensions
import com.optrak.jsonutil.CompanyTestData._
import IOTestData._
import play.api.libs.json._
//import scala.language.implicitConversions

class PlayJsonParseCompanyTest extends Specification with Logging {
  val j2Companies = Json.parse(s2Companies)
  val jWidgetCo = (j2Companies \ "Company")(0)
  val jHoldingCo = (j2Companies \ "Company")(1)
  //implicit def strToInt(s: String): Int = augmentString(s).toInt
  implicit val quotedIntReads = new Reads[Int] {
    def reads(js: JsValue): JsResult[Int] = js.validate[String].map(_.toInt)
  }

  "parse people" in {
    //don't see a way to cope with mixed quoted and unquoted ints
    implicit val personFormat = Json.format[Person]
    logger.info(jWidgetCo \ "Person")
    val people1 = Json.fromJson[Set[Person]](jWidgetCo \ "Person").asOpt.get
    logger.info(people1)
    people1 mustEqual Set(joe, jane, fred)
  }

  /*"parse team" in {
    /*case class Team(name: String, boss: Person, members: Set[Person])
    val teamFields = "name" :: ReferenceField("boss") :: SetReferenceField("member") :: HNil*/
    val teams = {
      val people = jWidgetCo.Person.as[Set[Person]]
      val peopleMap = people.map(p => (p.name -> p)).toMap
      implicit val personRefExtractor = BasicExtractor[Person, Json](x => peopleMap(x.as[String]))
      val teams = jWidgetCo.Team.as[Set[Team]]
    }
    teams mustEqual Set(aTeam,bTeam)
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
  }*/
}
 

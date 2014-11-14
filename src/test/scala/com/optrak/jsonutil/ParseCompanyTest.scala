/*
package com.optrak.scalautil.jsonutil

import org.specs2.mutable.Specification
import com.optrak.scalautil._
import ScalazBits._
import scalaz._
import org.joda.time.DateTime
import grizzled.slf4j.Logging

import CompanyTestData._
import IOTestData._

class ParseCompanyTest extends Specification with Logging {

  val newCompanyContext = HeadContext("people and team")

  "parse people" in {
    implicit val context = newCompanyContext
    val people1 = parsePeople(jWidgetCo \ "Person")
        //peopleMap = people.map(p => (p.name -> p)).toMap
    people1 mustEqual Success(Set(joe, jane, fred))
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
}
*/

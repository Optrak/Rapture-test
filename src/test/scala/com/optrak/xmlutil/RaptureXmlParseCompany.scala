package com.optrak.xmlutil

import com.optrak.IOTestData
import com.optrak.xmlutil.CompanyTestData._
import IOTestData._
import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import rapture.data._
import rapture.xml._
import rapture.xml.xmlBackends.scalaXml._

class RaptureXmlParseCompany extends Specification with Logging {
  val xmlWidgetCo = Xml(widgetXmlBase)
  val xmlHoldingCo = Xml(holdingCompanyBase)

  //implicit val maybeQuotedIntExtractor not needed

  "parse people" in {
    val people1 = xmlWidgetCo.Person.as[Set[Person]] //doesn't complain that Person field doesn't exist in source
    people1 mustEqual Set(joe, jane, fred)
  }

  "parse normal team with out-of-the-box extractor" in {
    val people = xmlWidgetCo.Person.as[Set[Person]]
    val peopleMap = people.map(p => p.name -> p).toMap

    implicit val personRefExtractor = BasicExtractor[Person, Xml](x => peopleMap(x.as[String]))

    val teams = xmlWidgetCo.Team.as[Set[Team]]
    teams mustEqual Set(aTeam, bTeam)
  }

  "company" should {
    /*case class Company(name: String,
                       ceo: Person,
                       complianceOfficer: Option[Person],
                       people: Set[Person],
                       teams: Set[Team],
                       dateProcessed: DateTime)
    val companyFields = "name" ::
      ReferenceField("ceo") ::
      Some(ReferenceField("complianceOfficer")) ::
      SetField("Person") ::
      SetField("Team") ::
      NowField("dateProcessed") :: HNil*/

    "parse two companies ok" in {
      implicit val companyExtractor = BasicExtractor[Company, Xml]{ xml =>
        val people = xml.Person.as[Set[Person]]
        val peopleMap = people.map(p => p.name -> p).toMap

        val personRefExtractor = BasicExtractor[Person, Xml](x => peopleMap(x.as[String])) //throws
        implicit val mixedPersonExtractor = implicitly[Extractor[Person, Xml]] orElse personRefExtractor
        implicit val dateTimeExtractor = BasicExtractor[DateTime, Xml](x => DateTime.parse(x.as[String]))
        xml.as[Company]
      }
      val inCo = Xml(companiesXml).Companies.as[Array[Company]]
      (inCo(0) almost_== widgets) must beTrue
      (inCo(1) almost_== holdings) must beTrue
    }
  }
    /*
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
 

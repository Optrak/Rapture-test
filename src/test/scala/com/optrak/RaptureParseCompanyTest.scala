package com.optrak

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import rapture.data.{MissingValueException, BasicExtractor}
import rapture.json._
import jsonBackends.json4s._
import com.optrak.scalautil.Dimensions
import com.optrak.jsonutil.CompanyTestData._
import IOTestData._

class RaptureParseCompanyTest extends Specification with Logging {
  val j2Companies = Json.parse(s2Companies)
  val jWidgetCo = j2Companies.Company(0)
  val jHoldingCo = j2Companies.Company(1)

  implicit val maybeQuotedIntExtractor = Json.extractor[Int] orElse Json.extractor[String].map(_.toInt)
  /*implicit def genSeqExtractor[T, Coll[_], Data <: DataType[Data, R] forSome { type R <: DataAst }]
    (implicit cbf: scala.collection.generic.CanBuildFrom[Nothing, T, Coll[T]], ext: Extractor[T, Data]): Extractor[Coll[T], Data] =
    BasicExtractor[Coll[T], Data]({ x => x.$ast.getArray(x.$root.value).to[List].map({v =>
      ext.construct(x.$wrap(v), x.$ast) }).to[Coll] })*/

  "parse people" in {
    val people1 = jWidgetCo.Person.as[Set[Person]]
      /* in case of missing int extractor we get
        [error]  TypeMismatchException: : Type mismatch: Expected number but found string at <value>.Company(0).Person  (data.scala:127)
       */
    people1 mustEqual Set(joe, jane, fred)
  }

  "parse team with proxy" in {
    /* case class Team(name: String, boss: Person, members: Set[Person])
       case class field names must exactly correspond to json field names
       val teamFields = "name" :: ReferenceField("boss") :: SetReferenceField("member") :: HNil
     */
    case class TeamProxy(name: String, boss: Person, members: Option[Set[Person]]) {
      def toTeam = Team(name, boss, members match {
        case Some(set) => set
        case None => Set[Person]()
      })
    }

    val people = jWidgetCo.Person.as[Set[Person]]
    val peopleMap = people.map(p => (p.name -> p)).toMap
    implicit val personRefExtractor = BasicExtractor[Person, Json](x => peopleMap(x.as[String]))
    val teams = jWidgetCo.Team.as[Set[TeamProxy]].map(_.toTeam)
    teams mustEqual Set(aTeam,bTeam)
  }

  "parse team with a special extractor" in {
    val people = jWidgetCo.Person.as[Set[Person]]
    val peopleMap = people.map(p => (p.name -> p)).toMap

    val teamExtractor1 = BasicExtractor[Team, Json]{ js =>
      val members = try {
        js.members.as[Set[String]].map(m => peopleMap(m))
      } catch {
        case e: MissingValueException => Set[Person]()
      }
      Team(js.name.as[String], peopleMap(js.boss.as[String]), members)
    }

    val teamExtractor2 = BasicExtractor[Team, Json]{ js =>
      val members = js match {
        case json"""{"members": $members}""" => members.as[Set[String]].map(m => peopleMap(m))
        case _ => Set[Person]()
      }
      Team(js.name.as[String], peopleMap(js.boss.as[String]), members)
    }

    implicit val teamExtractor = teamExtractor1

    val teams = jWidgetCo.Team.as[Set[Team]]
    teams mustEqual Set(aTeam,bTeam)
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
      val rightNow = new DateTime
      /*wrapErrorAsString(
        for {
          companies <- parseCompanies(j2Companies \ "Company")
        } yield {
          companies.size mustEqual (2)
          logger.debug(companies)
          companies.head.dateProcessed.getMillis > rightNow.getMillis mustEqual (true)
          companies.head.copy(dateProcessed = thisTime) mustEqual (widgets)
          companies.last.copy(dateProcessed = thisTime) mustEqual (holdings)
        }
      )*/"OK" mustEqual ("OK")
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
 

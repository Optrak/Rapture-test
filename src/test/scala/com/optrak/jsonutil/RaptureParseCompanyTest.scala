package com.optrak.jsonutil

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import rapture.data._
import rapture.json._
import rapture.core._
import jsonBackends.json4s._
import com.optrak.scalautil.Dimensions
import com.optrak.jsonutil.CompanyTestData._
import scala.reflect.ClassTag
import scalaz._
//import scalaz.syntax.validation._
//import scalaz.syntax.applicative._
import Scalaz._
import com.optrak.IOTestData._

class RaptureParseCompanyTest extends Specification with Logging {
  class ReturnValidationNelMode[+G <: ModeGroup] extends Mode[G] {
    type Wrap[+T, E <: Exception] = ValidationNel[E, T]
    def wrap[T, E <: Exception: ClassTag](t: => T): ValidationNel[E, T] =
      try t.successNel[E] catch { case e: E => e.failureNel[T] }
  }

  type JsReadValid[T] = ValidationNel[DataGetException, T]
  val j2Companies = Json.parse(s2Companies)
  val jExperimentalCo = Json.parse(sExperimentalCo)
  val jWidgetCo = j2Companies.Company(0)
  val jHoldingCo = j2Companies.Company(1)

  implicit val maybeQuotedIntExtractor = implicitly[Extractor[Int,Json]] orElse Json.extractor[String].map(_.toInt)

  "parse people" in {
    val people1 = jWidgetCo.people.as[Set[Person]]
      /* in case of missing int extractor we get
        [error]  TypeMismatchException: : Type mismatch: Expected number but found string at <value>.Company(0).Person
       */
    people1 mustEqual Set(joe, jane, fred)
  }

  "parse people by hand" in {
    implicit val personExtractor = BasicExtractor[Person, Json] { js =>
      Person(
        js.name.as[String],
        js.age.as[Option[Int]],
        js.years.as[Int])
    };
    {
      val people1 = jWidgetCo.people.as[Set[Person]]
      //people1 mustEqual Set(joe, jane, fred)
    } must throwA[MissingValueException](message = "Missing value: <value>.age")
  }

  "parse validated people by hand" in {
    implicit val validPersonExtractor = BasicExtractor[JsReadValid[Person], Json] { js =>
      import scalazModes.returnValidations
      (js.name.as[String].toValidationNel |@|
        js.age.as[Option[Int]].toValidationNel |@|
        js.years.as[Int].toValidationNel){ Person(_, _, _) }
    }
    val vPeople1 = jWidgetCo.people.as[List[JsReadValid[Person]]].sequenceU.map(_.toSet)
    vPeople1 mustEqual Success(Set(joe, jane, fred))
  }

  "parse tricky team with proxy" in {
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

    val people = jExperimentalCo.people.as[Set[Person]]
    val peopleMap = people.map(p => p.name -> p).toMap
    implicit val personRefExtractor = BasicExtractor[Person, Json](x => peopleMap(x.as[String]))
    val teams = jExperimentalCo.teams.as[Set[TeamProxy]].map(_.toTeam)
    teams mustEqual Set(aTeam, bTeam, cTeam, dTeam)
  }

  "parse tricky team with a custom Team extractor" in {
    val people = jExperimentalCo.people.as[Set[Person]]
    val peopleMap = people.map(p => p.name -> p).toMap

    val teamExtractor1 = BasicExtractor[Team, Json]{ js =>
      val members = try {
        js.members.as[Set[String]].map(m => peopleMap(m))
      } catch {
        case _: MissingValueException | _: TypeMismatchException => Set[Person]()
      }
      Team(js.name.as[String], peopleMap(js.boss.as[String]), members)
    }

    val teamExtractor2 = BasicExtractor[Team, Json]{ js =>
      val members = js match {
        //doesn't work for null collection
        case json"""{"members": $members}""" => members.as[Set[String]].map(m => peopleMap(m))
        case _ => Set[Person]()
      }
      Team(js.name.as[String], peopleMap(js.boss.as[String]), members)
    }

    implicit val teamExtractor = teamExtractor1
    val teams = jExperimentalCo.teams.as[Set[Team]]
    teams mustEqual Set(aTeam, bTeam, cTeam, dTeam)
  }

  "parse normal team with out-of-the-box extractor" in {
    val people = jWidgetCo.people.as[Set[Person]]
    val peopleMap = people.map(p => p.name -> p).toMap

    implicit val personRefExtractor = BasicExtractor[Person, Json](x => peopleMap(x.as[String]))

    val teams = jWidgetCo.teams.as[Set[Team]]
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
      /*import scalazModes.returnValidations
      /*implicit def returnValidationNel[G <: ModeGroup]: Mode[G] =
        new ReturnValidationNelMode[G]*/

      implicit val companyExtractor = BasicExtractor[Company, Json]{ js =>
        val vName = js.name.as[String]
        val vPeopleSet: Validation[DataGetException, Set[Person]] = js.people.as[Set[Person]]
        val vPeopleMap = vPeopleSet.map(peopleSet => peopleSet.map{ p => p.name -> p }.toMap)
        //rapture doesn't support plugging custom validated extractors into macro-generated ones
        implicit val personRefExtractor = BasicExtractor[Validation[DataGetException, Person], Json] { js =>
          val odPerson: DataGetException \/ Option[Person] = for {
            peopleMap <- vPeopleMap.disjunction
            name <- js.as[String].disjunction
          } yield peopleMap.get(name)
          val vPerson: Validation[DataGetException, Person] = odPerson.fold(
            (e: DataGetException) => e.failure,
            (op: Option[Person]) => op match {
              case Some(person) => person.success
              case None => new DataGetException(s"Couldn't find $js in people map").failure
            }
          )
          vPerson
        }
        val vCeo = js.ceo.as[Person].leftMap(NonEmptyList(_))
        val vMaybeCompOfficer = js.complianceOfficer.as[Option[Person]].leftMap(NonEmptyList(_))
        val vTeams = js.teams.as[Set[Team]].leftMap(NonEmptyList(_))
        implicit val dateTimeExtractor = Json.extractor[String].map(DateTime.parse)
        val vDate = js.dateProcessed.as[DateTime].leftMap(NonEmptyList(_))
        val vPars = List(vName, vCeo, vMaybeCompOfficer, vPeopleSet, vTeams, vDate)
          .map(v => v.leftMap(NonEmptyList(_))) //we need this because DataGetExceptions are not a semigroup yet
          //.toVector
        //applicative builders are limited to 12 args. Can't use lists, because packing validations in a list downcasts contained types
        (vName |@| vPars(1) |@| vPars(2) |@| vPars(3) |@| vPars(4) |@| vPars(5)){
          Company(_, _, _, _, _, _)
        }
      }*/
       /*commenting out the next line results in a compile-time error
         "cannot extract type Seq[com.optrak.IOTestData.Company] from rapture.json.Json"*/
      implicit val companyExtractor = BasicExtractor[Company, Json]{ js =>
        val people = js.people.as[Set[Person]]
        val peopleMap = people.map(p => p.name -> p).toMap

        val personRefExtractor = BasicExtractor[Person, Json](x => peopleMap(x.as[String])) //throws
        implicit val mixedPersonExtractor = implicitly[Extractor[Person, Json]] orElse personRefExtractor
        implicit val dateTimeExtractor = BasicExtractor[DateTime,Json](x => DateTime.parse(x.as[String]))
        js.as[Company]
      }

      val inCo = j2Companies.Company.as[Seq[Company]]
      //if companyExtractor is not in scope, TypeMismatchException: : Type mismatch: Expected object but found string at <value>.Company
      (inCo(0) almost_== widgets) must beTrue
      (inCo(1) almost_== holdings) must beTrue
      1 must_== 1
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

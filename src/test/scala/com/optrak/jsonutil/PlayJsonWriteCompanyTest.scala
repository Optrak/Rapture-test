package com.optrak.jsonutil

import com.optrak.IOTestData._
import com.optrak.jsonutil.CompanyTestData._
import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import play.api.libs.json._
import play.api.libs.functional.syntax._

class PlayJsonWriteCompanyTest extends Specification with Logging {
  val j2Companies = Json.parse(s2Companies)
  val jWidgetCo = (j2Companies \ "Company")(0)
  val jHoldingCo = (j2Companies \ "Company")(1)

  implicit val quotedIntReads = new Reads[Int] {
    def reads(js: JsValue): JsResult[Int] = js.validate[Int] orElse (try
    {
      js.validate[String].map(_.toInt)
    } catch {
      case _: NumberFormatException => JsError(s"Could not extract int from ${js.as[String]}")
    }) //need to handle exceptions like this to get JsResults instead of exceptions
  }
  val personWrites = Json.writes[Person]

  "write people" in {
    implicit val personWrts = personWrites
    val peopleWritten: JsValue = Json.toJson(Set(joe, jane, fred))
    val people = jWidgetCo \ "people"
    peopleWritten mustEqual people
  }

  "write teams" in {
    /* case class Team(name: String, boss: Person, members: Set[Person])
       val teamFields = "name" :: ReferenceField("boss") :: SetReferenceField("member") :: HNil */
    val manualTeamWrites1: Writes[Team] = (
      (__ \ "name").write[String] and
      (__ \ "boss").write[String].contramap( (p: Person) => p.name ) and
      (__ \ "members").write[Set[String]].contramap( (ps: Set[Person]) => ps map (_.name) )
    )(unlift(Team.unapply))

    implicit val personRefWrites = new Writes[Person] {
      def writes(p: Person) = JsString(p.name)
    }
    val manualTeamWrites2: Writes[Team] = (
      (__ \ "name").write[String] and
      (__ \ "boss").write[Person] and
      (__ \ "members").write[Set[Person]]
    )(unlift(Team.unapply))
    val generatedTeamWrites = Json.writes[Team]

    val teamWritesList = List(manualTeamWrites1, manualTeamWrites2, generatedTeamWrites)
    val teamsWritten = teamWritesList map {wrt =>
      implicit val teamWrites = wrt
      Json.toJson(Set(aTeam, bTeam))
    }
    val teams = jWidgetCo \ "teams"
    teamsWritten must contain(be_==(teams)).foreach
  }

  "write company" in {
    /* case class Company(name: String,
                     ceo: Person,
                     complianceOfficer: Option[Person],
                     people: Set[Person],
                     teams: Set[Team],
                     dateProcessed: DateTime) {
      def almost_== (another: Company): Boolean = {
        this.name == another.name &&
        this.ceo == another.ceo &&
        this.complianceOfficer == another.complianceOfficer &&
        this.people == another.people &&
        this.teams == another.teams
      }
    }
    val companyFields = "name" ::
      ReferenceField("ceo") ::
      Some(ReferenceField("complianceOfficer")) ::
      SetField("Person") ::
      SetField("Team") ::
      NowField("dateProcessed") :: HNil */
    implicit val personWrts = personWrites
    val personRefWrites = new Writes[Person] {
      def writes(p: Person) = JsString(p.name)
    }
    implicit val manualTeamWrites1: Writes[Team] = (
      (__ \ "name").write[String] and
      (__ \ "boss").write[String].contramap( (p: Person) => p.name ) and
      (__ \ "members").write[Set[String]].contramap( (ps: Set[Person]) => ps map (_.name) )
      )(unlift(Team.unapply))

    implicit val manualCompanyWrites: Writes[Company] = (
      (__ \ "name").write[String] and
      (__ \ "ceo").write[Person](personRefWrites) and
      (__ \ "complianceOfficer").writeNullable[Person](personRefWrites) and
      (__ \ "people").write[Set[Person]] and
      (__ \ "teams").write[Set[Team]] and
      (__ \ "dateProcessed").writeNullable[DateTime].contramap((_: DateTime) => None)
    )(unlift(Company.unapply))

    implicit val companyReads = PlayJsonParseCompanyTest.companyReads

    val scalaData = Set(widgets, holdings)
    val companiesWritten = Json.toJson(scalaData)
    val companiesRead = companiesWritten.as[Array[Company]]
    //val companies = jWidgetCo \ "teams"
    logger.debug(companiesWritten)
    (companiesRead(0) almost_== widgets) && (companiesRead(1) almost_== holdings) must beTrue
  }
}
 

package com.optrak.jsonutil

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import com.optrak.scalautil.Dimensions
import com.optrak.jsonutil.CompanyTestData._
import com.optrak.IOTestData._
import play.api.libs.json._

object PlayJsonParseCompanyTest extends Specification with Logging {
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
  val personReads = Json.reads[Person]

  implicit val companyReads = new Reads[Company] {
    def reads(jCompany: JsValue) = {
      val vPeopleMap = {
        implicit val personRds = personReads
        (jCompany \ "people").validate[Set[Person]]
      }.map(people =>
        people.map(p =>
          p.name -> p).toMap)
      val personRefReads = new Reads[Person] {
        def reads(js: JsValue): JsResult[Person] =
          (for {
            s <- js.validate[String]
            peopleMap <- vPeopleMap
          } yield peopleMap.get(s)).flatMap {
            case Some(p) => JsSuccess(p)
            case None => JsError(s"$js not found in people map")
          }
      } /* peopleMap.apply(_) may throw an exception */
      implicit val personMixedExtractor = personReads orElse personRefReads //order of reads matters!
      // commenting out the next line results in a runtime error, because there is a DateTime parser in play
      // limited to a certain format
      implicit val dateTimeExtractor = Reads.of[String] map DateTime.parse
      implicit val teamReads = Json.reads[Team]
      implicit val innerCompanyReads = Json.reads[AuxCompany] // many things would be easier with a proxy case class.
      // This is for dateProcessed only, the rest of the code shows how to extract everything without a proxy.
      jCompany.validate[AuxCompany].map(_.toCompany)
    }
  }
  //never mix implicit formats with implicit reads of the same type. It produces extremely hard to debug errors

  "parse people" in {
    implicit val personRds = personReads
    val people1 = (jWidgetCo \ "people").validate[Set[Person]]
    //in case of error: JsError(List(((0)/years,List(ValidationError(error.expected.jsstring,WrappedArray()))),
    // ((2)/age,List(ValidationError(error.expected.jsstring,WrappedArray())))))
    people1 mustEqual JsSuccess(Set(joe, jane, fred))
  }

  "parse teams" in {
    /*case class Team(name: String, boss: Person, members: Set[Person])
    val teamFields = "name" :: ReferenceField("boss") :: SetReferenceField("member") :: HNil*/
    val people = {
      implicit val personRds = personReads
      (jWidgetCo \ "people").as[Set[Person]]
    }
    val peopleMap = people.map(p => (p.name -> p)).toMap
    implicit val personRefReads = new Reads[Person] {
      def reads(js: JsValue): JsResult[Person] = js.validate[String] map peopleMap
    }
    implicit val teamReads = Json.reads[Team]
    //validate is mandatory in order to use implicit personRefReads. Otherwise it has to be passed explicitly
    val boss = ((jWidgetCo \ "teams")(0) \ "boss").as[Person](personRefReads)
    val teams = (jWidgetCo \ "teams").validate[Set[Team]]
    teams mustEqual JsSuccess(Set(aTeam,bTeam))
  }

  "parse a case class contained in another one" in {
    val in = Json.parse(
      """{
        |"int": 8,
        |"simple": "hi"
      |}""".stripMargin)
    implicit val simpleReads = new Reads[Simple] {
      def reads(js: JsValue): JsResult[Simple] = js.validate[String] map (Simple)
    }
    implicit val containerReads = Json.reads[Container]
    val inSimple = (in \ "simple").as[Simple]
    val inCont = in.as[Container]
    1 must_== 1
  }

  "company" should {
    "parse two companies ok" in {
      val inCo = (j2Companies \ "Company").as[Seq[Company]]
      (inCo(0) almost_== widgets) must beTrue
      (inCo(1) almost_== holdings) must beTrue
    }

    "parse error ceo" in {
      val jBadRefCo = Json.parse(sBadRefCo)
      val vInCo = (jBadRefCo \ "Company").validate[Company]
      logger.error(vInCo)
      vInCo must beAnInstanceOf[JsError]
    }

    "parse error bad years" in {
      val jBadIntsCo = Json.parse(sBadIntsCo)
      val vInCo = (jBadIntsCo \ "Company").validate[Company]
      logger.error(vInCo)
      vInCo must beAnInstanceOf[JsError]
    }
  }
}
 

package com.optrak.jsonutil

import org.json4s._
import shapeless._
//import JsonExtraction._
//import ExtractAux._
import com.optrak.scalautil._
//import com.optrak.scalautil.IOTestData._
import ScalazBits._
import scalaz.syntax.validation._
import org.joda.time.DateTime
//import rapture.json._
//import jsonBackends.json4s._

object CompanyTestData {
  /*val personExtractor = extractor[Person](personFields)

  def parsePerson(jperson: JValue)(implicit context: ValidationContext): Validated[Person] = personExtractor.extract(jperson)

  def parsePeople(jpeople: JValue)(implicit context: ValidationContext): Validated[Set[Person]] =
    stackSafeSequence(jpeople.children map personExtractor.extract) map (_.toSet)

  class TeamExtractor(people: (String) => Option[Person])(implicit context: ValidationContext) extends ValueExtractor[Team] {
    implicit val peopleExtractor = new ReferenceExtractor(people)
    implicit val peopleSetReferenceExtractor = new SetReferenceExtractor(people)
    val isoExtractor = extractor[Team](teamFields)

    def extract(jvalue: JValue)(implicit context: ValidationContext) = isoExtractor.extract(jvalue)
  }

  def parseTeam(jteam: JValue, people: (String) => Option[Person])(implicit context: ValidationContext): Validated[Team] =
    (new TeamExtractor(people)).extract(jteam)

  def parseTeams(teamNodes: JValue, people: (String) => Option[Person])(implicit context: ValidationContext): Validated[Set[Team]] = {
    val teamExtractor = new TeamExtractor(people)
    stackSafeSequence(teamNodes.children map teamExtractor.extract) map (_.toSet)
  }

  def parseCompany(jvalue: JValue)(implicit context: ValidationContext): Validated[Company] = {
    def parseCompanyI(jvalue: JValue, people: Set[Person]): Validated[Company] = {
      implicit val peopleExtractor = new Extractor[Set[Person]] {
        def extract(fieldName: String, node: JValue)(implicit context: ValidationContext) = people.success
      }
      val peopleMap = people.map(person => (person.name -> person)).toMap
      implicit val optionalPersonReferenceExtractor = new OptionalReferenceExtractor(peopleMap.get)
      implicit val personReferenceExtractor = new ReferenceExtractor(peopleMap.get)
      implicit val teamSetExtractor = new SetExtractor(new TeamExtractor(peopleMap.get))
      val isoExtractor = extractor[Company](companyFields)
      isoExtractor.extract(jvalue)
    }

    (for {
      people <- parsePeople(jvalue \ "Person")("parse people in company").disjunction
      company <- parseCompanyI(jvalue, people).disjunction
    } yield company).validation
  }

  def parseCompanies(jcompanies: JValue)(implicit context: ValidationContext): Validated[Set[Company]] =
    stackSafeSequence(jcompanies.children map(parseCompany(_)(context))) map (_.toSet)
*/
  val s2Companies = """{
    "Company": [
      {
        "name": "Widget Co",
        "ceo": "fred",
        "Person": [
          {
            "name": "joe",
            "years": "3"
          },
          {
            "name": "jane",
            "years": "1"
          },
          {
            "name": "fred",
            "years": "5",
            "age": "55"
          }
        ],
        "Team": [
          {
            "name": "a-team",
            "boss": "fred",
            "member": [
              "jane",
              "joe"
            ]
          },
          {
            "name": "b-team",
            "boss": "joe"
          }
        ]
      },
      {
        "name": "Widget Holdings",
        "ceo": "fred",
        "complianceOfficer": "andrew",
        "Person": [
          {
            "name": "andrew",
            "years": "3"
          },
          {
            "name": "fred",
            "years": "5",
            "age": "55"
          }
        ]
      }
    ]
  }"""

  val sBadRefCo =
    """{
      "Company": {
        "name": "Widget Holdings",
        "ceo": "fred",
        "complianceOfficer": "andrew",
        "Person": [
      {
        "name": "andrew",
        "years": "3"
      },
      {
        "name": "fredx",
        "years": "5",
        "age": "55"
      }
        ]
      }
    }"""

  val sBadIntsCo =
    """{
      "Company": {
        "name": "Widget Holdings",
        "ceo": "fred",
        "complianceOfficer": "andrew",
        "Person": [
      {
        "name": "andrew",
        "years": "3"
      },
      {
        "name": "fred",
        "years": "5x",
        "age": "55y"
      }
        ]
      }
    }"""
}

/*
package com.optrak.scalautil.jsonutil

import org.json4s._
import org.json4s.native.JsonMethods._
import JsonWriting._
import WriteAux._
import JsonExtraction._
import ExtractAux._

import grizzled.slf4j.Logging
import org.specs2.mutable.Specification
import com.optrak.scalautil.ScalazBits._
import scalaz._
import scalaz.syntax.validation._

import CompanyTestData._
import com.optrak.scalautil.IOTestData._


class WriteCompanyTest extends Specification with Logging {
  "basic write" should {
    "match input" in {
      val joe = Simple("joseph")

      val simpleWriter = writer[Simple](simpleFields)
      val joeAST = simpleWriter.write(joe)

      //logger.debug("simple object in JSON: " + pretty(render(joeAST)))
      joeAST mustEqual JObject(List(("name",JString("joseph"))))

    }
  }

  /*"DefaultField" should {
    implicit val context = HeadContext("class with DefaultField: write and parse back")
    val intDefaultExtractor = extractor[IntWithDefault](intDefaultFields)

    "deserialize from empty json with a default value" in {
      val empty = parse("{}")
      intDefaultExtractor.extract(empty) mustEqual IntWithDefault(1).success
    }

    "serialize and deserialize back" in {
      val int2 = IntWithDefault(2)
      val intDefaultWriter = writer[IntWithDefault](intDefaultFields)
      val back = (intDefaultWriter.write _ andThen intDefaultExtractor.extract)(int2)

      back mustEqual int2.success
    }
  }*/

  "people write" should {
    "match input" in {
      implicit val context = HeadContext("Person: write and parse back")

      val personWriter = writer[Person](personFields)
      val written = pretty(render(personWriter.write(joe)))

      val parsed = parsePerson(parse(written))

      //logger.debug(s"joe in JSON: $written")
      parsed mustEqual Success(joe)
    }
  }

  "team write" should {
    "match input" in {
      implicit val context= HeadContext("Team: write and parse back")
      implicit val personReferenceWriter = new ReferenceWriter[Person](_.name)
      implicit val personSetReferenceWriter = new IterableReferenceWriter[Person](_.name)

      val teamWriter = writer[Team](teamFields)
      val written = pretty(render(teamWriter.write(aTeam)))

      val peopleMap = List(joe,jane,fred).map(p => (p.name -> p)).toMap
      val parsed = parseTeam(parse(written),peopleMap.get)

      //logger.debug(s"aTeam in JSON: $written")
      parsed mustEqual Success(aTeam)
    }
  }

  "company write" should {
    "match input" in {
      implicit val context= HeadContext("company")
      wrapErrorAsString(
        for {
          companies <- parseCompanies(j2Companies \ "Company")
        } yield {
          companies.size mustEqual(2)
          //companies.head mustEqual(widgets) TODO: uncomment and fix Company.dateProcessed
          // now write it
          val personWriter = writer[Person](personFields)
          implicit val personReferenceWriter = new ReferenceWriter[Person](_.name)
          implicit val personSetReferenceWriter = new IterableReferenceWriter[Person](_.name)
          val teamWriter = writer[Team](teamFields)
          implicit val personSetWriter = new IterableWriter[Person](personWriter)
          implicit val teamSetWriter = new IterableWriter[Team](teamWriter)
          val companyWriter = writer[Company](companyFields)
          val written = pretty(render(companyWriter.write(companies.head)))

          logger.debug(s"written $written")
          val parsed = parseCompany(parse(written))
          //logger.debug(s"parsed $parsed")
          parsed mustEqual Success(companies.head)
        }
      ) mustEqual("OK")
    }
  }

}
*/

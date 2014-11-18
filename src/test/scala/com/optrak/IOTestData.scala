package com.optrak

import com.optrak.scalautil.Dimensions.{XYZOpt, XYZ}
import shapeless._
import com.optrak.scalautil.Fields._
import org.joda.time.DateTime

object IOTestData {
  case class Simple(name: String)//nothing to do with Company, just used in all writers
  val simpleFields = "name" :: HNil

  case class Person(name: String, age: Option[Int], years: Int)
  val personFields = "name" :: Some("age") :: "years" :: HNil

  val joe = Person("joe", None, 3)
  val andrew = Person("andrew", None, 3)
  val jane = Person("jane", None, 1)
  val fred = Person("fred", Some(55), 5)

  case class Team(name: String, boss: Person, members: Set[Person])
  val teamFields = "name" :: ReferenceField("boss") :: SetReferenceField("member") :: HNil

  val aTeam = Team("a-team", fred, Set(jane, joe))
  val bTeam = Team("b-team", joe, Set.empty)
  val cTeam = bTeam.copy(name = "c-team")
  val dTeam = bTeam.copy(name = "d-team")

  case class Company(name: String,
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

  val thisTime = new DateTime
  val widgets = Company("Widget Co", fred, None, Set(joe, jane, fred), Set(aTeam, bTeam), thisTime)
  val holdings = Company("Widget Holdings", fred, Some(andrew), Set(andrew, fred), Set.empty, thisTime)

  val companyFields = "name" ::
    ReferenceField("ceo") ::
    Some(ReferenceField("complianceOfficer")) ::
    SetField("Person") ::
    SetField("Team") ::
    NowField("dateProcessed") :: HNil

  case class Box(name: String, dimensions: XYZ)
  case class Parcel(name: String, dimensions: Option[XYZOpt])
  case class Bundle(name: String, dimensions: XYZOpt)

  val boxFields = "name" :: "dimensions":: HNil
  val parcelFields = "name" :: Some("dimensions"):: HNil
  val bundleFields = "name" :: Some("dimensions"):: HNil

  val box = Box("Jack", XYZ(10.2, 1.1, 3.0))
  val parcelNone = Parcel("Joe", Some(XYZOpt(None, None, None)))
  val parcelNoneAtAll = Parcel("Joe", None)
  val parcelABit = Parcel("Joey", Some(XYZOpt(None,Some(1.0), None )))
  val bundle = Bundle("jane", XYZOpt(None, Some(1.0), None))

  case class IntWithDefault(i: Int)
  case class DefaultIntField(fName: String) extends DefaultField[Int](fName) {
    def defaultValue = 1
  }
  val intDefaultFields = DefaultIntField("i") :: HNil
}

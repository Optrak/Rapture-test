package com.optrak.xmlutil

import shapeless._
import com.optrak.scalautil._
import Fields._
import ScalazBits._
import xml.{Node, NodeSeq}
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: timpigden
 * Date: 11/09/13
 * Time: 11:30
 * Copyright Optrak Distribution Software Ltd, Hertford, UK, 2013
 */
object CompanyTestData {
  val widgetXmlBase = <Company>
    <name>Widget Co</name>
    <ceo>fred</ceo>
    <people>
      <name>joe</name> <years>3</years>
    </people>
    <people>
      <name>jane</name> <years>1</years>
    </people>
    <people>
      <name>fred</name> <years>5</years> <age>55</age>
    </people>
    <teams>
      <name>a-team</name> <boss>fred</boss> <members>jane</members> <members>joe</members>
    </teams>
    <teams>
      <name>b-team</name> <boss>joe</boss>
    </teams>
  </Company>

  val holdingCompanyBase = <Company>
    <name>Widget Holdings</name>
    <ceo>fred</ceo>
    <complianceOfficer>andrew</complianceOfficer>
    <people>
      <name>andrew</name> <years>3</years>
    </people>
    <people>
      <name>fred</name> <years>5</years> <age>55</age>
    </people>
  </Company>

  val error1Base = <Company>
    <name>Widget Holdings</name>
    <ceo>fred</ceo>
    <complianceOfficer>andrew</complianceOfficer>
    <people>
      <name>andrew</name> <years>3</years>
    </people>
    <people>
      <name>fredx</name> <years>5</years> <age>55</age>
    </people>
  </Company>
  val error2 = <Company>
    <name>Widget Holdings</name>
    <ceo>fred</ceo>
    <complianceOfficer>andrew</complianceOfficer>
    <people>
      <name>andrew</name> <years>3</years>
    </people>
    <people>
      <name>fred</name> <years>5x</years> <age>55y</age>
    </people>
  </Company>



  val widgetXml = <Companies>{widgetXmlBase}</Companies>
  val holidngCompany = <Companies>{holdingCompanyBase}</Companies>

  val companiesXml = <Companies>{ widgetXmlBase } { holdingCompanyBase } </Companies>

  val joeXml = <people><name>joe</name><years>3</years></people>


}

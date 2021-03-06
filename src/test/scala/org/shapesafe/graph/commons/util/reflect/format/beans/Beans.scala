package org.shapesafe.graph.commons.util.reflect.format.beans

import org.shapesafe.graph.commons.util.reflect.format.FormatOvrd.Only
import org.shapesafe.graph.commons.util.reflect.format.FormatOvrd.Only
import shapeless.{::, HNil}

trait Beans {
  trait XX[T] {

    trait ZZ[TT]
  }
//  object XX

  object YY

  type T1 = XX[XX[YY.type]]

  type T2 = XX[YY.type] :: XX[YY.type] :: XX[YY.type] :: HNil

  type T3 = XX[XX[YY.type]]#ZZ[YY.type]

  object Ovrd {

    type Plain = XX[XX[Only[3]]]

    type Ref = XX[XX[3]]

    type T1 = XX[Only[3]] :: XX[Only[3]] :: HNil
    type T2 = XX[Only[3]] :: XX[3] :: HNil
    type T3 = XX[Only[3]] :: XX[Only[3]] :: XX[3] :: HNil
    type T4 = XX[Only[3]] :: XX[Only[3]] :: XX[Only[3]] :: XX[3] :: HNil
  }

}

object Beans extends Beans {}

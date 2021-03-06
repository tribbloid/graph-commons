package org.shapesafe.graph.commons.util.viz

import org.shapesafe.graph.commons.testlib.BaseSpec
import org.shapesafe.graph.commons.util.reflect.ScalaReflection.WeakTypeTag
import shapeless.{syntax, HNil, Witness}

class TypeVizSpec extends BaseSpec {

  import TypeVizSpec._

  def infer[T: WeakTypeTag](v: T): String = {

    TypeViz
      .infer(v)
      .toString
//      .split('\n')
//      .filterNot { line =>
//        line.contains("+ Serializable")
//      }
//      .mkString("\n")
  }

  it("String") {

    TypeViz[String].toString.shouldBe(
      """
        |-+ String .......................................................................... [0]
        | !-+ CharSequence
        | : !-+ Object
        | :   !-- Any
        | !-- Comparable[String]
        | :         ┏ -+ Comparable [ 1 ARG ] :
        | :         ┃  !-- String .......................................................................... [0]
        | !-- java.io.Serializable
        |""".stripMargin.trim
    )
  }

  it("HList") {

    val book =
      "author" ::
        "title" ::
        HNil

    infer(book)
      .shouldBe(
        """
        |-+ String :: String :: shapeless.HNil
        | :       ┏ -+ shapeless.:: [ 2 ARGS ] :
        | :       ┃  !-+ String .......................................................................... [0]
        | :       ┃  : !-+ CharSequence
        | :       ┃  : : !-- Object .......................................................................... [1]
        | :       ┃  : !-- Comparable[String]
        | :       ┃  : :         ┏ -+ Comparable [ 1 ARG ] :
        | :       ┃  : :         ┃  !-- String .......................................................................... [0]
        | :       ┃  : !-- java.io.Serializable ............................................................ [2]
        | :       ┃  !-+ String :: shapeless.HNil
        | :       ┃    :       ┏ -+ shapeless.:: [ 2 ARGS ] :
        | :       ┃    :       ┃  !-- String .......................................................................... [0]
        | :       ┃    :       ┃  !-+ shapeless.HNil
        | :       ┃    :       ┃    !-- shapeless.HList ................................................................. [3]
        | :       ┃    !-- shapeless.HList ................................................................. [3]
        | !-+ shapeless.HList ................................................................. [3]
        |   !-+ java.io.Serializable ............................................................ [2]
        |   : !-- Any
        |   !-+ Product
        |   : !-- Equals
        |   !-- Object .......................................................................... [1]
        |""".stripMargin.trim
      )
  }

  it("record") {
    import syntax.singleton._

    val book =
      ("author" ->> "Benjamin Pierce") ::
        HNil

    infer(book)
      .shouldBe(
        """
          |-+ String with shapeless.labelled.KeyTag[String("author"),String] :: shapeless.HNil
          | :       ┏ -+ shapeless.:: [ 2 ARGS ] :
          | :       ┃  !-+ String with shapeless.labelled.KeyTag[String("author"),String]
          | :       ┃  : !-+ shapeless.labelled.KeyTag[String("author"),String]
          | :       ┃  : : :       ┏ -+ shapeless.labelled.KeyTag [ 2 ARGS ] :
          | :       ┃  : : :       ┃  !-+ String("author")
          | :       ┃  : : :       ┃  : !-- String .......................................................................... [0]
          | :       ┃  : : :       ┃  !-- String .......................................................................... [0]
          | :       ┃  : : !-- Object .......................................................................... [1]
          | :       ┃  : !-+ String .......................................................................... [0]
          | :       ┃  :   !-- CharSequence
          | :       ┃  :   !-- Comparable[String]
          | :       ┃  :   !-- java.io.Serializable ............................................................ [2]
          | :       ┃  !-+ shapeless.HNil
          | :       ┃    !-- shapeless.HList ................................................................. [3]
          | !-+ shapeless.HList ................................................................. [3]
          |   !-+ java.io.Serializable ............................................................ [2]
          |   : !-- Any
          |   !-+ Product
          |   : !-- Equals
          |   !-- Object .......................................................................... [1]
          |""".stripMargin.trim
      )
  }

  describe("Singleton") {

    it("global") {

      TypeViz[TypeVizSpec.singleton.type].toString.shouldBe(
        """
          |-+ org.shapesafe.graph.commons.util.viz.TypeVizSpec.singleton.type
          | !-+ Int
          |   !-+ AnyVal
          |     !-- Any
          |""".stripMargin
      )
    }

    it("local") {

      val w = 3

      TypeViz[w.type].toString.shouldBe(
        """
          |-+ w.type
          | !-+ Int
          |   !-+ AnyVal
          |     !-- Any
          |""".stripMargin
      )
    }

    it("global Witness.T") {

      infer(singletonW.value)
        .shouldBe(
          """
            |-+ org.shapesafe.graph.commons.util.viz.TypeVizSpec.singletonW.T
            | !-+ Int
            |   !-+ AnyVal
            |     !-- Any
            |""".stripMargin
        )

      infer(adhocW.value)
        .shouldBe(
          """
            |-+ Int(3)
            | !-+ Int
            |   !-+ AnyVal
            |     !-- Any
            |""".stripMargin
        )

    }

    it("local Witness.T") {

      {
        val vv = adhocW.value

        infer(vv)
          .shouldBe(
            infer(adhocW.value)
          )
      }

      {
        val vv = singletonW.value

        infer(vv)
          .shouldBe(
            infer(singletonW.value)
          )
      }
    }

  }

  describe("Witness") {

    it("global") {

      val adhocTree = infer(adhocW)
      adhocTree
        .shouldBe(
          """
            |-+ shapeless.Witness{type T = Int(3)} ≅ shapeless.Witness.Aux[Int(3)]
            | :       ┏ -+ shapeless.Witness.Aux [ 1 ARG ] :
            | :       ┃  !-+ Int(3)
            | :       ┃    !-+ Int
            | :       ┃      !-+ AnyVal
            | :       ┃        !-- Any ............................................................................. [0]
            | !-+ shapeless.Witness{type T = T0}
            |   !-+ shapeless.Witness
            |     !-+ java.io.Serializable
            |     : !-- Any ............................................................................. [0]
            |     !-- Object
            |""".stripMargin.trim
        )

      infer(singletonW).shouldBe(adhocTree)
    }

    it("local") {

      val ww = adhocW

      // CAUTION: copying witness into a new variable will lose its type information
      infer(ww.value)
        .shouldBe(
          """
            |-+ ww.T
            | !-+ Int
            |   !-+ AnyVal
            |     !-- Any
            |""".stripMargin
        )

      val wwErased: Witness.Lt[Int] = adhocW

      infer(wwErased)
        .shouldBe(
          """
            |-+ shapeless.Witness{type T <: Int} ≅ shapeless.Witness.Lt[Int]
            | :       ┏ -+ shapeless.Witness.Lt [ 1 ARG ] :
            | :       ┃  !-+ Int
            | :       ┃    !-+ AnyVal
            | :       ┃      !-- Any ............................................................................. [0]
            | !-+ shapeless.Witness{type T <: Lub}
            |   !-+ shapeless.Witness
            |     !-+ java.io.Serializable
            |     : !-- Any ............................................................................. [0]
            |     !-- Object
            |""".stripMargin
        )
    }
  }

  it("refined") {

    val v = TypeViz[Witness.Aux[Int]]
    v.toString.shouldBe(
      """
        |-+ shapeless.Witness{type T = Int} ≅ shapeless.Witness.Aux[Int]
        | :       ┏ -+ shapeless.Witness.Aux [ 1 ARG ] :
        | :       ┃  !-+ Int
        | :       ┃    !-+ AnyVal
        | :       ┃      !-- Any ............................................................................. [0]
        | !-+ shapeless.Witness{type T = T0}
        |   !-+ shapeless.Witness
        |     !-+ java.io.Serializable
        |     : !-- Any ............................................................................. [0]
        |     !-- Object
        |""".stripMargin
    )
  }

  describe("path-dependent") {

    it("with upper bound") {

      val e = new E { type D <: Int }
      TypeViz[e.D].toString.shouldBe(
        """
          |-+ e.D
          | !-+ Int
          |   !-+ AnyVal
          |     !-- Any
          |""".stripMargin
      )
    }

    it("with lower bound") {

      val e = new E { type D >: String <: CharSequence }
      TypeViz[e.D].toString.shouldBe(
        """
          |-+ e.D
          | !-+ CharSequence
          |   !-+ Object
          |     !-- Any
          |""".stripMargin
      )
    }

    it("with final type") {

      val e = new E { final type D = Int }

      TypeViz[e.D].toString.shouldBe(
        """
          |-+ e.D
          | !-+ Int
          |   !-+ AnyVal
          |     !-- Any
          |""".stripMargin
      )
    }

    it("with final type in path") {

      TypeViz[EE.D].toString.shouldBe(
        """
          |-+ Int ≅ org.shapesafe.graph.commons.util.viz.TypeVizSpec.EE.D
          | !-+ AnyVal
          |   !-- Any
          |""".stripMargin
      )

      val e = new EE
      TypeViz[e.D].toString.shouldBe(
        """
          |-+ Int ≅ e.D
          | !-+ AnyVal
          |   !-- Any
          |""".stripMargin
      )

    }

  }

  it("this") {

    val e = new EE
    TypeViz[e.THIS].toString.shouldBe(
      """
        |-+ e.type ≅ e.THIS
        | !-+ org.shapesafe.graph.commons.util.viz.TypeVizSpec.EE
        |   !-+ org.shapesafe.graph.commons.util.viz.TypeVizSpec.E
        |     !-+ Object
        |       !-- Any
        |""".stripMargin
    )
  }
}

object TypeVizSpec {

  val singleton = 3

  def adhocW = Witness(3)

  val singletonW = Witness(3)

  class E { type D }

  class EE extends E {

    final type D = Int

    final type THIS = this.type
  }

  object EE extends EE
}

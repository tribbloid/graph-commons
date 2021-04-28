package com.tribbloids.graph.commons.util.reflect.format

import com.tribbloids.graph.commons.testlib.BaseSpec
import com.tribbloids.graph.commons.util.reflect.Reflection
import com.tribbloids.graph.commons.util.reflect.format.FormatOvrd.{~~, Singleton}
import com.tribbloids.graph.commons.util.reflect.format.Formats.{ClassName, KindName, TypeImpl}
import com.tribbloids.graph.commons.util.viz.TypeViz
import shapeless.Witness

class FormatOvrdSpec extends BaseSpec {

  import FormatOvrdSpec._

  val format: TypeFormat = EnableOvrd(TypeImpl.DeAlias)

  val viz: TypeViz[Reflection.Runtime.type] = TypeViz.withFormat(format)

  describe("fallback") {

    it("1") {

      viz[String].typeStr.shouldBe("String: ClassNoArgsTypeRef")
    }

    it("2") {
      val tt = Reflection.Runtime.universe.typeOf[Undefined[Int]]

      viz[Undefined[Int]].typeStr.shouldBe(
        s"$tt: ClassArgsTypeRef"
      )
    }
  }

  describe(Singleton.toString) {

    it("1") {

      viz[Singleton[3]].typeStr.shouldBe("3")
    }

    it("2") {

      val o = W_Singleton(3)
      viz[o._Info].typeStr.shouldBe("3")
    }

    it("3") {

      viz[Singleton[global.type]].typeStr.shouldBe(
        s"${FormatOvrdSpec.getClass.getCanonicalName.stripSuffix("$")}.global.type"
      )
    }

    it("4") {

      val o3 = W_Singleton(global)

      viz[o3._Info].typeStr
        .shouldBe(
          s"${FormatOvrdSpec.getClass.getCanonicalName.stripSuffix("$")}.global.type"
        )
    }

    it("5") {

      val local = 3
      val o2 = W_Singleton(local)

      viz[o2._Info].typeStr.shouldBe("local.type")
    }

    it("fallback") {

      viz[W_Singleton[Int]#_Info].typeStr.shouldBe(
        s"com.tribbloids.graph.commons.util.reflect.format.FormatOvrd.Singleton[Int]: ClassArgsTypeRef"
      )

      viz[W_Singleton[Int]#_InfoWFallback].typeStr.shouldBe(
        s"${FormatOvrdSpec.getClass.getCanonicalName.stripSuffix("$")}.W_Singleton"
      )
    }
  }

  describe(~~.toString) {

    it("1") {
      viz[W_~~[3]#_Info].typeStr.shouldBe("3 3 Int(3): UniqueConstantType")
    }

    it("2") {

      val o = W_~~(3)
      viz[o._Info].typeStr.shouldBe("3 3 Int(3): UniqueConstantType")
    }
  }
}

object FormatOvrdSpec {

  val global = 3

  class Undefined[T]

  class W_Singleton[T <: Int](w: Witness.Aux[T]) {

    type _Info = Singleton[T]

    type _InfoWFallback = _Info with ClassName[this.type]
  }
  object W_Singleton {

    def apply(w: Witness.Lt[Int]) = new W_Singleton[w.T](w)
  }

  class W_~~[T <: Int](w: Witness.Aux[T]) {

    type _Info = Singleton[T] ~~ W_Singleton[T]#_Info ~~ T
  }
  object W_~~ {

    def apply(w: Witness.Lt[Int]) = new W_~~[w.T](w)
  }
}

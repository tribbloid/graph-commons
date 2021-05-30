package com.tribbloids.graph.commons.util

import com.tribbloids.graph.commons.util.TextBlock.Padding
import com.tribbloids.graph.commons.util.reflect.ScalaReflection

trait ProductTree extends TreeLike with Product {

  import ProductTree._

  private lazy val argList = this.productIterator.toList

  lazy val constructorString: String = {

    val hasOuter = this.getClass.getDeclaringClass != null

    if (hasOuter) {
      val list = HasOuter.outerListOf(this)

      val names = list.map { v =>
        val dec = decodedStrOf(v)

        dec
      }

      names.reverse.mkString(" ‣ ")
    } else {
      decodedStrOf(this)
    }

  }

  final override lazy val nodeString = {

    val notTree = this.argList
      .filterNot(v => v.isInstanceOf[TreeLike])

    if (notTree.isEmpty) {

      constructorString
    } else {

      val _notTree = notTree.map { str =>
        TextBlock("" + str).padLeft(Padding.argLeftBracket).build
      }

      TextBlock(constructorString)
        .zipRight(
          TextBlock(_notTree.mkString("\n"))
        )
        .build
    }
  }

  final override lazy val children: List[TreeLike] = {

    this.argList.collect {
      case v: TreeLike => v
    }
  }
}

object ProductTree {

  def decodedStrOf(v: AnyRef): String = {
    val clz = v.getClass
    val enc =
      clz.getCanonicalName.replace(clz.getPackage.getName, "").stripPrefix(".").stripSuffix("$")

    val dec = ScalaReflection.universe.TypeName(enc).decodedName
    dec.toString
  }
}

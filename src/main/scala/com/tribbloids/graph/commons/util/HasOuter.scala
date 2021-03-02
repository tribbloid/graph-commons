package com.tribbloids.graph.commons.util

trait HasInner {

  trait Inner {

    final val outer: HasInner.this.type = HasInner.this

    type Self <: outer.Inner

    final val inner: Self = this.asInstanceOf[Self]
  }
}

object HasInner {}

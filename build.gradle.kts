val vs: Versions = versions()

dependencies {


//    api("eu.timepit:refined_${vv.scalaBinaryV}:0.9.14")
//    /TODO: remove, most arity inspection macros doesn't work on collection/tuple, using shapeless Length as cheap alternative

    testImplementation("com.chuusai:shapeless_${vs.scalaBinaryV}:2.3.3")

    testFixturesApi("org.scalatest:scalatest_${vs.scalaBinaryV}:${vs.scalatestV}")
}
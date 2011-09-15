import coffeescript._

scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8",
                      "-deprecation")

libraryDependencies ++= {
  val ufv = "0.4.2-SNAPSHOT"
  Seq("net.databinder" %% "unfiltered-filter" % ufv,
    "net.databinder" %% "unfiltered-jetty" % ufv,
    "net.databinder" %% "unfiltered-uploads" % ufv,
   "org.clapper" %% "avsl" % "0.3.1")
}

seq(CoffeeScript.coffeeSettings: _*)

targetDirectory in Coffee <<= (resourceManaged in Compile) {
  _ / "public" / "js"
}

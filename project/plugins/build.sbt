libraryDependencies <+= sbtVersion(v =>
  "me.lessis" %% "coffeescripted-sbt" % "0.1.4-%s".format(v)
)

resolvers += "lessis" at "http://repo.lessis.me"

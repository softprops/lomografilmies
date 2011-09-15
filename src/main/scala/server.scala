package lomgr

object Server {
  import unfiltered.jetty.Http
  import unfiltered.filter.Planify
  import unfiltered.util.Browser

  def main(args: Array[String]) {
    Http(8080)
    .context("/assets") { _.resources(getClass.getResource("/public/")) }
    .filter(Planify(Filmies.accept))
    .filter(Planify(Filmies.index orElse Filmies.films)).run(s =>
        args match {
          case a@Array(_) => if(a.contains("-b")) Browser.open(s.url)
          case _ => ()
        }
     )
  }
}

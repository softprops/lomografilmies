package lomgr

import java.io.File

object Paths {
  def toSrc(org: String) = org.replaceAll("""(\S+)[.]""", "src.")
  def toLink(file: File) = file.getPath.replaceAll("""\S+/files""", "/assets/files")
  def toFile(path: String)(base: java.net.URL) = new File(
    path.replaceAll("""/assets/files""", base.getFile.replace("""/files/\S+""","files"))
  )
}

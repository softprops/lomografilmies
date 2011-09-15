package lomgr

import java.io.{File, FileInputStream => FIS, FileOutputStream => FOS}

object Copy {
  def apply(from: File)(to: File) = {
    to.getParentFile().mkdirs()
    @annotation.tailrec
    def transfer(fis: FIS, fos: FOS, buf: Array[Byte]): Unit = {
      fis.read(buf, 0, buf.length) match {
        case -1 =>
          fos.flush()
        case read =>
           fos.write(buf, 0, read)
           transfer(fis, fos, buf)
      }
    }
    transfer(new FIS(from), new FOS(to), new Array[Byte](1024*16))
  }
}

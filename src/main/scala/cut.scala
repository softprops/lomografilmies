package lomgr

object Cut {
  import javax.imageio.ImageIO
  import java.awt.image.BufferedImage
  import java.io.{FileInputStream => FIS}
  import java.io.File
  import java.awt._

  def apply(src: File)(rows: Int = 2, cols: Int = 4, padding: Int = 0) = {
    val fis = new FIS(src)
    val image = ImageIO.read(fis)
    val chunks = rows * cols
    val (cwidth, cheight) = (image.getWidth / cols, image.getHeight / rows)
    val imgs = for(x <- 0 until rows; y <- 0 until cols) yield {
       val bi = new BufferedImage(cwidth, cheight, image.getType)
       val gr = bi.createGraphics
       gr.drawImage(image, 0, 0, cwidth, cheight,
                    cwidth * y, cheight * x,
                    cwidth * y + cwidth, cheight * x + cheight, null)
       gr.dispose()
       bi
    }
    for(i <- 0 until imgs.size) yield {
       val file = new File(src.getParentFile(),
                           src.getPath.split(File.separator).last.replaceAll("""\S+[.]""","%d." format i))
       ImageIO.write(imgs(i), "jpg", file)
       file
    }
  }
}

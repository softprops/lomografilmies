package lomgr

import javax.imageio._
import javax.imageio.metadata._
import javax.imageio.stream._
import java.awt.image._
import java.io._
import java.util.Iterator

case class Giffy(out: ImageOutputStream, `type`: Int, timeBetween: Int, loop: Boolean = true)(imgs: Seq[RenderedImage]) {

  val gifWriter = writer()
  val imageWriteParam = gifWriter.getDefaultWriteParam()
  val imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(`type`)
  val imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam)
  val metaFormatName = imageMetaData.getNativeMetadataFormatName()

  val root = imageMetaData.getAsTree(metaFormatName).asInstanceOf[IIOMetadataNode]

  val gctrls = node(root, "GraphicControlExtension")
  gctrls.setAttribute("disposalMethod", "none")
  gctrls.setAttribute("userInputFlag", "FALSE")
  gctrls.setAttribute("transparentColorFlag", "FALSE")
  gctrls.setAttribute("delayTime", Integer.toString(timeBetween / 10));
  gctrls.setAttribute("transparentColorIndex", "0")

  val commentsNode = node(root, "CommentExtensions")
  commentsNode.setAttribute("CommentExtension", "Created by MAH")

  val appEntensionsNode = node(root, "ApplicationExtensions")

  appEntensionsNode.appendChild(new IIOMetadataNode("ApplicationExtension") {
    setAttribute("applicationID", "NETSCAPE")
    setAttribute("authenticationCode", "2.0")
    val loopv = if(loop) 0 else 1
    setUserObject(Array[Byte](
      0x1, (loopv & 0xFF).asInstanceOf[Byte], ((loopv >> 8) & 0xFF).asInstanceOf[Byte]
    ))
  })

  imageMetaData.setFromTree(metaFormatName, root)

  gifWriter.setOutput(out)
  gifWriter.prepareWriteSequence(null)

  def include(img: RenderedImage) =
    gifWriter.writeToSequence(
      new IIOImage(img, null, imageMetaData),
      imageWriteParam
    )

  private def close() = gifWriter.endWriteSequence()

  private def writer() = {
    val iter = ImageIO.getImageWritersBySuffix("gif")
    if(!iter.hasNext()) throw new IIOException("No GIF Image Writers Exist")
    else iter.next()
  }

  private def node(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode = {
    val nNodes = rootNode.getLength
    (for (i <- 0 until nNodes
         if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0)) yield
        rootNode.item(i).asInstanceOf[IIOMetadataNode]) headOption match {
          case Some(found) => found
          case _ =>
            val c = new IIOMetadataNode(nodeName)
            rootNode.appendChild(c)
            c
        }
  }

  for(i <- imgs) include(i)
  close()
  out.close()
}

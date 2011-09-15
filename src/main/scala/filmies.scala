package lomgr

import unfiltered.request._
import lomgr.{DELETE, PUT, POST}
import unfiltered.request.{DELETE => _, PUT => _, POST => _}

import unfiltered.response._
import java.io.File
import org.clapper.avsl.Logger


object Filmies {
  import QParams._
  import javax.imageio.ImageIO
  import javax.imageio.stream.{FileImageOutputStream => FIOS,
                               ImageOutputStream => IOS}
  val log = Logger(getClass)
  val files = getClass().getResource("/public/files/")
  val published = getClass().getResource("/public/published/")

  val GifName = "filmie.gif"

  val Oktomat = "okto"
  val Pop9 = "pop9"
  val ActionSampler = "actionsampler"
  val SuperSampler = "supersampler"
  val Cuts = Oktomat :: Pop9 :: ActionSampler :: SuperSampler :: Nil

  val Slomo = "slomo"
  val Medio = "medio"
  val Speedio = "speedio"
  val Speeds = Slomo :: Medio :: Speedio :: Nil

  def index: unfiltered.Cycle.Intent[Any, Any] = {
    case GET(Path("/") & Params(p)) => p("s") match {
      case Seq(scrapped, _*) =>
        Pages.index(msgs = "Scraped Film" :: Nil)
      case _ => Pages.index()
    }
  }

  def films: unfiltered.Cycle.Intent[Any, Any] = {
    case POST(Path("/films") & Params(p)) =>
      val expect = for {
         f <- lookup("file") is required("missing")
      } yield {
        val from = Paths.toFile(f.get)(files)
        val to = new File(published.getFile, from.getPath.replaceAll("""\S+/files/""", ""))
        if(from.exists) {
          Copy(from)(new File(published.getFile, from.getPath.replaceAll("""\S+/files/""", "")))
          ResponseString("published %s" format to.getPath)
        } else {
          ResponseString("file %s does not exist" format f.get)
        }
      }
      expect(p) orFail { errs =>
        BadRequest ~> ResponseString(errs map(_.error) mkString(", "))
      }
    case PUT(Path("/films") & Params(p)) =>
      val expect = for {
        f <- lookup("file") is required("missing")
        frames <- lookup("frames") is optional[String, String]
      } yield {
        ResponseString("should update %s with %s" format(f.get, frames.get))
      }
      expect(p) orFail { errs =>
        BadRequest ~> ResponseString(errs map(_.error) mkString(", "))
      }
    case DELETE(Path("/films") & Params(p)) =>
      val expect = for {
        f <- lookup("file") is required("missing")
      } yield {
        new File(f.get.replaceAll("""\S+/files""", files.getFile)) match {
          case f if(f.exists) =>
            f.delete()
            Redirect("/?s=%s" format(java.net.URLEncoder.encode("✓", "utf-8")))
          case _ =>
            ResponseString("file does not exist")
        }
      }
      expect(p) orFail { errs =>
         BadRequest ~> ResponseString(errs map(_.error) mkString(", "))
      }
  }

  def accept[A, B]: unfiltered.Cycle.Intent[A, B] = {
    case POST(Path("/up") & MultiPart(req)) =>
      val mpp = MultiPartParams.Disk(req)
      (mpp.files("f"), mpp.params("cut"), mpp.params("speed")) match {
        case (Seq(f, _*), cut :: Nil, speed :: Nil) if(f.size > 0) =>
          log.info("file name:%s size:%s" format(f.name, f.size))
          val (base, src) = (Hash(f.bytes), Paths.toSrc(f.name))
          log.info("base %s src %s" format(base, src))
          val dest = new File("%s%s/%s" format(
            files.getFile, base, Keys.uniq), src)
          dest.getParentFile.mkdirs

          f.write(dest) match {
            case Some(written) =>
              val c = Cut(written)_
              val cuts = (cut match {
                case Oktomat       => c(2, 4, 0)
                case Pop9          => c(3, 3, 0)
                case ActionSampler => c(2, 2, 0)
                case SuperSampler  => c(1, 4, 0)
                case _             => c(1, 1, 0)
              })

              val frames = cuts.zipWithIndex.map {
                case (c, i) => Frame(Paths.toLink(c), i, true)
              }

              val gif = new File(dest.getParentFile, GifName)
              Giffy(new FIOS(gif), ImageIO.read(cuts.head).getType(),
                speed match {
                  case Slomo => 400
                  case Medio => 100
                  case Speedio => 50
                  case _ => 100
                }, true)(
                cuts map ImageIO.read
              )

              Pages.show(Paths.toLink(gif), frames)
            case _ => Pages.failedWrite("could not write this file :/")
          }
        case _ => Pages.index(errors = "missing params" :: Nil)
     }
  }
}

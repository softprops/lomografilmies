package lomgr

object Hash {
  import java.security.MessageDigest
  val Alg = "SHA1"
  def apply(bytes: Array[Byte]) =
    new java.math.BigInteger(1, MessageDigest.getInstance(Alg).digest(bytes)).toString(16)
}

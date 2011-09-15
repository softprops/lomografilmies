package lomgr

import unfiltered.request.HttpRequest
import unfiltered.request.{PUT => RealPUT, DELETE => RealDELETE,
                           POST => RealPOST, Params}

/** Used for cases where http clients do not support a means of
 *  requesting DELETE or PUT requests. Alternatively clients may
 *  request DELETE or PUT by using a POST request with the target method
 *  defined in the `_method` param */
trait Method {
  val Override = "_method"
  val Backup = "POST"

  /** The target method to match */
  def target: String

  /** @return Some(r) if POST method and target override matches */
  def matches[T](r: HttpRequest[T]) =
    if (!r.method.equalsIgnoreCase(Backup)) None
    else Params.unapply(r) match {
      case Some(params) => params(Override) match {
        case Seq(value, _*) =>
          if(value.equalsIgnoreCase(target)) Some(r)
          else None
        case _ => None
      }
      case _ => None
    }
}

object PUT extends Method {
  val target = "PUT"
  def unapply[T](r: HttpRequest[T]) = RealPUT.unapply(r) orElse matches(r)
}

object DELETE extends Method {
  val target = "DELETE"
  def unapply[T](r: HttpRequest[T]) = RealDELETE.unapply(r) orElse matches(r)
}

object POST extends Method {
  val target = "POST"
  /** @return Some(r) if r is a POST method and does NOT have a
   *          conflicting override param */
  def unapply[T](r: HttpRequest[T]) = RealPOST.unapply(r) flatMap {
    case r =>
      Params.unapply(r) match {
       case Some(params) => params(Override) match {
         case Seq(value, _*) =>
           if(!value.equalsIgnoreCase(target)) None
           else Some(r)
         case _ => Some(r)
       }
       case _ => Some(r)
      }
  }
}

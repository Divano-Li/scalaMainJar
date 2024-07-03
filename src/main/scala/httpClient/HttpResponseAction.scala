package httpClient


object HttpResponseAction {
  private val Ok = 200
  private val NotFound = 404

  def apply(statusCode: Int = Ok, body: String = ""): HttpResponseAction = {
    new HttpResponseAction(body, statusCode)
  }

}

class HttpResponseAction(val body: String, val statusCode: Int) {
  private val mu = 300

  def get[T]()(implicit m: Manifest[T]): T = {
    JsonMapper.from[T](body)
  }

  def ok: Boolean = statusCode < mu

  def onSuccess[T]()(f: T => Unit)(implicit m: Manifest[T]): Unit = {
    if (ok) {
      f(JsonMapper.from[T](body))
    }
  }

  def onComplete[T, RT](onSuccess: T => RT, default: RT)(implicit m: Manifest[T]): RT = {
    if (ok) {
      onSuccess(JsonMapper.from[T](body))
    } else {
      default
    }
  }
}
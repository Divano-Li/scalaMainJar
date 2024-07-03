import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, duration}

object PrivateExecutionContext {

  import duration._

  implicit val Timeout = 10.seconds

  implicit lazy val EC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}

object SingleExecutionContext {

  import duration._

  implicit val Timeout = 10.seconds

  implicit lazy val SingleEC = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
}

object SingleExecutionContext1 {

  import duration._

  implicit val Timeout = 10.seconds

  implicit lazy val SingleEC = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
}

object HttpClientExecutionContext {

  import duration._

  implicit val Timeout = 10.seconds

  implicit lazy val HttpClientEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}
object HttpClientExecutionContext1 {

  import duration._

  implicit val Timeout = 10.seconds

  implicit lazy val HttpClientEC = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))
}

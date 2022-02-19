package controllers

import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures.FutureOps
import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class TimeoutController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext) extends BaseController {

  def timeout(duration: Long = 1000): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val start = System.currentTimeMillis()
    val absDuration = Math.abs(duration)
    Future {
      Thread.sleep(absDuration + 100)
      "hello world"
    }
      .withTimeout(absDuration.milliseconds)
      .map { response =>
        Ok(s"$response after ${System.currentTimeMillis() - start} milliseconds.")
      }.recover {
      case e => InternalServerError(s"something went wrong. $e")
    }
  }

}

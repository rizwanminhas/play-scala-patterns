package controllers

import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures.FutureOps
import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class TimeoutController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext) extends BaseController {

  def timeout(duration: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val start = System.currentTimeMillis()
    Future {
      Thread.sleep(1000 + (2000 * Math.random()).toInt)
      "hello world"
    }
      .withTimeout(duration.milliseconds)
      .map { response =>
        Ok(s"$response after ${System.currentTimeMillis() - start} milliseconds.")
      }.recover {
      case e => InternalServerError(s"something went wrong. $e")
    }
  }

}

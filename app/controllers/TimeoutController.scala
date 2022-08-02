package controllers

import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures.FutureOps
import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration._

@Singleton
class TimeoutController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext) extends BaseController {

  def withTimeout(durationInMillis: Long)(callback: Request[AnyContent] => Future[Result]): Action[AnyContent] =
    Action.async { implicit request =>
      callback(request).withTimeout(durationInMillis.milliseconds).recover {
        case e: TimeoutException => RequestTimeout(s"request timed out: $e")
      }
    }


  def timeout(durationInMillis: Long = 1000): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val start = System.currentTimeMillis()
    val absDuration = Math.abs(durationInMillis)
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

  def actionWithTimeout: Action[AnyContent] = withTimeout(1000) { implicit request =>
    Future {
      Thread.sleep(1100)
      Ok("This should timeout if Thread.sleep > delay value in withTimeout")
    }
  }

}

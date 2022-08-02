package controllers

import akka.actor.ActorSystem
import play.api.libs.concurrent.Futures
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DelayController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext, system: ActorSystem) extends BaseController {

  def fromTheFuture(message: String): Future[String] = Future {
    message
  }

  def delay(sleepInMillis: Long, delayInMillis: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val delayedFuture = akka.pattern.after(delayInMillis.millis)(fromTheFuture("delayed response"))

    val sleepyFuture = Future {
      Thread.sleep(sleepInMillis)
      "sleepy response"
    }
    val result = Future.firstCompletedOf(Seq(sleepyFuture, delayedFuture))

    result.map(res => Ok(res)).recover {
      case e => InternalServerError(s"something went wrong. $e")
    }
  }
}

package controllers

import akka.actor.ActorSystem
import play.api.libs.concurrent.Futures
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DelayController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext, system: ActorSystem) extends BaseController {

  val delayedPattern = akka.pattern.after(3000.millis)(fromTheFuture("bar"))
    //akka.pattern.after(5000.millis)(Future.failed(new IllegalStateException("no...")))

  def fromTheFuture(message: String): Future[String] = Future {
    message
  }

  def delay(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val future = Future {
      println("fut is called")
      Thread.sleep(1000)
      println("fut is awake")
      "foo"
    }
    val result = Future.firstCompletedOf(Seq(future, delayedPattern))

    result.map(res => Ok(res)).recover {
      case e => InternalServerError(s"something went wrong. $e")
    }
  }
}

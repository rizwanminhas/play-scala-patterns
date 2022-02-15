package controllers

import akka.actor.ActorSystem
import play.api.libs.concurrent.Futures
import play.api.mvc._

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class RetryController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext, system: ActorSystem) extends BaseController {
  val MAX_RETRIES = 5

  def retry(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    println(s"${"*" * 50} - ${request.id}")

    @volatile var retryCount = 1

    def futureToAttempt(reqId: Long): Future[String] = {
      println(s"Retrying [$retryCount of $MAX_RETRIES] [RequestId: $reqId]")
      if (Random.nextInt(10) < 8 && retryCount < MAX_RETRIES) {
        retryCount += 1
        Future.failed(new IllegalStateException(retryCount.toString))
      } else if (retryCount >= MAX_RETRIES) {
        val message = s"Couldn't get an answer after $MAX_RETRIES tries"
        println(message)
        Future.successful(message)
      } else {
        val message = s"Got answer on try $retryCount of $MAX_RETRIES [RequestId: $reqId]"
        println(message)
        Future.successful(message)
      }
    }

    implicit val scheduler = system.scheduler
    retryCount = 1
    val retried: Future[String] = akka.pattern.retry(() => futureToAttempt(request.id), attempts = MAX_RETRIES, 500.milliseconds)
    retried.map { response =>
      Ok(s"$response")
    }.recover {
      case e => InternalServerError(s"something went wrong. $e")
    }
  }

}

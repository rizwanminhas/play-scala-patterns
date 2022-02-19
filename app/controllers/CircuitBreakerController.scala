package controllers

import akka.actor.ActorSystem
import akka.pattern.CircuitBreaker
import play.api.libs.concurrent.Futures
import play.api.mvc._
import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CircuitBreakerController @Inject()(val controllerComponents: ControllerComponents)(implicit futures: Futures, ec: ExecutionContext, system: ActorSystem) extends BaseController {
  val breaker =
    new CircuitBreaker(system.scheduler, maxFailures = 3, callTimeout = 1.seconds, resetTimeout = 1.minute)
      .onOpen(notifyMeOnOpen())

  def notifyMeOnOpen(): Unit = {
    println("*" * 50)
    println("Circuit is now open.")
  }

  def service(): Future[Int] = Future {
    println("*" * 50)
    println("Service is called.")
    throw new IllegalArgumentException("oops...")
  }

  def circuitBreaker(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    breaker.withCircuitBreaker(service())
      .map(i => Ok(s"service call returned: $i"))
      .recover {
        case e => InternalServerError(s"something went wrong. $e")
      }
  }
}
